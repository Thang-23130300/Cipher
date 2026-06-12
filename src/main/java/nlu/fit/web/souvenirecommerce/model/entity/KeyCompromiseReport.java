package nlu.fit.web.souvenirecommerce.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "key_compromise_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeyCompromiseReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_id", nullable = false)
    private UserKey userKey;

    @Column(name = "report_type", nullable = false, length = 30)
    private String reportType;

    @Column(name = "compromised_from")
    private LocalDateTime compromisedFrom;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
