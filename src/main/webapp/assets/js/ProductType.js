document.addEventListener("DOMContentLoaded", () => {
    const filterForm = document.querySelector(".filter-sidebar form");
    const content = document.querySelector(".product-type-content");
    if (!filterForm || !content) return;

    const sortSelect = filterForm.querySelector("select[name='sort']");
    const ratingRadios = [...filterForm.querySelectorAll("input[name='rating']")];
    const priceRangeRadios = [...filterForm.querySelectorAll("input[name='priceRange']")];
    const minPrice = filterForm.querySelector("input[name='minPrice']");
    const maxPrice = filterForm.querySelector("input[name='maxPrice']");

    function resetPageToFirst() {
        const pageInput = filterForm.querySelector("input[name='page']");
        if (pageInput) pageInput.value = "1";
    }

    function buildUrl() {
        const url = new URL(filterForm.action, window.location.origin);
        const formData = new FormData(filterForm);
        url.search = "";

        formData.forEach((value, key) => {
            const normalized = String(value || "").trim();
            if (normalized) url.searchParams.set(key, normalized);
        });

        url.searchParams.set("ajax", "true");
        return url;
    }

    function cleanUrl(url) {
        const clean = new URL(url.toString());
        clean.searchParams.delete("ajax");
        return clean;
    }

    function syncFilterForm(url) {
        const params = url.searchParams;
        ["keyword", "panel", "id", "page", "minPrice", "maxPrice", "sort"].forEach((name) => {
            const control = filterForm.querySelector(`[name='${name}']`);
            if (control) {
                control.value = params.get(name)
                    || (name === "sort" ? "popular" : (name === "page" ? "1" : ""));
            }
        });

        const min = params.get("minPrice") || "";
        const max = params.get("maxPrice") || "";
        const selectedPriceRange = params.get("priceRange")
            || (min === "" && max === "100000" ? "under-100" : "")
            || (min === "100000" && max === "300000" ? "100-300" : "")
            || (min === "300000" && max === "500000" ? "300-500" : "")
            || (min === "500000" && max === "" ? "over-500" : "");
        priceRangeRadios.forEach((radio) => {
            radio.checked = radio.value === selectedPriceRange;
        });

        const rating = params.get("rating") || "";
        ratingRadios.forEach((radio) => {
            radio.checked = radio.value === rating;
        });
    }

    async function loadFragment(url, pushState = true) {
        content.classList.add("is-loading");
        try {
            const response = await fetch(url, {
                headers: {"X-Requested-With": "XMLHttpRequest"}
            });
            if (!response.ok) throw new Error("Search filter failed");

            const current = document.getElementById("productTypeResults");
            if (!current) throw new Error("Search result container is missing");
            current.outerHTML = await response.text();

            const browserUrl = cleanUrl(url);
            syncFilterForm(browserUrl);

            if (pushState) window.history.pushState({}, "", browserUrl);
        } catch (error) {
            window.location.href = cleanUrl(url).toString();
        } finally {
            content.classList.remove("is-loading");
        }
    }

    priceRangeRadios.forEach((radio) => {
        radio.addEventListener("change", () => {
            if (minPrice) minPrice.value = radio.dataset.minPrice || "";
            if (maxPrice) maxPrice.value = radio.dataset.maxPrice || "";
        });
    });

    [minPrice, maxPrice].forEach((input) => {
        input?.addEventListener("input", () => {
            priceRangeRadios.forEach((radio) => {
                radio.checked = false;
            });
        });
    });

    filterForm.addEventListener("submit", (event) => {
        event.preventDefault();
        resetPageToFirst();
        loadFragment(buildUrl());
    });

    sortSelect?.addEventListener("change", () => {
        filterForm.requestSubmit();
    });

    ratingRadios.forEach((radio) => {
        radio.addEventListener("change", () => {
            filterForm.requestSubmit();
        });
    });

    document.addEventListener("click", (event) => {
        const link = event.target.closest("#productTypeResults .pagination a, #productTypeResults .active-filter-clear");
        if (!link) return;

        event.preventDefault();
        if (link.classList.contains("active-filter-clear")) {
            if (minPrice) minPrice.value = "";
            if (maxPrice) maxPrice.value = "";
            priceRangeRadios.forEach((radio) => radio.checked = false);
            ratingRadios.forEach((radio) => radio.checked = radio.value === "");
            if (sortSelect) sortSelect.value = "popular";
            resetPageToFirst();
        }
        const url = new URL(link.href, window.location.origin);
        url.searchParams.set("ajax", "true");
        loadFragment(url);
    });

    window.addEventListener("popstate", () => {
        const url = new URL(window.location.href);
        url.searchParams.set("ajax", "true");
        loadFragment(url, false);
    });
});
