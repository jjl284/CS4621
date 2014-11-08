package awtGUI;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolbarActionListener implements ActionListener{

	String toolbar = "";
	boolean alreadyExists;
	Frame frame;
	public ToolbarActionListener(String bar, boolean exists, Frame frme){
		toolbar=bar;
		alreadyExists=exists;
		frame=frme;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub		
		switch(toolbar){
		case "Edit Bar": 
		    EditPanel ep = new EditPanel();
			frame.add(ep);
		    ep.setVisible(true); break;	 
		case "Color Bar":
			ColorPanel cp = new ColorPanel();
			frame.add(cp);
			cp.setVisible(true);break;
		case "Manipulator Bar": 
			ManipPanel mp = new ManipPanel();
			frame.add(mp);
			mp.setVisible(true);break; 
		}
	}

}
