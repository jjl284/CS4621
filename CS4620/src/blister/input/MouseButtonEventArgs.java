package blister.input;

import ext.csharp.EventArgs;

public class MouseButtonEventArgs extends EventArgs {
	public final MouseState State;
	public int Button;
	
	public MouseButtonEventArgs(MouseState s) {
		State = s;
	}
}
