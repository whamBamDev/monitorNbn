/**
 * 
 */
package me.ineson.monitorNbn.dataLoader.dao;

import static com.mongodb.client.model.Filters.eq;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;

import me.ineson.monitorNbn.dataLoader.entity.Outage;

/**
 * @author peter
 *
 */
public class OutageDao {

    private static final Logger LOG = LogManager.getLogger(OutageDao.class);

    final static String COLLECTION_NAME = Outage.class.getSimpleName();;

    final static String KEY_FIELD = "startTime";

    private MongoDatabase mongoDatabase;

	/**
	 * @param mongoDatabase
	 */
	public OutageDao(MongoDatabase mongoDatabase) {
  		super();
		this.mongoDatabase = mongoDatabase;

        MongoCollection<Outage> collection = createCollection();
        boolean addIndex = true;
        Iterable<Document>inedexs = collection.listIndexes();
        for (Document index : inedexs) {
        	if( KEY_FIELD.equals(index.getString("name"))) {
        		addIndex = false;
        		break;
        	}
		}
        if( addIndex) {
        	LOG.info("Adding an index on the {} field on {} document", KEY_FIELD, COLLECTION_NAME);
        	collection.createIndex(Indexes.descending(KEY_FIELD),new IndexOptions().unique(true));
        }
	}

    public Outage add(Outage outage) {
        if(Objects.isNull(outage.getId())) {
        	outage.setId(new ObjectId());
        }
        createCollection().insertOne(outage);
        return outage;
    }

	public void update(Outage outage) {
        createCollection().replaceOne(eq("_id", outage.getId()), outage);		
	}

	public long delete(LocalDate date) {
		LocalDateTime startTime = date.atStartOfDay();
		return createCollection()
				.deleteMany(Filters.and(Filters.gte(KEY_FIELD, startTime), Filters.lt(KEY_FIELD, startTime.plusDays(1L))))
				.getDeletedCount();
	}

	public long deleteAll() {
		MongoCollection<Outage>collection = createCollection(); 
		long count = collection.count();
		collection.drop();
		return count;
	}

	public Iterable<Outage> findByDate(LocalDate date) {
		LocalDateTime startTime = date.atStartOfDay();
        return createCollection()
        		.find(Filters.and(Filters.gte(KEY_FIELD, startTime), Filters.lt(KEY_FIELD, startTime.plusDays(1L))))
        		.sort(Sorts.orderBy(Sorts.ascending(KEY_FIELD)));
	}

    public Iterable<Outage> findAll() {
        return createCollection().find().sort(Sorts.orderBy(Sorts.ascending(KEY_FIELD)));
    }

    private MongoCollection<Outage> createCollection() {
        return mongoDatabase.getCollection(COLLECTION_NAME, Outage.class);
    }
}



