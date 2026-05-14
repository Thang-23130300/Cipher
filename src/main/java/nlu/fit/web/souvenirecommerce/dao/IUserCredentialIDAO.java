package nlu.fit.web.souvenirecommerce.dao;

import nlu.fit.web.souvenirecommerce.model.entity.UserCredential;

import java.util.Optional;

public interface IUserCredentialIDAO extends IDAO<Long, UserCredential> {

    Optional<UserCredential> findByVerificationToken(String token);

    Optional<UserCredential> findByResetToken(String token);
}
