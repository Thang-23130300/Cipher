package nlu.fit.web.souvenirecommerce.features.order.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderManagementServiceTest {

    @Test
    void signedPendingOrdersCanBeAccepted() {
        assertTrue(OrderManagementService.canAcceptStatus("Chờ xử lý", "SIGNED"));
        assertTrue(OrderManagementService.canAcceptStatus("Chờ ký xác nhận", "SIGNED"));
        assertTrue(OrderManagementService.canAcceptStatus("Chờ ký số", "SIGNED"));
    }

    @Test
    void unsignedOrInvalidOrdersCannotBeAccepted() {
        assertFalse(OrderManagementService.canAcceptStatus("Chờ xử lý", "WAITING_SIGNATURE"));
        assertFalse(OrderManagementService.canAcceptStatus("Chờ xử lý", "SIGNATURE_INVALID"));
        assertFalse(OrderManagementService.canAcceptStatus("Đã xác nhận", "SIGNED"));
    }

    @Test
    void onlyOrdersBeforeDeliveryCanBeCancelled() {
        assertTrue(OrderManagementService.canCancelStatus("Chờ xử lý"));
        assertTrue(OrderManagementService.canCancelStatus("Đã xác nhận"));
        assertFalse(OrderManagementService.canCancelStatus("Đang giao hàng"));
        assertFalse(OrderManagementService.canCancelStatus("Đã giao hàng"));
        assertFalse(OrderManagementService.canCancelStatus("Hoàn thành"));
        assertFalse(OrderManagementService.canCancelStatus("Đã hủy"));
    }

    @Test
    void standardTransitionsFollowTheOrderLifecycle() {
        assertTrue(OrderManagementService.isStandardTransition("Chờ xử lý", "Đã xác nhận"));
        assertTrue(OrderManagementService.isStandardTransition("Đã xác nhận", "Đang xử lý"));
        assertTrue(OrderManagementService.isStandardTransition("Đang xử lý", "Đang giao hàng"));
        assertTrue(OrderManagementService.isStandardTransition("Đang giao hàng", "Đã giao hàng"));
        assertTrue(OrderManagementService.isStandardTransition("Đã giao hàng", "Hoàn thành"));
    }

    @Test
    void skippingOrReversingLifecycleIsAnOverride() {
        assertFalse(OrderManagementService.isStandardTransition("Chờ xử lý", "Đang giao hàng"));
        assertFalse(OrderManagementService.isStandardTransition("Đã giao hàng", "Đang xử lý"));
        assertFalse(OrderManagementService.isStandardTransition("Hoàn thành", "Đã xác nhận"));
    }
}
