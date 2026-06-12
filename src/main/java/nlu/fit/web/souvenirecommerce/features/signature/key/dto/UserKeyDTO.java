package nlu.fit.web.souvenirecommerce.features.signature.key.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserKeyDTO {
    private Long id;
    private Long userId;
    private String publicKey;
    private String keyAlgorithm;
    private Integer keySize;
    private String signatureAlgorithm;
    private String keyStatus;
    private LocalDateTime createdAt;
    private LocalDateTime revokedAt;
}