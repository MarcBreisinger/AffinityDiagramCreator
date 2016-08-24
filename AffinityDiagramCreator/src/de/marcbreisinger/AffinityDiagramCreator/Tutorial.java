package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Tutorial extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4773167831085148944L;
	private ADBPreferences prefs;
	private String filename = "/index.html";

	public Tutorial(){
		
		prefs = ADBPreferences.getPreferenes();
		
		JEditorPane text = createEditorPane();
		text.setMargin(new Insets(5, 5, 5, 5));
		
		text.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		JScrollPane pane = new JScrollPane(text);
		getContentPane().add(pane);
		setPreferredSize(new Dimension(1440, 1024));
		//show();
		pack();
		validate();
		//GUI.openWindows++;
		setVisible(true);
		
	}
	 private JEditorPane createEditorPane() {
	        JEditorPane editorPane = new JEditorPane();
	        editorPane.setEditable(false);
	        editorPane.setContentType("text/html");
	        java.net.URL helpURL = AffinityDiagramCreator.class.getResource(
	                                        filename);
	        if (helpURL != null) {
	            try {
	                editorPane.setPage(helpURL);
	            } catch (IOException e) {
	            	
	            	editorPane.setText("<html>Could not load "+helpURL);
	            }
	        } else {
	    
            	editorPane.setText("<html>Could not find "+helpURL);
	        }
	 
	        return editorPane;
	    }

}
