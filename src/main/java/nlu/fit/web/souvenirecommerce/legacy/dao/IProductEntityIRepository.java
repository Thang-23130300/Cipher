package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.common.base.IRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Product;

import java.util.List;

public interface IProductEntityIRepository extends IRepository<Long, Product> {

    List<Product> findByCategoryId(Long categoryId);
}
