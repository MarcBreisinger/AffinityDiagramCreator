package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import de.marcbreisinger.AffinityDiagramCreator.PrintUIWindow.ExportStatus;

public class Controls extends JPanel implements Observer, PropertyChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1353371815864942866L;
	private JTextField tf_zoom;
	private JTextField tf_status;
	private Model model;
	private JProgressBar progressBar;
	JPanel left = new JPanel();
	JPanel center = new JPanel();
	JPanel right = new JPanel();
	
	public Controls(JFrame frame, Model m){
		
		
		model = m;
		model.addObserver(this);
		
		setLayout(new BorderLayout());
		//left.setBackground(Color.RED);
		//center.setBackground(Color.BLUE);
		//right.setBackground(Color.GREEN);
		
		left.setLayout(new FlowLayout(FlowLayout.LEFT));
		right.setLayout(new FlowLayout(FlowLayout.RIGHT));
		tf_zoom = new JTextField("100 %");
		tf_zoom.setEditable(false);
		tf_zoom.setOpaque(false);
		tf_zoom.setFocusable(false);
		tf_zoom.setRequestFocusEnabled(false);
		tf_zoom.setColumns(7);
		tf_zoom.setHorizontalAlignment(JTextField.CENTER);
		tf_zoom.setBorder(null);
		
		tf_status = new JTextField();
		tf_status.setBorder(null);
		//tf_status.setColumns(300);
		tf_status.setOpaque(false);
		
		
		
		
		JButton zoomIn = new JButton("+");
		JButton zoomOut = new JButton("-");
		zoomIn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setZoomFactorIndex(model.getZoomFactorIndex()+1);
				
			}
		});
		zoomOut.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setZoomFactorIndex(model.getZoomFactorIndex()-1);
				
			}
		});
		zoomOut.setFocusable(false);
		zoomIn.setFocusable(false);
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		left.add(tf_status);
		left.add(progressBar);
		
		right.add(zoomOut);
		right.add(tf_zoom);
		right.add(zoomIn);
		
		add(left, BorderLayout.CENTER);
		//add(center, BorderLayout.CENTER);
		add(right, BorderLayout.EAST);
	}
	@Override
	public void update(Observable o, Object arg) {
		tf_zoom.setText((((int)(model.getZoomFactor() * 10000))/100.0)+" %");
		
	}
	public void updateStatus(List<ExportStatus> fs) {
		tf_status.setText("Exporting: ");
		ExportStatus f = fs.get(fs.size()-1);
		if(f.fileNumber == 0){
			progressBar.setMaximum(f.totalFiles);
	        progressBar.setVisible(true);
	        progressBar.setValue(0);
		} else {
			progressBar.setValue(f.fileNumber);
		}
		 progressBar.setString(f.fileNumber+"/"+f.totalFiles);
		
	}
	public void exportFinished(ExportStatus exportStatus) {
		tf_status.setText("Export finished! "+ exportStatus.path);
		progressBar.setVisible(false);
	}
	public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
        	tf_status.setText("Pb: ");
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        } 
    }
	
}
