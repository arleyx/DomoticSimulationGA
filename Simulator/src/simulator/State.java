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
	
	public String getEvent(String component) {
		return get(component).replaceAll("State", "");
	}
	
	public String toJSON() {
		String response = "{";
		
		for (Entry<String, String> entry : this.entrySet())
			response += "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\",";
		
		return response.substring(0, response.length() - 1) + "}";
	}
}
