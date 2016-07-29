package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JViewport;

public class Page extends JPanel implements MouseListener, MouseMotionListener, Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 432326581941945601L;

	public Point2D.Double drawingLocation = new Point2D.Double(0,0);
	
	private Rectangle initialBounds;
	private Vector<Cluster> movables = new Vector<Cluster>();

	private Model model;
	private Color pageColor;
	private Color gridColor;

	private Color outlineColor;


	
	public Page(Model m){
		setLayout(null);
		initialBounds = new Rectangle(0,0, (int) (8.5 * Settings.PIXEL2INCH), (int) (11 * Settings.PIXEL2INCH));
		model = m;
		model.addObserver(this);
		init();
	}
	public Page(float widthInInch, float heightInInch, Canvas canvas, Model m){
		setLayout(null);
		initialBounds = new Rectangle(0,0, (int) (widthInInch *Settings.PIXEL2INCH), (int) (heightInInch *Settings.PIXEL2INCH));
		model = m;
		model.addObserver(this);
		init();
	}
	public void setInitialBounds(Rectangle r){
		initialBounds = r;
	}
	public Rectangle getInitialBounds(){
		return initialBounds;
	}

	public void addMovable(Cluster m){
		this.movables.add(m);
		//m.addMouseMotionListener(this);
	}
	public void init(){
		this.setOpaque(false);
		this.setSize(new Dimension(initialBounds.width, initialBounds.height));
		pageColor = Color.decode(ADBPreferences.getPreference(ADBPreferences.PAGE_COLOR));
		gridColor = Color.BLACK;
		outlineColor = Color.BLACK;
		//addMouseListener(this);
		//addMouseMotionListener(this);
	}
	public int getLayoutWidth(){
		return this.initialBounds.width;
	}
	public Component addCluster(Component c){
		int w = c.getWidth();
		int h = c.getHeight();
		int pageMargin = (int)((Float.valueOf(ADBPreferences.getPreference(ADBPreferences.PAGE_MARGIN)) * Settings.PIXEL2INCH));
		c.setBounds((int)drawingLocation.x + pageMargin, (int)drawingLocation.y + pageMargin, w, h);
		int clusterOffset = (int)((Float.valueOf(ADBPreferences.getPreference(ADBPreferences.CLUSTER_OFFSET))*Settings.PIXEL2INCH));
		this.drawingLocation.y += h+ clusterOffset;
		return super.add(c);
	}
	public void addForExport(Cluster c){
		int x = c.getBounds().x - initialBounds.x;
		int y = c.getBounds().y - initialBounds.y;

		Cluster copy = c.copy(true);
		copy.setBounds(x, y, c.getBounds().width, c.getBounds().height);
		add(copy);
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		System.out.println("Page - dragged! "+e.getPoint());
		
		Cluster m = null;
		for(int i = 0; i < this.movables.size(); i++){
			if(this.movables.get(i).isSelected()) m = this.movables.get(i);	
		}
		if(m!=null){
			Point p = new Point(e.getX() - m.mouseX, e.getY() - m.mouseY);
			p.x = (int) (p.x*model.getZoomFactor()); p.y = (int) (p.y*model.getZoomFactor());
			m.setBounds(p.x, p.y, m.getBounds().width, m.getBounds().height);
			m.startX = m.getBounds().x;
			m.startY = m.getBounds().y;
			repaint();
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	public void paint(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(pageColor);
		g2.fillRect(0, 0,  getBounds().width, getBounds().height);
		g2.setStroke(new BasicStroke(1.0f));
		g2.setColor(outlineColor);
		g2.drawRect(0, 0, getBounds().width-1,  getBounds().height -1);
		float grid = Float.valueOf(ADBPreferences.getPreference(ADBPreferences.GRID_SIZE_INCH));
		if(grid != 0){
			g2.setColor(gridColor);
			for (int i = 0; i < getBounds().width / (grid * Settings.PIXEL2INCH); i++) {
				g2.drawLine((int)(i * grid * Settings.PIXEL2INCH), 0, (int)(i * grid * Settings.PIXEL2INCH), getBounds().height);
			}
			for (int i = 0; i < getBounds().height / (grid * Settings.PIXEL2INCH); i++) {
				g2.drawLine(0, (int) (i * grid * Settings.PIXEL2INCH), getBounds().width, (int)(i * grid * Settings.PIXEL2INCH));
			}
		}
		super.paint(g);
		
	}
	public double getLayoutHeight() {
		
		return this.drawingLocation.y;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("Page - Mouse pressed! "+ e.getPoint());
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
				JViewport vport = (JViewport) getParent().getParent();
			    Point vp = vport.getViewPosition();
				System.out.println("ScrollPane: "+ vp +" Width: "+getPreferredSize().width);
				int v2X = (int)((vport.getWidth()/2));
			    
			    System.out.println("Vector: "+ v2X + ", e: "+e.getX()+ "Sum vp+v2: "+(vp.x+v2X)+ ", e_zoomed: "+e.getX()*(1/model.getZoomFactor()));
				Point press = e.getPoint();

			    System.out.println("X: " + press.x +" - Y: " + press.y);
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
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}
