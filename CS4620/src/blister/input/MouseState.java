package blister.input;

import org.lwjgl.input.Mouse;

public class MouseState {
	public int X;
	public int Y;
	public int Buttons;
	public int Scroll;

	public MouseState() {
	}
	public void Refresh() {
		Mouse.poll();
		X = Mouse.getX();
		Y = Mouse.getY();
		for(int i = 0;i < 16;i++) Buttons |= Mouse.isButtonDown(i) ? 0 : (1 << i);
		Scroll=0;
	}

	public boolean IsButtonDown(int b) {
		return (Buttons & b) != 0;
	}
}
