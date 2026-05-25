document.addEventListener("DOMContentLoaded", () => {
    const menuButton = document.getElementById("headerMenuButton");
    const categoryDropdown = document.getElementById("headerCategoryDropdown");
    const overlay = document.getElementById("headerOverlay");

    const userToggle = document.getElementById("userToggle");
    const userDropdown = document.getElementById("userDropdown");

    function closeAll() {
        categoryDropdown?.classList.remove("open");
        userDropdown?.classList.remove("open");
        overlay?.classList.remove("active");

        menuButton?.setAttribute("aria-expanded", "false");
        categoryDropdown?.setAttribute("aria-hidden", "true");
    }

    menuButton?.addEventListener("click", (event) => {
        event.preventDefault();
        event.stopPropagation();

        if (!categoryDropdown) {
            return;
        }

        const isOpen = categoryDropdown.classList.toggle("open");

        overlay?.classList.toggle("active", isOpen);
        userDropdown?.classList.remove("open");

        menuButton.setAttribute("aria-expanded", String(isOpen));
        categoryDropdown.setAttribute("aria-hidden", String(!isOpen));
    });

    userToggle?.addEventListener("click", (event) => {
        event.preventDefault();
        event.stopPropagation();

        if (!userDropdown) {
            return;
        }

        const isOpen = userDropdown.classList.toggle("open");

        overlay?.classList.toggle("active", isOpen);
        categoryDropdown?.classList.remove("open");

        menuButton?.setAttribute("aria-expanded", "false");
        categoryDropdown?.setAttribute("aria-hidden", "true");
    });

    overlay?.addEventListener("click", () => {
        closeAll();
    });

    document.addEventListener("click", (event) => {
        if (!menuButton?.contains(event.target) && !categoryDropdown?.contains(event.target) && !userToggle?.contains(event.target) && !userDropdown?.contains(event.target)) {
            closeAll();
        }
    });

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            closeAll();
        }
    });

    document.querySelectorAll(".header-category-dropdown a").forEach((link) => {
        link.addEventListener("click", () => {
            closeAll();
        });
    });

    document.querySelectorAll(".header-menu-bar a").forEach((link) => {
        link.addEventListener("click", (event) => {
            const href = link.getAttribute("href");

            if (!href) {
                return;
            }

            const hashIndex = href.indexOf("#");

            if (hashIndex === -1) {
                return;
            }

            const targetSelector = href.substring(hashIndex);
            const target = document.querySelector(targetSelector);

            if (!target) {
                return;
            }

            event.preventDefault();
            closeAll();

            target.scrollIntoView({
                behavior: "smooth", block: "start"
            });
        });
    });
});