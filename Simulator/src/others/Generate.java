package others;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import utils.Config;
import utils.ConfigComponents;

public class Generate {
	
	Config fileSetup;
	ConfigComponents fileComponents;
	ConfigComponents fileConnections;
	
	List<String> actuators;
	
	public Generate(String setup) {
		fileSetup = new Config(setup);
		fileComponents = new ConfigComponents(fileSetup.getProperty("COMPONENTS_FILE"));
		fileConnections = new ConfigComponents(fileSetup.getProperty("CONNECTIONS_FILE"));
		
		actuators = Arrays.asList(fileSetup.getProperty("ACTUATORS").split("\\,"));
	}
	
	public String header(String initialState) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<!DOCTYPE scxml SYSTEM \"template.dtd\">\n" +
		"<!-- @device=User -->\n" +
		"<scxml xmlns=\"http://www.w3.org/2005/07/scxml\" version=\"1.0\">\n" +
		"	<state id=\"&id;userMachine\">\n" +
		"		<datamodel>\n" +
		"			<data name=\"&id;state\" expr=\"''\"/>\n" +
		"		</datamodel>\n" + 
		"		<initial>\n" + 
		"			<transition target=\"&id;"+ initialState +"\"/>\n" + 
		"		</initial>\n";
	}
	
	public String place(String place, String[] connections, String[] components) {
		
		int nActuators = 0;
		
		for (String component : components) {
			String[] element = component.split("\\:");
			if (actuators.contains(element[1])) {
				nActuators++;
			}
		}
		
		int nTransitions = connections.length + components.length + 1 - nActuators;
		
		String header = "			<state id=\"&id;"+ place +"\">\n" + 
		"	    	<onentry>\n" + 
		"	    		<send sendid=\"&id;decide"+ place +"\" targettype=\"'scxml'\" event=\"'&id;decide"+ place +"'\" delay=\"random.time()\"/>\n" + 
		"	    		<assign name=\"&id;state\" expr=\"random.state("+ nTransitions +")\"/>\n" + 
		"			</onentry>\n"; 
		
		String body = connection(place, place);
		
		for (String connection : connections) {
			body += connection(place, connection);
		}

		for (String component : components) {
			String[] element = component.split("\\:");
			if (!actuators.contains(element[1])) {
				body += component(place, element);
			}
		}
		
		String footer = "	    	<onexit>\n" +
		"				<cancel sendid=\"&id;decide"+ place +"\"/>\n" + 
		"			</onexit>\n" + 
		"	    </state>\n";
		
		return header + body + footer;
	}
	
	public String connection(String place, String placeConnection) {
		return "			<transition event=\"&id;decide"+ place +"\" cond=\"&id;state eq "+ placeConnection +"\" target=\"&id;"+ placeConnection +"\"/>\n"; 
	}
	
	public String component(String place, String[] component) {
		// component[0]: Type: 'Lamp'
		// component[1]: Name: 'lamp1'
		// component[2]: Connection: '[bombillo1_on]'
		
		return "			<transition event=\"&id;decide"+ place +"\" cond=\"&id;state eq "+ place +"\" target=\"&id;"+ place +"\">\n" +
			   					"<send targettype=\"'scxml'\" event=\"'&id;"+ component[1] +"_' + states.getEvent('"+ component[1] +"')\"/>\n" +
			   "			</transition>\n";
	}
	
	public String footer() {
		return "	</state>" + 
			"</scxml>";
	}
	
	public String load() {
		String body = "";
		
		for (String place : fileConnections.getPlaces()) {
			System.out.println(place);
			
			
			String[] connections = ((String) fileConnections.get(place)).split("\\,");
			String[] components = ((String) fileComponents.get(place)).split("\\,");
			
			for (String c : connections) {
				System.out.println("\t" + c);
			}
			
			System.out.println("\t");
			
			for (String c : components) {
				System.out.println("\t" + c);
			}
			
			System.out.println("\t\t" + components[0]);
			
			body += place(place, connections, components[0].equals("") ? new String[0] : components);
		}
		
		String file = header(fileSetup.getProperty("INITIAL_STATE")) + body + footer();
		
		System.out.println(file);
		
		return file;
	}
	
	public void write(String pathfile) {
		String file = load();
		
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			fw = new FileWriter(pathfile);
			bw = new BufferedWriter(fw);
			bw.write(file);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		pathfile = pathfile.replaceAll("\\.xml", "");
		
		file = "NAME=User\n" +
			   "STATES=" + String.join(",", fileConnections.getPlaces()) + "\n" +
			   "STATE_INITIAL=" + fileSetup.getProperty("INITIAL_STATE");
		
		try {
			fw = new FileWriter(pathfile);
			bw = new BufferedWriter(fw);
			bw.write(file);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
