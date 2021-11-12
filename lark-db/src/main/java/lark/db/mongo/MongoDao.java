package lark.db.mongo;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

/**
 * @author cuigh
 */
public class MongoDao<T, K> {
    protected MongoOperations operations;
    protected Class<T> type;

    @SuppressWarnings("unchecked")
    public MongoDao(MongoOperations operations) {
        this.operations = operations;
        this.type = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    /**
     * Insert the object into the collection for the entity type of the object to save.
     *
     * @param object the object to store in the collection. Must not be {@literal null}.
     */
    public void insert(T object) {
        operations.insert(object);
    }

    public void insert(Collection<T> objects) {
        operations.insert(objects, type);
    }

    /**
     * Save the object to the collection for the entity type of the object to save. This will perform an insert if the
     * object is not already present, that is an 'upsert'.
     *
     * @param object the object to store in the collection. Must not be {@literal null}.
     */
    public void save(T object) {
        operations.save(object);
    }

    public List<T> findList(Query query) {
        return operations.find(query, type);
    }

    public T findOne(Query query) {
        return operations.findOne(query, type);
    }

    public T find(K id) {
        return operations.findById(id, type);
    }

    public long count(Query query) {
        return operations.count(query, type);
    }

    public DeleteResult remove(K id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return operations.remove(query, type);
    }

    public DeleteResult remove(Query query) {
        return operations.remove(query, type);
    }

    public UpdateResult update(K id, Update update) {
        Query query = new Query(Criteria.where("_id").is(id));
        return operations.updateFirst(query, update, type);
    }

    public UpdateResult updateOne(Query query, Update update) {
        return operations.updateFirst(query, update, type);
    }

    public UpdateResult updateAll(Query query, Update update) {
        return operations.updateMulti(query, update, type);
    }

    public UpdateResult upsert(Query query, Update update) {
        return operations.upsert(query, update, type);
    }
}
