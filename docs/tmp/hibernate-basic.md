# Huong Dan Su Dung Hibernate Co Ban

Tai lieu nay tom tat cach dung Hibernate trong du an Souvenir E-commerce. Khi viet DAO moi, chi lam viec voi cac class entity trong package:

```text
nlu.fit.web.souvenirecommerce.model.entity
```

Khong dung cac model legacy trong `nlu.fit.web.souvenirecommerce.model` cho DAO Hibernate.

## 1. Entity

Entity la class Java duoc map voi table trong database bang annotation JPA/Hibernate.

Vi du:

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;
}
```

Cac entity cua du an dang nam trong:

```text
src/main/java/nlu/fit/web/souvenirecommerce/model/entity
```

Khi them entity moi, can khai bao mapping trong `src/main/resources/hibernate.cfg.xml`:

```xml
<mapping class="nlu.fit.web.souvenirecommerce.model.entity.User"/>
```

## 2. SessionFactory Va Session

`SessionFactory` duoc tao mot lan trong `HibernateUtil`:

```java
HibernateUtil.getSessionFactory()
```

Moi thao tac database nen mo mot `Session` ngan han:

```java
try (var session = HibernateUtil.getSessionFactory().openSession()) {
    User user = session.find(User.class, 1L);
}
```

`Session` khong nen luu thanh field trong DAO, vi no dai dien cho mot don vi lam viec ngan va khong phu hop de dung chung lau dai.

## 3. Doc Du Lieu

Doc theo khoa chinh:

```java
try (var session = HibernateUtil.getSessionFactory().openSession()) {
    User user = session.find(User.class, id);
}
```

Doc bang HQL:

```java
String hql = "select u from User u where lower(u.email) = lower(:email)";

try (var session = HibernateUtil.getSessionFactory().openSession()) {
    User user = session.createQuery(hql, User.class)
            .setParameter("email", email)
            .uniqueResult();
}
```

Dung parameter thay vi noi chuoi SQL/HQL de tranh loi injection va loi escape du lieu.

## 4. Ghi Du Lieu Va Transaction

Cac thao tac `persist`, `merge`, `remove` phai nam trong transaction.

Them moi:

```java
var transaction = (org.hibernate.Transaction) null;

try (var session = HibernateUtil.getSessionFactory().openSession()) {
    transaction = session.beginTransaction();
    session.persist(user);
    transaction.commit();
} catch (RuntimeException e) {
    if (transaction != null && transaction.isActive()) {
        transaction.rollback();
    }
    throw e;
}
```

Cap nhat:

```java
session.merge(user);
```

Xoa:

```java
User user = session.find(User.class, id);
if (user != null) {
    session.remove(user);
}
```

## 5. Lazy Loading Va Fetch Join

Nhieu quan he entity dang de `FetchType.LAZY`. Neu can dung quan he sau khi `Session` dong, can fetch truoc bang HQL:

```java
String hql = """
        select distinct u from User u
        left join fetch u.credentials
        left join fetch u.roles r
        left join fetch r.permissions
        where u.id = :id
        """;
```

Dung `distinct` khi fetch collection de giam ket qua trung lap o object goc.

## 6. Mau DAO Trong Du An

DAO Hibernate nen implements interface trong package `dao`, thao tac tren entity, va khong tra ve model legacy.

Vi du rut gon:

```java
public interface UserDAO extends DAO<Long, User> {
    Optional<User> findByUserEmail(String userEmail);
}
```

Implementation chi nen nam trong package `dao.impl`:

```java
public class UserDAOImpl2 extends AbstractHibernateDAO<Long, User> implements UserDAO {
    @Override
    public Optional<User> findById(Long id) {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(User.class, id));
        }
    }
}
```

Neu method can doc password, role, permission hoac oauth account, nen dung fetch join nhu `UserDAOImpl2`.

Nhung method co the khong tim thay du lieu nen tra ve `Optional<T>` thay vi `null`, vi du `findById`, `findByEmail`, `findByToken`.

## 7. Luu Y Khi Viet DAO

- Khong mo transaction cho query chi doc neu khong can.
- Luon rollback khi ghi du lieu gap exception.
- Khong import cac class trong `nlu.fit.web.souvenirecommerce.model` vao DAO Hibernate.
- Khong dung JDBC `Connection`/`PreparedStatement` trong DAO Hibernate.
- Khong de `Session` thanh bien static hoac bien instance dung chung.
- Uu tien HQL theo ten entity va field Java, khong theo ten table/cot database.
- Moi DAO Hibernate nen co interface rieng de controller/service phu thuoc vao abstraction thay vi class cu the.
- Dung `Optional.empty()` cho input khong hop le hoac khong tim thay du lieu, khong tra `null`.
