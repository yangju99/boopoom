document.addEventListener('DOMContentLoaded', function () {
    const chips = document.querySelectorAll('[data-product-id]');
    const input = document.querySelector('#selectedProductId');

    if (!chips.length || !input) {
        return;
    }

    chips.forEach(function (chip) {
        chip.addEventListener('click', function () {
            chips.forEach(function (el) {
                el.classList.remove('btn-primary');
                el.classList.add('btn-outline-primary');
            });

            chip.classList.remove('btn-outline-primary');
            chip.classList.add('btn-primary');
            input.value = chip.getAttribute('data-product-id');
        });
    });
});
