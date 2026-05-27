package nlu.fit.web.souvenirecommerce.profile.dao;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.util.Map;
import java.util.Optional;

public class AccountDAO {



    /**
     * Fetches a User entity based on the provided email address, including related
     * entities such as credentials, roles, OAuth accounts, and addresses.
     *
     * This method uses lazy loading with `LEFT JOIN FETCH` to retrieve associated
     * data for the selected User entity in two steps.
     *
     * @param email The email address of the User to retrieve.
     * @param em    The EntityManager used to interact with the database.
     * @return An Optional containing the User if found, or an empty Optional if no User
     *         exists with the specified email.
     */
    public Optional<User> findByEmail(String email, EntityManager em) {
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u " +
                                    "LEFT JOIN FETCH u.credentials " +
                                    "LEFT JOIN FETCH u.roles " +
                                    "WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();

            user = em.createQuery(
                            "SELECT u FROM User u " +
                                    "LEFT JOIN FETCH u.oauthAccounts " +
                                    "LEFT JOIN FETCH u.addresses " +
                                    "WHERE u = :user", User.class)
                    .setParameter("user", user)
                    .getSingleResult();

            return Optional.ofNullable(user);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves a User entity by its unique identifier, including its associated entities such
     * as credentials, roles, OAuth accounts, and addresses.
     *
     * The method performs two lazy-loading queries using LEFT JOIN FETCH to retrieve the User
     * and its related entities, ensuring all required data is fetched in a single transaction.
     * If no User is found with the specified ID, an empty Optional is returned.
     *
     * @param userId The unique identifier of the User to retrieve.
     * @param em     The EntityManager used to interact with the persistence context and database.
     * @return An Optional containing the User if found, or an empty Optional if no User exists
     *         with the given ID.
     */
    public Optional<User> findById(Long userId, EntityManager em){
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u " +
                                    "LEFT JOIN FETCH u.credentials " +
                                    "LEFT JOIN FETCH u.roles " +
                                    "WHERE u.id = :userId", User.class)
                    .setParameter("userId", userId)
                    .getSingleResult();

            user = em.createQuery(
                            "SELECT u FROM User u " +
                                    "LEFT JOIN FETCH u.oauthAccounts " +
                                    "LEFT JOIN FETCH u.addresses " +
                                    "WHERE u = :user", User.class)
                    .setParameter("user", user)
                    .getSingleResult();

            return Optional.ofNullable(user);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public User findUserByEmailWithEntityGraph(String email, EntityManager em) {
        try {
            EntityGraph<User> graph = em.createEntityGraph(User.class);
            graph.addAttributeNodes("credentials", "roles", "oauthAccounts", "addresses");
            Map<String, Object> hints = Map.of("jakarta.persistence.fetchgraph", graph);

            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .setHint("jakarta.persistence.fetchgraph", graph)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }
}
