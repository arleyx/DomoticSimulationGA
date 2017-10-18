package simulator;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class State extends LinkedHashMap<String, String> {
	private static final long serialVersionUID = 1L;
	
	public State() {
		super();
	}
	
	public String getComponents() {
		String components = "";
		for (Entry<String, String> entry : this.entrySet())
			components += ","+ entry.getKey();
		return components;
	}
	
	public String getStates() {
		String states = "";
		for (Entry<String, String> entry : this.entrySet())
			states += ","+ entry.getValue();
			//states += ","+ entry.getKey() + "=" + entry.getValue();
		return states;
	}
}
