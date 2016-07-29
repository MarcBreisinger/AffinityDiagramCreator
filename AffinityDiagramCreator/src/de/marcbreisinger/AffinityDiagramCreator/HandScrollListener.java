package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JViewport;



public class HandScrollListener extends MouseAdapter {
    private final Point pp = new Point();
    @Override public void mouseDragged(MouseEvent e) {
      JViewport vport = (JViewport)((Canvas)e.getSource()).getParent();
      JComponent label = (JComponent)vport.getView();
      Point cp = e.getPoint();
      Point vp = vport.getViewPosition();
      vp.translate(pp.x-cp.x, pp.y-cp.y);
      label.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
      //vport.setViewPosition(vp);
      pp.setLocation(cp);
    }
    @Override public void mousePressed(MouseEvent e) {
    	((Canvas)e.getSource()).setCursor(((Canvas)e.getSource()).cursorPanClosed);
      pp.setLocation(e.getPoint());
    }
    @Override public void mouseReleased(MouseEvent e) {
    	((Canvas)e.getSource()).setCursor(((Canvas)e.getSource()).cursorPanOpen);
    }
  }