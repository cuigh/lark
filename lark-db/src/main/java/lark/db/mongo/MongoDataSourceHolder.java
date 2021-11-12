package lark.db.mongo;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MongoDataSourceHolder {

    private final Map<String, MongoDbFactory> factories = new ConcurrentHashMap<>();
    private final Map<String, MongoTemplate> templates = new ConcurrentHashMap<>();

    private Map<String, MongoProperties> properties = new HashMap<>();

    public void addProperty(String name, MongoProperties properties) {
        this.properties.put(name, properties);
        MongoClientOptions.Builder options = new MongoClientOptions.Builder();
        options.connectionsPerHost(100);
        SimpleMongoDbFactory dbFactory = new SimpleMongoDbFactory(new MongoClientURI(properties.getUri(), options));
        factories.put(name, dbFactory);
        //TODO: 指定converter
        MongoTemplate mongoTemplate = new MongoTemplate(dbFactory);
        templates.put(name, mongoTemplate);
    }

    public MongoDbFactory getDbFactory(String name) {
        return factories.get(name);
    }

    public MongoTemplate getTemplate(String name) {
        return templates.get(name);
    }

}
