// ===== MENU TOGGLE =====
$(document).ready(function () {
    $('#menuBtn').on('click', function () {
        $('#dropdownMenu').toggleClass('show');
        $('#headerOverlay').toggleClass('show');
    });

    $('#headerOverlay').on('click', function () {
        $('#dropdownMenu').removeClass('show');
        $(this).removeClass('show');
    });
});

// ===== TOAST MESSAGE =====
function showToast(message) {
    const toast = $('<div class="toast-message">' + message + '</div>');
    $('body').append(toast);

    setTimeout(() => toast.addClass('show'), 50);
    setTimeout(() => {
        toast.removeClass('show');
        toast.remove();
    }, 2600);
}
