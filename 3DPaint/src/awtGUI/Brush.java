package awtGUI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Brush {

	public int DEFAULT_BRUSH_SIZE = 10;
	
	private int size;
	private int id;
	private String filename;
	private BufferedImage image;
	
	public Brush(int id, File file) {
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
