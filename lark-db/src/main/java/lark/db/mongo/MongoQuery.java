package lark.db.mongo;

import org.springframework.data.mongodb.core.query.Query;

/**
 * @author cuigh
 */
public class MongoQuery extends Query {
    public MongoQuery page(int size, int index) {
        skip((long) size * (index - 1)).limit(size);
        return this;
    }
}
