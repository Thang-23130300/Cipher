package nlu.fit.web.souvenirecommerce.common.base;

import java.util.List;
import java.util.Optional;

/**
 * A generic repository interface that defines basic CRUD (Create, Read, Update, Delete)
 * operations for handling entities in a data store.
 *
 * @param <K> the type of the primary key for the entities handled by the repository
 * @param <T> the type of the entities managed by the repository
 */
public interface IRepository<K, T> {

    Optional<T> save(T t);

    Optional<T> update(T t);

    List<T> findAll();

    Optional<T> findById(K id);

    void delete(K id);
}
