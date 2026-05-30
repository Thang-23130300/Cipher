package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.common.base.IRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Permission;

import java.util.Optional;

public interface IPermissionEntityIRepository extends IRepository<Long, Permission> {

    Optional<Permission> findByResourceAndAction(String resource, String action);
}
