package awtGUI;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.lwjgl.BufferUtils;

import egl.NativeMem;
import egl.math.Color;
import egl.math.Vector2i;

public class Brush {

	public static int DEFAULT_BRUSH_SIZE = 10;
	
	private int size;
	private int id;
	private String filename;
	private BufferedImage image;
	private JToggleButton button;
	protected Vector2i justPainted = new Vector2i(0,0);
	
	public Brush(final int id, File file, JPanel panel, ButtonGroup group, boolean selected) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.size = DEFAULT_BRUSH_SIZE;
		this.filename = file.toString();
		
		BufferedImage img = null;
		
		try {
		    img = ImageIO.read(file);
		    BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g = newImage.createGraphics();
		    g.scale(1,-1);
	        g.drawImage(img, 0, -img.getHeight(), null);
		    
		    g.dispose();
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
		
		button = new JToggleButton(new ImageIcon(((new ImageIcon(file.toString()))
				.getImage()).getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH)));
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
	
	private float blendFunction1(float x1, float x2, float a) {
		return x1 + (x2 - x1)*a/255f;
	}

	public ByteBuffer getByteBuffer(int offX, int offY, int oldW, int oldH, ByteBuffer oldBuffer){		
		justPainted.x = offX; justPainted.y = offY;
		
		int width = image.getWidth();
		int height = image.getHeight();
		ByteBuffer bb = NativeMem.createByteBuffer(size * size * 4);
		
		int i = 0;
		
		for(int y = 0;y < size;y++) {
			i = offX*4 + (offY+y)*oldW*4;
			int texY = (int)((y + 0.5f) / (size) * height);
			
			for(int x = 0;x < size;x++) {
				if (i >= oldBuffer.capacity()) i = 0;
				if (i < 0) i = oldBuffer.capacity() - 4;
				
				int texX = (int)((x + 0.5f) / (size) * width);
				int pixel = image.getRGB(texX,texY);
				
	    		float a2 = ((pixel >> 24) & 0xFF);
	    		float r2 = PaintCanvas.activeColor.R;
	    		float g2 = PaintCanvas.activeColor.G;
	    		float b2 = PaintCanvas.activeColor.B;
	    		
	    	    float r1 = oldBuffer.get(i);
	    	    float g1 = oldBuffer.get(i+1);
	    	    float b1 = oldBuffer.get(i+2);
	    	    
	    	    float r,g,b,a;

    	    	a = 255f; // alpha * fg + invAlpha * bg
	    	    r = blendFunction1(r1,r2,a2);
	    	    g = blendFunction1(g1,g2,a2);
	    	    b = blendFunction1(b1,b2,a2);
    	    	
	    		bb.put((byte)r); oldBuffer.put(i, (byte)r);
				bb.put((byte)g); oldBuffer.put(i+1, (byte)g);
				bb.put((byte)b); oldBuffer.put(i+2, (byte)b); 
				bb.put((byte)a); oldBuffer.put(i+2, (byte)a); 
				
				i += 4;
			}
		}
		
	    bb.flip();
	    return bb;
	}

}
