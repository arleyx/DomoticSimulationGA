package utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Config extends Properties {
	private static final long serialVersionUID = 1L;
	private String path;
	
	public Config(String path) {
		super();
		this.path = path;
		try { this.load(new FileReader(this.path)); } 
		catch (IOException e) { System.err.println(e); }
	}
	
	public void store() {
		try { this.store(new FileWriter(path),""); }
		catch (IOException e) { System.err.println(e); }
	}
	
	public void store(String path) {
		try { this.store(new FileWriter(path),""); }
		catch (IOException e) { System.err.println(e); }
	}

	public String[] getItemsByKey(String key) {
		return this.getProperty(key).isEmpty() ? new String[0] : this.getProperty(key).split("\\,");
	}
}
