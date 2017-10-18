package simulator;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import graphic.Stage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.elite.domotics.models.simulator.StateChangeListener;
import it.polito.elite.domotics.models.simulator.events.StateChangeEvent;
import processing.core.PApplet;
import simulator.utils.RandomGenerator;
import utils.Config;
import utils.ConfigComponents;
import utils.ConfigDevice;
import utils.DeviceInformation;
import utils.Result;

public class Simulator implements StateChangeListener {
	private Config setup;
	private ConfigComponents components;
	
	private Timer timer;
	private int milliseconds;
	
	private SimulatorEngine simulatorEngine;

	private String result;
	private State states;
	private Stage stage;
	
    public Simulator(Config setup, ConfigComponents components, PApplet handler) {
    	this.setup = setup;
		this.components = components;
		this.states = new State();
		this.stage = new Stage(handler, setup, components);
		loadInitialStates();
		loadTimer();
		init();
    }
    
    public void init() {
    	simulatorEngine = new SimulatorEngine(setup, components);
    	simulatorEngine.registerStateChangeListener(this);
    	simulatorEngine.getSimulator().addContextObject("states", states);
    	simulatorEngine.getSimulator().addContextObject("random", new RandomGenerator(
			Integer.parseInt(setup.getProperty("DELAY")),
			Integer.parseInt(setup.getProperty("MAX_MILLISECONDS"))
		));
    }
    
    private void loadTimer() {
		int delay = Integer.parseInt(setup.getProperty("DELAY"));
		int time = Integer.parseInt(setup.getProperty("TIME"));
		
		result = "MILISEGUNDOS"+ states.getComponents() +"\n";
		milliseconds = 0;
				
		timer = new Timer(delay, new ActionListener () {
		    public void actionPerformed(ActionEvent e) {		    	
		    	milliseconds += delay;
		    	result += milliseconds +"ms"+ states.getStates() +"\n";
		    	if (milliseconds >= time) stop();
//		    	if (milliseconds >= 7 * delay) {
//		    		//simulatorEngine.getSimulator().fireEvent("lamp1_on");
//		    		simulatorEngine.getSimulator().fireEvent("user1_onLamp1");
//		    		simulatorEngine.getSimulator().fireEvent("user1_floorOneState");
//		    		//JOptionPane.showMessageDialog(null, "Ok!!");
//		    	}
	    	}
		});
	}
    
    private void loadInitialStates() {
    	for (String place : components.getPlaces()) {
			String[] devices = components.getItemsByKey(place);
			for (String device : devices) {
				DeviceInformation deviceInformation = components.getDeviceInformation(device);
				ConfigDevice configDevice = new ConfigDevice(
					setup.getProperty("MACHINES_PATH") + deviceInformation.getMachine() +"/"+ deviceInformation.getMachine() 
				);
				states.put(deviceInformation.getName(), configDevice.getProperty("STATE_INITIAL"));
			}
		}
    }
    
    public void setupStage() {
		stage.setup();
	}
    
    public void buildStage() {
		stage.build(states);
	}
    
    /*private String getPlaceByDevice(String deviceName) {
    	for (String place : components.getPlaces()) {
			String[] devices = components.getItemsByKey(place);
			for (String device : devices) {
				DeviceInformation deviceInformation = components.getDeviceInformation(device);
				if (deviceInformation.getName().equals(deviceName))
					return place;
			}
		}
		
		return null;
	}
    
    private DeviceInformation getDeviceByName(String deviceName) {
		for (String place : components.getPlaces()) {
			String[] devices = components.getItemsByKey(place);
			for (String device : devices) {
				DeviceInformation deviceInformation = components.getDeviceInformation(device);
				if (deviceInformation.getName().equals(deviceName))
					return deviceInformation;
			}
		}
		
		return null;
	}*/

	public void start() {
    	simulatorEngine.start();
    	timer.start();
    }
    
    public void stop() {
    	simulatorEngine.stop();
    	timer.stop();
    	
    	exportData();
    }
    
    public void exportData() {
    	Date date = new Date(System.currentTimeMillis());
    	SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yyyy-HH:mm:ss");
    	
    	String path = setup.getProperty("RESULT_PATH");
    	path = path.charAt(path.length() - 1) == '/' ? path : path + "/";
    	path = path + ft.format(date);
    	
    	File folder = new File(path);
    	folder.mkdir();
    	
    	Result resultFile = new Result(path, "simulation.csv");
    	resultFile.write(result);
    	
    	String placeInformation = "\n\nLugares:\n\n";
    	for (String place : components.getPlaces()) {
    		placeInformation += place;
    		String[] devices = components.getItemsByKey(place);
			for (String device : devices) {
				DeviceInformation deviceInformation = components.getDeviceInformation(device);
				placeInformation += ","+ deviceInformation.getName();
			}
			placeInformation += "\n";
		}
    	
    	Statistic statistic = new Statistic(setup, components, resultFile);
    	
    	statistic.chartTimeByStates(path);
    	
    	resultFile.write(placeInformation, true);
    	resultFile.write(statistic.getTimeByStates(), true);
    	resultFile.write(statistic.getTimeOptimalByStates(), true);
    	resultFile.write(statistic.getTimeFailedByStates(), true);
    	//resultFile.write(statistic.getPowerByStates(), true);
    	
//    	Date date = new Date(System.currentTimeMillis());
//    	SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
//    	
//    	String path = setup.getProperty("RESULT_PATH");
//    	path = path.charAt(path.length() - 1) == '/' ? path : path + "/";
//    	path = path + ft.format(date);
//    	
//    	File folder = new File(path);
//    	folder.mkdir();
//    	
//    	Result resultFile = new Result(path, "simulation");
//    	resultFile.write(result);
//    	
//    	Statistic statistic = new Statistic(setup, components, resultFile);
//    	Result statisticFile = new Result(path, "result");
//    	statisticFile.write(statistic.getAccountantsToString());
    }
    
	@Override
	public void stateChanged(StateChangeEvent e) {
		if (milliseconds > 0) {
			e.getDevice();
			e.getCurrentState();
			
			System.out.println(e.getDevice());
			System.out.println(e.getCurrentState());
			
			String[] event = e.getCurrentState().split("\\_"); 
			
			if (states.containsKey(event[0]))
				states.put(event[0], event[1]);
			
			
			
			//String place = getPlace(e.getDevice());
			
			//result += place +",";
			//result += e.getCurrentState() +"\n";
			
			//resultFile.write(milliseconds +"s: "+ place +":\n", true);
		}
	}
}
