package nlu.fit.web.souvenirecommerce.common.base;

import java.util.List;
import java.util.Optional;

/**
 * Generic Service Interface định nghĩa các method chung cho tất cả services
 * K: kiểu của primary key
 * T: kiểu của entity
 */
public interface IService<K, T> {

    /**
     * Lưu một entity mới
     */
    Optional<T> save(T entity);

    /**
     * Cập nhật một entity
     */
    void update(T entity);

    /**
     * Lấy tất cả entities
     */
    List<T> findAll();

    /**
     * Lấy entity theo ID
     */
    Optional<T> findById(K id);

    /**
     * Xóa entity theo ID
     */
    void delete(K id);

    /**
     * Lấy số lượng tất cả entities
     */
    long count();
}

