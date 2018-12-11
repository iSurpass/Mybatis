package bmatch.tech.hello.web;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig(){
        packages("bmatch.tech.hello.web","bmatch.tech.hello");
        register(MultiPartFeature.class);
    }
}
