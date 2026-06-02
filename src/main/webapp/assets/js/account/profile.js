document.addEventListener('DOMContentLoaded', function () {
    // Confirm dialog for links with data-confirm attribute
    document.querySelectorAll('[data-confirm]').forEach(function (link) {
        link.addEventListener('click', function (event) {
            if (!window.confirm(link.getAttribute('data-confirm'))) {
                event.preventDefault();
            }
        });
    });

    const addressForm = document.querySelector('[data-address-form]');
    if (!addressForm) {
        return;
    }

    if (!window.jQuery || !jQuery.fn.select2) {
        return;
    }

    const provinceSelect = jQuery(addressForm).find('#provinceSelect');
    const wardSelect = jQuery(addressForm).find('#wardSelect');
    const wardsUrl = addressForm.dataset.wardsUrl;

    provinceSelect.select2({
        width: '100%',
        placeholder: 'Chọn tỉnh/thành phố',
        allowClear: true
    });

    wardSelect.select2({
        width: '100%',
        placeholder: 'Chọn phường/xã',
        allowClear: true,
        ajax: {
            url: wardsUrl,
            dataType: 'json',
            delay: 200,
            data: function (params) {
                return {
                    provinceCode: provinceSelect.val(),
                    q: params.term || ''
                };
            },
            processResults: function (data) {
                return data;
            }
        }
    });

    provinceSelect.on('change', function () {
        wardSelect.val(null).trigger('change');
        wardSelect.prop('disabled', !provinceSelect.val());
    });
});
