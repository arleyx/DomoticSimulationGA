package utils;

import java.util.List;

public class DeviceInformation {
	private String machine;
	private String name;
	private List<String> connections;

	public DeviceInformation(String machine, String name, List<String> connections) {
		this.machine = machine;
		this.name = name;
		this.connections = connections;
	}
	
	public boolean hasConnections() {
		return !connections.isEmpty();
	}
	
	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getConnections() {
		return connections;
	}

	public void setConnections(List<String> connections) {
		this.connections = connections;
	}
}
