<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

<footer class="site-footer">
    <div class="layout-shell footer-main">
        <section class="footer-brand">
            <a href="${pageContext.request.contextPath}/home" class="footer-logo" aria-label="Trang chủ INOLA">
                <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="INOLA">
            </a>
            <p>INOLA giới thiệu đặc sản, quà tặng và sản phẩm thủ công Việt Nam với trải nghiệm mua sắm rõ ràng, tiện lợi.</p>
            <div class="footer-social">
                <a href="#" aria-label="Facebook"><i class="fa-brands fa-facebook-f"></i></a>
                <a href="#" aria-label="Instagram"><i class="fa-brands fa-instagram"></i></a>
                <a href="#" aria-label="TikTok"><i class="fa-brands fa-tiktok"></i></a>
                <a href="#" aria-label="YouTube"><i class="fa-brands fa-youtube"></i></a>
            </div>
        </section>

        <section class="footer-col">
            <h2>Mua hàng</h2>
            <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
            <a href="${pageContext.request.contextPath}/category">Danh mục</a>
            <a href="${pageContext.request.contextPath}/cart">Giỏ hàng</a>
            <a href="${pageContext.request.contextPath}/user/orders">Đơn hàng</a>
        </section>

        <section class="footer-col">
            <h2>Hỗ trợ</h2>
            <a href="#">Chính sách đổi trả</a>
            <a href="#">Phương thức vận chuyển</a>
            <a href="#">Hướng dẫn thanh toán</a>
            <a href="#">Liên hệ</a>
        </section>

        <section class="footer-col footer-payments">
            <h2>Thanh toán</h2>
            <div class="footer-icon-grid">
                <span><img src="${pageContext.request.contextPath}/assets/images/Payment/visa.jpg" alt="Visa"></span>
                <span><img src="${pageContext.request.contextPath}/assets/images/Payment/mastercard.png" alt="Mastercard"></span>
                <span><img src="${pageContext.request.contextPath}/assets/images/Payment/momo.webp" alt="Momo"></span>
                <span><img src="${pageContext.request.contextPath}/assets/images/Payment/vnpay.webp" alt="VNPAY"></span>
            </div>

            <h2 class="footer-subtitle">Vận chuyển</h2>
            <div class="footer-icon-grid">
                <span><img src="${pageContext.request.contextPath}/assets/images/Transport/ghtk.webp" alt="GHTK"></span>
                <span><img src="${pageContext.request.contextPath}/assets/images/Transport/ghn.png" alt="GHN"></span>
                <span><img src="${pageContext.request.contextPath}/assets/images/Transport/jnt.webp" alt="J&T Express"></span>
            </div>
        </section>
    </div>

    <div class="footer-bottom">
        <div class="layout-shell footer-bottom__inner">
            <span>Địa chỉ: Đại học Nông Lâm Thành phố Hồ Chí Minh</span>
            <span>© 2026 INOLA - Group 32</span>
        </div>
    </div>
</footer>
