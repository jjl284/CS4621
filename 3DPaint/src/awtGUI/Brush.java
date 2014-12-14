package awtGUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class Brush {

	public int DEFAULT_BRUSH_SIZE = 10;
	
	private int size;
	private int id;
	private String filename;
	private BufferedImage image;
	private JToggleButton button;
	
	public Brush(final int id, File file, JPanel panel, ButtonGroup group, boolean selected) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.size = DEFAULT_BRUSH_SIZE;
		this.filename = file.toString();
		
		BufferedImage img = null;
		
		try {
		    img = ImageIO.read(file);
		} catch (IOException e) {
			System.out.println("Unable to load brush image file");
		}
		
		this.image = img;
		
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//AbstractButton abstractButton = (AbstractButton) e.getSource();
		        //boolean selected = abstractButton.getModel().isSelected();
		        BrushPanel.setSelected(id);
		        PaintMainGame.canvas.setToolSize(size);
		        return;
			}
		};
		
		button = new JToggleButton(new ImageIcon(file.toString()));
		button.setPreferredSize(new Dimension(50, 50));
		button.setToolTipText(file.getName());
		button.addActionListener( actionListener);
		if (selected) button.setSelected(true);
		group.add(button);
		panel.add(button);
	}
	
	/**
	 * 
	 * @param r an int between 1 and 500
	 */
	public void setSize(int r) {
		if (r > 0 && r <= 500) this.size = r;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int getID() {
		return this.id;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * 
	 * @return the buffered image that this brush uses
	 */
	public BufferedImage getImage() {
		return this.image;
	}

}
