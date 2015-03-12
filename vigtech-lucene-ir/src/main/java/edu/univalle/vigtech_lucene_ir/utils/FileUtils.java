package edu.univalle.vigtech_lucene_ir.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public class FileUtils {
	
	
	// save uploaded file to new location
		public void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {
	 
			try {
				OutputStream out = new FileOutputStream(new File(
						uploadedFileLocation));
				int read = 0;
				byte[] bytes = new byte[1024];
				
				File uploadedFile = new File(uploadedFileLocation);
				
				out = new FileOutputStream(uploadedFile);
				while ((read = uploadedInputStream.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.flush();
				out.close();
			} catch (IOException e) {
	 
				e.printStackTrace();
			}
	 
		}
		
		public String unZipFile(String source, String destination){
		
			File directory = null;
			String unzipedPath = "";
			
		    try {
		        
		    	ZipFile zipFile = new ZipFile(source);
		    	FileHeader header = (FileHeader) zipFile.getFileHeaders().get(0);
		    	unzipedPath = destination + zipFile.getFile().getName().replace(".zip", "/");
		    	
		    	if(header.isDirectory())
		    	{
		    		directory = new File(destination);
		    	}
		    	else
		    	{
		    		directory = new File(unzipedPath);

		    	}
		         
		         if(!directory.exists())
		        	 directory.mkdir();
		        
		         zipFile.extractAll(directory.getPath());
		        	 
		    } catch (ZipException e) {
		        e.printStackTrace();
		    }
		    finally{
		    	new File(source).delete();
		    }
		    
		    return unzipedPath;
			
		}

}
