package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.common.base.IRepository;
import nlu.fit.web.souvenirecommerce.model.entity.UserSession;

import java.time.LocalDateTime;
import java.util.List;

public interface ISessionEntityIRepository extends IRepository<String, UserSession> {

    List<UserSession> findExpired(LocalDateTime now);
}
