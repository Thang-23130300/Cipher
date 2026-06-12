/**
 * Search Autocomplete – FINAL FIX (100% SYNC)
 */
document.addEventListener('DOMContentLoaded', function () {

    const searchInput = document.querySelector('.search-bar');
    if (!searchInput) return;

    const wrapper = searchInput.closest('.search-wrapper');
    if (!wrapper) return;

    // ===== CONTEXT PATH (FROM JSP) =====
    const contextInput = document.getElementById('contextPath');
    const contextPath = contextInput ? contextInput.value : '';

    let debounceTimer = null;
    let suggestionsBox = null;
    let selectedIndex = -1;

    // ===== CREATE BOX (GẮN VÀO SEARCH-WRAPPER) =====
    function createSuggestionsBox() {
        if (suggestionsBox) return suggestionsBox;

        suggestionsBox = document.createElement('div');
        suggestionsBox.className = 'search-suggestions';
        suggestionsBox.style.display = 'none';

        wrapper.appendChild(suggestionsBox);

        return suggestionsBox;
    }

    // ===== FORMAT PRICE =====
    function formatPrice(price) {
        return new Intl.NumberFormat('vi-VN').format(price) + ' ₫';
    }

    // ===== NORMALIZE IMAGE =====
    function normalizeImage(img) {
        if (!img) return '';

        img = img.replace(/\s+/g, '');

        const match = img.match(/(\/assets\/.*?\.(jpg|jpeg|png|webp))/i);
        return match ? match[1] : img;
    }

    // ===== RENDER =====
    function showSuggestions(products) {
        const box = document.getElementById('searchSuggestionsBox') || createSuggestionsBox();

        // 1. Kiểm tra dữ liệu đầu vào
        if (!Array.isArray(products) || products.length === 0) {
            box.style.display = 'none';
            box.innerHTML = ''; // Xóa trắng nội dung cũ
            return;
        }

        selectedIndex = -1;

        // 2. Render HTML dựa trên layout đã có
        box.innerHTML = `
        <div class="suggestions-list">
            ${products.map(p => {
            // Đảm bảo có giá trị image, nếu không dùng placeholder
            const imagePath = p.image ? p.image : '/assets/images/placeholder.png';

            // Sử dụng p.price (đã gán ở Backend) hoặc p.salePrice làm dự phòng
            const displayPrice = p.price || p.salePrice || 0;

            return `
                    <a href="${contextPath}/product-detail?id=${encodeURIComponent(p.id)}"
                       class="suggestion-item">
                        <img src="${contextPath}${imagePath}"
                             onerror="this.src='${contextPath}/assets/images/placeholder.png'">
                        <div class="suggestion-info">
                            <div class="suggestion-name" title="${p.name}">${p.name}</div>
                            <div class="suggestion-price">
                                ${formatPrice(displayPrice)}
                            </div>
                        </div>
                    </a>
                `;
        }).join('')}
        </div>
    `;

        box.style.display = 'block';
    }

    function hideSuggestions() {
        if (suggestionsBox) {
            suggestionsBox.style.display = 'none';
        }
    }

    // ===== FETCH =====
    function fetchSuggestions(keyword) {
        if (!keyword || keyword.trim().length < 2) {
            hideSuggestions();
            return;
        }

        fetch(`${contextPath}/search-suggestions?q=${encodeURIComponent(keyword.trim())}`)
            .then(res => res.ok ? res.json() : [])
            .then(showSuggestions)
            .catch(hideSuggestions);
    }

    // ===== EVENTS =====
    searchInput.addEventListener('input', function (e) {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
            fetchSuggestions(e.target.value);
        }, 300);
    });

    searchInput.addEventListener('focus', function () {
        if (searchInput.value.trim().length >= 2) {
            fetchSuggestions(searchInput.value);
        }
    });

    document.addEventListener('click', function (e) {
        if (!wrapper.contains(e.target)) {
            hideSuggestions();
        }
    });

    searchInput.addEventListener('keydown', function (e) {
        if (!suggestionsBox || suggestionsBox.style.display === 'none') return;

        const items = suggestionsBox.querySelectorAll('.suggestion-item');
        if (!items.length) return;

        if (e.key === 'ArrowDown') {
            e.preventDefault();
            selectedIndex = Math.min(selectedIndex + 1, items.length - 1);
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            selectedIndex = Math.max(selectedIndex - 1, -1);
        } else if (e.key === 'Enter' && selectedIndex >= 0) {
            e.preventDefault();
            items[selectedIndex].click();
        } else if (e.key === 'Escape') {
            hideSuggestions();
            return;
        }

        items.forEach((item, i) => {
            item.classList.toggle('selected', i === selectedIndex);
            if (i === selectedIndex) {
                item.scrollIntoView({ block: 'nearest' });
            }
        });
    });

});
