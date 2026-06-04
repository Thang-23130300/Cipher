**Đã làm**
- Thêm entity Hibernate:
    - [Order.java](/home/vthiet/develop/java/SouvenirE-commerce/src/main/java/nlu/fit/web/souvenirecommerce/model/entity/Order.java)
    - [OrderItem.java](/home/vthiet/develop/java/SouvenirE-commerce/src/main/java/nlu/fit/web/souvenirecommerce/model/entity/OrderItem.java)
    - [OrderStatus.java](/home/vthiet/develop/java/SouvenirE-commerce/src/main/java/nlu/fit/web/souvenirecommerce/model/entity/OrderStatus.java)
    - [PaymentTransaction.java](/home/vthiet/develop/java/SouvenirE-commerce/src/main/java/nlu/fit/web/souvenirecommerce/model/entity/PaymentTransaction.java)
- Đăng ký entity mới trong [HibernateUtil.java](/home/vthiet/develop/java/SouvenirE-commerce/src/main/java/nlu/fit/web/souvenirecommerce/core/config/HibernateUtil.java).
- Thêm repository Hibernate cho order/status/product/payment trong `features/order/repository`.
- Thêm [CheckoutService.java](/home/vthiet/develop/java/SouvenirE-commerce/src/main/java/nlu/fit/web/souvenirecommerce/features/order/service/CheckoutService.java):
    - validate user/cart/address
    - tạo order + order items
    - tạo payment transaction
    - trừ tồn kho và tăng `totalSold`
    - tạo status mặc định
- Cập nhật [CheckoutController.java](/home/vthiet/develop/java/SouvenirE-commerce/src/main/java/nlu/fit/web/souvenirecommerce/features/order/controller/CheckoutController.java):
    - dùng service Hibernate
    - chuẩn hóa lấy user session như `AddressController`
    - redirect login nếu chưa đăng nhập
    - clear cart sau khi đặt hàng thành công
- Mở rộng [AddressService.java](/home/vthiet/develop/java/SouvenirE-commerce/src/main/java/nlu/fit/web/souvenirecommerce/features/user/address/AddressService.java) và [AddressRepository.java](/home/vthiet/develop/java/SouvenirE-commerce/src/main/java/nlu/fit/web/souvenirecommerce/features/user/address/AddressRepository.java) để checkout dùng địa chỉ đã lưu hoặc tạo địa chỉ mới.
- Cập nhật [checkout.jsp](/home/vthiet/develop/java/SouvenirE-commerce/src/main/webapp/checkout.jsp):
    - chọn địa chỉ đã lưu hoặc nhập địa chỉ mới
    - load phường/xã qua `/user/address/wards`
    - COD hoạt động
    - VNPay QR để disabled/placeholder, sẵn sàng gắn gateway sau
- Thêm [order-success.jsp](/home/vthiet/develop/java/SouvenirE-commerce/src/main/webapp/WEB-INF/views/order-success.jsp).

**VNPay future-ready**
- Đã có abstraction:
    - `PaymentGateway`
    - `PaymentGatewayRegistry`
    - `PaymentPreparation`
    - `PaymentTransaction`
- Khi thêm VNPay sandbox, chỉ cần thêm gateway mới cho `PaymentMethod.VNPAY_QR` để sinh `paymentUrl` hoặc `qrPayload`, rồi đăng ký vào `PaymentGatewayRegistry`.

**Kiểm tra**
- Đã chạy thành công:
```bash
./mvnw -q -DskipTests package
```