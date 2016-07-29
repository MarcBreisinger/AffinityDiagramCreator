package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JPanel;

public class Cluster extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4803559426276552734L;

	private Point2D.Double drawingLocation = new Point2D.Double(0,0);
	
	private int maxheight = 0;
	private int labelheight;
	protected int startX;
	protected int startY;
	protected int mouseX, mouseY;
	private boolean selected = false;
	private PostIt label;
	Vector<ExpressionColumn> columns = new Vector<>();
	public void setSelected(boolean b){
		selected = b;
	}
	public boolean isSelected(){
		return selected;
	}
	
	
	public Cluster(PostIt label){
		setLayout(null);
		this.label = label;
		this.setOpaque(false);
		int w = label.getWidth();
		int h = label.getHeight();
		int pitOffset = (int)((Float.valueOf(ADBPreferences.getPreference(ADBPreferences.POSTIT_OFFSET))*Settings.PIXEL2INCH));
		labelheight = h + pitOffset;
		label.setBounds((int)drawingLocation.x, (int)drawingLocation.y, w, h);
		this.drawingLocation.y += labelheight;
		this.setSize(new Dimension(w +pitOffset, labelheight));
		this.add(label);
	}
	public Cluster copy(boolean export){
		Cluster c = new Cluster(label.copy(export));
		for (ExpressionColumn column : columns) {
			c.add(column.copy(export));
		}
		return c;
	}
	public String getTitle(){
		return label.text;
	}
	public Component add(ExpressionColumn c){
		
		int w = c.getWidth();
		int h = c.getHeight();
		if (this.maxheight < h){
			this.maxheight = h;
		}
		c.setBounds((int)drawingLocation.x, (int)drawingLocation.y, w, h);
		int pitOffset = (int)((Float.valueOf(ADBPreferences.getPreference(ADBPreferences.POSTIT_OFFSET))*Settings.PIXEL2INCH));
		this.drawingLocation.x += w + pitOffset;
		this.setSize(new Dimension((int)this.drawingLocation.x, labelheight + this.maxheight));
		columns.add(c);
		return super.add(c);
	}
}
