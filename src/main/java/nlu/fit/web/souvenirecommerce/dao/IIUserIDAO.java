package nlu.fit.web.souvenirecommerce.dao;

import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.util.Optional;

public interface IIUserIDAO extends IDAO<Long, User> {

    boolean hasEmailExist(String email);

    Optional<User> findByUserEmailAndPassword(String userEmail, String password);

    Optional<User> findByUserEmail(String userEmail);
}
