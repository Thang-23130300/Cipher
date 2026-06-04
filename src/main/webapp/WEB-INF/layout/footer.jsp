<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<footer class="site-footer">
    <div class="layout-shell footer-main">
        <section class="footer-brand">
            <a href="${pageContext.request.contextPath}/home" class="footer-logo" aria-label="Trang chủ ${not empty settings.site_name ? settings.site_name : "Trang chủ"}">
                <c:choose>
                    <c:when test="${not empty settings.site_logo_url}">
                        <img src="${settings.site_logo_url}" alt="${not empty settings.site_name ? settings.site_name : "Site Logo"}">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="${not empty settings.site_name ? settings.site_name : "Site Logo"}">
                    </c:otherwise>
                </c:choose>
            </a>
            <p>${not empty settings.meta_description ? settings.meta_description : "INOLA giới thiệu đặc sản, quà tặng và sản phẩm thủ công Việt Nam với trải nghiệm mua sắm rõ ràng, tiện lợi."}</p>
            <div class="footer-social">
                <c:if test="${not empty settings.social_facebook}">
                    <a href="${settings.social_facebook}" aria-label="Facebook" target="_blank" rel="noopener"><i class="fa-brands fa-facebook-f"></i></a>
                </c:if>
                <c:if test="${not empty settings.social_instagram}">
                    <a href="${settings.social_instagram}" aria-label="Instagram" target="_blank" rel="noopener"><i class="fa-brands fa-instagram"></i></a>
                </c:if>
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
                <c:if test="${settings.payment_card == "true"}">
                    <span><img src="${pageContext.request.contextPath}/assets/images/Payment/visa.jpg" alt="Visa"></span>
                    <span><img src="${pageContext.request.contextPath}/assets/images/Payment/mastercard.png" alt="Mastercard"></span>
                </c:if>
                <c:if test="${settings.payment_momo == "true"}">
                    <span><img src="${pageContext.request.contextPath}/assets/images/Payment/momo.webp" alt="MoMo"></span>
                </c:if>
                <c:if test="${settings.payment_vnpay == "true"}">
                    <span><img src="${pageContext.request.contextPath}/assets/images/Payment/vnpay.webp" alt="VNPAY"></span>
                </c:if>
                <c:if test="${settings.payment_cod == "true"}">
                    <span class="payment-label">Thanh toán khi nhận hàng</span>
                </c:if>
            </div>

            <h2 class="footer-subtitle">Vận chuyển</h2>
            <div class="footer-icon-grid">
                <c:if test="${settings.shipping_ghtk == "true"}">
                    <span><img src="${pageContext.request.contextPath}/assets/images/Transport/ghtk.webp" alt="GHTK"></span>
                </c:if>
                <c:if test="${settings.shipping_ghn == "true"}">
                    <span><img src="${pageContext.request.contextPath}/assets/images/Transport/ghn.png" alt="GHN"></span>
                </c:if>
                <c:if test="${settings.shipping_jnt == "true"}">
                    <span><img src="${pageContext.request.contextPath}/assets/images/Transport/jnt.webp" alt="J&T Express"></span>
                </c:if>
            </div>
        </section>
    </div>

    <div class="footer-bottom">
        <div class="layout-shell footer-bottom__inner">
            <span>Địa chỉ: ${not empty settings.site_address ? settings.site_address : "Đại học Nông Lâm Thành phố Hồ Chí Minh"}</span>
            <span>Điện thoại: ${not empty settings.site_phone ? settings.site_phone : "Chưa cập nhật"}</span>
            <span>Email: <a href="mailto:${settings.site_email}">${not empty settings.site_email ? settings.site_email : "info@yourdomain.com"}</a></span>
            <span>© 2026 ${not empty settings.site_name ? settings.site_name : "INOLA"} - Group 32</span>
        </div>
    </div>
</footer>
