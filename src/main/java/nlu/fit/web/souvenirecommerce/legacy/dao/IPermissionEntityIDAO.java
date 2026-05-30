package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.model.entity.Permission;

import java.util.Optional;

public interface IPermissionEntityIDAO extends IDAO<Long, Permission> {

    Optional<Permission> findByResourceAndAction(String resource, String action);
}
