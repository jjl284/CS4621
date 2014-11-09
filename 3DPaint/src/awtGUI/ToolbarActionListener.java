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

		System.out.println("alreadyexists"+alreadyExists);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("alreadyexists"+alreadyExists);
		// TODO Auto-generated method stub	
		if (!alreadyExists){
			switch(toolbar){
			case "Edit Bar": 
			    EditPanel ep = new EditPanel();
				frame.add(ep);
			    ep.setVisible(true);
			    ep.updateUI();

			    System.out.println("ep.setVisibleTrue");
			    break;	 
			case "Color Bar":
				ColorPanel cp = new ColorPanel();
				frame.add(cp);
				cp.setVisible(true);
				cp.updateUI();
				System.out.println("cp.setVisibleTrue");
				break;
			case "Manipulator Bar": 
				ManipPanel mp = new ManipPanel();
				frame.add(mp);
				mp.setVisible(true);
				mp.updateUI(); 
				System.out.println("mp.setVisibleTrue");
				break;
			}
		}
	}

}
