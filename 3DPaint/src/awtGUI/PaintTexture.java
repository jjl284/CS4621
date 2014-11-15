package awtGUI;

import cs4620.ray1.Image;

public class PaintTexture extends Image{
	
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

	// Creates an image "filename.png" of size w x h and initialized to black
	public PaintTexture(int w, int h, String filename) {
		super(w,h);
		clear();
		write(filename);
	}
	
	// Creates an image copy of oldImage in "filename.png"
	public PaintTexture(Image oldImage, String filename) {
		super(oldImage);
		write(filename);
	}

}
