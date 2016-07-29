package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JViewport;

public class Canvas extends Movable implements KeyListener, Observer {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2273706035367348561L;

	public Dimension initialDimension;
	
	private boolean panMode = false;
	private boolean zoomMode = false;
	private HandScrollListener hsl = new HandScrollListener();
	private MouseWheelZoomer mwz = new MouseWheelZoomer();
	private Point zoomPoint = null;
	private AffineTransform scaleXform,inverseXform;
	private Point2D.Double drawingLocation = new Point2D.Double(0,0);
	private Vector<Cluster> movables = new Vector<Cluster>();
	private Vector<Page> pages = new Vector<Page>();
	private Model model;
	private double oldZoomFactor = 1.0;
	private KeyEventDispatcher ked;
	public Cursor cursorZoom, cursorPanClosed, cursorPanOpen;

	public Canvas(Model m){
		setLayout(null);
		model = m;
		model.addObserver(this);
		init();
	}
	public Canvas(float widthInInch, float heightInInch, Model m){
		setLayout(null);
		model = m;
		model.addObserver(this);
		init();
	}
	public void preparePage(Page p){
		int w = p.getWidth();
		int h = p.getHeight();
		int canvasMargin = (int)((Float.valueOf(ADBPreferences.getPreference(ADBPreferences.CANVAS_MARGIN))*Settings.PIXEL2INCH));
		p.setBounds((int)(drawingLocation.x + canvasMargin), (int)canvasMargin, w, h);
		p.setInitialBounds(new Rectangle((int)(drawingLocation.x + canvasMargin), (int)canvasMargin, w, h));
		this.drawingLocation.x += w + canvasMargin;
		this.pages.add(p);
	}
	public Page[] getPages(){
		return pages.toArray(new Page[pages.size()]);
	}
	
	public void setZoomFactorIndex(int index){	
		model.setZoomFactorIndex(index);
		adjustZoom();
	}
	public void setZoomMode(boolean on){
		if(on && !zoomMode){
			zoomMode = true;
			setCursor(cursorZoom);
			//setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			removeMouseListener(this);
			addMouseListener(mwz);
			addMouseWheelListener(mwz);
		} else if(!on && zoomMode){
			zoomMode = false;
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			removeMouseListener(mwz);
			removeMouseWheelListener(mwz);
			addMouseListener(this);
		}
	}
	public void setPanMode(boolean on){
		if(on && !panMode){
			panMode = true;
			
			//setCursor(new Cursor(Cursor.HAND_CURSOR));
			setCursor(cursorPanOpen);
			removeMouseListener(this);
			removeMouseMotionListener(this);
			
			addMouseListener(hsl);
			addMouseMotionListener(hsl);
			
		} else if(!on && panMode){
			panMode = false;
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			removeMouseListener(hsl);
			removeMouseMotionListener(hsl);
			addMouseListener(this);
			addMouseMotionListener(this);
		}
	}
	public void init(){
		this.setOpaque(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		//addMouseWheelListener(this);
		addKeyListener(this);
		setFocusable(true);
		requestFocusInWindow();
		ked = new MyDispatcher();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		BufferedImage ic_pan_closed = null, ic_pan_open = null, ic_zoom = null;
		
		try {
			URL ipcURL = AffinityDiagramCreator.class.getResource("ic_mousepanclosed.png");
			if(ipcURL!=null){
				ic_pan_closed = ImageIO.read(ipcURL);
				cursorPanClosed = toolkit.createCustomCursor(ic_pan_closed , new Point(0,0), "pan closed");
			} else {
				cursorPanClosed = new Cursor(Cursor.HAND_CURSOR);
			}
			URL ipo = AffinityDiagramCreator.class.getResource("ic_mousepanopen.png");
			if(ipo!=null){
				ic_pan_open = ImageIO.read(ipo);
				cursorPanOpen = toolkit.createCustomCursor(ic_pan_open , new Point(0,0), "pan open");
			} else {
				cursorPanOpen = new Cursor(Cursor.HAND_CURSOR);
			}
			URL iz = AffinityDiagramCreator.class.getResource("ic_zoom.png");
			if(iz!=null){
				ic_zoom = ImageIO.read(iz);
				cursorZoom = toolkit.createCustomCursor(ic_zoom , new Point(0,0), "zoom");
			} else {
				cursorZoom = new Cursor(Cursor.CROSSHAIR_CURSOR);
			}	
			
		} catch (IOException e) {
			cursorPanClosed = new Cursor(Cursor.HAND_CURSOR);
			cursorPanOpen = new Cursor(Cursor.HAND_CURSOR);
			cursorZoom = new Cursor(Cursor.CROSSHAIR_CURSOR);
			e.printStackTrace();
		}
			
		
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ked);
		Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		System.out.println("Focus: "+hasFocus()+", c:"+c);
		
	}

	public void addMovable(Cluster c){
		this.movables.add(c);
	}
	public Cluster[] getMovables(){
		return movables.toArray(new Cluster[movables.size()]);
	}
	public void add(JComponent c, Page p){
		int w = c.getWidth();
		int h = c.getHeight();
		//c.setBounds(0, 0, w, h);
		int dlx = (int)p.drawingLocation.x;
		int dly = (int)p.drawingLocation.y;
		
		int pageOnCanvasX = p.getBounds().x;
		int pageOnCanvasY = p.getBounds().y;
		
		int pageMargin = (int)((Float.valueOf(ADBPreferences.getPreference(ADBPreferences.PAGE_MARGIN)) * Settings.PIXEL2INCH));
		c.setBounds(dlx +pageMargin + pageOnCanvasX,  dly + pageMargin + pageOnCanvasY, w, h);
		int clusterOffset = (int)((Float.valueOf(ADBPreferences.getPreference(ADBPreferences.CLUSTER_OFFSET))*Settings.PIXEL2INCH));
		this.drawingLocation.y += h+ clusterOffset;
		p.drawingLocation.y += h +clusterOffset;
		
		super.add(c);
	}
	  public void paint(Graphics g) {
		 //  super.paintComponent(g); // clears background
		   Graphics2D g2 = (Graphics2D) g;
		   g2.setColor(Color.LIGHT_GRAY);
		   g2.fillRect(0, 0, getBounds().width, getBounds().height);
		   /*1) Backup current transform*/
		   AffineTransform backup = g2.getTransform();

		   /*2) Create a scale transform*/
		   scaleXform = new AffineTransform(model.getZoomFactor(),
		                                           0.0, 0.0,
		                                    model.getZoomFactor(),
		                                           0.0, 0.0);

		   /*3) Create the inverse of scale (used on mouse evt points)*/
		    try {
		      inverseXform = new AffineTransform();
		      inverseXform = scaleXform.createInverse();
		    } catch (Exception ex) {  }

		   /*4) Apply transformation*/
		   g2.transform(scaleXform);
		   
		   super.paint(g);

		   /*After drawing do*/
		   g2.setTransform(backup);
		   
		  }//end paint()
	public void zoomIn() {
		setZoomFactorIndex(model.getZoomFactorIndex()+1);
		
	}
	public void zoomOut() {
		setZoomFactorIndex(model.getZoomFactorIndex()-1);
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		Cluster m = null;
		for(int i = 0; i < this.movables.size(); i++){
			if(this.movables.get(i).isSelected()) m = this.movables.get(i);	
		}
		if(m!=null){
			Point p = new Point(e.getX() - m.mouseX, e.getY() - m.mouseY);
			inverseXform.transform(p, p);
					
			m.setBounds((int)p.x, (int)p.y, m.getBounds().width, m.getBounds().height);
			m.startX = (int)(m.getBounds().x);
			m.startY = (int)(m.getBounds().y);
			repaint();
		}
		repaint();
	}
	@Override
	public void mousePressed(MouseEvent e) {
		//Check if you have hit a cluster
		int x = (int)(e.getX()/model.getZoomFactor());
		int y = (int)(e.getY()/model.getZoomFactor());
				
		for (int i = 0; i < movables.size(); i++) {
			Cluster c = movables.get(i);
			if(c.getBounds().contains(new Point(x,y))){
				c.setSelected(true);
				c.startX = (int)(c.getBounds().x * model.getZoomFactor());
				c.startY = (int)(c.getBounds().y * model.getZoomFactor());
				c.mouseX = e.getX() - c.startX;
				c.mouseY = e.getY() - c.startY;
				JViewport vport = (JViewport) getParent();
			    Point vp = vport.getViewPosition();
				System.out.println("ScrollPane: "+ vp +" Width: "+getPreferredSize().width);
				int v2X = (int)((vport.getWidth()/2));
			    
			    System.out.println("Vector: "+ v2X + ", e: "+e.getX()+ "Sum vp+v2: "+(vp.x+v2X)+ ", e_zoomed: "+e.getX()*(1/model.getZoomFactor()));
				Point press = e.getPoint();

			    System.out.println("X: " + press.x +" - Y: " + press.y);
			    
			    //Inverse the Point
			    inverseXform.transform(press, press);
			    System.out.println("Inverse X: " + press.x +" - Y: " + press.y);
			}
		}
		this.repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		for (int i = 0; i < movables.size(); i++) {
			movables.get(i).setSelected(false);
		}
		this.repaint();
	}
	public void setInitialSize(Dimension dimension) {
		this.initialDimension = dimension;
		 setPreferredSize(dimension);
	}
	public void panUp(int pixel){
		JViewport vport = (JViewport) getParent();
	      JComponent label = (JComponent)vport.getView();
	      Point vp = vport.getViewPosition();
	      vp.translate(0, -pixel);
	      label.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
	}
	public void panDown(int pixel){
		JViewport vport = (JViewport) getParent();
	      JComponent label = (JComponent)vport.getView();
	      Point vp = vport.getViewPosition();
	      vp.translate(0, pixel);
	      label.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
	}
	public void panLeft(int pixel){
		JViewport vport = (JViewport) getParent();
	      JComponent label = (JComponent)vport.getView();
	      Point vp = vport.getViewPosition();
	      vp.translate(-pixel, 0);
	      label.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
	}
	public void panRight(int pixel){
		JViewport vport = (JViewport) getParent();
	      JComponent label = (JComponent)vport.getView();
	      Point vp = vport.getViewPosition();
	      vp.translate(pixel, 0);
	      label.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
	}
	private class MouseWheelZoomer extends MouseAdapter {
		@Override
        public void mouseWheelMoved(MouseWheelEvent e) {
			zoomPoint = e.getPoint();
            double delta = e.getPreciseWheelRotation();
            if(delta < 0) setZoomFactorIndex(model.getZoomFactorIndex()+1);
            else setZoomFactorIndex(model.getZoomFactorIndex()-1);
            zoomPoint = null;
        }
		@Override
		public void mousePressed(MouseEvent e){
			zoomIn();
		}
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case 32://Space
			setPanMode(true);
			break;
		case 38://Up
			if(zoomMode)zoomIn();
			else panUp(100);
			break;
			
		case 37://Left
			panLeft(100);
			break;
		case 40://Down
			if(zoomMode)zoomOut();
			else panDown(100);
			break;
		case 39://Right
			panRight(100);
			break;
		case 18://Alt
			setZoomMode(true);
			break;
		default:
			
		}
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()){
		case 32://Space
			setPanMode(false);
			break;
		case 18://Alt
			setZoomMode(false);
			break;
		
		default:
			System.out.println("Key Released - keyCode: "+ e.getKeyCode());
		}
		
	}
	@Override
	public void update(Observable o, Object arg) {
		
		adjustZoom();
	}
	private void adjustZoom(){
		int newWidth = (int)(this.initialDimension.width * model.getZoomFactor());
		int newHeight = (int)(this.initialDimension.height * model.getZoomFactor());
		this.setPreferredSize(new Dimension(newWidth, newHeight));
		JViewport vport = (JViewport) getParent();
	    JComponent label = (JComponent)vport.getView();
	    
	    Point vp = vport.getViewPosition();
	    int v1X = (int)((vport.getWidth()/2)); 
	    int v1Y = (int)((vport.getHeight()/2));
	    if(zoomPoint != null){
	    	v1X = zoomPoint.x - vp.x;
	    	v1Y = zoomPoint.y - vp.y;
	    }
	    int newX = (int)((vp.x + v1X) * (1/oldZoomFactor)*model.getZoomFactor())-v1X ;
	    int newY = (int)((vp.y + v1Y) * (1/oldZoomFactor)*model.getZoomFactor())-v1Y ;
	    oldZoomFactor = model.getZoomFactor();
	    vp.x = newX;
	    vp.y = newY;
	    label.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
		revalidate();
		repaint();
	}
	private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                keyPressed(e);
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                keyReleased(e);
            } else if (e.getID() == KeyEvent.KEY_TYPED) {
            	keyTyped(e);
            }
            return false;
        }
    }
}
