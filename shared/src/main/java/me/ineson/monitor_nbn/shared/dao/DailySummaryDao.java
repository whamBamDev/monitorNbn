/**
 * 
 */
package me.ineson.monitor_nbn.shared.dao;

import static com.mongodb.client.model.Filters.eq;

import java.time.LocalDate;
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

import me.ineson.monitor_nbn.shared.entity.DailySummary;

/**
 * @author peter
 *
 */
public class DailySummaryDao {

    private static final Logger LOG = LogManager.getLogger(DailySummaryDao.class);

    final static String COLLECTION_NAME = DailySummary.class.getSimpleName();

    final static String KEY_FIELD = "date";

	private MongoDatabase mongoDatabase;

	/**
	 * @param mongoDatabase
	 */
	public DailySummaryDao(MongoDatabase mongoDatabase) {
		super();
		this.mongoDatabase = mongoDatabase;
		
        MongoCollection<DailySummary> collection = createCollection();
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

    public DailySummary add(DailySummary dailySummary) {
        if(Objects.isNull(dailySummary.getId())) {
            dailySummary.setId(new ObjectId());
        }
        createCollection().insertOne(dailySummary);
        return dailySummary;
    }

	public void update(DailySummary dailySummary) {
        createCollection().replaceOne(eq(KEY_FIELD, dailySummary.getDate()), dailySummary);
    }

	public long deleteAll() {
		MongoCollection<DailySummary>collection = createCollection(); 
		long count = collection.count();
		collection.drop();
		return count;
	}
	
	public long delete(LocalDate date) {
        return createCollection().deleteMany(eq(KEY_FIELD, date)).getDeletedCount();
	}

	public DailySummary findByDate(LocalDate date) {
        return createCollection().find(eq(KEY_FIELD, date)).first();
	}

	public Iterable<DailySummary> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return createCollection()
        		.find(Filters.and(Filters.gte(KEY_FIELD, startDate), Filters.lte(KEY_FIELD, endDate)))
        		.sort(Sorts.orderBy(Sorts.descending(KEY_FIELD)));
	}
	
    public Iterable<DailySummary> findAll()  {
        return createCollection().find()
        		.sort(Sorts.orderBy(Sorts.descending(KEY_FIELD)));
    }

    private MongoCollection<DailySummary> createCollection() {
        return mongoDatabase.getCollection(COLLECTION_NAME, DailySummary.class);
    }

}
