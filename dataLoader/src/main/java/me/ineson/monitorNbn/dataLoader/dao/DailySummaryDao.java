/**
 * 
 */
package me.ineson.monitorNbn.dataLoader.dao;

import static com.mongodb.client.model.Filters.eq;

import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;

import me.ineson.monitorNbn.dataLoader.entity.DailySummary;

/**
 * @author peter
 *
 */
public class DailySummaryDao {

    private static final Logger LOG = LogManager.getLogger(DailySummaryDao.class);

    final static String COLLECTION_NAME = DailySummary.class.getSimpleName();;

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
        createCollection().insertOne(dailySummary);
        return dailySummary;
    }

	public long update(DailySummary dailySummary) {
        return createCollection().replaceOne(eq(KEY_FIELD, dailySummary.getDate()), dailySummary).getModifiedCount();		
	}

	public long deleteAll() {
		MongoCollection<DailySummary>collection = createCollection(); 
		long count = collection.countDocuments();
		collection.drop();
		return count;
	}
	
	public long delete(LocalDate date) {
        return createCollection().deleteMany(eq(KEY_FIELD, date)).getDeletedCount();
	}

	public DailySummary findByDate(LocalDate date) {
        return createCollection().find(eq(KEY_FIELD, date)).first();
	}

    public Iterable<DailySummary> findAll()  {
        return createCollection().find()
        		.sort(Sorts.orderBy(Sorts.descending(KEY_FIELD)));
    }

    private MongoCollection<DailySummary> createCollection() {
        return mongoDatabase.getCollection(COLLECTION_NAME, DailySummary.class);
    }

}



