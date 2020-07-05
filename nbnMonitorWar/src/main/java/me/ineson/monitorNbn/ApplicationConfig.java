package me.ineson.monitorNbn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.ineson.monitorNbn.shared.dao.DailySummaryDao;
import me.ineson.monitorNbn.shared.dao.DatasourceManager;
import me.ineson.monitorNbn.shared.dao.OutageDao;

@Configuration
public class ApplicationConfig {

    private static final Logger LOG = LogManager.getLogger(ApplicationConfig.class);

    @Bean
    public DatasourceManager DatasourceManager(@Value("${mongoDb.url}") String url){
    	LOG.info("---------------------- URL: {}", url);
        return new DatasourceManager(url);
    }

    @Bean DailySummaryDao dailySummaryDao(DatasourceManager datasourceManager) {
    	return new DailySummaryDao(datasourceManager.getDatabase());
    }

    @Bean OutageDao outageDao(DatasourceManager datasourceManager) {
    	return new OutageDao(datasourceManager.getDatabase());
    }

}
