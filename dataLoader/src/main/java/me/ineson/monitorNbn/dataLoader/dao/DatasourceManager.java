/**
 * 
 */
package me.ineson.monitorNbn.dataLoader.dao;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * @author peter
 *
 */
public class DatasourceManager {

    private MongoDatabase mongoDatabase;        

    private MongoClient mongoClient;
    
    private String dbUrl = null;

    private String dbName = "nbn";

    public DatasourceManager() {
        super();
        mongoClient = MongoClients.create();
        createDatabase(mongoClient);
    }

    public DatasourceManager(String url) {
        super();
        dbUrl = url;
        mongoClient = MongoClients.create(url);
        createDatabase(mongoClient);
    }
    
    public DatasourceManager(String url, String name) {
        super();
        dbUrl = url;
        dbName = name;
        mongoClient = MongoClients.create(url);
        createDatabase(mongoClient);
    }

    public MongoDatabase getDatabase() {
    	if(mongoDatabase == null) {
    		throw new IllegalStateException("MongoDatase has not been initialised");
    	}
    	
    	return mongoDatabase;
    }
	
    private void createDatabase(MongoClient client) {
        MongoDatabase database = mongoClient.getDatabase(dbName);
        
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoDatabase = database.withCodecRegistry(pojoCodecRegistry);
    	
    }
	
}
