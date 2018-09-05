
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Scanner;

import others.Generate;
import others.GenerateRandom;
import simulator.Simulator;
import utils.Config;
import utils.ConfigComponents;


public class Main {
	
	public static void main(String[] args) {
//		try {
//			if (args.length < 1) 
//				throw new Exception("Invalid number of arguments.");
//		} catch (Exception e) {
//			System.err.println(help());
//			System.exit(1);
//		}
		
		if (args.length < 1) {
			
			Scanner reader = new Scanner(System.in);
			
			System.out.print("\nEnter a scenario number (1-5):");
			int nStage = reader.nextInt();
			
			System.out.print("\nEnter a case number (1-3):");
			int nCase = reader.nextInt();
			
			String url = "Stages/stage"+ nStage +"/case"+ nCase +"/setup";
			
			System.out.println("\n\nUrl scenary: " + url);
			
			Config setup = new Config(url);
			ConfigComponents components = new ConfigComponents(setup.getProperty("COMPONENTS_FILE"));
			
			System.out.println("Url client: Client/dist/index.html");
			
			try {
				new Simulator(setup, components).start();
				
		        Desktop enlace=Desktop.getDesktop();
		        try {
		        	enlace.open(new File("Client/dist/index.html"));
		        } catch (IOException e) {
		            e.getMessage();
		        }
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
		} else {
			
			if (args[0].equals("-h")) {
				System.err.println(help());
				System.exit(0);
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
			
			if (args[0].equals("-fr")) {
				GenerateRandom generator = new GenerateRandom(args[1]);
				if (args.length > 2) generator.write(args[2]); else generator.load();
				System.exit(0);
			}
			
			Config setup = new Config(args[0]);
			ConfigComponents components = new ConfigComponents(setup.getProperty("COMPONENTS_FILE"));
			
			try {
				new Simulator(setup, components).start();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}

	public static String help() {
		return "Help:\n\t<Archivo de configuración>\n"+
			   "\t-l Lista de maquinas\n" +
			   "\t-f <setup> [<path file export XML, include '.xml'>] Generar file\n" +
			   "\t-fr <setup> [<path file export XML, include '.xml'>] Generar file random\n";
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
}
