package utils;

public class ConfigDevice extends Config {

	private static final long serialVersionUID = 1L;

	public ConfigDevice(String path) {
		super(path);
	}
	
	public String[] getValidStates(String state) {
		return getProperty("STATE_CONNECTION." + state).split("\\,");
	}
	
	public int getCostState(String state) {
		return Integer.parseInt(getProperty("STATE_VALUE." + state).replaceAll("w", ""));
	}
	
	/*public String[] getPlaces() {
		Enumeration<Object> keys = this.keys();
		ArrayList<String> places = new ArrayList<String>();
		while (keys.hasMoreElements()) places.add((String) keys.nextElement());
		return places.toArray(new String[places.size()]);
	}
	
	public DeviceInformation getDeviceInformation(String information) {		
		String[] deviceInformation = information.split("\\:");
		HashMap<String, String> connections = new HashMap<String, String>();
		
		if (deviceInformation.length > 2) {
			String[] connectionsInformation = deviceInformation[2].substring(1, deviceInformation[2].length() - 1).split("\\]\\[");
			
			for (String connection : connectionsInformation) {
				String[] events = connection.split("\\/");
				connections.put(events[0], events[1]);
			}
		}
		
		return new DeviceInformation(deviceInformation[0], deviceInformation[1], connections);
	}*/
}
