document.addEventListener('DOMContentLoaded', function () {
    // Avatar change via AJAX with Cloudinary
    const avatarUrlInput = document.getElementById('avatarUrlInput');
    const avatarUrlButton = document.getElementById('avatarUrlButton');
    const avatarUrlForm = document.getElementById('avatarUrlForm');

    if (avatarUrlInput && avatarUrlButton && avatarUrlForm) {
        // Trigger file input when button clicked
        avatarUrlButton.addEventListener('click', function (e) {
            e.preventDefault();
            avatarUrlInput.click();
        });

        // Handle file selection
        avatarUrlInput.addEventListener('change', function () {
            if (this.files && this.files[0]) {
                // Show loading state
                avatarUrlButton.disabled = true;
                const originalHTML = avatarUrlButton.innerHTML;
                avatarUrlButton.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i><span>Đang tải...</span>';

                // Create FormData
                const formData = new FormData(avatarUrlForm);

                // Send request via AJAX
                fetch(avatarUrlForm.action, {
                    method: 'POST',
                    body: formData
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            // Update avatar image
                            const avatarImg = document.querySelector('.account-avatarUrl img');
                            if (avatarImg) {
                                avatarImg.src = data.avatarUrl + '?t=' + new Date().getTime(); // Force refresh
                            } else {
                                // If no img, replace the icon with image
                                const avatarContainer = document.querySelector('.account-avatarUrl');
                                if (avatarContainer) {
                                    avatarContainer.innerHTML = '<img src="' + data.avatarUrl + '" alt="Avatar" style="width: 100%; height: 100%; object-fit: cover;">';
                                }
                            }

                            // Show success notification
                            showNotification(data.message || 'Thay đổi ảnh đại diện thành công', 'success');

                            // Reset button state
                            avatarUrlButton.disabled = false;
                            avatarUrlButton.innerHTML = originalHTML;

                            // Reset input
                            avatarUrlInput.value = '';
                        } else {
                            showNotification(data.error || 'Lỗi khi tải ảnh', 'error');
                            avatarUrlButton.disabled = false;
                            avatarUrlButton.innerHTML = originalHTML;
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        showNotification('Lỗi: ' + error.message, 'error');
                        avatarUrlButton.disabled = false;
                        avatarUrlButton.innerHTML = originalHTML;
                    });
            }
        });
    }

    // Confirm dialog for links with data-confirm attribute
    document.querySelectorAll('[data-confirm]').forEach(function (link) {
        link.addEventListener('click', function (event) {
            if (!window.confirm(link.getAttribute('data-confirm'))) {
                event.preventDefault();
            }
        });
    });
});

// Notification helper
function showNotification(message, type = 'info') {
    // Check if notification container exists
    let notificationContainer = document.querySelector('.notification-container');
    if (!notificationContainer) {
        notificationContainer = document.createElement('div');
        notificationContainer.className = 'notification-container';
        notificationContainer.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            display: flex;
            flex-direction: column;
            gap: 10px;
        `;
        document.body.appendChild(notificationContainer);
    }

    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification--${type}`;

    let bgColor = '#d1ecf1';
    let borderColor = '#bee5eb';
    let textColor = '#0c5460';

    if (type === 'success') {
        bgColor = '#d4edda';
        borderColor = '#c3e6cb';
        textColor = '#155724';
    } else if (type === 'error') {
        bgColor = '#f8d7da';
        borderColor = '#f5c6cb';
        textColor = '#721c24';
    }

    notification.style.cssText = `
        padding: 12px 16px;
        border-radius: 8px;
        font-weight: 600;
        min-width: 250px;
        background: ${bgColor};
        color: ${textColor};
        border: 1px solid ${borderColor};
        animation: slideInRight 0.3s ease;
    `;
    notification.textContent = message;
    notificationContainer.appendChild(notification);

    // Auto-remove notification after 3 seconds
    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease';
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}

// Add CSS animations if not already added
if (!document.querySelector('style[data-avatar-animations]')) {
    const style = document.createElement('style');
    style.setAttribute('data-avatar-animations', 'true');
    style.textContent = `
        @keyframes slideInRight {
            from {
                transform: translateX(400px);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        
        @keyframes slideOutRight {
            from {
                transform: translateX(0);
                opacity: 1;
            }
            to {
                transform: translateX(400px);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
}


