package eu.recred.fidouafsvc.configuration;

import eu.recred.fidouafsvc.model.FidoConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by sorin.teican on 8/30/2016.
 */
@Configuration
@ComponentScan({ "eu.recred.fidouafsvc.configuration" })
public class OtherConfig {

    @Bean
    public FidoConfig fidoConfig() {
        return new FidoConfig();
    }
}
