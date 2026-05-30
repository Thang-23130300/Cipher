package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.util.Optional;

public interface IUserDAO extends IDAO<Long, User> {

    boolean hasEmailExist(String email);

    Optional<User> findByUserEmailAndPassword(String userEmail, String password);

    Optional<User> findByUserEmail(String userEmail);

    Optional<User> createUser(String email, String password, String firstName, String lastName, String phone, String gender);
}
