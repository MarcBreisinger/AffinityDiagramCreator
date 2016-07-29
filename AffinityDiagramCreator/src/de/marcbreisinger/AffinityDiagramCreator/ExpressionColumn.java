package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JComponent;

public class ExpressionColumn extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4202437799606712439L;

	private Point2D.Double drawingLocation = new Point2D.Double(0,0);
	

	private Vector<PostIt> postIts = new Vector<>();
	
	public ExpressionColumn(){
		setLayout(null);
		init();
	}

	public void init(){
		this.setOpaque(true);
		this.setBackground(Color.YELLOW);
		this.setForeground(Color.RED);
	}
	public Component add(PostIt c){
		
		int w = c.getWidth();
		int h = c.getHeight();
		c.setBounds((int)drawingLocation.x, (int)drawingLocation.y, w, h);
		int pitOffset = (int)((Float.valueOf(ADBPreferences.getPreference(ADBPreferences.POSTIT_OFFSET))*Settings.PIXEL2INCH));
		this.drawingLocation.y += h + pitOffset;
		this.setSize(new Dimension(w, (int)this.drawingLocation.y));
		postIts.add(c);
		return super.add(c);
	}

	public ExpressionColumn copy(boolean export) {
		ExpressionColumn ec = new ExpressionColumn();
		for (PostIt p : postIts) {
			ec.add(p.copy(export));
		}
		return ec;
	}

}
