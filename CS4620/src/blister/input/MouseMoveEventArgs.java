package blister.input;

import ext.csharp.EventArgs;

public class MouseMoveEventArgs extends EventArgs {
	public final MouseState State;
	public int DX;
	public int DY;
	
	public MouseMoveEventArgs(MouseState s) {
		State = s;
	}
}
