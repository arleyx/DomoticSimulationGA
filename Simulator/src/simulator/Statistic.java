package simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import utils.Config;
import utils.ConfigComponents;
import utils.ConfigDevice;
import utils.DeviceInformation;
import utils.Result;

public class Statistic {
	
	private Config setup;
	private ConfigComponents components;
	private Result result;
	private LinkedHashMap<String, LinkedHashMap<String, ArrayList<Integer>>> accountants;
	
	public Statistic(Config setup, ConfigComponents components, Result result) {
		this.setup = setup;
		this.components = components;
		this.result = result;
		
		accountants = new LinkedHashMap<String, LinkedHashMap<String, ArrayList<Integer>>>();
		
		initAccountants();
		processResult();
	}

	/*private void initAccountants() {
		for (String place : components.getPlaces()) {
			String[] devices = components.getItemsByKey(place);
			for (String device : devices) {
				DeviceInformation deviceInformation = components.getDeviceInformation(device);
				accountants.put(deviceInformation.getName(), new LinkedHashMap<String, Integer>());
			}
		}
	}*/
	
	private void initAccountants() {
		for (String place : components.getPlaces()) {
			String[] devices = components.getItemsByKey(place);
			for (String device : devices) {
				DeviceInformation deviceInformation = components.getDeviceInformation(device);
				ConfigDevice configDevice = new ConfigDevice(
					setup.getProperty("MACHINES_PATH") + deviceInformation.getMachine() +"/"+ deviceInformation.getMachine() 
				);
				String[] states = configDevice.getProperty("STATES").split("\\,");
				LinkedHashMap<String, ArrayList<Integer>> statesAccountants = new LinkedHashMap<String, ArrayList<Integer>>();
				for (String state : states) {
					ArrayList<Integer> values = new ArrayList<Integer>();
					values.add(0, 0);
					values.add(1, 0);
					values.add(2, 0);
					statesAccountants.put(state, values);
				}
				accountants.put(deviceInformation.getName(), statesAccountants);
			}
		}
	}
	
	private void processStatistic(String[] states, String[] components) {
		for (int i = 1; i < states.length; i++) {	
			LinkedHashMap<String, ArrayList<Integer>> statesAccountants = accountants.get(components[i]);
			if (accountants.get(components[i]).containsKey(states[i])) {
				ArrayList<Integer> valuesAccountants = accountants.get(components[i]).get(states[i]);
				
				int nActuatorsInPlace = 0;
				for (String actuator : setup.getProperty("ACTUATORS").split("\\,")) {
					int key = Arrays.asList(components).indexOf(actuator);
					if (this.components.getPlaceByDevice(components[i]).equals(states[key]))
						nActuatorsInPlace++;
				}
				
				valuesAccountants.set(0, valuesAccountants.get(0) + 1);
				if (nActuatorsInPlace > 0) valuesAccountants.set(1, valuesAccountants.get(1) + 1);
				if (nActuatorsInPlace < 1) valuesAccountants.set(2, valuesAccountants.get(2) + 1);
				statesAccountants.put(states[i], valuesAccountants);
				accountants.put(components[i], statesAccountants);
			} else {
				ArrayList<Integer> valuesAccountants = new ArrayList<Integer>();
				valuesAccountants.add(0, 0);
				valuesAccountants.add(1, 0);
				valuesAccountants.add(2, 0);
				statesAccountants.put(states[i], valuesAccountants);
				accountants.put(components[i], statesAccountants);
			}
		}
	}
	
	public void processResult() {
		FileReader fileReader;
		BufferedReader bufferedReader;
		
        try {
        	fileReader = new FileReader(result.getPath() + result.getName());
			bufferedReader = new BufferedReader(fileReader);
			
        	String[] components = bufferedReader.readLine().split(",");
        	String line = "";
			while((line = bufferedReader.readLine()) != null) {
				if (!line.isEmpty()) {
					String[] states = line.split(",");
					processStatistic(states, components);
				}
			}
		} catch (IOException e) {
			System.err.println("Error in file to process results.");
			e.printStackTrace(System.err);
		}
	}
	
	public String getTimeByStates() {
		String toString = "\n\nTiempo total (ms):\n\nCOMPONENTES\n";
		for (Entry<String, LinkedHashMap<String, ArrayList<Integer>>> component : accountants.entrySet()) {
			toString += component.getKey();
			for (Entry<String, ArrayList<Integer>> state : component.getValue().entrySet()) {
				int milliseconds = state.getValue().get(0) * Integer.parseInt(setup.getProperty("DELAY")); 
				toString += ","+ state.getKey() +"="+ milliseconds + "ms";
			}
			toString += "\n";
		}
		
		return toString;
	}
	
	public void chartTimeByStates(String path) {
		path = path.charAt(path.length() - 1) == '/' ? path : path + "/";
		
		DefaultPieDataset dataset = new DefaultPieDataset( );
		dataset.setValue("IPhone 5s", new Double( 20 ) );
		dataset.setValue("SamSung Grand", new Double( 20 ) );
		dataset.setValue("MotoG", new Double( 40 ) );
		dataset.setValue("Nokia Lumia", new Double( 10 ) );
		
		PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator("{0} : {1}");
		
		JFreeChart chart = ChartFactory.createPieChart(
		"chartTimeByStates",   // chart title
		dataset,          // data
		true,             // include legend
		true,
		true);
		
		PiePlot plot1 = (PiePlot) chart.getPlot();
        plot1.setLabelGenerator(labelGenerator);
		
		int width = 640;
		int height = 480; 
		File pieChart = new File(path + "chartTimeByStates.jpg");
		try {
			ChartUtilities.saveChartAsJPEG(pieChart, chart, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getTimeOptimalByStates() {
		String toString = "\n\nTiempo optimo (ms):\n\nCOMPONENTES\n";
		for (Entry<String, LinkedHashMap<String, ArrayList<Integer>>> component : accountants.entrySet()) {
			toString += component.getKey();
			for (Entry<String, ArrayList<Integer>> state : component.getValue().entrySet()) {
				int milliseconds = state.getValue().get(1) * Integer.parseInt(setup.getProperty("DELAY")); 
				toString += ","+ state.getKey() +"="+ milliseconds + "ms";
			}
			toString += "\n";
		}
		
		return toString;
	}
	
	public String getTimeFailedByStates() {
		String toString = "\n\nTiempo fatal (ms):\n\nCOMPONENTES\n";
		for (Entry<String, LinkedHashMap<String, ArrayList<Integer>>> component : accountants.entrySet()) {
			toString += component.getKey();
			for (Entry<String, ArrayList<Integer>> state : component.getValue().entrySet()) {
				int milliseconds = state.getValue().get(2) * Integer.parseInt(setup.getProperty("DELAY")); 
				toString += ","+ state.getKey() +"="+ milliseconds + "ms";
			}
			toString += "\n";
		}
		
		return toString;
	}
	
	/*public String getPowerByStates() {
		String toString = "\n\nElectricidad (ms):\n\nCOMPONENTES\n";
		for (Entry<String, LinkedHashMap<String, Integer>> component : accountants.entrySet()) {
			toString += component.getKey();
			for (Entry<String, Integer> state : component.getValue().entrySet()) {
				ConfigDevice configDevice = new ConfigDevice(
					setup.getProperty("MACHINES_PATH") + component.getKey() +"/"+ component.getKey()
				);
				
				int milliseconds = state.getValue() * Integer.parseInt(setup.getProperty("DELAY"));
				int watts = milliseconds * (Integer.parseInt(configDevice.getProperty("STATE."+ state.getKey())) / Integer.parseInt(setup.getProperty("WATTS_MILLISECONDS")));
				
				toString += ","+ state.getKey() +"="+ (state.getValue() * Integer.parseInt(setup.getProperty("WATTS_MILLISECONDS"))) + "w";
			}
			toString += "\n";
		}
		
		return toString;
	}*/
}
