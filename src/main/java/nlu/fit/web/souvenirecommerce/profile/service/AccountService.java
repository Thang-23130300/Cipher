package nlu.fit.web.souvenirecommerce.profile.service;

import nlu.fit.web.souvenirecommerce.profile.dao.AccountDAO;
import nlu.fit.web.souvenirecommerce.profile.exception.UserNotFoundException;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import nlu.fit.web.souvenirecommerce.util.HibernateUtil;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public AccountService() {
        this.accountDAO = new AccountDAO();
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
        return accountDAO.findByEmail(email, HibernateUtil.getEntityManager())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public User findById(Long userId){
        return accountDAO.findById(userId, HibernateUtil.getEntityManager())
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + userId));
    }
}
