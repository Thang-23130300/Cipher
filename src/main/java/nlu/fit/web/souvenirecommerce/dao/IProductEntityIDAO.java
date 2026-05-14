package nlu.fit.web.souvenirecommerce.dao;

import nlu.fit.web.souvenirecommerce.model.entity.Product;

import java.util.List;

public interface IProductEntityIDAO extends IDAO<Long, Product> {

    List<Product> findByCategoryId(Long categoryId);
}
