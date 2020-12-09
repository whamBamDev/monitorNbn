package me.ineson.monitor_nbn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import me.ineson.monitor_nbn.shared.dao.DailySummaryDao;
import me.ineson.monitor_nbn.shared.dao.DatasourceManager;
import me.ineson.monitor_nbn.shared.dao.OutageDao;
import me.ineson.monitor_nbn.thymeleaf.UtilitiesDialect;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class ApplicationConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger LOG = LogManager.getLogger(ApplicationConfig.class);

    @Bean(destroyMethod = "close")
    public DatasourceManager DatasourceManager(@Value("${mongoDb.url}") String url){
    	LOG.info("Mongo Db URL: {}", url);
        return new DatasourceManager(url);
    }

    @Bean DailySummaryDao dailySummaryDao(DatasourceManager datasourceManager) {
    	return new DailySummaryDao(datasourceManager.getDatabase());
    }

    @Bean OutageDao outageDao(DatasourceManager datasourceManager) {
    	return new OutageDao(datasourceManager.getDatabase());
    }

    @Bean
    public UtilitiesDialect utilitiesDialect() {
        return new UtilitiesDialect();
    }
    
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
      config.enableSimpleBroker("/topic");
      config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
      registry.addEndpoint("/portfolio").withSockJS();
    }
    
}
