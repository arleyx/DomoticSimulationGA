package utils;

import java.io.FileWriter;
import java.io.IOException;

public class Result {
	private String path;
	private String name;
	
	public Result(String path, String name) {
		this.path = path.charAt(path.length() - 1) == '/' ? path : path + "/";
		this.name = name;
	}
	
	public void write(String text) {
		try {
			FileWriter fileWriter = new FileWriter(path + name);
			fileWriter.write(text);
			fileWriter.close();
		} catch (IOException e) {
			System.err.println("Error in file to results.");
			e.printStackTrace(System.err);
		}
	}
	
	public void write(String text, boolean append) {
		try {
			FileWriter fileWriter = new FileWriter(path + name, append);
			fileWriter.write(text);
			fileWriter.close();
		} catch (IOException e) {
			System.err.println("Error in file to results.");
			e.printStackTrace(System.err);
		}
	}

	public String getPath() {
		return path;
	}
	
	public String getPathName() {
		return path + name;
	}
	
	public String getName() {
		return name;
	}

	public void setPath(String path) {
		this.path = path.charAt(path.length() - 1) == '/' ? path : path + "/";
	}

	public void setName(String name) {
		this.name = name;
	}
}
