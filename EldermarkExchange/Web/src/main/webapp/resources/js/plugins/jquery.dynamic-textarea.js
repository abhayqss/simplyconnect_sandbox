;(function ($) {
    $.fn.autoresize = function () {
        if (this.is('textarea')) {
            this.each(function () {
                var h = this.offsetHeight - this.clientHeight;

                $(this).on("keyup input", function () {
                    $(this)
                        .css("height", "auto")
                        .css("height", this.scrollHeight + h)
                })
            });
        }
    }
})(jQuery);