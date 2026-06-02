package nlu.fit.web.souvenirecommerce.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class AbsBaseEntity implements Serializable {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false,
            columnDefinition = "datetime(6) default CURRENT_TIMESTAMP(6) on update CURRENT_TIMESTAMP(6)")
    protected LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
