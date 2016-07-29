package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ADBPreferencesDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5015308049478009738L;
	private static String BUTTON_TEXT_SAVE = "Save";
	private static String BUTTON_TEXT_CANCEL = "Cancel";
	private ADBPreferences preferences;
	private String[] dialogKeys = { 
			ADBPreferences.PAGE_WIDTH_INCH, 
			ADBPreferences.PAGE_HEIGHT_INCH, 
			ADBPreferences.PAGE_COLOR, 
			ADBPreferences.PAGE_MARGIN,
			ADBPreferences.COLOR_LEVEL_1, 
			ADBPreferences.COLOR_LEVEL_2, 
			ADBPreferences.COLOR_LEVEL_3, 
			ADBPreferences.COLOR_LEVEL_4,
			ADBPreferences.POSTIT_SIDELENGTH_INCH,
			ADBPreferences.FONT_SIZE,
			ADBPreferences.CANVAS_MARGIN,
			ADBPreferences.POSTIT_MARGIN,
			ADBPreferences.POSTIT_OFFSET,
			ADBPreferences.CLUSTER_OFFSET,
			ADBPreferences.GRID_SIZE_INCH,
			ADBPreferences.TEXT_TOO_LONG_MARKER_COLOR
			};
	private Hashtable<String, Boolean> valid = new Hashtable<>();
	private Hashtable<String, JTextField> valueFields = new Hashtable<String, JTextField>();
	private Hashtable<String, String> prefs;

	public ADBPreferencesDialog(Frame owner, String title, boolean modal, ADBPreferences p){
		super(owner, title, modal);
		this.preferences = p;
		JPanel contentPanel = (JPanel) this.getContentPane();
		contentPanel.setLayout(new BorderLayout());
		JPanel table = new JPanel();
		GridLayout layout = new GridLayout(0,2);
		table.setLayout(layout);
		contentPanel.add(table, BorderLayout.NORTH);
		
		
		prefs = p.getDialogList();
		
		
		for (int i = 0; i < dialogKeys.length; i++) {
			
			String value = prefs.get(dialogKeys[i]);
			JTextField tfkey = new JTextField(dialogKeys[i]);
			JTextField tfvalue = new JTextField(value);
			tfkey.setEditable(false);
			table.add(tfkey);
			table.add(tfvalue);
			valueFields.put(dialogKeys[i], tfvalue);
		}
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		JButton save = new JButton(BUTTON_TEXT_SAVE);
		JButton cancel = new JButton(BUTTON_TEXT_CANCEL);
		buttonsPanel.add(save);
		buttonsPanel.add(cancel);
		cancel.requestFocus();
		contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Enumeration<Boolean> b = valid.elements();
				boolean ok = true;
				while (b.hasMoreElements()) {
					boolean boo = b.nextElement();
					ok = ok && boo;
				}
				if(!ok) {
					((JButton)e.getSource()).setBackground(Color.RED);
					return;
				}
				for (int i = 0; i < dialogKeys.length; i++) {
					JTextField tf = valueFields.get(dialogKeys[i]);
					prefs.put(dialogKeys[i], tf.getText());
					
				}
				preferences.saveValues();
				ADBPreferencesDialog.this.dispose();
			}
			
		});
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ADBPreferencesDialog.this.dispose();
			}
		});
		
		//checker
		valueFields.get(ADBPreferences.PAGE_WIDTH_INCH).getDocument().addDocumentListener(new FloatChecker(ADBPreferences.PAGE_WIDTH_INCH));
		valueFields.get(ADBPreferences.PAGE_HEIGHT_INCH).getDocument().addDocumentListener(new FloatChecker(ADBPreferences.PAGE_HEIGHT_INCH));
		valueFields.get(ADBPreferences.COLOR_LEVEL_1).getDocument().addDocumentListener(new HexColorChecker(ADBPreferences.COLOR_LEVEL_1));
		valueFields.get(ADBPreferences.COLOR_LEVEL_2).getDocument().addDocumentListener(new HexColorChecker(ADBPreferences.COLOR_LEVEL_2));
		valueFields.get(ADBPreferences.COLOR_LEVEL_3).getDocument().addDocumentListener(new HexColorChecker(ADBPreferences.COLOR_LEVEL_3));
		valueFields.get(ADBPreferences.COLOR_LEVEL_4).getDocument().addDocumentListener(new HexColorChecker(ADBPreferences.COLOR_LEVEL_4));
		valueFields.get(ADBPreferences.POSTIT_SIDELENGTH_INCH).getDocument().addDocumentListener(new FloatChecker(ADBPreferences.POSTIT_SIDELENGTH_INCH));
		valueFields.get(ADBPreferences.FONT_SIZE).getDocument().addDocumentListener(new IntegerChecker(ADBPreferences.FONT_SIZE));
		valueFields.get(ADBPreferences.CANVAS_MARGIN).getDocument().addDocumentListener(new FloatChecker(ADBPreferences.CANVAS_MARGIN));
		valueFields.get(ADBPreferences.PAGE_MARGIN).getDocument().addDocumentListener(new FloatChecker(ADBPreferences.PAGE_MARGIN));
		valueFields.get(ADBPreferences.POSTIT_MARGIN).getDocument().addDocumentListener(new FloatChecker(ADBPreferences.POSTIT_MARGIN));
		valueFields.get(ADBPreferences.POSTIT_OFFSET).getDocument().addDocumentListener(new FloatChecker(ADBPreferences.POSTIT_OFFSET));
		valueFields.get(ADBPreferences.CLUSTER_OFFSET).getDocument().addDocumentListener(new FloatChecker(ADBPreferences.CLUSTER_OFFSET));
		valueFields.get(ADBPreferences.GRID_SIZE_INCH).getDocument().addDocumentListener(new FloatChecker(ADBPreferences.GRID_SIZE_INCH));
		valueFields.get(ADBPreferences.PAGE_COLOR).getDocument().addDocumentListener(new HexColorChecker(ADBPreferences.PAGE_COLOR));
		valueFields.get(ADBPreferences.TEXT_TOO_LONG_MARKER_COLOR).getDocument().addDocumentListener(new HexColorChecker(ADBPreferences.TEXT_TOO_LONG_MARKER_COLOR));
		
		pack();
		setVisible(true);
	}
	private class HexColorChecker implements DocumentListener{

		private String key;
		private JTextField tf;
		public HexColorChecker(String key) {
			this.key = key;
			this.tf = valueFields.get(key);
			valid.put(key, true);
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			valid.put(key, isValid());
			if(!isValid()){
				tf.setBackground(Color.RED);
			} else 
				tf.setBackground(null);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			valid.put(key, isValid());
			if(!isValid()){
				tf.setBackground(Color.RED);
			} else 
				tf.setBackground(null);
			
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			valid.put(key, isValid());
			if(!isValid()){
				tf.setBackground(Color.RED);
			} else 
				tf.setBackground(null);
			
		}
		private boolean isValid(){
			try{
				Color.decode(tf.getText());
			} catch (NumberFormatException e){
				return false;
			}
			return true;
		}
		
	}
	private class FloatChecker implements DocumentListener{

	private JTextField tf;
	private String key;
		public FloatChecker(String key) {
			this.key = key;
			tf = valueFields.get(key);
			valid.put(key, true);
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			valid.put(key, isValid());
			if(!isValid()){
				tf.setBackground(Color.RED);
			} else 
				tf.setBackground(null);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			valid.put(key, isValid());
			if(!isValid()){
				tf.setBackground(Color.RED);
			} else 
				tf.setBackground(null);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			valid.put(key, isValid());
			if(!isValid()){
				tf.setBackground(Color.RED);
			} else 
				tf.setBackground(null);
		}
		private boolean isValid(){
			try{
				Float.parseFloat(tf.getText());
			} catch (NumberFormatException e){
				return false;
			}
			return true;
		}
		
	}
	private class IntegerChecker implements DocumentListener{

		private JTextField tf;
		private String key;
			public IntegerChecker(String key) {
				this.key = key;
				tf = valueFields.get(key);
				valid.put(key, true);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				valid.put(key, isValid());
				if(!isValid()){
					tf.setBackground(Color.RED);
				} else 
					tf.setBackground(null);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				valid.put(key, isValid());
				if(!isValid()){
					tf.setBackground(Color.RED);
				} else 
					tf.setBackground(null);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				valid.put(key, isValid());
				if(!isValid()){
					tf.setBackground(Color.RED);
				} else 
					tf.setBackground(null);
			}
			private boolean isValid(){
				try{
					Integer.parseInt(tf.getText());
				} catch (NumberFormatException e){
					return false;
				}
				return true;
			}
			
		}
}
