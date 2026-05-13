<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - INOLA</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/signup.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/HomePageFooter.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<body class="auth-page">
<main class="auth-shell signup-shell">
    <aside class="auth-media" aria-label="Sản phẩm INOLA">
        <img src="${pageContext.request.contextPath}/assets/images/products/lang-nghe-thu-cong/bo-am-chen-bach-ngoc-hoa-sen-qua-tang-cao-cap.jpg" alt="Quà tặng thủ công">
        <div class="media-caption">
            <strong>Tạo tài khoản INOLA</strong>
            <span>Lưu thông tin mua hàng, theo dõi đơn và nhận gợi ý sản phẩm phù hợp.</span>
        </div>
    </aside>

    <section class="auth-panel">
        <a class="brand-link" href="${pageContext.request.contextPath}/home" aria-label="Về trang chủ">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="INOLA">
        </a>

        <div class="auth-copy">
            <p class="eyebrow">Thành viên mới</p>
            <h1>Đăng ký</h1>
            <p class="supporting-text">Hoàn tất hai bước ngắn để bắt đầu mua sắm và lưu giỏ hàng của bạn.</p>
        </div>

        <div class="step-indicator" aria-label="Tiến trình đăng ký">
            <div class="step active" id="step1">
                <span>1</span>
                <small>Email</small>
            </div>
            <div class="step" id="step2">
                <span>2</span>
                <small>Thông tin</small>
            </div>
        </div>

        <div id="messageArea" class="message-area"></div>

        <form class="signup-form" id="signupForm">
            <div class="form-step active" id="step1Content">
                <label class="field">
                    <span>Địa chỉ email</span>
                    <span class="input-wrap">
                        <i class="fa fa-envelope"></i>
                        <input type="email" name="email" id="email" autocomplete="email" required>
                    </span>
                </label>

                <div class="loading" id="emailCheckLoading">
                    <div class="spinner"></div>
                    <span>Đang kiểm tra email...</span>
                </div>

                <button type="button" class="primary-button" id="continueBtn">
                    <span>Tiếp tục</span>
                    <i class="fa fa-arrow-right"></i>
                </button>

                <p class="switch-link">
                    Đã có tài khoản?
                    <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                </p>
            </div>

            <div class="form-step" id="step2Content">
                <label class="field">
                    <span>Họ và tên</span>
                    <span class="input-wrap">
                        <i class="fa fa-user"></i>
                        <input type="text" name="full_name" id="full_name" autocomplete="name" required>
                    </span>
                </label>

                <label class="field">
                    <span>Số điện thoại</span>
                    <span class="input-wrap">
                        <i class="fa fa-phone"></i>
                        <input type="tel" name="phone" id="phone" pattern="[0-9]{10,20}" autocomplete="tel" required>
                    </span>
                </label>

                <label class="field">
                    <span>Mật khẩu</span>
                    <span class="input-wrap password-wrap">
                        <i class="fa fa-lock"></i>
                        <input type="password" name="password" id="matkhau" minlength="8" autocomplete="new-password" required>
                        <button type="button" class="eye-button" onclick="togglePassword('matkhau', this)" aria-label="Hiện hoặc ẩn mật khẩu">
                            <i class="fa fa-eye"></i>
                        </button>
                    </span>
                    <span class="password-strength" id="passwordStrength"></span>
                </label>

                <label class="field">
                    <span>Xác nhận mật khẩu</span>
                    <span class="input-wrap password-wrap">
                        <i class="fa fa-lock"></i>
                        <input type="password" name="confirm_password" id="confirm_password" minlength="8" autocomplete="new-password" required>
                        <button type="button" class="eye-button" onclick="togglePassword('confirm_password', this)" aria-label="Hiện hoặc ẩn mật khẩu xác nhận">
                            <i class="fa fa-eye"></i>
                        </button>
                    </span>
                </label>

                <div class="loading" id="submitLoading">
                    <div class="spinner"></div>
                    <span>Đang xử lý đăng ký...</span>
                </div>

                <div class="button-grid">
                    <button type="button" class="secondary-button" id="backBtn">
                        <i class="fa fa-arrow-left"></i>
                        <span>Quay lại</span>
                    </button>
                    <button type="button" class="primary-button" id="submitBtn">
                        <i class="fa fa-user-plus"></i>
                        <span>Đăng ký</span>
                    </button>
                </div>
            </div>
        </form>
    </section>
</main>

<script>
    let currentStep = 1;
    const totalSteps = 2;

    function showMessage(message, type) {
        const messageArea = $('#messageArea');
        const classes = {
            'error': 'error-text',
            'success': 'success-text',
            'warning': 'warning-text'
        };
        messageArea.html(`<p class="${classes[type] || 'warning-text'}">${message}</p>`);
    }

    function clearMessage() {
        $('#messageArea').html('');
    }

    function togglePassword(inputId, button) {
        const input = document.getElementById(inputId);
        const icon = button.querySelector('i');
        input.type = (input.type === "password") ? "text" : "password";
        icon.classList.toggle('fa-eye');
        icon.classList.toggle('fa-eye-slash');
    }

    function checkPasswordStrength(password) {
        const strengthDiv = $('#passwordStrength');

        if (!password) {
            strengthDiv.html('').removeClass().addClass('password-strength');
            return;
        }

        if (password.length < 8) {
            strengthDiv.html('Mật khẩu cần ít nhất 8 ký tự').removeClass().addClass('password-strength weak');
            return;
        }

        let strength = 'weak';
        let message = 'Mật khẩu yếu';

        if (/[a-z]/.test(password) && /[A-Z]/.test(password) && /[0-9]/.test(password)) {
            strength = 'strong';
            message = 'Mật khẩu mạnh';
        } else if (/[a-zA-Z]/.test(password) && /[0-9]/.test(password)) {
            strength = 'medium';
            message = 'Mật khẩu trung bình';
        }

        strengthDiv.html(message).removeClass().addClass('password-strength ' + strength);
    }

    function goToStep(step) {
        clearMessage();
        $('.form-step').removeClass('active');
        $(`#step${step}Content`).addClass('active');

        for (let i = 1; i <= totalSteps; i++) {
            $(`#step${i}`).removeClass('active completed');
            if (i < step) {
                $(`#step${i}`).addClass('completed');
            } else if (i === step) {
                $(`#step${i}`).addClass('active');
            }
        }

        currentStep = step;
    }

    $('#continueBtn').click(function() {
        const email = $('#email').val().trim();

        if (!email) {
            showMessage('Email không được để trống', 'error');
            return;
        }

        if (!/^[A-Za-z0-9+_.-]+@(.+)$/.test(email)) {
            showMessage('Email không hợp lệ', 'error');
            return;
        }

        $('#emailCheckLoading').show();
        $('#continueBtn').prop('disabled', true);

        $.ajax({
            url: '${pageContext.request.contextPath}/api/check-email',
            type: 'POST',
            data: { email: email },
            dataType: 'json',
            timeout: 5000,
            success: function(response) {
                $('#emailCheckLoading').hide();
                $('#continueBtn').prop('disabled', false);

                if (response.status === 'success') {
                    showMessage('Email có thể sử dụng', 'success');
                    setTimeout(() => {
                        goToStep(2);
                    }, 500);
                } else {
                    showMessage(response.message, 'error');
                }
            },
            error: function() {
                $('#emailCheckLoading').hide();
                $('#continueBtn').prop('disabled', false);
                showMessage('Có lỗi xảy ra. Vui lòng thử lại', 'error');
            }
        });
    });

    $('#backBtn').click(function() {
        goToStep(1);
    });

    $('#submitBtn').click(function() {
        const email = $('#email').val().trim();
        const fullName = $('#full_name').val().trim();
        const phone = $('#phone').val().trim();
        const password = $('#matkhau').val();
        const confirmPassword = $('#confirm_password').val();

        if (!fullName) {
            showMessage('Họ tên không được để trống', 'error');
            return;
        }

        if (!phone || !/[0-9]{10,20}/.test(phone)) {
            showMessage('Số điện thoại phải từ 10-20 chữ số', 'error');
            return;
        }

        if (!password || password.length < 8) {
            showMessage('Mật khẩu phải ít nhất 8 ký tự', 'error');
            return;
        }

        if (password !== confirmPassword) {
            showMessage('Mật khẩu xác nhận không trùng khớp', 'error');
            return;
        }

        $('#submitLoading').show();
        $('#submitBtn').prop('disabled', true);

        $.ajax({
            url: '${pageContext.request.contextPath}/api/register',
            type: 'POST',
            data: {
                email: email,
                fullName: fullName,
                phone: phone,
                password: password,
                confirmPassword: confirmPassword
            },
            dataType: 'json',
            timeout: 5000,
            success: function(response) {
                $('#submitLoading').hide();
                $('#submitBtn').prop('disabled', false);

                if (response.status === 'success') {
                    showMessage('Đăng ký thành công. Đang chuyển sang trang đăng nhập...', 'success');
                    setTimeout(() => {
                        window.location.href = '${pageContext.request.contextPath}/login';
                    }, 1600);
                } else {
                    showMessage(response.message, 'error');
                }
            },
            error: function() {
                $('#submitLoading').hide();
                $('#submitBtn').prop('disabled', false);
                showMessage('Có lỗi xảy ra. Vui lòng thử lại', 'error');
            }
        });
    });

    $('#matkhau').on('keyup', function() {
        checkPasswordStrength($(this).val());
    });

    $('#email').keypress(function(e) {
        if (e.which === 13) {
            e.preventDefault();
            $('#continueBtn').click();
        }
    });
</script>
</body>
</html>
