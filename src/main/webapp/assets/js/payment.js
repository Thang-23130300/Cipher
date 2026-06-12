(function () {
    const form = document.getElementById('checkoutForm');
    if (!form) return;

    const contextPath = document.querySelector('meta[name="context-path"]')?.content || '';
    const newAddressFields = document.getElementById('newAddressFields');
    const addressInputs = newAddressFields.querySelectorAll('input, select');
    const savedAddressRadios = document.querySelectorAll('input[name="savedAddressId"]');
    const provinceSelect = document.getElementById('provinceCode');
    const wardSelect = document.getElementById('wardCode');
    const wardStatus = document.getElementById('wardStatus');
    const submitButton = document.getElementById('submitOrder');
    const submitText = submitButton.querySelector('span');

    function syncAddressMode() {
        const selected = document.querySelector('input[name="savedAddressId"]:checked');
        const usingSavedAddress = Boolean(selected && selected.value);
        addressInputs.forEach(input => {
            input.disabled = usingSavedAddress;
            input.required = !usingSavedAddress;
        });
        newAddressFields.classList.toggle('is-disabled', usingSavedAddress);
    }

    function syncPaymentButton() {
        const method = document.querySelector('input[name="paymentMethod"]:checked')?.value;
        submitText.textContent = method === 'VNPAY_QR' ? 'Thanh toán qua VNPay' : 'Đặt hàng';
    }

    savedAddressRadios.forEach(radio => radio.addEventListener('change', syncAddressMode));
    document.querySelectorAll('input[name="paymentMethod"]').forEach(radio => {
        radio.addEventListener('change', syncPaymentButton);
    });

    provinceSelect.addEventListener('change', async function () {
        wardSelect.innerHTML = '<option value="">Chọn Phường/Xã</option>';
        wardStatus.classList.remove('is-error');
        if (!this.value) {
            wardStatus.textContent = '';
            return;
        }

        wardSelect.disabled = true;
        wardStatus.textContent = 'Đang tải danh sách phường/xã...';
        try {
            const response = await fetch(contextPath + '/user/address/wards?provinceCode=' + encodeURIComponent(this.value));
            if (!response.ok) throw new Error('Request failed');
            const data = await response.json();
            (data.results || []).forEach(ward => {
                const option = document.createElement('option');
                option.value = ward.id;
                option.textContent = ward.text;
                wardSelect.appendChild(option);
            });
            wardStatus.textContent = data.results?.length ? '' : 'Không tìm thấy phường/xã.';
        } catch (error) {
            wardStatus.textContent = 'Không thể tải phường/xã. Vui lòng thử lại.';
            wardStatus.classList.add('is-error');
        } finally {
            wardSelect.disabled = false;
        }
    });

    form.addEventListener('submit', function () {
        submitButton.disabled = true;
        submitText.textContent = 'Đang xử lý...';
    });

    syncAddressMode();
    syncPaymentButton();
}());
