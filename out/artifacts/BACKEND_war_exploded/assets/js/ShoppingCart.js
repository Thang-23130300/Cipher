document.addEventListener('DOMContentLoaded', () => {

    /* ================== CONFIG ================== */

    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || '';

    /* ================== HELPERS ================== */

    const formatVND = (value) =>
        value.toLocaleString('vi-VN') + '₫';

    /* ===== UPDATE SUMMARY (HEADER + RIGHT) ===== */

    const updateSummary = (total, totalQty) => {
        const qtyEl       = document.getElementById('cart-total-qty');
        const subtotalEl  = document.getElementById('cart-subtotal');
        const totalEl     = document.getElementById('cart-total');
        const payTotalEl  = document.getElementById('cart-total-pay');

        if (qtyEl)      qtyEl.textContent = `Tổng cộng: ${totalQty} sản phẩm`;
        if (subtotalEl) subtotalEl.textContent = formatVND(total);
        if (totalEl)    totalEl.textContent = formatVND(total);
        if (payTotalEl) payTotalEl.textContent = formatVND(total);
    };

    /* ===== CLIENT-SIDE CALC (SOURCE OF TRUTH UI) ===== */

    const calcClientSummary = () => {
        let totalQty = 0;
        let totalPrice = 0;

        document.querySelectorAll('.cart-item-card').forEach(card => {
            const qtyInput = card.querySelector('.qty-input');
            if (!qtyInput) return;

            const qty = parseInt(qtyInput.value, 10) || 0;

            const unitPriceText =
                card.querySelector('.item-details p')
                    ?.innerText.replace(/[^\d]/g, '') || '0';

            const unitPrice = parseInt(unitPriceText, 10) || 0;

            totalQty += qty;
            totalPrice += qty * unitPrice;
        });

        updateSummary(totalPrice, totalQty);
    };

    /* ================== MAIN ================== */

    document.querySelectorAll('.cart-item-card').forEach(card => {

        const productId = card.dataset.productId;
        if (!productId) return;

        const minusBtn  = card.querySelector('.minus-btn');
        const plusBtn   = card.querySelector('.plus-btn');
        const qtyInput  = card.querySelector('.qty-input');
        const priceEl   = card.querySelector('.item-price');
        const removeBtn = card.querySelector('.remove-item-btn');

        /* ===== UNIT PRICE ===== */

        const unitPriceText =
            card.querySelector('.item-details p')
                ?.innerText.replace(/[^\d]/g, '') || '0';

        const unitPrice = parseInt(unitPriceText, 10) || 0;

        /* ===== OPTIMISTIC UI ===== */

        const updateItemUI = (qty) => {
            if (qty <= 0) {
                card.remove();
            } else {
                qtyInput.value = qty;
                priceEl.textContent = formatVND(unitPrice * qty);
            }
        };

        /* ===== BACKEND SYNC (NO UI UPDATE) ===== */

        const syncBackend = (qty) => {
            fetch(`${contextPath}/cart/update`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: `productId=${productId}&quantity=${qty}`
            }).catch(err => {
                console.error(err);
                alert('Lỗi kết nối server');
            });
        };

        /* ================== EVENTS ================== */

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
            if (isNaN(newQty) || newQty < 1) newQty = 1;

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
