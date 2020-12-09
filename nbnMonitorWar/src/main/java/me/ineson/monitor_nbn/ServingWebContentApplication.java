/**
 * 
 */
package me.ineson.monitor_nbn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author peter
 *
 */
@SpringBootApplication(exclude = {
	    MongoAutoConfiguration.class, 
	    MongoDataAutoConfiguration.class
	})
public class ServingWebContentApplication extends SpringBootServletInitializer {

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServingWebContentApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ServingWebContentApplication.class, args);
    }

}
