package nlu.fit.web.souvenirecommerce.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "public_key", nullable = false, columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "key_algorithm", nullable = false, length = 50)
    @Builder.Default
    private String keyAlgorithm = "RSA";

    @Column(name = "key_size", nullable = false)
    @Builder.Default
    private int keySize = 2048;

    @Column(name = "signature_algorithm", nullable = false, length = 50)
    @Builder.Default
    private String signatureAlgorithm = "SHA256withRSA";

    @Column(name = "key_status", nullable = false, length = 30)
    @Builder.Default
    private String keyStatus = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "compromised_from")
    private LocalDateTime compromisedFrom;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
