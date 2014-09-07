package cs4620.ray1;

public class SceneFolderPath {
	private static String path = null;
	
	public static void set(String newPath)
	{
		path = newPath;
	}
	
	public static String get()
	{
		return path;
	}
}
