/**
 * 
 */
package me.ineson.monitorNbn.shared.dao;

import java.util.Objects;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

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
        mongoClient = new MongoClient();
        createDatabase(mongoClient);
    }

    public DatasourceManager(String url) {
        super();
        dbUrl = url;
        MongoClientURI connectionString = new MongoClientURI(dbUrl);
        mongoClient = new MongoClient(connectionString);
        createDatabase(mongoClient);
    }
    
    public DatasourceManager(String url, String name) {
        super();
        dbUrl = url;
        dbName = name;
        MongoClientURI connectionString = new MongoClientURI(dbUrl);
        mongoClient = new MongoClient(connectionString);
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

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
        		CodecRegistries.fromCodecs(new LocalDateCodec(), new LocalDateTimeCodec()),
        		MongoClient.getDefaultCodecRegistry(),
       		    CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        mongoDatabase = database.withCodecRegistry(pojoCodecRegistry);
    }
    
    public synchronized void close() {
    	if (Objects.nonNull(mongoClient)) {
            mongoClient.close();
            mongoClient = null;
    	}
    }
	
}
