package blister.input;

import org.lwjgl.input.Keyboard;

import ext.csharp.EventArgs;

public class KeyboardKeyEventArgs extends EventArgs {
	public int Key;
	public boolean IsRepeat;
	public final boolean[] KeyStates;

	public boolean getAlt() { return KeyStates[Keyboard.KEY_LMENU] | KeyStates[Keyboard.KEY_RMENU]; 	}
	public boolean getShift() { return KeyStates[Keyboard.KEY_LSHIFT] | KeyStates[Keyboard.KEY_RSHIFT]; 	}
	public boolean getControl() { return KeyStates[Keyboard.KEY_LCONTROL] | KeyStates[Keyboard.KEY_RCONTROL]; 	}

	public KeyboardKeyEventArgs() {
		KeyStates = new boolean[Keyboard.KEYBOARD_SIZE];
	}
	public void Refresh() {
		Key = 0;
		Keyboard.poll();
		KeyStates[Keyboard.KEY_LMENU] = Keyboard.isKeyDown(Keyboard.KEY_LMENU);
		KeyStates[Keyboard.KEY_RMENU] = Keyboard.isKeyDown(Keyboard.KEY_RMENU);
		KeyStates[Keyboard.KEY_LSHIFT] = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		KeyStates[Keyboard.KEY_RSHIFT] = Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
		KeyStates[Keyboard.KEY_LCONTROL] = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
		KeyStates[Keyboard.KEY_RCONTROL] = Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
		IsRepeat = false;
	}
}
