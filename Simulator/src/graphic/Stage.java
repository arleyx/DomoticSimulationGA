package graphic;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import utils.Config;
import utils.ConfigComponents;
import utils.ConfigDevice;
import utils.DeviceInformation;

public class Stage {
	
	Config setup;
	ConfigComponents config;
	LinkedHashMap<String, String[]> actuators;
	LinkedHashMap<String, LinkedHashMap<String, String[]>> places;
	LinkedHashMap<String, LinkedHashMap<String, String[]>> components;
		
	public Stage(Config s, ConfigComponents c) {
		setup = s;
		config = c;
		
		actuators = new LinkedHashMap<String, String[]>();
		places = new LinkedHashMap<String, LinkedHashMap<String, String[]>>();
		components = new LinkedHashMap<String, LinkedHashMap<String, String[]>>();
		
		load();
	}
	
	public void load() {
		List<String> a = Arrays.asList(setup.getProperty("ACTUATORS").split("\\,"));
		
		for (String place : config.getPlaces()) {			
			String[] devices = config.getItemsByKey(place);
			LinkedHashMap<String, String[]> c = new LinkedHashMap<String, String[]>();
			for (String device : devices) {
				DeviceInformation deviceInformation = config.getDeviceInformation(device);
				ConfigDevice configDevice = new ConfigDevice(
					setup.getProperty("MACHINES_PATH") + deviceInformation.getMachine() +"/"+ deviceInformation.getMachine() 
				);
				String[] states = configDevice.getProperty("STATES").split("\\,");
				if (a.contains(deviceInformation.getName()))
					actuators.put(deviceInformation.getName(), states);
				else 
					c.put(deviceInformation.getName(), states);
				
				LinkedHashMap<String, String[]> statesConnections = new LinkedHashMap<String, String[]>();
				for (String state : states)
					statesConnections.put(state, configDevice.getValidStates(state));
				components.put(deviceInformation.getName(), statesConnections);
			}
			places.put(place, c);
		}
	}
	
	public String actuatorsToJSON() {
		String response = "\"actuators\":[";
		
		for (Entry<String, String[]> actuator : actuators.entrySet()) {
			response += "{\"name\":\"" + actuator.getKey() + "\",\"states\":[\"" + String.join("\",\"", actuator.getValue()) + "\"]},";
		}

		return response.substring(0, response.length() - (actuators.size() > 0 ? 1 : 0)) + "]";
	}
	
	public String placesToJSON() {
		String response = "\"places\":[";
		
		for (Entry<String, LinkedHashMap<String, String[]>> place : places.entrySet()) {
			response += "{\"name\":\"" + place.getKey() + "\",\"components\":[";
			
			for (Entry<String, String[]> component : place.getValue().entrySet())
				response += "{\"name\":\"" + component.getKey() + "\",\"states\":[\"" + String.join("\",\"", component.getValue()) + "\"]},";
			
			response = response.substring(0, response.length() - (place.getValue().size() > 0 ? 1 : 0)) + "]},";
		}
		
		return response.substring(0, response.length() - (places.size() > 0 ? 1 : 0)) + "]";
	}
	
	public String componentsToJSON() {
		String response = "\"components\":{";
		
		for (Entry<String, LinkedHashMap<String, String[]>> component : components.entrySet()) {
			response += "\"" + component.getKey() + "\":{";
			
			for (Entry<String, String[]> state : component.getValue().entrySet())
				response += "\"" + state.getKey() + "\":[\"" + String.join("\",\"", state.getValue()) + "\"],";
			
			response = response.substring(0, response.length() - (component.getValue().size() > 0 ? 1 : 0)) + "},";
		}
		
		return response.substring(0, response.length() - (places.size() > 0 ? 1 : 0)) + "}";
	}
	
	public String toJSON() {
		return "{" + actuatorsToJSON() + "," + placesToJSON() + "," + componentsToJSON() + "}";
	}
}