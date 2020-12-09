/**
 * 
 */
package me.ineson.monitor_nbn.shared.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.client.MongoCollection;

import me.ineson.monitor_nbn.shared.dao.DailySummaryDao;
import me.ineson.monitor_nbn.shared.dao.DatasourceManager;
import me.ineson.monitor_nbn.shared.entity.DailySummary;

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
        dao = new DailySummaryDao(datasourceManager.getDatabase());
    }

    @AfterEach
    void shutdown(@Autowired MongoTemplate mongoTemplate) {
        listDbContents("After", mongoTemplate);
        datasourceManager.close();
    }

    void listDbContents(String comment, MongoTemplate mongoTemplate) {
    	LOG.info("======= {} =======", comment);
    	Set<String>names = mongoTemplate.getCollectionNames();
    	LOG.info("Collections: {}", String.join(",", names));
    	MongoCollection<Document>collection = mongoTemplate.getCollection(DailySummaryDao.COLLECTION_NAME);
    	LOG.info("document count: {}", collection.count());
    	
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
        dailySummary.setOutageCount(4);
        dailySummary.setTestCount(400);

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
        assertEquals(Integer.valueOf(400), returnedDailySummary.getTestCount());
        
        LOG.info("returnedOutage: {}", returnedDailySummary);
        listDbContents("post test", mongoTemplate);
    }

    @Test
    @DisplayName("test update success")
    void testInsertAndUpdateSuccess(@Autowired MongoTemplate mongoTemplate) {
        // Given: record to save
        DailySummary dailySummary = new DailySummary();
        final LocalDate date = LocalDate.now();
        dailySummary.setDate(date);
        dailySummary.setDatafile("test/file.todate.date");
        dailySummary.setOutageCount(4);
        dailySummary.setTestCount(400);

        //    and record is saved
        DailySummary newRecord =  dao.add(dailySummary);
        LOG.info("Save record Outage: {}", newRecord);

        //    and record is saved with and id.
        assertNotNull(newRecord);
        assertNotNull(newRecord.getId());

        // When: change the outage count and update
        newRecord.setOutageCount(22);
        newRecord.setTestCount(1000);
        dao.update(newRecord);

        // Then the updated record can be read from the DB
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
        assertEquals(Integer.valueOf(1000), returnedDailySummary.getTestCount());
        
        LOG.info("returnedOutage: {}", returnedDailySummary);
        listDbContents("post test", mongoTemplate);
    }

    @Test
    @DisplayName("Test findByDate success")
    void testFindByDateSuccess() {

        // Given: Save a couple of record on different dates.
        DailySummary dailySummary = new DailySummary();
        final LocalDate date = LocalDate.now();
        dailySummary.setDate(date);
        dailySummary.setDatafile("test/file.todate.date");
        dailySummary.setOutageCount(4);
        dao.add(dailySummary);

        dailySummary = new DailySummary();
        dailySummary.setDate(date.minusDays(1));
        dailySummary.setDatafile("test2/file.todate.date");
        dailySummary.setOutageCount(2);
        dao.add(dailySummary);

        // When: Find by the date of the first saved record.
        DailySummary searchResult = dao.findByDate(date);

        // Then: Just one record is returned
        assertNotNull(searchResult);

        //     and the return record is the first one that was saved.
        LOG.debug("returnedOutage: {}", searchResult);
        assertEquals(Integer.valueOf(4), searchResult.getOutageCount());
    }

    @Test
    @DisplayName("Test findByDate not found")
    void testFindByDateNotFound() {

        // Given: Save a record.
        DailySummary dailySummary = new DailySummary();
        final LocalDate date = LocalDate.now();
        dailySummary.setDate(date);
        dailySummary.setDatafile("test/file.todate.date");
        dailySummary.setOutageCount(4);
        dao.add(dailySummary);

        // When: Find by the date one day prior to the saved record.
        DailySummary searchResult = dao.findByDate(date.minusDays(1));

        // Then: Record not found.
        assertNull(searchResult);
    }

    @Test
    @DisplayName("Test deleteByDate success")
    void testDeleteByDateSuccess() {

        // Given: Save a couple of record on different dates.
        DailySummary dailySummary = new DailySummary();
        final LocalDate date = LocalDate.now();
        dailySummary.setDate(date);
        dailySummary.setDatafile("test/file.todate.date");
        dailySummary.setOutageCount(4);
        dao.add(dailySummary);

        dailySummary = new DailySummary();
        dailySummary.setDate(date.minusDays(1));
        dailySummary.setDatafile("test/file2.todate.date2");
        dailySummary.setOutageCount(1);
        dao.add(dailySummary);

        // When: Find by the date of the first saved record.
        long deleteCount = dao.delete(date);

        // Then: Just one record is deleted
        assertEquals(1L, deleteCount);

        // and the second saved record has not been deleted.
        Iterable<DailySummary> searchResults = dao.findAll();
        assertNotNull(searchResults);
        Iterator<DailySummary> searchResultsIterator = searchResults.iterator();
        DailySummary returnedRecord = searchResultsIterator.next();
        assertNotNull(returnedRecord);
        assertFalse("Has more than one result", searchResultsIterator.hasNext());
        assertEquals(Integer.valueOf(1), returnedRecord.getOutageCount());
    }

    @Test
    @DisplayName("Test deleteByDate not found")
    void testDeleteByDateNotFound() {

        // Given: Save a record.
        DailySummary dailySummary = new DailySummary();
        final LocalDate date = LocalDate.now();
        dailySummary.setDate(date);
        dailySummary.setDatafile("test/file.todate.date");
        dailySummary.setOutageCount(4);
        dao.add(dailySummary);

        // When: Find by the date one day prior to the saved record.
        long deleteCount = dao.delete(date.minusDays(1));

        // Then: Just one record is deleted
        assertEquals(0L, deleteCount);

        // Then: The saved record is found.
        Iterable<DailySummary> searchResults = dao.findAll();
        assertNotNull(searchResults);
        assertTrue("Has a result", searchResults.iterator().hasNext());
    }

    @Test
    @DisplayName("Test findByDateRange success")
    void testFindByDateRangeSuccess() {

        // Given: Save 4 records on different dates.
        DailySummary dailySummary = new DailySummary();
        final LocalDate date = LocalDate.now();
        dailySummary.setDate(date);
        dailySummary.setDatafile("test/file.todate.date");
        dailySummary.setOutageCount(4);
        dao.add(dailySummary);

        dailySummary = new DailySummary();
        dailySummary.setDate(date.minusDays(1));
        dailySummary.setDatafile("test1/file.todate.date");
        dailySummary.setOutageCount(1);
        dao.add(dailySummary);

        dailySummary = new DailySummary();
        dailySummary.setDate(date.minusDays(2));
        dailySummary.setDatafile("test2/file.todate.date");
        dailySummary.setOutageCount(2);
        dao.add(dailySummary);

        dailySummary = new DailySummary();
        dailySummary.setDate(date.minusDays(3));
        dailySummary.setDatafile("test3/file.todate.date");
        dailySummary.setOutageCount(3);
        dao.add(dailySummary);

        // When: Find by the date of the first saved record.
        Iterable<DailySummary> searchResults = dao.findByDateRange(date.minusDays(2), date.minusDays(1));

        // Then: Two records are returned
        assertNotNull(searchResults);
        Iterator<DailySummary> searchResultsIterator = searchResults.iterator();
        DailySummary result1 = searchResultsIterator.next();
        assertNotNull(result1);
        DailySummary result2 = searchResultsIterator.next();
        assertNotNull(result2);
        assertFalse("Has more than two result", searchResultsIterator.hasNext());

        // and the first return record is the second one that was saved.
        LOG.debug("result1: {}", result1);
        assertEquals(Integer.valueOf(1), result1.getOutageCount());
        
        // and the second return record is the third one that was saved.
        LOG.debug("result2: {}", result2);
        assertEquals(Integer.valueOf(2), result2.getOutageCount());
    }

    @Test
	@DisplayName("Test deleteAll success")
	void testDeleteAllSuccess() {

        // Given: Save a record.
        DailySummary dailySummary = new DailySummary();
        final LocalDate date = LocalDate.now();
        dailySummary.setDate(date);
        dailySummary.setDatafile("test/file.todate.date");
        dailySummary.setOutageCount(4);
        dao.add(dailySummary);

        // When: Find by the date one day prior to the saved record.
        long deleteCount = dao.deleteAll();

        // Then: Just one record is deleted
        assertEquals(1L, deleteCount);

        // Then: The saved record is found.
        Iterable<DailySummary> searchResults = dao.findAll();
        assertNotNull(searchResults);
        assertFalse("No results are found", searchResults.iterator().hasNext());
	}

}
