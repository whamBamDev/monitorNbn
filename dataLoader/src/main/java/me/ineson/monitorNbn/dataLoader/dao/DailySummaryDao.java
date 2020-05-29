/**
 * 
 */
package me.ineson.monitorNbn.dataLoader.dao;

import static com.mongodb.client.model.Filters.eq;

import java.time.LocalDate;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import me.ineson.monitorNbn.dataLoader.entity.DailySummary;

/**
 * @author peter
 *
 */
public class DailySummaryDao {

    private final static String KEY_FIELD = "date";

	private MongoDatabase mongoDatabase;

	/**
	 * @param mongoDatabase
	 */
	public DailySummaryDao(MongoDatabase mongoDatabase) {
		super();
		this.mongoDatabase = mongoDatabase;
	}

    public DailySummary add(DailySummary dailySummary) {
        createCollection().insertOne(dailySummary);
        return dailySummary;
    }

	public void update(DailySummary dailySummary) {
        createCollection().replaceOne(eq(KEY_FIELD, dailySummary.getDate()), dailySummary);		
	}

	public long delete(LocalDate date) {
        return createCollection().deleteMany(eq(KEY_FIELD, date)).getDeletedCount();
	}

	public DailySummary findByDate(LocalDate date) {
        return createCollection().find(eq(KEY_FIELD, date)).first();
	}

    public Iterable<DailySummary> findAll()  {
        return createCollection().find();
    }

    private MongoCollection<DailySummary> createCollection() {
        return mongoDatabase.getCollection("DailySummary", DailySummary.class);
    }
}



