package awtGUI;

import javax.swing.JColorChooser;
import javax.swing.JToolBar;

public class ColorPanel extends JToolBar {
	public ColorPanel(){
		JColorChooser pallet = new JColorChooser();
		this.add(pallet);
	}

}
