package nlu.fit.web.souvenirecommerce.features.notification.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long recipientUserId;
    private String recipientName;
    private Long orderId;
    private String orderCode;
    private String type;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}