/**
 * 
 */
package me.ineson.monitorNbn.shared.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import me.ineson.monitorNbn.shared.entity.Outage;

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

	@Value("${mongodb.client.url}")
	private String dbUrl;

	@Value("${spring.data.mongodb.database}")
	private String dbName;

	@BeforeEach
	void setup(@Autowired MongoTemplate mongoTemplate) {
		mongoTemplate.dropCollection(OutageDao.COLLECTION_NAME);
		listDbContents("Before", mongoTemplate);

		datasourceManager = new DatasourceManager(dbUrl, dbName);
		dao = new OutageDao(datasourceManager.getDatabase());
	}

	@AfterEach
	void shutdown(@Autowired MongoTemplate mongoTemplate) {
		listDbContents("After", mongoTemplate);
        datasourceManager.close();
	}

	void listDbContents(String comment, MongoTemplate mongoTemplate) {
		LOG.info("======= {} =======", comment);
		Set<String> names = mongoTemplate.getCollectionNames();
		LOG.info("Collections: {}", String.join(",", names));
		MongoCollection<Document> collection = mongoTemplate.getCollection("Outage");
		LOG.info("document: {}", collection);
		LOG.info("document count: {}", collection.count());

		StringBuilder string = new StringBuilder();
		collection.listIndexes().forEach((Consumer<? super Document>) (Document document) -> {
			if (string.length() > 0)
				string.append(", ");
			string.append(document);
		});

		LOG.info("documents: {}", collection.find());
		collection.find().forEach((Consumer<? super Document>) (Document document) -> {
			LOG.info("Data: {}", document.toJson());
		});

		LOG.info("=============================");
	}

    @Test
    @DisplayName("Test insert and findAll success")
    void testInsertAndFindAllSuccess() {
        // Given: record to save
        Outage outage = new Outage();

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);

        outage.setNumberOfLines(25);
        outage.setStartFilePosition(12345L);
        outage.setStartTime(startTime);
        outage.setEndTime(endTime);

        Outage newOutage = dao.add(outage);
        LOG.debug("newOutage: {}", newOutage);

        Iterable<Outage> searchResults = dao.findAll();
        assertNotNull(searchResults);
        Iterator<Outage> searchResultsIterator = searchResults.iterator();

        Outage returnedOutage = searchResultsIterator.next();
        assertNotNull(returnedOutage);
        assertFalse("Has more than one result", searchResultsIterator.hasNext());
        LOG.debug("returnedOutage: {}", returnedOutage);
        assertEquals(Long.valueOf(12345L), returnedOutage.getStartFilePosition());
        assertEquals(Integer.valueOf(25), returnedOutage.getNumberOfLines());
        assertEquals(startTime, returnedOutage.getStartTime());
        assertEquals(endTime, returnedOutage.getEndTime());
    }
    
    @Test
    @DisplayName("test update success")
    void testUpdateSuccess() {
        // Given: record seaved
        Outage outage = new Outage();

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);

        outage.setNumberOfLines(25);
        outage.setStartFilePosition(12345L);
        outage.setStartTime(startTime);
        outage.setEndTime(endTime);

        Outage newRecord = dao.add(outage);

        // When: change the number of lines and update
        newRecord.setNumberOfLines(512);
        dao.update(newRecord);
        
        // Then: the updated record can be read from the DB
        Iterable<Outage> searchResults = dao.findAll();
        assertNotNull(searchResults);
        Iterator<Outage> searchResultsIterator = searchResults.iterator();

        //     and record can be read from the DB
        Outage returnedRecord =  searchResultsIterator.next();
        assertNotNull(returnedRecord);
        assertFalse("Has more than one result", searchResultsIterator.hasNext());

        //     and record contents match.
        assertEquals(Integer.valueOf(512), returnedRecord.getNumberOfLines());
        
        LOG.info("returnedOutage: {}", returnedRecord);
    }

    @Test
    @DisplayName("Test find success")
    void testFindSuccess() {

        // Given: Save a couple of record on different dates.
        Outage outage = new Outage();
        LocalDateTime startTime = LocalDateTime.parse("2020-08-12T13:22:56");
        outage.setNumberOfLines(25);
        outage.setStartFilePosition(12345L);
        outage.setStartTime(startTime);
        outage.setEndTime(startTime.plusMinutes(3L));
        dao.add(outage);

        outage = new Outage();
        outage.setNumberOfLines(1);
        outage.setStartFilePosition(5L);
        outage.setStartTime(startTime.plusMinutes(4L));
        outage.setEndTime(startTime.plusMinutes(7L));
        dao.add(outage);

        // When: Find by the date/time of the first saved record.
        Outage searchResult = dao.find(startTime);

        // Then: One record is returned
        assertNotNull(searchResult);

        // and the return record is the first one that was saved.
        LOG.debug("returnedOutage: {}", searchResult);
        assertEquals(Integer.valueOf(25), searchResult.getNumberOfLines());
    }
    
    @Test
    @DisplayName("Test find record not found returns null")
    void testFindNotFoundReturnNull() {

        // Given: Save a couple of record on different dates.
        Outage outage = new Outage();
        LocalDateTime startTime = LocalDateTime.parse("2020-08-12T13:22:56");
        outage.setNumberOfLines(25);
        outage.setStartFilePosition(12345L);
        outage.setStartTime(startTime);
        outage.setEndTime(startTime.plusMinutes(3L));
        dao.add(outage);

        outage = new Outage();
        outage.setNumberOfLines(1);
        outage.setStartFilePosition(5L);
        outage.setStartTime(startTime.plusMinutes(4L));
        outage.setEndTime(startTime.plusMinutes(7L));
        dao.add(outage);

        // When: Find by the date/time of the first saved record.
        Outage searchResult = dao.find(startTime.minusMinutes(2L));

        // Then: no record found, null returned.
        assertNull(searchResult);
    }

    @Test
    @DisplayName("Test findByDate success")
    void testFindByDateSuccess() {

        // Given: Save a couple of record on different dates.
        Outage outage = new Outage();
        LocalDateTime startTime = LocalDateTime.now();
        outage.setNumberOfLines(25);
        outage.setStartFilePosition(12345L);
        outage.setStartTime(startTime);
        outage.setEndTime(startTime.plusHours(1));
        dao.add(outage);

        outage = new Outage();
        outage.setNumberOfLines(1);
        outage.setStartFilePosition(5L);
        outage.setStartTime(startTime.minusDays(2));
        outage.setEndTime(startTime.plusHours(1));
        dao.add(outage);

        // When: Find by the date of the first saved record.
        Iterable<Outage> searchResults = dao.findByDate(startTime.toLocalDate());

        // Then: Just one record is returned
        assertNotNull(searchResults);
        Iterator<Outage> searchResultsIterator = searchResults.iterator();
        Outage returnedOutage = searchResultsIterator.next();
        assertNotNull(returnedOutage);
        assertFalse("Has more than one result", searchResultsIterator.hasNext());

        // and the return record is the first one that was saved.
        LOG.debug("returnedOutage: {}", returnedOutage);
        assertEquals(Integer.valueOf(25), returnedOutage.getNumberOfLines());
    }

    @Test
    @DisplayName("Test findByDate not found")
    void testFindByDateNotFound() {

        // Given: Save a record.
        Outage outage = new Outage();
        LocalDateTime startTime = LocalDateTime.now();
        outage.setNumberOfLines(25);
        outage.setStartFilePosition(12345L);
        outage.setStartTime(startTime);
        outage.setEndTime(startTime.plusHours(1));
        dao.add(outage);

        // When: Find by the date one day prior to the saved record.
        Iterable<Outage> searchResults = dao.findByDate(startTime.toLocalDate().minusDays(1));

        // Then: No records are found.
        assertNotNull(searchResults);
        assertFalse("Has more than one result", searchResults.iterator().hasNext());
    }

    @Test
    @DisplayName("Test deleteByDate success")
    void testDeleteByDateSuccess() {
        // Given: Save a couple of record on different dates.
        Outage outage = new Outage();
        LocalDateTime startTime = LocalDateTime.now();
        outage.setNumberOfLines(25);
        outage.setStartFilePosition(12345L);
        outage.setStartTime(startTime);
        outage.setEndTime(startTime.plusHours(1));
        dao.add(outage);

        outage = new Outage();
        outage.setNumberOfLines(1);
        outage.setStartFilePosition(5L);
        outage.setStartTime(startTime.minusDays(2));
        outage.setEndTime(startTime.plusHours(1));
        dao.add(outage);

        // When: Find by the date of the first saved record.
        long deleteCount = dao.delete(startTime.toLocalDate());

        // Then: Just one record is deleted
        assertEquals(1L, deleteCount);

        // and the second saved record has not been deleted.
        Iterable<Outage> searchResults = dao.findAll();
        assertNotNull(searchResults);
        Iterator<Outage> searchResultsIterator = searchResults.iterator();
        Outage returnedOutage = searchResultsIterator.next();
        assertNotNull(returnedOutage);
        assertFalse("Has more than one result", searchResultsIterator.hasNext());
        assertEquals(Integer.valueOf(1), returnedOutage.getNumberOfLines());
    }

    @Test
    @DisplayName("Test findByDate not found")
    void testDeleteByDateNotFound() {

        // Given: Save a record.
        Outage outage = new Outage();
        LocalDateTime startTime = LocalDateTime.now();
        outage.setNumberOfLines(25);
        outage.setStartFilePosition(12345L);
        outage.setStartTime(startTime);
        outage.setEndTime(startTime.plusHours(1));
        dao.add(outage);

        // When: Find by the date one day prior to the saved record.
        long deleteCount = dao.delete(startTime.toLocalDate().minusDays(1));

        // Then: Just one record is deleted
        assertEquals(0L, deleteCount);

        // Then: The saved record is found.
        Iterable<Outage> searchResults = dao.findAll();
        assertNotNull(searchResults);
        assertTrue("Has a result", searchResults.iterator().hasNext());
    }


    @Test
    @DisplayName("Test findLatestForDate success")
    void testfindLatestForDateSuccess() {

        // Given: Save a couple of records on the same date.
        Outage outage = new Outage();
        LocalDateTime startTime = LocalDate.now().atTime(1, 22, 21);
        outage.setNumberOfLines(25);
        outage.setStartFilePosition(12345L);
        outage.setStartTime(startTime);
        outage.setEndTime(startTime.plusHours(1));
        dao.add(outage);

        outage = new Outage();
        outage.setNumberOfLines(1);
        outage.setStartFilePosition(5L);
        outage.setStartTime(startTime.plusHours(4));
        outage.setEndTime(startTime.plusHours(5));
        dao.add(outage);

        // When: Find by the date of the latest for the day.
        Outage searchResult = dao.findLatestForDate(startTime.toLocalDate());

        // Then: Record is returned
        assertNotNull(searchResult);

        // and the return record is the first one that was saved.
        LOG.debug("returnedOutage: {}", searchResult);
        assertEquals(Integer.valueOf(1), searchResult.getNumberOfLines());
    }
    
    @Test
    @DisplayName("Test findLatestForDate not found")
    void testfindLatestForDateNotFoundReturnsNull() {

        // Given: Save a couple of records on the same date.
        Outage outage = new Outage();
        LocalDateTime startTime = LocalDate.now().atTime(1, 22, 21);
        outage.setNumberOfLines(25);
        outage.setStartFilePosition(12345L);
        outage.setStartTime(startTime);
        outage.setEndTime(startTime.plusHours(1));
        dao.add(outage);

        outage = new Outage();
        outage.setNumberOfLines(1);
        outage.setStartFilePosition(5L);
        outage.setStartTime(startTime.plusHours(4));
        outage.setEndTime(startTime.plusHours(5));
        dao.add(outage);

        // When: Find by the date of the latest for the day.
        Outage searchResult = dao.findLatestForDate(startTime.toLocalDate().plusDays(1));

        // Then: Record is returned
        assertNull(searchResult);
    }
    
	@Test
	@DisplayName("Test deleteAll success")
	void testDeleteAllSuccess() {

        // Given: Save a record.
        Outage outage = new Outage();
        LocalDateTime startTime = LocalDateTime.now();
        outage.setNumberOfLines(25);
        outage.setStartFilePosition(12345L);
        outage.setStartTime(startTime);
        outage.setEndTime(startTime.plusHours(1));
        dao.add(outage);

        // When: Find by the date one day prior to the saved record.
        long deleteCount = dao.deleteAll();

        // Then: Just one record is deleted
        assertEquals(1L, deleteCount);

        // Then: The saved record is found.
        Iterable<Outage> searchResults = dao.findAll();
        assertNotNull(searchResults);
        assertFalse("No results are found", searchResults.iterator().hasNext());
	}

}
