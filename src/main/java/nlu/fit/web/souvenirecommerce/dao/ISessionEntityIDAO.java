package nlu.fit.web.souvenirecommerce.dao;

import nlu.fit.web.souvenirecommerce.model.entity.Session;

import java.time.LocalDateTime;
import java.util.List;

public interface ISessionEntityIDAO extends IDAO<String, Session> {

    List<Session> findExpired(LocalDateTime now);
}
