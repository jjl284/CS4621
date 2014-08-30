package ext.csharp;

import java.util.ArrayList;

public class Event<T extends EventArgs> {
	private final Object sender;
	private final ArrayList<ACEventFunc<T>> listeners;
	
	public Event(Object _sender) {
		sender = _sender;
		listeners = new ArrayList<>();
	}
	
	public void Add(ACEventFunc<T> f) {
		listeners.add(f);
	}
	public void Remove(ACEventFunc<T> f) {
		listeners.remove(f);
	}

	public void Invoke(T args) {
		for(ACEventFunc<T> f : listeners) f.Receive(sender, args);
	}
}
