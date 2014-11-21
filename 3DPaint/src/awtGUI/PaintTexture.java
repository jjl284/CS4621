package awtGUI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import egl.math.Color;
import egl.math.Colord;

public class PaintTexture {
	
	/**
	 * A PaintTexture is a BufferedImage generated as an image specified by:
	 * 		256 x 256
	 * 		2048 x 2048
	 * 		2048 x 1024
	 * 		Image File specified by parsed XML
	 * 
	 * The image should be drawn on the interface-- once in the TexturePreview and once on the Mesh
	 * 
	 * Painting should setPixelColor(Color c, int uvX, int uvY) (inherited from Image)
	 * 
	 * Saving should write(String filename) (inherited from Image)
	 * 
	 * */
	
	/** Image width * */
	protected int width;
	
	/** Image height * */
	protected int height;
	
	/** Data array* */
	protected Colord[][] data;
	

	// Creates an image "filename.png" of size w x h and initialized to black
	public PaintTexture(int w, int h, String filename) {
		setSize(w, h);
		clear();
		write(filename);
	}
	
	// Creates an image copy of oldImage in "filename.png"
	public PaintTexture(String oldName, String filename) {
		BufferedImage oldImage = null;
		
		try {
		    oldImage = ImageIO.read(new File(oldName));
		    setSize(oldImage.getWidth(), oldImage.getHeight());
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					data[i][j].set(oldImage.getRGB(i,j));
				}
			}
			write(filename);
		} catch (IOException e) {
		
		}
	}
	
	/**
	 * Set the image to black
	 */
	public void clear() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				data[i][j] = new Colord(1,1,1);
			}
		}
	}
	
	/**
	 * @return the width of the image
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @return the height of the image
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Set the size of the image by recreating it.  Destroys all current image data.
	 * @param newWidth width
	 * @param newHeight height
	 */
	public void setSize(int newWidth, int newHeight) {
		width = newWidth;
		height = newHeight;
		data = new Colord[width][height];
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				data[i][j] = new Colord();
			}
		}
	}
	
	/**
	 * Get the color of a pixel.
	 *
	 * @param outPixel Color value of pixel (inX,inY)
	 * @param inX inX coordinate
	 * @param inY inY Coordinate
	 */
	public void getPixelColor(Color outPixel, int inX, int inY) {
		if (inX < 0 || inY < 0 || inX >= width || inY >= height)
			throw new IndexOutOfBoundsException();
		outPixel.set(data[inX][inY]);
	}
	
	/**
	 * Get the color of a pixel.
	 *
	 * @param outPixel Colord value of pixel (inX,inY)
	 * @param inX inX coordinate
	 * @param inY inY Coordinate
	 */
	public void getPixelColor(Colord outPixel, int inX, int inY) {
		if (inX < 0 || inY < 0 || inX >= width || inY >= height)
			throw new IndexOutOfBoundsException();
		outPixel.set(data[inX][inY]);
	}
	
	/**
	 * Set the color of a pixel.
	 * @param inPixel Color value of pixel (inX,inY)
	 * @param inX inX coordinate
	 * @param inY inY Coordinate
	 */
	public void setPixelColor(Color inPixel, int inX, int inY) {
		if (inX < 0 || inY < 0 || inX >= width || inY >= height)
			throw new IndexOutOfBoundsException();
		data[inX][inY].set(inPixel);
	}
	
	/**
	 * Set the color of a pixel.
	 * @param inPixel Colord value of pixel (inX, inY)
	 * @param inX inX coordinate
	 * @param inY inY coordinate
	 */
	public void setPixelColor(Colord inPixel, int inX, int inY) {
		if (inX < 0 || inY < 0 || inX >= width || inY >= height)
			throw new IndexOutOfBoundsException();
		data[inX][inY].set(inPixel);
	}
	
	/**
	 * Write this image to the filename.  The output is always written as a PNG regardless
	 * of the extension on the filename given.
	 * @param fileName the output filename
	 */
	public void write(String fileName) {
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		Colord pixelColor= new Colord();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				this.getPixelColor(pixelColor, x, y);
				pixelColor.gammaCorrect(2.2);
				Color c = pixelColor.toColor();
				bufferedImage.setRGB(x, (height - 1 - y), c.toIntRGB());
			}
		}
		
		try {
			ImageIO.write(bufferedImage, "PNG", new File(fileName));
		}
		catch (Exception e) {
			System.out.println("Error occured while attempting to write file: "+fileName);
			System.err.println(e);
			e.printStackTrace();
		}
	}
}
