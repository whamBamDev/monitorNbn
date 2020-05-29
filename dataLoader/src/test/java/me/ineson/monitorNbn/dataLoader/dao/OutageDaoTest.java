/**
 * 
 */
package me.ineson.monitorNbn.dataLoader.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.client.MongoCollection;

import me.ineson.monitorNbn.dataLoader.entity.Outage;

/**
 * @author peter
 *
 */
@DataMongoTest
@ExtendWith(SpringExtension.class)
class OutageDaoTest {

    private static final Logger LOG = LogManager.getLogger(OutageDaoTest.class);

    private DatasourceManager datasourceManager;

    private OutageDao dao;
	
    @Value( "${mongodb.client.url}" )
    private String dbUrl;

    @Value( "${spring.data.mongodb.database}")
    private String dbName;
    
    @BeforeEach
    void setup(@Autowired MongoTemplate mongoTemplate) {
        listDbContents("Before", mongoTemplate);
        datasourceManager = new DatasourceManager(dbUrl, dbName);
//        dao = new OutageDao(mongoTemplate.getDb());
        dao = new OutageDao(datasourceManager.getDatabase());
    }

    @AfterEach
    void shutdown(@Autowired MongoTemplate mongoTemplate) {
        listDbContents("After", mongoTemplate);
    }

    void listDbContents(String comment, MongoTemplate mongoTemplate) {
    	LOG.info("======= {} =======", comment);
    	Set<String>names = mongoTemplate.getCollectionNames();
    	LOG.info("Collections: {}", String.join(",", names));
    	MongoCollection<Document>collection = mongoTemplate.getCollection("Outage");
    	LOG.info("document count: {}", collection.countDocuments());
    	
    	StringBuilder string = new StringBuilder();
    	collection.listIndexes().forEach((Consumer<? super Document>) (Document document) -> {
        	if( string.length() > 0 ) string.append(", ");
        	string.append(document);
    	});
    		
    	
    	LOG.info("documents: {}", collection.find());
    	collection.find().forEach((Consumer<? super Document>) (Document document) -> {
        	LOG.info("Data: {}", document.toJson());
    	});

    	LOG.info("=============================");
    }
    

    @Test
    void testInsertAndFindSuccess(@Autowired MongoTemplate mongoTemplate) {
    	Outage outage = new Outage();
    	
    	LocalDateTime startTime = LocalDateTime.now();
    	LocalDateTime endTime = startTime.plusHours(1);
    	
    	outage.setNumberOfLines(25);
    	outage.setStartFilePosition(12345L);
    	outage.setStartTime(startTime);
    	outage.setEndTime(endTime);
    	
    	Outage newOutage =  dao.add(outage);
    	LOG.info("newOutage: {}", newOutage);
    	
    	Iterable<Outage> searchResults = dao.findAll();
    	assertNotNull(searchResults);
    	Iterator<Outage> searchResultsIterator = searchResults.iterator();

    	Outage returnedOutage =  searchResultsIterator.next();
    	assertNotNull(returnedOutage);
    	assertFalse("Has more than one result", searchResultsIterator.hasNext());
    	LOG.info("returnedOutage: {}", returnedOutage);
        listDbContents("post test", mongoTemplate);
    }

}
