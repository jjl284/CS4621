package awtGUI;

import java.awt.Button;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class EditPanel extends JToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EditPanel(){
		Button bUndo =new Button("undo");
		Button bRedo =new Button("redo");
		Button bIncSize =new Button("Size +");
		Button bdecSize =new Button("Size -");
		JSlider bSize = new JSlider(JSlider.HORIZONTAL,1,50,12);
		bSize.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		JTextField txtSize = new JTextField("12");
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		this.setBackground(Color.gray);
		
		this.add(bUndo);
		this.add(bRedo);
		this.add(bIncSize);
		this.add(bdecSize);
		this.add(bSize);
		this.add(txtSize);
	}
}
