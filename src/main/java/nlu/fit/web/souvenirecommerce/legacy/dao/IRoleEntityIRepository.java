package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.common.base.IRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Role;

import java.util.Optional;

public interface IRoleEntityIRepository extends IRepository<Long, Role> {

    Optional<Role> findByName(String name);
}
