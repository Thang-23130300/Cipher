package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.model.entity.OAuthAccount;

import java.util.Optional;

public interface IOAuthAccountEntityIDAO extends IDAO<Long, OAuthAccount> {

    Optional<OAuthAccount> findByProviderAndProviderUserId(String provider, String providerUserId);
}
