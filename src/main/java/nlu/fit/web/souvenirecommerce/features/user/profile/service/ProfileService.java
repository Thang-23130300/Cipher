package nlu.fit.web.souvenirecommerce.features.user.profile.service;

import nlu.fit.web.souvenirecommerce.common.base.AbsBaseRepository;
import nlu.fit.web.souvenirecommerce.features.user.profile.repository.ProfileRepository;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.util.Optional;

public class ProfileService extends AbsBaseRepository<Long, User> {
    private static final ProfileRepository profileRepository = new ProfileRepository();

    public ProfileService(){
        super(User.class);
    }
}
