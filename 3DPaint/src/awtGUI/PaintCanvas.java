package awtGUI;

import java.awt.Canvas;
import java.awt.Color;
import java.util.ArrayList;

public class PaintCanvas extends Canvas{
	//public ToolType activeTool;
	public Color activeColor;
	public int activeToolSize;
	
	public boolean editMode; //true: editMode false: viewMode
	
	public ArrayList<PaintCanvas> statesList = new ArrayList<PaintCanvas>(); //for undo and redo
	public int currState; //index of current state of PaintCanvas
	
	//public Shading shading;
	
	public PaintCanvas() {
		// TODO Auto-generated constructor stub
		statesList.add(this);
		this.setColor(Color.BLACK);
		this.setEdit(true);
	}

	//public void setActiveTool(ToolType t){
		//activeTool = t;
	//}
	
	public void setColor (Color c){
		activeColor = c;
	}
	
	public void setToolSize(int ts){
		activeToolSize = ts;
	}
	
	public void setEdit(boolean m){
		editMode=m;
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
