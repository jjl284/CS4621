package awtGUI;

import javax.swing.JColorChooser;
import javax.swing.JToolBar;

public class ColorPanel extends JToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ColorPanel(){
		JColorChooser pallet = new JColorChooser();
		this.add(pallet);
	}

}
