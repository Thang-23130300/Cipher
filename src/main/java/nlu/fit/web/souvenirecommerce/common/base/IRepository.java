package nlu.fit.web.souvenirecommerce.common.base;

import java.util.List;
import java.util.Optional;

public interface IRepository<K, T> {

    Optional<T> save(T t);

    void update(T t);

    List<T> findAll();

    Optional<T> findById(K id);

    void delete(K id);
}
