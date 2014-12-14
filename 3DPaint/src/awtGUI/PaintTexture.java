package awtGUI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import cs4620.gl.RenderEnvironment;
import cs4620.mesh.MeshData;
import egl.NativeMem;
import egl.math.Color;
import egl.math.Colord;
import egl.math.MathHelper;
import egl.math.Vector2d;

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
	
	protected String filepath;

	// Creates an image "filename.png" of size w x h and initialized to black
	public PaintTexture(int w, int h, String file) {
		setSize(w, h);
		clear();
		this.filepath = file;
		write(filepath);
	}
	
	// Creates an instance of PaintTexture from "filename.png"
		public PaintTexture(String file) {
			this.filepath = file;
			
			BufferedImage image = null;
			
			try {
			    image = ImageIO.read(new File(filepath));
			    setSize(image.getWidth(), image.getHeight());
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						data[i][j].set(Color.fromIntRGB( image.getRGB(i, height - 1 - j) ));
					}
				}
			} catch (IOException e) {
				
			}
		}
	
	// Creates an image copy of oldImage in "filename.png"
	public PaintTexture(String oldName, String file) {
		
		this.filepath = file;
		
		BufferedImage oldImage = null;
		
		try {
		    oldImage = ImageIO.read(new File(oldName));
		    setSize(oldImage.getWidth(), oldImage.getHeight());
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					data[i][j].set(Color.fromIntRGB( oldImage.getRGB(i, height - 1 - j) ));
				}
			}
			write(this.filepath);
		} catch (IOException e) {
		
		}
	}
	
	/**
	 * Set the image to white
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
	
	public void paintSquare(Color color, int posX, int posY, int squareWidth) {
		int halfSquare = (int)(squareWidth/2.0);
		for (int i = posX - halfSquare; i <= posX + halfSquare; i++) {
			for (int j = posY - halfSquare; j <= posY + halfSquare; j++) {
				int pixelX = MathHelper.clamp(i, 0, width-1);
				int pixelY = MathHelper.clamp(j, 0, height-1);
				setPixelColor(color, pixelX, pixelY);
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
	
	public void writeFromGL(String filename) throws IOException {
		ByteBuffer bb = BufferUtils.createByteBuffer(4*width*height);
		RenderEnvironment.paintTextureGL.writeToImage(GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bb);
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		img.getAlphaRaster().setDataElements(0, 0, width, height, bb);
		ImageIO.write(img, "PNG", new File(filename));
	}
	
	/**
	 * "Paints" the mesh, given the location in coordinates on the mesh it 
	 * should paint and the point's texture coordinates.
	 * @param location the coordinates of the center of the spot that should be painted
	 * @param texCoords coordinates at location
	 * @param meshData the corresponding MeshData object
	 */
	public void addPaint(Vector2d texCoords, MeshData meshData) {
		int size = BrushPanel.selectedBrush.getSize(); // Brush size
		
		// Center paint around click
		int paintX = (int)(texCoords.x*width + 0.5) - (int)(size/2.0);
		int paintY = (int)(texCoords.y*height + 0.5) - (int)(size/2.0);
		
		// Clamp painted location to image bounds
		int x = MathHelper.clamp(paintX, 0, width);
		int y  = MathHelper.clamp(paintY, 0, height);
		
		ByteBuffer brushBuffer = BrushPanel.selectedBrush.getByteBuffer();
		ByteBuffer oldBuffer = NativeMem.createByteBuffer(size * size * 4);
		RenderEnvironment.paintTextureGL.readImage(x, y, size, size, 
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, oldBuffer);
		
		for (int i= 0; i < brushBuffer.capacity(); i+=4) {
			if (brushBuffer.get(i+3) >= 0) {
				brushBuffer.put(i, oldBuffer.get(i));
				brushBuffer.put(i+1, oldBuffer.get(i+1));
				brushBuffer.put(i+2, oldBuffer.get(i+2));
				brushBuffer.put(i+3, oldBuffer.get(i+3));
			}
			//System.out.println(brushBuffer.get(i+3));
		}
		
		RenderEnvironment.paintTextureGL.updateImage(x, y, size, size, 
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, brushBuffer);
	}
}