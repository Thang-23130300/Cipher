document.addEventListener('DOMContentLoaded', () => {
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || '';

    const formatVND = (value) =>
        Number(value || 0).toLocaleString('vi-VN') + '₫';

    const updateSummary = (total, totalQty) => {
        const qtyEl = document.getElementById('cart-total-qty');
        const subtotalEl = document.getElementById('cart-subtotal');
        const totalEl = document.getElementById('cart-total');
        const payTotalEl = document.getElementById('cart-total-pay');
        const headerCountEl = document.getElementById('header-cart-count');

        if (qtyEl) qtyEl.textContent = `Tổng cộng: ${totalQty} sản phẩm`;
        if (subtotalEl) subtotalEl.textContent = formatVND(total);
        if (totalEl) totalEl.textContent = formatVND(total);
        if (payTotalEl) payTotalEl.textContent = formatVND(total);
        if (headerCountEl) headerCountEl.textContent = totalQty;
    };

    const calcClientSummary = () => {
        let totalQty = 0;
        let totalPrice = 0;

        document.querySelectorAll('.cart-item-card').forEach(card => {
            const qtyInput = card.querySelector('.qty-input');
            if (!qtyInput) return;

            const qty = parseInt(qtyInput.value, 10) || 0;
            const unitPrice = parseInt(
                card.querySelector('.item-details p')?.innerText.replace(/[^\d]/g, '') || '0',
                10
            ) || 0;

            totalQty += qty;
            totalPrice += qty * unitPrice;
        });

        updateSummary(totalPrice, totalQty);
    };

    document.querySelectorAll('.cart-item-card').forEach(card => {
        const productId = card.dataset.productId;
        if (!productId) return;

        const minusBtn = card.querySelector('.minus-btn');
        const plusBtn = card.querySelector('.plus-btn');
        const qtyInput = card.querySelector('.qty-input');
        const priceEl = card.querySelector('.item-price');
        const removeBtn = card.querySelector('.remove-item-btn');
        const unitPrice = parseInt(
            card.querySelector('.item-details p')?.innerText.replace(/[^\d]/g, '') || '0',
            10
        ) || 0;

        const updateItemUI = (qty) => {
            if (qty <= 0) {
                card.remove();
                return;
            }

            qtyInput.value = qty;
            priceEl.textContent = formatVND(unitPrice * qty);
        };

        const syncBackend = (qty) => {
            fetch(`${contextPath}/cart/update`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: `productId=${encodeURIComponent(productId)}&quantity=${encodeURIComponent(qty)}`
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        updateSummary(data.total, data.totalQuantity);
                    }
                })
                .catch(err => {
                    console.error(err);
                    alert('Lỗi kết nối server');
                });
        };

        minusBtn?.addEventListener('click', () => {
            const newQty = parseInt(qtyInput.value, 10) - 1;
            if (newQty < 1) return;

            updateItemUI(newQty);
            calcClientSummary();
            syncBackend(newQty);
        });

        plusBtn?.addEventListener('click', () => {
            const newQty = parseInt(qtyInput.value, 10) + 1;

            updateItemUI(newQty);
            calcClientSummary();
            syncBackend(newQty);
        });

        qtyInput?.addEventListener('change', () => {
            let newQty = parseInt(qtyInput.value, 10);
            if (Number.isNaN(newQty) || newQty < 1) newQty = 1;

            updateItemUI(newQty);
            calcClientSummary();
            syncBackend(newQty);
        });

        removeBtn?.addEventListener('click', () => {
            if (!confirm('Xóa sản phẩm này?')) return;

            updateItemUI(0);
            calcClientSummary();
            syncBackend(0);
        });
    });
});
