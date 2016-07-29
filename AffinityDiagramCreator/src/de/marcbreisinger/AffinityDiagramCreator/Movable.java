package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

public class Movable extends JComponent implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1154414107711897587L;
	protected boolean selected = false;
	protected int startX;
	protected int startY;
	protected int mouseX, mouseY;
	AffineTransform inverseXform;
	
	public boolean isSelected(){
		return selected;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		selected = true;
		
		startX = this.getBounds().x;
		startY = this.getBounds().y;
		mouseX = e.getX();
		mouseY = e.getY();
		System.out.println("Selected - startX:"+startX+", startY:"+startY+", mouseX:"+mouseX+", mouseY:"+mouseY+", "+this);
		Point press = e.getPoint();

	    System.out.println("X: " + press.x +" - Y: " + press.y);
	    
	    //Inverse the Point
	    inverseXform.transform(press, press);
		//this.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		selected = false;
		this.repaint();
		System.out.println("UN-Selected: "+this);
		
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
	public void mouseDragged(MouseEvent e) {
		System.out.println(e);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void paint(Graphics g){
		super.paintComponent(g);
		inverseXform = new AffineTransform();
		
		super.paint(g);
	}

}
