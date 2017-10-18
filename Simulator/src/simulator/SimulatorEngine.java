package simulator;

import java.util.Map;

import it.polito.elite.domotics.models.simulator.SCXMLHomeSimulatorEngine;
import it.polito.elite.domotics.models.simulator.StateChangeListener;
import it.polito.elite.domotics.models.statemachine.ParallelStateMachine;
import it.polito.elite.domotics.models.statemachine.SCXMLDeviceConnector;
import it.polito.elite.domotics.models.statemachine.SCXMLDeviceInstance;
import it.polito.elite.domotics.models.statemachine.template.TemplateFactory;

import utils.Config;
import utils.ConfigComponents;
import utils.DeviceInformation;

public class SimulatorEngine {
		
	private Config setup;
	private ConfigComponents components;
	
	private ParallelStateMachine machine;
	private SCXMLHomeSimulatorEngine simulator;
	
	public SimulatorEngine (Config setup, ConfigComponents components) {
		this.setup = setup;
		this.components = components;
		this.machine = new ParallelStateMachine("HomeMachine");
		
		loadComponents();
		
		if (setup.getProperty("DEBUG", "false").equals("true")) {
			System.out.println(machine.getSCXML());
//			System.out.println(machine.getIncomingEvents());
//			System.out.println(machine.getOutgoingEvents());
//			System.out.println(machine.getIds());
		}
		
		this.simulator = new SCXMLHomeSimulatorEngine(machine);
	}
	
	private void loadComponents() {
		TemplateFactory factory = new TemplateFactory(setup.getProperty("MACHINES_PATH"));
		
		for (String place : components.getPlaces()) {
			String[] devices = components.getItemsByKey(place);
			for (String device : devices) {
				DeviceInformation deviceInformation = components.getDeviceInformation(device);
				SCXMLDeviceInstance deviceInstance;
				try {
					deviceInstance = factory.getTemplate(deviceInformation.getMachine()).getSCXMLDeviceInstance(deviceInformation.getName());
					machine.addMachine(deviceInstance, false);
				} catch (Exception e) {
					System.err.println("Exception while loading the environment machines");
					e.printStackTrace(System.err);
				}
				if (deviceInformation.hasConnections()) loadConnection(deviceInformation);
			}
		}
	}
	
	private void loadConnection(DeviceInformation deviceInformation) {
		SCXMLDeviceConnector connector = new SCXMLDeviceConnector(deviceInformation.getName());
		
		for (String connection : deviceInformation.getConnections())
			connector.addConnection(deviceInformation.getName() +"_"+ connection, connection);
			
		machine.addConnector(connector, false, false);
	}
	
	public void start() {
		simulator.startSimulation();
	}
	
	public void stop() {
		simulator.stopSimulation();
	}
	
	public void registerStateChangeListener(StateChangeListener listener) {
		simulator.addSCXMLStateChangeListener(listener);
	}

	public ParallelStateMachine getMachine() {
		return machine;
	}

	public SCXMLHomeSimulatorEngine getSimulator() {
		return simulator;
	}
}
