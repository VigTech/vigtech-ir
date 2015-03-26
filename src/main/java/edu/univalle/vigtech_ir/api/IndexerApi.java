
package edu.univalle.vigtech_ir.api;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
//import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
//import org.glassfish.jersey.media.multipart.FormDataParam;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import edu.univalle.vigtech_ir.Indexer;
import edu.univalle.vigtech_ir.Searcher;
import edu.univalle.vigtech_ir.utils.FileUtils;
import edu.univalle.vigtech_ir.utils.Index;
import edu.univalle.vigtech_ir.utils.IndexUtils;
import edu.univalle.vigtech_ir.utils.Match;
import edu.univalle.vigtech_ir.utils.PropertiesManager;

@Path("/")
public class IndexerApi {

	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Index> getIndexList() {
      IndexUtils utils =  new IndexUtils();
      return utils.getIndexList();
    }

    @Path("{index_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Index getIndexInfo(@PathParam("index_id") String id) {
    	Indexer indexer =  new Indexer();
    	return indexer.getIndex(id);
    	
    }
    
    @Path("{index_id}/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Match> search(@PathParam("index_id") String id,@QueryParam("query") String query ) {
    	
    	Searcher searcher = new Searcher();
    	String indexPath = PropertiesManager.getInstance().getProperty("index.path")+id;
    
       return searcher.Buscador(query, indexPath);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Index createIndex(@FormDataParam("upload") InputStream uploadedInputStream,
    						 @FormDataParam("upload") FormDataContentDisposition fileDetail) {
 
       String tempPath = PropertiesManager.getInstance().getProperty("temp.path");
       String repoPath = PropertiesManager.getInstance().getProperty("repository.path");
       String indexPath = PropertiesManager.getInstance().getProperty("index.path");
       
       String zipfile = tempPath + fileDetail.getFileName();
       FileUtils fileUtils = new FileUtils();
       fileUtils.writeToFile(uploadedInputStream, zipfile);
       String collectionPath = fileUtils.unZipFile(zipfile, repoPath);
       
       String indexFilePath = indexPath + fileDetail.getFileName().replace(".zip", "/");
       File indexDir = new File(indexFilePath);
       if(!indexDir.exists())
    	   indexDir.mkdir();
       Indexer indexer =  new Indexer();
       Index index = indexer.indexar(indexFilePath , collectionPath);
       
       
       return index;
    }
    
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Index updateIndex(@PathParam("id") String id) {
       Logger.getLogger(getClass()).info("Working???");
       return new Index();
    }
    
}

