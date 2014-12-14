package awtGUI;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lwjgl.BufferUtils;

public class BrushPanel extends JDialog {

	private static final long serialVersionUID = -7806098757885232440L;
	
	protected static ArrayList<Brush> brushes;
    public static Brush selectedBrush;
    static JSpinner sizeSpin;
	
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {
            if (name.endsWith(".png")) {
                return (true);
            }
            return (false);
        }
    };
	
	public BrushPanel() {
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		//this.setAlwaysOnTop(true);
		this.setTitle("Brush Panel");
		this.setSize(280, 400);
		this.setLocation(950,50);
		sizeSpin = null;
		
		// Create all the brushes
		BrushPanel.brushes = new ArrayList<Brush>();
		
		File dir = new File("../Brushes/");
		dir.mkdir();
		
		if (dir.isDirectory()) { // make sure it's a directory
			int numBrushes = dir.listFiles(IMAGE_FILTER).length;
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(250, 450));
			panel.setLayout(new GridLayout(numBrushes/3,3,5,5));
			ButtonGroup brushButtons = new ButtonGroup();
			
			int i = 0;
            for (final File f : dir.listFiles(IMAGE_FILTER)) {
            	BrushPanel.brushes.add(new Brush(i, f, panel, brushButtons, i==0));
				i++;
            }
            setSelected(0);
            
            JPanel superPanel = new JPanel();
            superPanel.setLayout(new BoxLayout(superPanel, BoxLayout.PAGE_AXIS));
            superPanel.add(panel);
            
            JPanel spinnerPanel = new JPanel();
            spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.LINE_AXIS));
            SpinnerModel spinnerModel = new SpinnerNumberModel(
            		PaintSceneApp.sliderInit,
            		PaintSceneApp.sliderMin,
            		PaintSceneApp.sliderMax,
            		1); //step
            sizeSpin = new JSpinner();
            sizeSpin.setModel(spinnerModel);
            sizeSpin.setPreferredSize(new Dimension(100, 30));
            ChangeListener changeListener = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					selectedBrush.setSize((int)sizeSpin.getValue());
					PaintMainGame.canvas.setToolSize(selectedBrush.getSize());					
				}
            };
            sizeSpin.addChangeListener(changeListener);
            JLabel spinLabel = new JLabel("Brush Size: ");
            spinLabel.setPreferredSize(new Dimension(100, 30));
            spinnerPanel.setAlignmentX(CENTER_ALIGNMENT);
            spinnerPanel.add(spinLabel);
            spinnerPanel.add(sizeSpin);
            superPanel.add(spinnerPanel);
            
            this.setContentPane(superPanel);
            this.pack();
    		this.setVisible(true);
		
		} else {
			System.out.println("Cannot find brushes directory");
		}
		
	}
	
	public static void setSelected(int id) {
		selectedBrush = brushes.get(id);
	}
	
	public static void setBrushSize(int r) {
		selectedBrush.setSize(r);
		sizeSpin.setValue(r);
	}
}
