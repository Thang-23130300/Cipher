document.addEventListener("DOMContentLoaded", () => {
    const filterForm = document.querySelector(".filter-sidebar form");
    const content = document.querySelector(".product-type-content");
    if (!filterForm || !content) return;

    const pageInput = filterForm.querySelector("input[name='page']");
    const priceRange = filterForm.querySelector("select[name='priceRange']");
    const minPrice = filterForm.querySelector("input[name='minPrice']");
    const maxPrice = filterForm.querySelector("input[name='maxPrice']");

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
        ["keyword", "panel", "id", "page", "minPrice", "maxPrice", "rating", "sort"].forEach((name) => {
            const control = filterForm.querySelector(`[name='${name}']`);
            if (control) control.value = params.get(name) || (name === "sort" ? "popular" : "");
        });

        if (priceRange) {
            const min = params.get("minPrice") || "";
            const max = params.get("maxPrice") || "";
            priceRange.value = params.get("priceRange")
                || (min === "" && max === "100000" ? "under-100" : "")
                || (min === "100000" && max === "300000" ? "100-300" : "")
                || (min === "300000" && max === "500000" ? "300-500" : "")
                || (min === "500000" && max === "" ? "over-500" : "");
        }
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

    priceRange?.addEventListener("change", () => {
        if (minPrice) minPrice.value = "";
        if (maxPrice) maxPrice.value = "";
    });

    [minPrice, maxPrice].forEach((input) => {
        input?.addEventListener("input", () => {
            if (priceRange) priceRange.value = "";
        });
    });

    filterForm.addEventListener("submit", (event) => {
        event.preventDefault();
        if (pageInput) pageInput.value = "1";
        loadFragment(buildUrl());
    });

    filterForm.querySelector("select[name='sort']")?.addEventListener("change", () => {
        filterForm.requestSubmit();
    });

    document.addEventListener("click", (event) => {
        const link = event.target.closest("#productTypeResults .pagination a, #productTypeResults .active-filter-clear");
        if (!link) return;

        event.preventDefault();
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
