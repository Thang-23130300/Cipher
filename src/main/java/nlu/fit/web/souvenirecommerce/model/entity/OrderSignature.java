package nlu.fit.web.souvenirecommerce.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_signatures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_id", nullable = false)
    private UserKey userKey;

    @Column(name = "signature_value", nullable = false, columnDefinition = "LONGTEXT")
    private String signatureValue;

    @Column(name = "signature_algorithm", nullable = false, length = 50)
    @Builder.Default
    private String signatureAlgorithm = "SHA256withRSA";

    @Column(name = "signed_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime signedAt = LocalDateTime.now();

    @Column(name = "verify_status", nullable = false, length = 30)
    @Builder.Default
    private String verifyStatus = "PENDING";

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}
