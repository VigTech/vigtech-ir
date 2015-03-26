
package edu.univalle.vigtech_ir.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import edu.univalle.vigtech_ir.utils.ApiVersion;


@Path("/version")
public class Root {
    
    /** Method processing HTTP GET requests, producing "text/plain" MIME media
     * type.
     * @return String that will be send back as a response of type "text/plain".
     */
    @GET 
    @Produces("application/json")
    public ApiVersion root() {
    	
        return new ApiVersion("VigTech - Lucene Information Retrieval API", "0.0.0");
        
    }
}
