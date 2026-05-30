package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.common.base.IRepository;
import nlu.fit.web.souvenirecommerce.model.entity.UserCredential;

import java.util.Optional;

public interface IUserCredentialIRepository extends IRepository<Long, UserCredential> {

    Optional<UserCredential> findByVerificationToken(String token);

    Optional<UserCredential> findByResetToken(String token);
}
