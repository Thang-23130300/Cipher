package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.common.base.IRepository;
import nlu.fit.web.souvenirecommerce.model.entity.OAuthAccount;

import java.util.Optional;

public interface IOAuthAccountEntityIRepository extends IRepository<Long, OAuthAccount> {

    Optional<OAuthAccount> findByProviderAndProviderUserId(String provider, String providerUserId);
}
