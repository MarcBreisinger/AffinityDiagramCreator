package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

public class PrintUIWindow implements Printable, ActionListener {

	private Canvas c;
	private Controls controls; 
    private Page[] framesToPrint;
	private String extension;
	private String dir;
	private String filename;
	private Model model;

    public int print(Graphics g, PageFormat pf, int page) throws
                                                        PrinterException {

        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }
        /*
        PrinterJob job = PrinterJob.getPrinterJob();
        Paper p = new Paper();
        p.setSize(36 * 72,  76 * 72);
        p.setImageableArea(0.0 * 72, 0.0 * 72, 36 * 72, 76 * 72);
        pf.setPaper(p);
        job.setPrintable(this, pf);
        */
        //DocFlavor df = DocFlavor.;
        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        /* Now print the window and its visible contents */
        framesToPrint[0].print(g2d);

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    public void actionPerformed(ActionEvent e) {
    	saveImage();
    }
    public void saveImage() {
    	SaveTask st = new SaveTask();
    	st.addPropertyChangeListener(controls);
    	st.execute();
	}
    @SuppressWarnings("unused")
	private BufferedImage cropImage(BufferedImage src, Rectangle rect) {
    	BufferedImage img = src.getSubimage(rect.x, rect.y, rect.width, rect.height); //fill in the corners of the desired crop location here
    	BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
    	Graphics g = copyOfImage.createGraphics();
    	g.drawImage(img, 0, 0, null);
    	return copyOfImage; //or use it however you want
     }
	public void printPage() {
        PrinterJob job = PrinterJob.getPrinterJob();
        //PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        //PageFormat pf = job.pageDialog(aset);
        PageFormat pf = job.defaultPage();
        Paper p = new Paper();
        p.setSize(8.5 * 72,  11 * 72);
        p.setImageableArea(0.0 * 72, 0.0 * 72, 8.5 * 72, 11 * 72);
        pf.setPaper(p);
        job.setPrintable(this, pf );
       
        boolean ok = job.printDialog();
        if (ok) {

            try {
            	//job.setPrintable(this, pf);
                 job.print();
            } catch (PrinterException ex) {
                System.out.println(ex.getMessage());
            }
       }

    }

    public PrintUIWindow(Canvas c, Model m, Controls controls, String dir, String filename, String extension) {
    	this.c = c;
    	this.controls = controls;
        framesToPrint = c.getPages();
        this.extension = extension;
        this.dir = dir;
        this.filename = filename;
        this.model = m;
    }
    private class SaveTask extends SwingWorker<ExportStatus, ExportStatus>{

	    @Override
	    protected ExportStatus doInBackground() {
	    	ExportStatus es = null;
	    	for(int i = 0; i < framesToPrint.length; i++){
	    		es = new ExportStatus(i, framesToPrint.length, dir, filename);
	    		publish(es);
		    	Page f = framesToPrint[i];
		    	Rectangle r = f.getBounds();
		    	float pw = Float.valueOf(ADBPreferences.getPreference(ADBPreferences.PAGE_WIDTH_INCH));
		    	float ph = Float.valueOf(ADBPreferences.getPreference(ADBPreferences.PAGE_HEIGHT_INCH));
		    	Page page = new Page(pw, ph, c, model);
		    	page.setInitialBounds(f.getInitialBounds());
		    	page.setBounds(0,0, f.getInitialBounds().width, f.getInitialBounds().height);
		    	//place clusters
		    	Cluster[] cluster = c.getMovables();
		    	for (int j = 0; j < cluster.length; j++) {
		    		page.addForExport(cluster[j]);
				}
		    	JFrame frame = new JFrame();
		    	frame.getContentPane().add(page);
		    	
		    	BufferedImage export = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
		    	page.paint(export.getGraphics());
		    	//BufferedImage export = cropImage(im,  r);
		    	try {
		    		String num = "";
		    		if(i<=9) num = "0";
		    		num += i;
					ImageIO.write(export, extension, new File(dir+"/"+filename+"_"+num+"."+extension));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	//setProgress(i);
	    	}
	    	return es;
	    }
	    @Override
        protected void process(List<ExportStatus> fs) {
            controls.updateStatus(fs);
        }
	    protected void done() {
	    	
			try {
				controls.exportFinished(get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
    public class ExportStatus{
    	int fileNumber;
    	int totalFiles;
    	String path;
    	String filename;
    	public ExportStatus(int fileNumber, int totalFiles, String path, String filename){
    		this.filename = filename;
    		this.fileNumber = fileNumber;
    		this.totalFiles = totalFiles;
    		this.path = path;
    	}
    }
}
