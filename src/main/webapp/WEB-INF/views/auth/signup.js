let currentStep = 1;
const totalSteps = 3;
const contextPath = window.appContextPath || '';
let verifiedEmail = '';
let resendTimer = null;
let resendSeconds = 0;

const clearMessage = () => $('#messageArea').html('');

const showMessage = (message, type = 'warning') =>
    $('#messageArea').html(`<p class="${type}-text">${message}</p>`);

const togglePassword = (inputId, button) => {
    const input = document.getElementById(inputId);
    const icon = button.querySelector('i');
    input.type = (input.type === 'password') ? 'text' : 'password';
    icon.classList.toggle('fa-eye');
    icon.classList.toggle('fa-eye-slash');
}

const checkPasswordStrength = (password) => {
    const strengthDiv = $('#passwordStrength');
    if (!password) {
        strengthDiv.html('').removeClass().addClass('password-strength');
        return;
    }
    if (password.length < 8){
        strengthDiv.html('Mật khẩu cần ít nhất 8 ký tự.').removeClass().addClass('password-strength weak');
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

const goToStep = (step) => {
    clearMessage();
    $('.form-step').removeClass('active');
    $(`#step${step}Content`).addClass('active');
    for (let i = 1; i <= totalSteps; i++){
        $(`#step${i}`).removeClass('active completed');
        if (i < step) $(`#step${i}`).addClass('completed');
        else if (i === step) $(`#step${i}`).addClass('active');
    }
    currentStep = step;
}

const setLoading = (target, loading) => {
    $(target.loading).toggle(loading);
    target.buttons.forEach((button) => $(button).prop('disabled', loading));
}

const updateSendCodeButton = () =>
    $('#sendCodeBtn').find('span').text(`Gửi lại ${resendSeconds}s`);

const startResendCountdown = () => {
    resendSeconds = 60;
    $('#sendCodeBtn').prop('disabled', true);
    updateSendCodeButton();
    clearInterval(resendTimer);
    resendTimer = setInterval(() => {
        resendSeconds -= 1;
        updateSendCodeButton();
        if (resendSeconds <= 0) {
            clearInterval(resendTimer);
            $('#sendCodeBtn').prop('disabled', false).find('span').text('Gửi mã');
        }
    }, 1000);
}

const getEmail = () => $('#email').val().trim();

const validateEmail = (email) => {
    if (!email) {
        showMessage('Email không được để trống', 'error');
        return false;
    }
    if (!/^[A-Za-z0-9+_.-]+@(.+)$/.test(email)) {
        showMessage('Email không hợp lệ', 'error');
        return false;
    }
    return true;
};

const sendVerificationCode = (showSuccessMessage) => {
    const email = getEmail();
    if (!validateEmail(email)) return;

    setLoading({
        loading: '#codeLoading',
        buttons: ['#sendCodeBtn', '#verifyCodeBtn', '#backToEmailBtn']
    }, true);

    $.ajax({
        url: `${contextPath}/api/signup/send-code`,
        type: 'POST',
        data: { email: email},
        dataType: 'json',
        success: function (response) {
            setLoading({
                loading: '#codeLoading',
                buttons: ['#verifyCodeBtn', '#backToEmailBtn']
            }, false);

            if (response.status === 'success'){
                if (showSuccessMessage) showMessage(response.message, 'success');
                startResendCountdown();
                $('#verificationCode').focus();
            } else {
                $('#sendCodeBtn').prop('disabled', false);
                showMessage(response.message, 'error');
            }
        },
        error: () => {
            setLoading({ loading: '#codeLoading', buttons: ['#sendCodeBtn', '#verifyCodeBtn', '#backToEmailBtn'] }, false);
            showMessage('Không gửi được mã xác thực.', 'error');
        }
    });
}

$('#continueBtn').click(function () {
    const email = getEmail();
    if (!validateEmail(email)) return;
    $('#emailCheckLoading').show();
    $(this).prop('disabled', true);
    $.ajax({
        url: `${contextPath}/api/check-email`,
        type: 'POST',
        data: {email: email},
        success: (response) => {
            $('#emailCheckLoading').hide();
            $(this).prop('disabled', false);
            if (response.status === 'success'){
                verifiedEmail = email;
                $('#selectedEmail').text(email);
                goToStep(2);
                sendVerificationCode(true);
            } else showMessage(response.message, 'error');
        },
        error: () => { $('#emailCheckLoading').hide(); $(this).prop('disabled', false); }
    });
});

$('#submitBtn').click(function () {
    const email = getEmail();
    const lastName = $('#last_name').val().trim();
    const firstName = $('#first_name').val().trim();
    const phone = $('#phone').val().trim();
    const password = $('#password').val();
    const confirmPassword = $('#confirm_password').val();

    if (verifiedEmail !== email) {
        showMessage('Email chưa xác thực', 'error');
        return;
    }

    setLoading({ loading: '#submitLoading', buttons: ['#submitBtn', '#backBtn'] }, true);

    $.ajax({
        url: `${contextPath}/api/signup`,
        type: 'POST',
        data: { email, firstName, lastName, phone, password, confirmPassword },
        success: (response) => {
            setLoading({ loading: '#submitLoading', buttons: ['#submitBtn'] }, false);
            if (response.status === 'success') {
                showMessage('Đăng ký thành công!', 'success');
                setTimeout(() => window.location.href = `${contextPath}/login`, 1600);
            } else showMessage(response.message, 'error');
        },
        error: () => setLoading({ loading: '#submitLoading', buttons: ['#submitBtn'] }, false)
    });
});

$('#password').on('keyup', function() { checkPasswordStrength($(this).val()); });
$('#email').on('input', () => verifiedEmail = '');
$('#verificationCode').on('input', function() { this.value = this.value.replace(/\D/g, '').slice(0, 6); });