package awtGUI;

import java.awt.Component;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class BrushPanel extends Component {

	private static final long serialVersionUID = -7806098757885232440L;
	
	protected ArrayList<Brush> brushes;
    
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
		// Create the actual panel for displaying brushes
		
		
		
		// Create all the brushes
		brushes = new ArrayList<Brush>();
		
		File dir = new File("../Brushes/");
		dir.mkdir();
		System.out.println(dir);
		
		int i = 0;
		if (dir.isDirectory()) { // make sure it's a directory
            for (final File f : dir.listFiles(IMAGE_FILTER)) {
            	System.out.println(f);
            	brushes.add(new Brush(i, f));
				i++;
            }
        }
		
	}
	
	
	
	public static void main(String[] args) {
		new BrushPanel();
	}

}
