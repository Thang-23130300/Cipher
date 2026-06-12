document.addEventListener("DOMContentLoaded", () => {

    const filterForm = document.querySelector(".filter-sidebar form");
    if (!filterForm) return;

    const sortSelect   = filterForm.querySelector("select[name='sort']");
    const ratingSelect = filterForm.querySelector("select[name='rating']");

    /**
     * Reset page về 1 khi filter thay đổi
     */
    function resetPageToFirst() {
        let pageInput = filterForm.querySelector("input[name='page']");

        if (!pageInput) {
            pageInput = document.createElement("input");
            pageInput.type = "hidden";
            pageInput.name = "page";
            filterForm.appendChild(pageInput);
        }

        pageInput.value = "1";
    }

    /**
     * Bind change event cho select
     */
    function autoSubmitOnChange(selectElement) {
        if (!selectElement) return;

        selectElement.addEventListener("change", () => {
            resetPageToFirst();
            filterForm.submit();
        });
    }

    // Auto submit khi đổi sort / rating
    autoSubmitOnChange(sortSelect);
    autoSubmitOnChange(ratingSelect);

    // Khi submit thủ công (bấm nút Áp dụng)
    filterForm.addEventListener("submit", () => {
        resetPageToFirst();
    });

});
