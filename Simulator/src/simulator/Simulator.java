package simulator;

import javax.swing.Timer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;

import graphic.Stage;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import it.polito.elite.domotics.models.simulator.StateChangeListener;
import it.polito.elite.domotics.models.simulator.events.StateChangeEvent;
//import simulator.utils.RandomGenerator;
import socket.SocketServer;
import utils.Config;
import utils.ConfigComponents;
import utils.ConfigDevice;
import utils.DeviceInformation;
import utils.Result;

public class Simulator extends SocketServer implements StateChangeListener {
	
	static final int PORT = 7777;
	
	private Config setup;
	private ConfigComponents components;
	
	private Timer timer;
	private int milliseconds;
	
//	private SimulatorEngine simulatorEngine;

	private String result;
	private State states;
	private Stage stage;
	
    public Simulator(Config setup, ConfigComponents components) throws UnknownHostException {
    	super(PORT);
    	
    	this.setup = setup;
		this.components = components;
		this.states = new State();
		this.stage = new Stage(setup, components);
		
		loadInitialStates();
		loadTimer();
		init();
    }
    
    public void init() {
//    	simulatorEngine = new SimulatorEngine(setup, components);
//    	simulatorEngine.registerStateChangeListener(this);
//    	simulatorEngine.getSimulator().addContextObject("states", states);
//    	simulatorEngine.getSimulator().addContextObject("random", new RandomGenerator(
//			Integer.parseInt(setup.getProperty("DELAY")),
//			Integer.parseInt(setup.getProperty("MAX_MILLISECONDS"))
//		));
    }
    
    private void loadTimer() {
		int delay = Integer.parseInt(setup.getProperty("DELAY"));
		//int time = Integer.parseInt(setup.getProperty("TIME"));
		
		result = "MILISEGUNDOS"+ states.getComponents() +"\n";
		milliseconds = 0;
				
		timer = new Timer(delay, new ActionListener () {
		    public void actionPerformed(ActionEvent e) {		    	
		    	milliseconds += delay;
		    	result += milliseconds +"ms"+ states.getStates() +"\n";
		    	
		    	sendData("{\"method\":\"timer\",\"data\":{\"milliseconds\":" + milliseconds + ", \"states\": " + states.toJSON() + "}}");
		    	
		    	//if (milliseconds >= time) stopSimulator();
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

	public void startSimulator() {
    	// simulatorEngine.start();
    	timer.start();
    }
    
    public void stopSimulator() {
    	// simulatorEngine.stop();
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
    	
    	Result resultFileGA = new Result(path, "GA.csv");
    	resultFileGA.write(result);
    	
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
    	
    	//statistic.chartTimeByStates(path);
    	
    	resultFile.write(placeInformation, true);
    	resultFile.write(statistic.getTimeByStates(), true);
    	resultFile.write(statistic.getTimeOptimalByStates(), true);
    	resultFile.write(statistic.getTimeFailedByStates(), true);
    	//resultFile.write(statistic.getPowerByStates(), true);
    	
    	
    	callGeneticAlgorithm(path, resultFileGA.getPathName());
    	
    	
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
    
    private void callGeneticAlgorithm(String filepathResult, String filepath) {
    	JSONObject _response = new JSONObject();
    	JSONObject _components = new JSONObject();
    	
    	_response.accumulate("actuators", setup.getProperty("ACTUATORS").split("\\,"));
    	
    	List<String> a = Arrays.asList(setup.getProperty("ACTUATORS").split("\\,"));
    	
		for (String place : components.getPlaces()) {			
			String[] devices = components.getItemsByKey(place);
			for (String device : devices) {
				DeviceInformation deviceInformation = components.getDeviceInformation(device);
				ConfigDevice configDevice = new ConfigDevice(
					setup.getProperty("MACHINES_PATH") + deviceInformation.getMachine() +"/"+ deviceInformation.getMachine() 
				);
				if (!a.contains(deviceInformation.getName())) {
					String[] states = configDevice.getProperty("STATES").split("\\,");
					
					JSONObject _component = new JSONObject();
					_component.accumulate("place", place);
					
					JSONObject _costs = new JSONObject();
					JSONObject _connections = new JSONObject();
					
					for (String state : states) {
						_costs.accumulate(state, configDevice.getCostState(state));
						_connections.accumulate(state, configDevice.getValidStates(state));
					}
					
					_component.accumulate("costs", _costs);
					_component.accumulate("connections", _connections);
					
					_components.accumulate(deviceInformation.getName(), _component);
				}
			}
		}
		
		_response.accumulate("components", _components);
		
		
		
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filepathResult + "/runGA.sh", "UTF-8");
			// System.out.println("\n exec file " + filepathResult + "/runGA.sh");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println("python 'GeneticAlgorithm' '"+ filepathResult +"/GA.csv' '" + _response.toString() + "'");
		writer.close();
		
		System.out.println("\nProcess GENETIC ALGORITHM...");
		System.out.println("exec 'sh " + filepathResult + "/runGA.sh'");
		
		try {
			String line;
			Process p = Runtime.getRuntime().exec("sh " + filepathResult + "/runGA.sh");
			
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				System.out.println(line);
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				System.out.println(line);
			}
			bre.close();
			p.waitFor();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error to execute '"+ "sh " + filepathResult + "/runGA.sh" +"'");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("End process GENETIC ALGORITHM...");
		
		Desktop enlace=Desktop.getDesktop();
        try {
        	enlace.open(new File(filepathResult + "/GAProcessed.csv"));
        } catch (IOException e) {
            e.getMessage();
            System.err.println("Error to open " + filepathResult + "/GAProcessed.csv");
        }
                
        System.out.println("\nFile to contains result genetic algorithm: " + filepathResult + "/GAProcessed.csv");
        
        try {
        	Files.copy(new File("template.html").toPath(), new File(filepathResult +"/index.html").toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
        	enlace.open(new File(filepathResult + "/index.html"));
        } catch (IOException e) {
            e.getMessage();
            System.err.println("Error to open " + filepathResult + "/index.html");
        }
        
        System.out.println("\nFile to contains result genetic algorithm: " + filepathResult + "/index.html");
		
		System.exit(0);
    }
    
	@Override
	public void stateChanged(StateChangeEvent e) {
		if (milliseconds > 0) {
			e.getDevice();
			e.getCurrentState();
			
//			System.out.println(e.getDevice());
//			System.out.println(e.getCurrentState());
			
			String[] event = e.getCurrentState().split("\\_"); 
			
			if (states.containsKey(event[0]))
				states.put(event[0], event[1]);
		}
	}
	
//	public void sendEvents() {
//		for (String place : components.getPlaces()) {
//			for (String device : components.getItemsByKey(place)) {
//				DeviceInformation deviceInformation = components.getDeviceInformation(device);
//				ConfigDevice configDevice = new ConfigDevice(
//					setup.getProperty("MACHINES_PATH") + deviceInformation.getMachine() +"/"+ deviceInformation.getMachine() 
//				);
//				validEvents.put(states.get(deviceInformation.getName()), configDevice.getValidStates(states.get(deviceInformation.getName())));
//			}
//		}
//		sendData("{\"method\":\"events\",\"data\":"+ validEvents.toJSON() +"}");
//	}
	
	@Override
	public void onOpen(WebSocket webSocket, ClientHandshake arg1) {
		webSocket.send("{\"method\":\"connected\"}");
		webSocketClient = webSocket;
	}
	
	@Override
	public void onMessage(WebSocket webSocket, String message) {
//		System.out.println("Message: " + message);
		
		JSONObject response = new JSONObject(message);

		switch (response.getString("method")) {
			case "connected":
				webSocket.send("{\"method\":\"stage\",\"data\":" + stage.toJSON() + "}");
				break;
				
			case "start":
				startSimulator();
				break;
				
			case "stop":
				stopSimulator();
				break;
				
			case "state":
				JSONObject newStates = (JSONObject) response.get("data");
				for (Entry<String, String> state : states.entrySet())
					states.put(state.getKey(), newStates.getString(state.getKey()));
				break;
	
			default:
				break;
		}
	}
}
