/**
 * 
 */
package me.ineson.monitorNbn.dataLoader.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
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

import me.ineson.monitorNbn.dataLoader.entity.DailySummary;

/**
 * @author peter
 *
 */
@DataMongoTest
@ExtendWith(SpringExtension.class)
class DailySummaryDaoTest {

    private static final Logger LOG = LogManager.getLogger(DailySummaryDaoTest.class);

    private DatasourceManager datasourceManager;

    private DailySummaryDao dao;
	
    @Value( "${mongodb.client.url}" )
    private String dbUrl;

    @Value( "${spring.data.mongodb.database}")
    private String dbName;
    
    @BeforeEach
    void setup(@Autowired MongoTemplate mongoTemplate) {
    	mongoTemplate.dropCollection(DailySummaryDao.COLLECTION_NAME);
        listDbContents("Before", mongoTemplate);
        datasourceManager = new DatasourceManager(dbUrl, dbName);
//        dao = new OutageDao(mongoTemplate.getDb());
        dao = new DailySummaryDao(datasourceManager.getDatabase());
    }

    @AfterEach
    void shutdown(@Autowired MongoTemplate mongoTemplate) {
        listDbContents("After", mongoTemplate);
    }

    void listDbContents(String comment, MongoTemplate mongoTemplate) {
    	LOG.info("======= {} =======", comment);
    	Set<String>names = mongoTemplate.getCollectionNames();
    	LOG.info("Collections: {}", String.join(",", names));
    	MongoCollection<Document>collection = mongoTemplate.getCollection(DailySummaryDao.COLLECTION_NAME);
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
        // Given: record to save
        DailySummary dailySummary = new DailySummary();
        final LocalDate date = LocalDate.now();
        dailySummary.setDate(date);
        dailySummary.setDatafile("test/file.todate.date");
        dailySummary.setOutageCount(4);;

        // When: Record is saved
        DailySummary newRecord =  dao.add(dailySummary);
        LOG.info("Save record Outage: {}", newRecord);

        // Then: Record is saved with and id.
        assertNotNull(newRecord);
        assertNotNull(newRecord.getId());

        //     and record can be read from the DB
        Iterable<DailySummary> searchResults = dao.findAll();
        assertNotNull(searchResults);
        Iterator<DailySummary> searchResultsIterator = searchResults.iterator();

        //     and record can be read from the DB
        DailySummary returnedDailySummary =  searchResultsIterator.next();
        assertNotNull(returnedDailySummary);
        assertFalse("Has more than one result", searchResultsIterator.hasNext());

        //     and record contents match.
        assertEquals(date, returnedDailySummary.getDate());
        assertEquals("test/file.todate.date", returnedDailySummary.getDatafile());
        assertEquals(Integer.valueOf(4), returnedDailySummary.getOutageCount());
        
        LOG.info("returnedOutage: {}", returnedDailySummary);
        listDbContents("post test", mongoTemplate);
    }

    @Test
    void testInsertAndUpdateSuccess(@Autowired MongoTemplate mongoTemplate) {
        // Given: record to save
        DailySummary dailySummary = new DailySummary();
        final LocalDate date = LocalDate.now();
        dailySummary.setDate(date);
        dailySummary.setDatafile("test/file.todate.date");
        dailySummary.setOutageCount(4);;

        //    and record is saved
        DailySummary newRecord =  dao.add(dailySummary);
        LOG.info("Save record Outage: {}", newRecord);

        //    and record is saved with and id.
        assertNotNull(newRecord);
        assertNotNull(newRecord.getId());

        // When: change the outage count and update
        newRecord.setOutageCount(22);
        long count = dao.update(newRecord);
        
        // Then: one record is updated
        assertEquals(1L, count);

        //     and the updated record can be read from the DB
        Iterable<DailySummary> searchResults = dao.findAll();
        assertNotNull(searchResults);
        Iterator<DailySummary> searchResultsIterator = searchResults.iterator();

        //     and record can be read from the DB
        DailySummary returnedDailySummary =  searchResultsIterator.next();
        assertNotNull(returnedDailySummary);
        assertFalse("Has more than one result", searchResultsIterator.hasNext());

        //     and record contents match.
        assertEquals(date, returnedDailySummary.getDate());
        assertEquals(Integer.valueOf(22), returnedDailySummary.getOutageCount());
        
        LOG.info("returnedOutage: {}", returnedDailySummary);
        listDbContents("post test", mongoTemplate);
    }


}
