package ext.csharp;

public abstract class ACEventFunc<T extends EventArgs>  {
	public abstract void Receive(Object sender, T args);
}
