package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public class ConfigComponents extends Config {

	private static final long serialVersionUID = 1L;

	public ConfigComponents(String path) {
		super(path);
	}
	
	public String[] getPlaces() {
		Enumeration<Object> keys = this.keys();
		//ArrayList<String> places = new ArrayList<String>();
		
		List places = Collections.list(keys);
		Collections.sort(places);
		return (String[]) places.toArray(new String[places.size()]);
		
		//while (keys.hasMoreElements()) places.add((String) keys.nextElement());
		//return places.toArray(new String[places.size()]);
	}
	
	public String getPlaceByDevice(String deviceName) {
    	for (String place : this.getPlaces()) {
			String[] devices = this.getItemsByKey(place);
			for (String device : devices) {
				DeviceInformation deviceInformation = this.getDeviceInformation(device);
				if (deviceInformation.getName().equals(deviceName))
					return place;
			}
		}
		
		return null;
	}
	
	public DeviceInformation getDeviceInformation(String information) {		
		String[] deviceInformation = information.split("\\:");
		List<String> connections = new ArrayList<String>();
		if (deviceInformation.length > 2) {
			String[] connectionsInformation = deviceInformation[2].substring(1, deviceInformation[2].length() - 1).split("\\]\\[");
			for (String connection : connectionsInformation)
				connections.add(connection);
		}
		return new DeviceInformation(deviceInformation[0], deviceInformation[1], connections);
	}
}
