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
		JPanel contentPanel = new JPanel();
		//JPanel controlBar = new JPanel();
//		this.addWindowListener(new WindowAdapter() {
//		       public void windowClosing(WindowEvent e) {
//		    	   GUI.openWindows--;
//		    	   if(GUI.openWindows == 0)
//		    		   System.exit(0);
//		    	   }
//		    });
		setFocusable(false);
		//controlBar.setFocusable(false);
		contentPanel.setFocusable(false);
		contentPanel.setOpaque(true);
		contentPanel.setBackground(Color.red);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		//controlBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		getContentPane().setLayout(new BorderLayout());
		//JCheckBox showAtStartupChkBox = new JCheckBox();
		//showAtStartupChkBox.setSelected(Boolean.valueOf(ADBPreferences.getPreference(ADBPreferences.SHOW_TUTORIAL)));
		//showAtStartupChkBox.setText(ADBPreferences.SHOW_TUTORIAL);
		//controlBar.add(showAtStartupChkBox);
		contentPanel.setBackground(Color.WHITE);
		JEditorPane text = createEditorPane();
		text.setMargin(new Insets(5, 5, 5, 5));
		contentPanel.add(text);
		contentPanel.setPreferredSize(new Dimension(800, 3000));
		
		text.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		JScrollPane pane = new JScrollPane(contentPanel);
		add(pane, BorderLayout.CENTER);
		//add(controlBar, BorderLayout.SOUTH);
		
	 /*   
	    showAtStartupChkBox.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				JCheckBox cb = ((JCheckBox)e.getSource());
				prefs.saveValue(ADBPreferences.SHOW_TUTORIAL, cb.isSelected()+"");
				
			}
		});
	    
		*/
		pack();
		validate();
		//GUI.openWindows++;
		setVisible(true);
		
	}
	 private JEditorPane createEditorPane() {
	        JEditorPane editorPane = new JEditorPane();
	        editorPane.setEditable(false);
	        java.net.URL helpURL = AffinityDiagramCreator.class.getResource(
	                                        filename);
	        if (helpURL != null) {
	            try {
	                editorPane.setPage(helpURL);
	            } catch (IOException e) {
	                System.err.println("Attempted to read a bad URL: " + helpURL);
	            }
	        } else {
	            System.err.println("Couldn't find file: "+filename);
	        }
	 
	        return editorPane;
	    }

}
