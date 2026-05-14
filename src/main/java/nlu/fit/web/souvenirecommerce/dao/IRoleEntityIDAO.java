package nlu.fit.web.souvenirecommerce.dao;

import nlu.fit.web.souvenirecommerce.model.entity.Role;

import java.util.Optional;

public interface IRoleEntityIDAO extends IDAO<Long, Role> {

    Optional<Role> findByName(String name);
}
