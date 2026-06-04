package nlu.fit.web.souvenirecommerce.common.base;

import java.util.List;
import java.util.Optional;

/**
 * A generic service interface that defines common operations for managing entities.
 *
 * @param <K> the type of the primary key for entities
 * @param <T> the type of the entities managed by the service
 */
public interface IService<K, T> {

    Optional<T> save(T entity);

    Optional<T> update(T entity);

    List<T> findAll();

    Optional<T> findById(K id);

    void delete(K id);

    long count();
}

