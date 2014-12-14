package awtGUI;

import java.awt.Canvas;
import egl.math.Color;
import java.util.ArrayList;

public class PaintCanvas extends Canvas{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//public ToolType activeTool;
	public static Color activeColor;
	public static Color oldColor;
	public int activeToolSize;
	public static String activeTool; // "Brush" "Eraser" or "Pointer"
	
	public boolean editMode; //true: editMode false: viewMode
	
	public ArrayList<PaintCanvas> statesList = new ArrayList<PaintCanvas>(); //for undo and redo
	public int currState; //index of current state of PaintCanvas
	
	//public Shading shading;
	
	public PaintCanvas() {
		// TODO Auto-generated constr
		this.setEdit(false);
		this.setBackground(java.awt.Color.BLACK);
		this.activeToolSize = Brush.DEFAULT_BRUSH_SIZE;
		this.oldColor = Color.Black;
		this.activeColor = Color.Black;
	}

	//public void setActiveTool(ToolType t){
		//activeTool = t;
	//}
	
	public void setColor (Color c){
		activeColor = c;
	}
	
	public void setToolSize(int ts){
		activeToolSize = ts;
		PaintSceneApp.toolSizeSlider.setValue(ts);
		PaintSceneApp.toolSizeLabel.setText("  " + ts);
	}
	
	public void setEdit(boolean m){
		editMode = m;
	}
	
	//public void setShading(Shading s){
		//shading = s;
	//}
	public void LoadPrevState(int index){
		if (index !=0){
			//set this to statesList.get(currState-1)
			currState--;
		}
		else
			System.out.println("***ERROR: NO PREVIOUS STATE***");
	}
	
	public void LoadNextState(int index){
		if (index != statesList.size()-1){
			//set this to statesList.get(currState+1)
			currState++;
		}
		else
			System.out.println("***ERROR: NO NEXT STATE***");
	}
}
