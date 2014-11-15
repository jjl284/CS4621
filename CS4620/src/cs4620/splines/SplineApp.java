package cs4620.splines;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import cs4620.common.Scene;
import cs4620.splines.form.ControlFrame;
import cs4620.splines.form.SplineEditScreen;
import cs4620.splines.form.SplineScreen;
import egl.math.Vector2;
import ext.java.Parser;
import blister.FalseFirstScreen;
import blister.MainGame;
import blister.ScreenList;

public class SplineApp extends MainGame {
	/* +-------+-------+-------+
	 * |       |       |       |
	 * |   A   |   B   |   C   |
	 * |       |       |       |
	 * +-------+-------+-------+
	 * 
	 * A: The left spline
	 * B: The center spline
	 * C: A translated along B (SweepSpline)
	 * 
	 * Valid control points live in the range x,y in [-1,1]
	 * 
	 *    (-1,1)         (1,1)
	 *       +------+------+
	 *       |      |      |
	 *       |      |      |
	 *       +----(0,0)----+
	 *       |      |      |
	 *       |      |      |
	 *       +------+------+
	 *     (-1,1)        (1,-1)
	 *     
	 * This makes drawing with OpenGL very simple.
	 */
	public ArrayList<Vector2> leftPoints;// A
	public static boolean leftOpen= false;
	public ArrayList<Vector2> centerPoints;// B
	public static boolean centerOpen;
	
	// Main spline display
	public Scene sweepScene;
	public SplineScreen scrView;
	
	// Options frame
	public ControlFrame options;
	// Dimensions for ControlFrame stored here so we can construct the display size correctly
	private static int min_width= 340;
	public static Dimension tolSlideDim= new Dimension(100,300),
	                        tolPanelDim= new Dimension(min_width, 325),
	                        modeDim= new Dimension(min_width, 150),
	                        lwsbDim= new Dimension(min_width, 75),
	                        configDim= new Dimension(min_width, 150),
	                        optionsDim= new Dimension(min_width,
	        		                                  tolPanelDim.height +
	        		                                      modeDim.height +
	        		                                      lwsbDim.height +
	        		                                      configDim.height),
	        		        screenDim= Toolkit.getDefaultToolkit().getScreenSize();
	
	public SplineApp() {
		super("CS 4620: Splines", screenDim.width-optionsDim.width, optionsDim.height);
		init_splines();
		init_display();
	}
	
	private void init_splines() {
		leftPoints= new ArrayList<Vector2>();
		centerPoints= new ArrayList<Vector2>();
		
		double incr= (2*Math.PI) / 5.0;
		double t= -Math.PI, x= 0.0, y= 0.0;
		double pi_inv= 1.0 / Math.PI;
		int num_initialized= 0;
		// add 5 control points to A and B
		while(num_initialized < 5) {
			// A starts closed, even spread along circle
			x= Math.cos(t);
			y= Math.sin(t);
			leftPoints.add(new Vector2((float)x, (float)y));
			// B starts open, even-ish spread along sine
			x= t*pi_inv;// phase from -pi to pi, need -1 to 1
			centerPoints.add(new Vector2((float)x, (float)y));
			if(num_initialized == 0 || num_initialized == 4) {// add two extra ctrl points for open to be more like a sine curve ;)
				t += incr / 2.0;
				x= t*pi_inv;
				y= Math.sin(t);
				centerPoints.add(new Vector2((float)x, (float)y));
				t -= incr / 2.0;
			}
			t += incr;
			num_initialized++;
		}
		// add 6th control point to B since it starts as open
		y= Math.sin(t);
		centerPoints.add(new Vector2(1.0f, (float)y));
		
		File f= new File("data/scenes/SweepSpline.xml");
		if(f.exists()) {
			Object o= (new Parser()).parse(f.getAbsolutePath(), Scene.class);
			sweepScene= (Scene)o;
		} else {
			System.err.println("Could not find \"data/scenes/SweepSpline.xml\"!!! Please find it and put it back!");
			sweepScene= new Scene();	
		}
	}
	
	private void init_display() {
		options= new ControlFrame("Options...", this);
	}

	@Override
	protected void buildScreenList() {
		scrView = new SplineScreen();
		screenList = new ScreenList(this, 0,
			new FalseFirstScreen(1),
			scrView,
			new SplineEditScreen()
			);
	}

	@Override
	protected void fullInitialize() {}

	@Override
	protected void fullLoad() {}
	
	public static void main(String[] args) {
		SplineApp app= new SplineApp();
		app.run();
		app.dispose();
	}
}