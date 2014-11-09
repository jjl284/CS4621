package awtGUI;

import java.awt.Component;
import java.awt.event.ComponentListener;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;

public class ManipPanel extends JToolBar{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ManipPanel(){
		JRadioButton zoom = new JRadioButton();
		zoom.setText("Zoom");
		JRadioButton rotate = new JRadioButton();
		rotate.setText("Rotate");
		JRadioButton pan = new JRadioButton();
		pan.setText("Pan");
		ButtonGroup manipGroup = new ButtonGroup();
		manipGroup.add(zoom);
		manipGroup.add(rotate);
		manipGroup.add(pan);
		this.add(zoom);
		this.add(rotate);
		this.add(pan);
	}
}
