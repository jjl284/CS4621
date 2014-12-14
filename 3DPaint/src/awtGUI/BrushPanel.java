package awtGUI;

import java.awt.GridLayout;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BrushPanel extends JDialog {

	private static final long serialVersionUID = -7806098757885232440L;
	
	protected static ArrayList<Brush> brushes;
    public static Brush selectedBrush;
	
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
		this.setAlwaysOnTop(true);
		this.setFocusable(true);
		this.setTitle("Brush Panel");
		this.setSize(300, 400);
		this.setLocation(100,100);
		
		// Create all the brushes
		BrushPanel.brushes = new ArrayList<Brush>();
		
		File dir = new File("../Brushes/");
		dir.mkdir();
		
		if (dir.isDirectory()) { // make sure it's a directory
			int numBrushes = dir.listFiles(IMAGE_FILTER).length;
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(3,numBrushes/3+1,5,5));
			ButtonGroup brushButtons = new ButtonGroup();
			
			int i = 0;
            for (final File f : dir.listFiles(IMAGE_FILTER)) {
            	BrushPanel.brushes.add(new Brush(i, f, panel, brushButtons, i==0));
				i++;
            }
            this.setContentPane(panel);
            setSelected(0);
            this.pack();
    		this.setVisible(true);
		
		} else {
			System.out.println("Cannot find brushes directory");
		}
		
	}
	
	public static void setSelected(int id) {
		selectedBrush = brushes.get(id);
	}

}
