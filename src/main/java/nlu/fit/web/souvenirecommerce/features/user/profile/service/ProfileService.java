package nlu.fit.web.souvenirecommerce.features.user.profile.service;

import nlu.fit.web.souvenirecommerce.features.user.profile.dao.ProfileDao;
import nlu.fit.web.souvenirecommerce.features.user.profile.exception.UserNotFoundException;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import nlu.fit.web.souvenirecommerce.core.config.HibernateUtil;

public class ProfileService {
    private ProfileDao profileDao;

    public ProfileService(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public ProfileService() {
        this.profileDao = new ProfileDao();
    }

    /**
     * Short summary of the method (appears in method summary table).
     *
     * Detailed description of what the method does, using <p> tags
     * for multiple paragraphs if necessary.
     *
     * @param email Email of User need to find
     * @return User if email exists
     * @throws UserNotFoundException If the user doesn't exist or the user is deleted
     */
    public User findByEmail(String email){
        return profileDao.findByEmail(email, HibernateUtil.getEntityManager())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public User findById(Long userId){
        return profileDao.findById(userId, HibernateUtil.getEntityManager())
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + userId));
    }

    /**
     * Updates user profile information
     * @param user User object with updated fields
     * @return Updated User object
     */
    public User updateProfile(User user) {
        return profileDao.updateUser(user, HibernateUtil.getEntityManager());
    }
}
