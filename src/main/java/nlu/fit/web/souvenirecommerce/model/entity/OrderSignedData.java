package nlu.fit.web.souvenirecommerce.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_signed_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSignedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "signed_data_json", nullable = false, columnDefinition = "LONGTEXT")
    private String signedDataJson;

    @Column(name = "hash_algorithm", nullable = false, length = 50)
    @Builder.Default
    private String hashAlgorithm = "SHA-256";

    @Column(name = "hash_value", nullable = false, length = 128)
    private String hashValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
