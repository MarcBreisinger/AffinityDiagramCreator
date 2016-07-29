package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI extends JFrame implements ActionListener, AncestorListener, KeyListener, ComponentListener{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4291919895190226540L;
	String path = "";
	Canvas canvas;
	Level1[] l1;
	JScrollPane pane;
	ADBPreferences prefs;
	String applicationTitle = "Affinity Diagram Creator";
	Model model = new Model();
	private Controls controls;
	private String workingDirectory = ".";
	
	
	public GUI(){
		
		URL url = AffinityDiagramCreator.class.getResource("icon.gif");
		Image image = null;
		try {
			image = ImageIO.read(url);
			setIconImage(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		addComponentListener(this);
		prefs = ADBPreferences.getPreferenes();
		
		setTitle(applicationTitle);

		int w = Integer.valueOf(ADBPreferences.getPreference(ADBPreferences.WINDOW_WIDTH));
		int h = Integer.valueOf(ADBPreferences.getPreference(ADBPreferences.WINDOW_HEIGHT));
		setPreferredSize(new Dimension(w,h));
		UIManager.put("swing.boldMetal", Boolean.TRUE);
		
		this.addKeyListener(this);
		this.addWindowListener(new WindowAdapter() {
		       public void windowClosing(WindowEvent e) {
		    		   System.exit(0);
		    	   }
		    });
		
	    JMenuBar menuBar = new JMenuBar();
	    JMenu fileMenu = new JMenu("File"); 
	    JMenu viewMenu = new JMenu("View");
	    JMenu helpMenu = new JMenu("Help");
	    JMenuItem openMenuItem = new JMenuItem("Open");
	    JMenuItem saveMenuItem = new JMenuItem("Save");
	    JMenuItem importMenuItem = new JMenuItem("Import .csv");
	    JMenuItem exportPngMenuItem = new JMenuItem("Export PNG");
	    JMenuItem exportGifMenuItem = new JMenuItem("Export GIF");
	    JMenuItem zoomInMenuItem = new JMenuItem("Zoom in");
	    JMenuItem preferencesMenuItem = new JMenuItem("Preferences");
	    JMenuItem zoomOutMenuItem = new JMenuItem("Zoom out");
	    JMenuItem tutorialMenuItem = new JMenuItem("Tutorial");
	    JMenuItem aboutMenuItem = new JMenuItem("About");
	    fileMenu.add(openMenuItem);
	    fileMenu.add(saveMenuItem);
	    fileMenu.add(exportPngMenuItem);
	    fileMenu.add(exportGifMenuItem);
	    fileMenu.addSeparator();
	    fileMenu.add(preferencesMenuItem);
	    viewMenu.add(zoomInMenuItem);
	    viewMenu.add(zoomOutMenuItem);
	    helpMenu.add(aboutMenuItem);
	    helpMenu.add(tutorialMenuItem);
	    menuBar.add(fileMenu);
	    menuBar.add(viewMenu);
	    menuBar.add(helpMenu);
	    
	    zoomInMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.zoomIn();
				
			}
		});
	    zoomOutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { canvas.zoomOut();}});
	    openMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();}}
	    );
	    saveMenuItem.addActionListener(new ActionListener() {  	
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser c = new JFileChooser(workingDirectory);
	
				String filename = null;
			      int rVal = c.showSaveDialog(null);
			      if (rVal == JFileChooser.APPROVE_OPTION) {
			        filename = (c.getSelectedFile().getName());
			        if(filename.contains(".")){
			        	filename = filename.substring(0, filename.lastIndexOf('.'));
			        }
			        workingDirectory = c.getCurrentDirectory().toString();
			      } else 
			    	  return;
			      String str = "// This file stores the locations of an AffinityDiagram. It needs the .csv file it was created with and does not store its content\n";
			      str +="Content File:"+path+"\n";
			      System.out.println("Filename: "+filename+", Dir: "+workingDirectory);
			      Object[] o = canvas.getMovables();
			      for (int i = 0; i < o.length; i++) {
			    	  Cluster cl = (Cluster)o[i];
					int x = cl.getBounds().x;
					int y = cl.getBounds().y;
					String title = cl.getTitle();
					str+=title+","+x+","+y+"\n";
				}
			      BufferedWriter writer = null;
			      try
			      {
			          writer = new BufferedWriter( new FileWriter( workingDirectory+"/"+filename+"."+Settings.LAYOUT_FILE_EXTENSION));
			          writer.write( str);
	
			      }
			      catch ( IOException ioe)
			      {
			    	  System.err.println(ioe);
			      }
			      finally
			      {
			          try
			          {
			              if ( writer != null)
			              writer.close( );
			          }
			          catch ( IOException ioe)
			          {
			        	  System.err.println(ioe);
			          }
			      }
				}
			}
	    );
	    importMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				importCsvFile();
				
			}
		});
	    preferencesMenuItem.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {openPreferences();}});
	    tutorialMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Tutorial();
				
			}
		});
	    aboutMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage icon = null;
				try {
					icon = ImageIO.read(AffinityDiagramCreator.class.getResource("icon@0,3x.gif"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ImageIcon ic = new ImageIcon(icon);
				//custom title, custom icon
				JOptionPane.showMessageDialog(null,
				    "Autor: marc.breisinger@bmw.de ",
				    "About Affinity Diagram Creator",
				    JOptionPane.INFORMATION_MESSAGE,
				    (Icon)ic);
				
			}
		});
	    exportGifMenuItem.addActionListener(this);
	    exportPngMenuItem.addActionListener(this);
	    setJMenuBar(menuBar);
	    
	    
	    controls = new Controls(this, model);
	    JButton open = new JButton("Open File (csv or "+Settings.LAYOUT_FILE_EXTENSION+")");
	    JButton tutorial = new JButton("How does this work?");
	    open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
	    tutorial.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Tutorial();
			}
		});
	    JPanel buttons = new JPanel();
	    
	    buttons.setLayout(new FlowLayout());
	    buttons.add(open);
	    buttons.add(tutorial);
	    getContentPane().add(buttons);
	    pack();
	    setVisible(true);
//	    if(Boolean.valueOf(ADBPreferences.getPreference(ADBPreferences.SHOW_TUTORIAL))){
//	    	new Tutorial();
//	    	
//	    }
	}
	
	public void importCsv(ReadFile rf){
		setVisible(false);
		getContentPane().removeAll();
		int canvasMargin = (int)((Float.valueOf(ADBPreferences.getPreference(ADBPreferences.CANVAS_MARGIN))*Settings.PIXEL2INCH));
		Point2D.Double drawingLocation = new Point2D.Double(canvasMargin,canvasMargin);
		String[] data = null;
		try {
			data = rf.openFile(true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Object[] options = {
					"Wait for an update of "+Settings.APPLICATION_NAME + " which will assist with this problem",
					"Update the path in the file via text editor."
					//"Choose .csv file for this layout",
                    //"Open different file (."+Settings.LAYOUT_FILE_EXTENSION+" or ."+Settings.CSV_FILE_EXTENSION+")"
                    };
			JOptionPane.showOptionDialog(this,
		    "The file \n"+ rf.path + "\ncould not be found. What would you like to do?",
		    "File not found!",
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.ERROR_MESSAGE,
		    null,
		    options,
		    options[0]);
			openFile();

		} catch (IOException e){
			e.printStackTrace();
		}
	    if(path != null){
			 //count the required pages
		    int numLevel1 = 0;
		    for (int i=0 ;i<data.length;i++) {
		    	if(!data[i].startsWith(",")){
		    		numLevel1++;
		    	}
		    }
		    l1 = new Level1[numLevel1];
		    //create the Level 1 data structure
		    int l1_count = 0;
		    for(int i = 0; i< data.length;i++){
		    	if(!data[i].startsWith(",")){
		    		l1[l1_count++] = new Level1(data, i);
		    	}
		    }
		    canvas = new Canvas(model);
	    
		    for(int g = 0; g < l1.length; g++){
		    	float pw = Float.valueOf(ADBPreferences.getPreference(ADBPreferences.PAGE_WIDTH_INCH));
		    	float ph = Float.valueOf(ADBPreferences.getPreference(ADBPreferences.PAGE_HEIGHT_INCH));
		    	Page page = new Page(pw, ph, canvas, model);
		    	Color col = Color.decode(ADBPreferences.getPreference(ADBPreferences.COLOR_LEVEL_1));
		    	if(l1[g].title.equals(""))
		    		continue;
		    	PostIt pit = new PostIt(col, l1[g].title, false);
		    	
		    	canvas.preparePage(page);
		    	Cluster c = new Cluster(pit);
		    	canvas.add(c, page);
		    	canvas.addMovable(c);
		    	page.addMovable(c);
		    	Level2[] l2 = l1[g].children;
		    	for (int m = 0; m < l2.length; m++){
		    		col = Color.decode(ADBPreferences.getPreference(ADBPreferences.COLOR_LEVEL_2));
		    		if(l2[m].title.equals(""))
		    			continue;
		    		pit = new PostIt(col, l2[m].title, false);
		    		c = new Cluster(pit);
		    		canvas.addMovable(c);
		    		//page.addMovable(c);
		    		Level3[] l3 = l2[m].children;
		    		for(int b = 0; b < l3.length; b++){
		    			ExpressionColumn ec = new ExpressionColumn();
		    			col = Color.decode(ADBPreferences.getPreference(ADBPreferences.COLOR_LEVEL_3));
		    			if(l3[b].title.equals(""))
		    				continue;
		    			pit = new PostIt(col, l3[b].title, false);
		    			ec.add(pit);
		    			
		    			Level4[] l4 = l3[b].children;
		    			for(int y = 0; y < l4.length; y++){
		    				col = Color.decode(ADBPreferences.getPreference(ADBPreferences.COLOR_LEVEL_4));
		    				if(l4[y].title.equals(""))
		    					continue;
		    				pit = new PostIt(col, l4[y].title, false);
		    				ec.add(pit);
		    				if(y ==l4.length-1 && b==l3.length-1 && m == l2.length-1 && g==l1.length-1){
		    					System.out.println("Last Postit: "+pit.text);
		    					pit.addAncestorListener(this);
		    				}
		    			}
		    			c.add(ec);
		    		}
		    		canvas.add(c, page);
		    	}
		    	int w = page.getLayoutWidth();
		    	drawingLocation.x +=  w + canvasMargin;
		    	
		    	if(page.getLayoutHeight() + canvasMargin > drawingLocation.y)
		    		drawingLocation.y = page.getLayoutHeight() + canvasMargin;
		    	canvas.add(page);
		    }
		    canvas.setInitialSize(new Dimension((int)drawingLocation.x, (int)drawingLocation.y));
		   
		    
		    pane = new JScrollPane(canvas);
		    pane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		    add(pane, BorderLayout.CENTER);
		    getContentPane().add(controls, BorderLayout.SOUTH);
		    pane.setEnabled(false);
		    pack();
		    validate();
		    setVisible(true);
		    //GUI.openWindows++;
	    }
	    
	}
	public void openFile(){
		JFileChooser fj = new JFileChooser(workingDirectory);
		fj.setFileFilter(new FileNameExtensionFilter("Affinity Diagram Builder layout files", Settings.LAYOUT_FILE_EXTENSION, Settings.PREV_LAYOUT_FILE_EXTENSION, Settings.CSV_FILE_EXTENSION));
		String filename = null;
	      int rVal = fj.showOpenDialog(null);
	      if (rVal == JFileChooser.APPROVE_OPTION) {
	        filename = (fj.getSelectedFile().getName());
	        workingDirectory = fj.getCurrentDirectory().toString();
	      } else 
	    	  return;
	      fj = null;
	      if(filename.endsWith(Settings.LAYOUT_FILE_EXTENSION) || filename.endsWith(Settings.PREV_LAYOUT_FILE_EXTENSION)){
	    	  System.out.println("Opening layout file: "+filename+", Dir: "+workingDirectory);
		      ReadFile rf = new ReadFile(workingDirectory+"/"+filename);
		      String[] text = null;
			try {
				text = rf.openFile(true);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			if(text != null){
			      path = text[1].split(":")[1];
			      rf = new ReadFile(path);
			      importCsv(rf);
			      
			      Object[] o = canvas.getMovables();
			      for (int i = 0; i < o.length; i++) {
			    	  Cluster cl = (Cluster)o[i];
			    	  String title = cl.getTitle();
			    	  for (int j = 2; j < text.length; j++) {
						int lastComma = text[j].lastIndexOf(",");
						int secToLastComma = text[j].substring(0, lastComma).lastIndexOf(",");
						
						if(text[j].substring(0, secToLastComma).trim().equals(title)){
							//System.out.println("Parsing1 "+ text[j].substring(lastComma+1));
							int y = Integer.valueOf(text[j].substring(lastComma+1));
							//System.out.println("Parsing2 "+ text[j].substring(secToLastComma+1, lastComma));
							int x = Integer.valueOf(text[j].substring(secToLastComma+1, lastComma));
							System.out.println("Title: "+title+", orig.x:"+cl.getBounds().x+", orig.y:"+cl.getBounds().y+"\n"
							+"Found title: "+text[j].substring(0, secToLastComma)+" - x:"+x+" y:"+y);
							cl.setBounds(x, y, cl.getBounds().width, cl.getBounds().height);
						}
					}
				}
			    canvas.repaint();
			}
	      } else {
	  	     System.out.println("Opening CSV file: "+filename+", Dir: "+workingDirectory);
	  	     path = workingDirectory+"/"+filename;
	  	      ReadFile rf = new ReadFile(path);
	  	      importCsv(rf);
	      }
	}
	public void importCsvFile(){
		JFileChooser fj = new JFileChooser(".");
		String filename = null, dir = null;
	      int rVal = fj.showOpenDialog(null);
	      if (rVal == JFileChooser.APPROVE_OPTION) {
	        filename = (fj.getSelectedFile().getName());
	        dir = fj.getCurrentDirectory().toString();
	      } else 
	    	  return;
	      System.out.println("Filename: "+filename+", Dir: "+dir);
	      ReadFile rf = new ReadFile(dir+"/"+filename);
	      importCsv(rf);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//export image
		JFileChooser fj = new JFileChooser(".");
		String filename = null, dir = null;
	      int rVal = fj.showSaveDialog(null);
	      if (rVal == JFileChooser.APPROVE_OPTION) {
	        filename = (fj.getSelectedFile().getName());
	        dir = fj.getCurrentDirectory().toString();
	      } else 
	    	  return;
	      System.out.println("Filename: "+filename+", Dir: "+dir);
		if(((JMenuItem)e.getSource()).getText().endsWith("GIF")){
			(new PrintUIWindow(canvas, model, controls, dir, filename, "gif")).saveImage();	
		} else if(((JMenuItem)e.getSource()).getText().endsWith("PNG")){
			(new PrintUIWindow(canvas, model, controls, dir, filename, "png")).saveImage();	
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {

	}
	@Override
	public void keyPressed(KeyEvent e) {
		
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	@Override
	public void ancestorAdded(AncestorEvent event) {
		System.out.println("Added: "+event);
		event.getComponent().removeAncestorListener(this);
		JScrollBar vbar = pane.getVerticalScrollBar();
		JScrollBar hbar = pane.getHorizontalScrollBar();
		vbar.setValue(0);
		hbar.setValue(0);
		int cvw = canvas.getWidth() ;
		int vPortWidth = pane.getViewport().getWidth();
		int cvh = canvas.getHeight();
		int vPortHeight = pane.getViewport().getHeight();
	
		while(cvw * model.getZoomFactor() > vPortWidth  || cvh * model.getZoomFactor() > vPortHeight){
			model.setZoomFactorIndex(model.getZoomFactorIndex()-1);
		}
		
	}
	@Override
	public void ancestorRemoved(AncestorEvent event) {
		System.out.println("Removed: "+event);
		
	}
	@Override
	public void ancestorMoved(AncestorEvent event) {
		//System.out.println("Moved: "+event);
		
	}
	@Override
	public void componentResized(ComponentEvent e) {
		prefs.saveValue(ADBPreferences.WINDOW_WIDTH, ""+((Component)e.getSource()).getWidth());
		prefs.saveValue(ADBPreferences.WINDOW_HEIGHT, ""+((Component)e.getSource()).getHeight());
		
	}
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentHidden(ComponentEvent e) {}
	
	private void openPreferences(){
		new ADBPreferencesDialog(this, "Preferences", true, prefs);
	}
}
