package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.model.entity.UserSession;

import java.time.LocalDateTime;
import java.util.List;

public interface ISessionEntityIDAO extends IDAO<String, UserSession> {

    List<UserSession> findExpired(LocalDateTime now);
}
