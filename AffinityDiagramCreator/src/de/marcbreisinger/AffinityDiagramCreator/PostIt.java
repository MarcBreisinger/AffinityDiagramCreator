package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ToolTipManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.View;

public class PostIt extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3606684329989000039L;
	public Color color;
	public String text;
	private int edgeLengthPixel;
	private BasicStroke stroke = new BasicStroke(2.0f);
	private Font font;
	private int fontSize;
	private JTextArea area;
	public int getFontSize() {
		return fontSize;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
		font = new Font("SansSerif", Font.PLAIN, fontSize);
		area.setFont(font);
	}
	private boolean textTooLong = false;
	//is this post it intended for export or for display on screen
	private boolean export = false;
	private Color textTooLongColor;
	
	public PostIt(final Color color, final String text, final boolean export){
		this.color = color;
		this.text = text;
		this.export = export;
		float postitEdge = Float.valueOf(ADBPreferences.getPreference(ADBPreferences.POSTIT_SIDELENGTH_INCH));
		textTooLongColor = Color.decode(ADBPreferences.getPreference(ADBPreferences.TEXT_TOO_LONG_MARKER_COLOR));
		this.edgeLengthPixel = (int) (postitEdge * Settings.PIXEL2INCH);
		this.setSize(new Dimension(this.edgeLengthPixel +1, this.edgeLengthPixel +1));
		fontSize = (int)Integer.valueOf(ADBPreferences.getPreference(ADBPreferences.FONT_SIZE));
		
		area = new JTextArea()
        {
			private static final long serialVersionUID = -3538767890819988969L;
			@Override
		    public synchronized void addMouseListener(MouseListener l) { }
		    @Override
		    public synchronized void addMouseMotionListener(MouseMotionListener l) {}
		    @Override
		    public synchronized void addMouseWheelListener(MouseWheelListener l) {}
		    @Override
		    public void addNotify() {
		        disableEvents(AWTEvent.MOUSE_EVENT_MASK | 
		                AWTEvent.MOUSE_MOTION_EVENT_MASK | 
		                AWTEvent.MOUSE_WHEEL_EVENT_MASK);
		        super.addNotify();
		    }
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);

                int preferredHeight = (int)getUI().getRootView(this).getPreferredSpan(View.Y_AXIS);
                if(preferredHeight > getSize().height){
                	textTooLong = true;
                	fontSize--;
                	font = new Font("SansSerif", Font.PLAIN, fontSize);
                	setFont(font);
                }
                
            }

            private void paintEllipsis(Graphics g)
            {
                try
                {
                    int caretWidth = 1;
                    FontMetrics fm = getFontMetrics( getFont() );
                    String ellipsis = "...";
                    int ellipsisWidth = fm.stringWidth( ellipsis ) + caretWidth;

                    Insets insets = getInsets();
                    int lineWidth = getSize().width - insets.right;
                    Point p = new Point(lineWidth, getSize().height - 1);

                    int end = viewToModel( p );
                    Rectangle endRectangle = modelToView( end );
                    int start = end;
                    Rectangle startRectangle = endRectangle;
                    int maxWidth = lineWidth - ellipsisWidth;

                    while (startRectangle.x + startRectangle.width > maxWidth)
                    {
                        startRectangle = modelToView( --start );

                    }

                    Rectangle union = startRectangle.union( endRectangle );
                    g.setColor( getBackground() );
                    g.fillRect(union.x + caretWidth, union.y, union.width, union.height);
                    g.setColor( getForeground() );
                    g.drawString("...", union.x + caretWidth, union.y + union.height - fm.getDescent());
                }
                catch(BadLocationException ble)
                {
                    System.out.println( ble );
                }
            }
        };
		
		area.setText(text);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setOpaque(false);
		area.setEditable(false);
		area.setFocusable(false);
		area.setRequestFocusEnabled(false);
		ToolTipManager.sharedInstance().unregisterComponent(area);
		area.setDragEnabled(false);
		font = new Font("SansSerif", Font.PLAIN, fontSize);
		area.setFont(font);
		
		
		

		
		setLayout(null);
		final Rectangle bounds = this.getBounds();
		int pMargin = (int)(Float.valueOf(ADBPreferences.getPreference(ADBPreferences.POSTIT_MARGIN)) * Settings.PIXEL2INCH);
		area.setBounds(bounds.x + pMargin, bounds.y + pMargin, bounds.width - 2 * pMargin, bounds.height - 2 * pMargin);
		JPanel bg = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -5298569377653694710L;

			public void paintComponent(Graphics g){
				Container c = this;
				while(!(c instanceof Cluster)){
					c = c.getParent();
				}
				Cluster cl = (Cluster)c;
				Color co = null;
				if(cl.isSelected()){
					 co = new Color(color.getRed(), color.getGreen(), color.getBlue(), 128);
				} else {
					co = color;
				}
				Graphics2D g2 = (Graphics2D) g;
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        if(!export && textTooLong)
		        	co = textTooLongColor;
		        g2.setPaint(co);
				int dim = (int) (edgeLengthPixel);
				g2.fillRoundRect(0, 0, dim, dim, Settings.POSTIT_CORNER_RADIUS, Settings.POSTIT_CORNER_RADIUS);
				g2.setPaint(Color.GRAY);
				g2.setStroke(stroke);
				g2.drawRoundRect(0, 0, dim, dim, Settings.POSTIT_CORNER_RADIUS, Settings.POSTIT_CORNER_RADIUS);
				g2.setPaint(Color.BLACK);
				
				
				
			}
		};
		bg.setBounds(this.getBounds());
		add(area);
		add(bg);
		
	}
	public PostIt copy(boolean export){
		PostIt p = new PostIt(this.color, this.text, export);
		p.setFontSize(fontSize);
		return p;
	}
	FontMetrics pickFont(JTextArea g2,
            String longString,
            int ySpace) {
		boolean fontFits = false;
		Font font = g2.getFont();
		FontMetrics fontMetrics = g2.getFontMetrics(font);
		int size = font.getSize();
		String name = font.getName();
		int style = font.getStyle();
		
		while ( !fontFits ) {
			if(longString.contains("he likes the concierge button"))
				System.out.println("Here");
			double h = fontMetrics.getStringBounds(longString, getGraphics()).getHeight();
			int lines = RXTextUtilities.getWrappedLines(g2);
			double he = lines * h;
			if ( (fontMetrics.getHeight() <= Settings.MAX_CHAR_HEIGHT)
					
			    && (he <= ySpace) ) {
			   fontFits = true;
			}
			else {
			   if ( size <= Settings.MIN_FONT_SIZE ) {
			       fontFits = true;
			   }
			   else {
			       g2.setFont(font = new Font(name,
			                                  style,
			                                  --size));
			       fontMetrics = g2.getFontMetrics(font);
			   }
			}
		}
	
	return fontMetrics;
	}

}
