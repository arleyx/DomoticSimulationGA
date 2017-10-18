package graphic;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import utils.Config;
import utils.ConfigComponents;
import utils.ConfigDevice;
import utils.DeviceInformation;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import simulator.State;

public class Stage {
	PApplet parent;
	
	Config setup;
	ConfigComponents config;
	LinkedHashMap<String, String[]> actuators;
	LinkedHashMap<String, LinkedHashMap<String, String[]>> places;
	
	public Stage(PApplet p, Config s, ConfigComponents c) {
		parent = p;
		setup = s;
		config = c;
		
		actuators = new LinkedHashMap<String, String[]>();
		places = new LinkedHashMap<String, LinkedHashMap<String, String[]>>();
		
		load();
	}
	
	public void load() {
		List<String> a = Arrays.asList(setup.getProperty("ACTUATORS").split("\\,"));
		
		for (String place : config.getPlaces()) {
			System.out.println("\t" + place);
			
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
			}
			places.put(place, c);
		}
	}

	PFont normal;
	PFont bold;
	
//	PImage imgActuator;
	
	public void setup() {
//		imgActuator = parent.loadImage("assets/actuator.png");
		
		normal = parent.createFont("Arial", 15);
		bold = parent.createFont("Arial Bold", 15);
	}
	
	public void build(State states) {
//		parent.background(-1);
//		for (int i = 0; i < 10; i++) {
//			for (int j = 0; j < 10; j++) {
//				int index = i + j * 10;
//				parent.rect(i * 50, j * 50, 50, 50);
//				parent.pushStyle();
//				parent.fill(255, 0, 0);
//				parent.text(index, i * 50 + 20, j * 50 + 20);
//				parent.popStyle();
//			}
//		}
		int nCols = 2;
		int nRows = (places.size() / nCols) + (places.size() % 2 == 0 ? 0 : 1);
		
		int nFieldsPaint = 0;
		
		int x = 0, y = 0, placesWidth = parent.width / nCols, placesHeight = parent.height / nRows;
		
		for (Entry<String, LinkedHashMap<String, String[]>> place : places.entrySet()) {
			buildPlace(x, y, placesWidth, placesHeight, place.getKey());
			
			int x1 = x + 20, y1 = y + 25;
			for (Entry<String, String[]> components : place.getValue().entrySet()) {
				buildComponent(x1, y1, components.getKey(), components.getValue(), states.get(components.getKey()));
				y1 += 25;
			}
			
//			int x2 = x + placesWidth - (imgActuator.width / 2) - 10;
//			int y2 = y + parent.height - (imgActuator.height / 2) - 25;
			
			int x2 = x + 10, y2 = y + placesHeight - 50;
			for (String actuator : setup.getProperty("ACTUATORS").split("\\,")) {
				if (states.get(actuator).equals(place.getKey())) {
					buildActuator(x2, y2, 0, 0, actuator);
					//x2 -= (imgActuator.width / 2) + 10;
					y2 -= 25;
				}
			}
			
			x += placesWidth;
			nFieldsPaint++;
			
			if (nFieldsPaint % nCols == 0) {
				x = 0;
				y += placesHeight;
			}
			
		}
	}
	
	public void buildPlace(int x, int y, int w, int h, String n) {
		parent.fill(240);
		parent.rect(x, y, w, h);
		parent.fill(0);
		parent.textFont(bold);
		parent.text(n, x + 10, y + h - 10);
	}
	
	public void buildComponent(int x, int y, String n, String[] s, String si) {		
		parent.fill(200);
		parent.ellipse(x - 10, y - 5, 10, 10);
		parent.fill(0);
		parent.textFont(bold);
		parent.text(n + ": ", x, y);
		
		float size = x + parent.textWidth(n + ": ");
		
		parent.textFont(normal);
		for (String state : s) {
			if (state.equals(si)) parent.fill(115, 230, 0); else parent.fill(255, 77, 77);
			state = state + " ";
			parent.rect(size - 2, y - 15, parent.textWidth(state) + 2, 20, 3);
			parent.fill(0);
			parent.text(state, size, y);
			size += parent.textWidth(state) + 5;
		}
			
	}
	
	public void buildActuator(int x, int y, int w, int h, String n) {
		parent.fill(255, 255, 154);
//		parent.image(imgActuator, x, y, w, h);
		parent.textFont(bold);
		parent.rect(x - 2, y + h, parent.textWidth(n) + 4, 20, 3);
		parent.fill(0);
		parent.text(n, x, y + h + 15);
	}
}
