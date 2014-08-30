package egl;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.lwjgl.opengl.GL11;

import egl.GL.ErrorCode;

public class GLError {
	public static OutputStreamWriter ErrorLog;

	public static void Get(String desc) {
		int error;
		while((error = GL11.glGetError()) != ErrorCode.NoError) {
			Write("ERROR - " + desc + " - " + error);
		}
	}
	public static void Write(String err) {
		if(ErrorLog != null) {
			try { ErrorLog.write(err + System.lineSeparator()); } 
			catch (IOException e) { }
		}
	}

	public static void Close() {
		if(ErrorLog == null) return;
		try {
			ErrorLog.flush();
			ErrorLog.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ErrorLog = null;
		return;
	}
}
