$(document).ready(function () {

    const productId = $('#productId').val();
    const contextPath = $('#contextPath').val();

    let currentRating = '';
    let currentSort = 'newest';
    let currentPage = 1;
    const pageSize = 5;

    function loadReviews(reset = false) {

        if (reset) {
            currentPage = 1;
            $('#reviewContainer').html('');
        }

        $.ajax({
            url: contextPath + '/reviews',
            method: 'GET',
            data: {
                productId: productId,
                rating: currentRating,
                sort: currentSort,
                page: currentPage,
                size: pageSize
            },
            success: function (html) {
                $('#reviewContainer').append(html);

                if ($.trim(html) === '') {
                    $('#loadMoreReview').hide();
                } else {
                    $('#loadMoreReview').show();
                }
            }
        });
    }

    // INIT
    loadReviews(true);

    // FILTER STAR
    $('.filter-btn').on('click', function () {
        $('.filter-btn').removeClass('active');
        $(this).addClass('active');

        currentRating = $(this).data('rating') || '';
        loadReviews(true);
    });

    // SORT
    $('.review-sort').on('change', function () {
        currentSort = $(this).val();
        loadReviews(true);
    });

    // LOAD MORE
    $('#loadMoreReview').on('click', function () {
        currentPage++;
        loadReviews(false);
    });

});
