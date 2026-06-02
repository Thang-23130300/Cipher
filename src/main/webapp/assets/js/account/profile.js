document.addEventListener('DOMContentLoaded', function () {
    // Confirm dialog for links with data-confirm attribute
    document.querySelectorAll('[data-confirm]').forEach(function (link) {
        link.addEventListener('click', function (event) {
            if (!window.confirm(link.getAttribute('data-confirm'))) {
                event.preventDefault();
            }
        });
    });
});
