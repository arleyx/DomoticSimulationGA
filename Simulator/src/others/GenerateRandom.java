package others;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import utils.Config;
import utils.ConfigComponents;

public class GenerateRandom {
	
	Config fileSetup;
	ConfigComponents fileComponents;
	ConfigComponents fileConnections;
	
	List<String> actuators;
	
	public GenerateRandom(String setup) {
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
		
		String body = connection(place, place, 0);
		
		int selector = 1;
		for (String connection : connections) {
			body += connection(place, connection, selector);
			selector++;
		}

		for (String component : components) {
			String[] element = component.split("\\:");
			if (!actuators.contains(element[1])) {
				body += component(place, element, selector);
				selector++;
			}
		}
		
		String footer = "	    	<onexit>\n" +
		"				<cancel sendid=\"&id;decide"+ place +"\"/>\n" + 
		"			</onexit>\n" + 
		"	    </state>\n";
		
		return header + body + footer;
	}
	
	public String connection(String place, String placeConnection, int selector) {
		return "			<transition event=\"&id;decide"+ place +"\" cond=\"&id;state eq "+ selector +"\" target=\"&id;"+ placeConnection +"\"/>\n"; 
	}
	
	public String component(String place, String[] component, int selector) {
		// component[0]: Type: 'Lamp'
		// component[1]: Name: 'lamp1'
		// component[2]: Connection: '[bombillo1_on]'
		
		return "			<transition event=\"&id;decide"+ place +"\" cond=\"&id;state eq "+ selector +"\" target=\"&id;"+ place +"\">\n" +
			   					callMethod(component[0], component[1]) + "\n" +
			   "			</transition>\n";
	}
	
	public String callMethod(String name, String param) {
		Class[] paramString = new Class[1];
		paramString[0] = String.class;
		
		Method method;
		String response = "";
		
		try {
			method = this.getClass().getDeclaredMethod(name, paramString);
			
			try {
				response = (String) method.invoke(this, param);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
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
	
	public String Lamp(String id) {
		return "<if cond=\"states.get('"+ id +"') eq 'offState'\">\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"<else/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_off'\"/>\n" + 
				"</if>";
	}
	
	public String Refrigerator(String id) {
		return "<if cond=\"states.get('"+ id +"') eq 'onlineState'\">\n" + 
				"	<if cond=\"random.state(2) eq 0\">\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_open'\"/>\n" + 
				"	<else/>\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_close'\"/>\n" + 
				"	</if>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'openState'\"/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_close'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'closeState'\"/>\n" + 
				"	<if cond=\"random.state(2) eq 0\">\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_open'\"/>\n" + 
				"	<else/>\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_offline'\"/>\n" + 
				"	</if>\n" + 
				"<else/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_online'\"/>\n" + 
				"</if>";
	}
	
	public String Microwave(String id) {
		return "<if cond=\"states.get('"+ id +"') eq 'onlineState'\">\n" + 
				"	<if cond=\"random.state(2) eq 0\">\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_open'\"/>\n" + 
				"	<else/>\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_close'\"/>\n" + 
				"	</if>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'openState'\"/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_close'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'closeState'\"/>\n" + 
				"	<if cond=\"random.state(2) eq 0\">\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_open'\"/>\n" + 
				"	<else/>\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_offline'\"/>\n" + 
				"	</if>\n" + 
				"<else/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_online'\"/>\n" + 
				"</if>";
	}
	
	public String Tv(String id) {
		return "<if cond=\"states.get('"+ id +"') eq 'onlineState'\">\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'onState'\"/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_off'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'offState'\"/>\n" + 
				"	<if cond=\"random.state(2) eq 0\">\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"	<else/>\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_offline'\"/>\n" + 
				"	</if>\n" + 
				"<else/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_online'\"/>\n" + 
				"</if>";
	}
	
	public String Stereo(String id) {
		return "<if cond=\"states.get('"+ id +"') eq 'onlineState'\">\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'onState'\"/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_off'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'offState'\"/>\n" + 
				"	<if cond=\"random.state(2) eq 0\">\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"	<else/>\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_offline'\"/>\n" + 
				"	</if>\n" + 
				"<else/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_online'\"/>\n" + 
				"</if>";
	}
	
	public String Blender(String id) {
		return "<if cond=\"states.get('"+ id +"') eq 'onlineState'\">\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'onState'\"/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_off'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'offState'\"/>\n" + 
				"	<if cond=\"random.state(2) eq 0\">\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"	<else/>\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_offline'\"/>\n" + 
				"	</if>\n" + 
				"<else/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_online'\"/>\n" + 
				"</if>";
	}
	
	public String Pc(String id) {
		return "<if cond=\"states.get('"+ id +"') eq 'onlineState'\">\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'onState'\"/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_off'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'offState'\"/>\n" + 
				"	<if cond=\"random.state(2) eq 0\">\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"	<else/>\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_offline'\"/>\n" + 
				"	</if>\n" + 
				"<else/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_online'\"/>\n" + 
				"</if>";
	}
	
	public String Charger(String id) {
		return "<if cond=\"states.get('"+ id +"') eq 'offlineState'\">\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_online'\"/>\n" + 
				"<else/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_offline'\"/>\n" + 
				"</if>";
	}
	
	public String Washing(String id) {
		return "<if cond=\"states.get('"+ id +"') eq 'onlineState'\">\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'onState'\"/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_off'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'offState'\"/>\n" + 
				"	<if cond=\"random.state(2) eq 0\">\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"	<else/>\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_offline'\"/>\n" + 
				"	</if>\n" + 
				"<else/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_online'\"/>\n" + 
				"</if>";
	}
	
	public String Iron(String id) {
		return "<if cond=\"states.get('"+ id +"') eq 'onlineState'\">\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'onState'\"/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_off'\"/>\n" + 
				"<elseif cond=\"states.get('"+ id +"') eq 'offState'\"/>\n" + 
				"	<if cond=\"random.state(2) eq 0\">\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_on'\"/>\n" + 
				"	<else/>\n" + 
				"		<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_offline'\"/>\n" + 
				"	</if>\n" + 
				"<else/>\n" + 
				"	<send targettype=\"'scxml'\" event=\"'&id;"+ id +"_online'\"/>\n" + 
				"</if>";
	}
}
