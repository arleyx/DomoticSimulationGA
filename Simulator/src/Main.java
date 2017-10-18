
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import others.Generate;
import simulator.Simulator;
import utils.Config;
import utils.ConfigComponents;

import processing.core.PApplet;
import processing.core.PImage;

public class Main extends PApplet {
	
	public static void main(String[] args) {
		try {
			if (args.length < 1) 
				throw new Exception("Invalid number of arguments.");
		} catch (Exception e) {
			System.err.println(help());
			System.exit(1);
		}
		
		if (args[0].equals("-l")) {
			System.out.println(printDevices());
			System.exit(0);
		}
		
		if (args[0].equals("-f")) {
			Generate generator = new Generate(args[1]);
			if (args.length > 2) generator.write(args[2]); else generator.load();
			System.exit(0);
		}

		PApplet.main(Main.class, args);
	}

	public static String help() {
		return "Help:\n\t<Archivo de configuración>\n"+
			   "\t-l Lista de maquinas\n" +
			   "\t-f <setup> [<path file export XML, include '.xml'>] Generar file";
	}
	
	public static String printDevices() {
		String list = "Devices:";
		BufferedReader br;
		
		try {
			br = new BufferedReader(new FileReader("list"));
			String device = "";
			while ((device = br.readLine()) != null)
				list += "\n\t- " + device;
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		
		return list;  
	}
	
	Simulator simulator;
	PImage imgActuator;
	
	public void init() {
		Config setup = new Config(args[0]);
		ConfigComponents components = new ConfigComponents(setup.getProperty("COMPONENTS_FILE"));
		
    	simulator = new Simulator(setup, components, this);
	}
	
	public void settings() {
        size(800, 600);
    }

    public void setup() {
    	surface.setResizable(true);
    	
    	init();
    	
        background(50);
        textFont(createFont("Arial", 15));

        simulator.setupStage();
        simulator.start();
    }

    public void draw() {
    	simulator.buildStage();
    }
}
