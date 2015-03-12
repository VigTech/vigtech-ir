
package edu.univalle.vigtech_lucene_ir.api;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class JerseyApplication extends ResourceConfig {
    public JerseyApplication() {
    
    	super(MultiPartFeature.class);
    	register(JacksonFeature.class);
      
    }
}


