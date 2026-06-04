package nlu.fit.web.souvenirecommerce.features.user.profile.repository;

import nlu.fit.web.souvenirecommerce.common.base.AbsBaseRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Address;

import java.util.List;

public class AddressRepository extends AbsBaseRepository<Long, Address> {

    public AddressRepository() {
        super(Address.class);
    }

    public List<Address> findByUserId(Long userId) {
        return getSession()
                .createQuery("from Address a where a.user.id = :userId order by a.isDefault desc, a.id desc", Address.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public long countByUserId(Long userId) {
        Long count = getSession()
                .createQuery("select count(a.id) from Address a where a.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .uniqueResult();
        return count == null ? 0 : count;
    }

    public boolean deleteByIdAndUserId(Long addressId, Long userId) {
        int updated = getSession()
                .createMutationQuery("delete from Address a where a.id = :addressId and a.user.id = :userId")
                .setParameter("addressId", addressId)
                .setParameter("userId", userId)
                .executeUpdate();
        return updated > 0;
    }

    public boolean setDefault(Long addressId, Long userId) {
        int exists = getSession()
                .createQuery("select count(a.id) from Address a where a.id = :addressId and a.user.id = :userId", Long.class)
                .setParameter("addressId", addressId)
                .setParameter("userId", userId)
                .uniqueResult()
                .intValue();
        if (exists == 0) {
            return false;
        }

        getSession()
                .createMutationQuery("update Address a set a.isDefault = false where a.user.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
        getSession()
                .createMutationQuery("update Address a set a.isDefault = true where a.id = :addressId and a.user.id = :userId")
                .setParameter("addressId", addressId)
                .setParameter("userId", userId)
                .executeUpdate();
        return true;
    }
}
