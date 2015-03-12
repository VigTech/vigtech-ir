package edu.univalle.vigtech_lucene_ir.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;


public class PropertiesManager implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6280192507802601766L;
	private static Properties INSTANCE = null;
	
	private PropertiesManager(){}
	
	 private synchronized static void createInstance() {
	        if (INSTANCE == null) { 
	            INSTANCE = new Properties();
	            try {
	 			   
					 INSTANCE.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("vigtech-lucene-ir.properties"));
					    
				   } catch (FileNotFoundException e) {
				    System.out.println("PropertiesManager file not found");
				   } catch (IOException e) {
				    System.out.println("Can't read properties file");
				   }
	        }
	    }
	
	public static Properties getInstance() {
		
		if (INSTANCE == null) createInstance();
	        return INSTANCE;
		
	}
	
	public static void main(String[] args){
		
		System.out.println(PropertiesManager.getInstance().getProperty("repository.dir"));
		
	}
	

}
