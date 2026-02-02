/**
 * Created by stsiushkevich on 05.09.18.
 */

/**
 * params:
 *
 * bool hasBackBtn
 * bool hasCancelBtn
 * */

var AssessmentFooter = (function ($) {

    var defaultOptions = {
        hasTopBorder: true,
        hasLeftArrow: true,
        hasBackBtn: true,
        backBtnText: 'Back',
        hasRightArrow: true,
        hasNextBtn: true,
        nextBtnText: 'Next',
        onBack: function () {
        },
        onNext: function () {
        },
        rightButtons: null,
        totalPages: 0,
        currentPage: 0,
        className: ''
    };

    function isNotEmpty(o) {
        return !$.isEmptyObject(o);
    }

    function goNext(me) {
        return function() {
            if (me.options.currentPage < me.options.totalPages - 1) {
                me.update({currentPage: me.options.currentPage + 1});
                me.options.onNext();
            }
        }
    }

    function goBack(me) {
        return function() {
            if (me.options.currentPage > 0) {
                me.update({currentPage: me.options.currentPage - 1});
                me.options.onBack();
            }
        }
    }

    function AssessmentFooter(options) {
        this.isRendered = false;

        this.container = $(options.container);
        this.options = $.extend({}, defaultOptions, options);
    }

    AssessmentFooter.prototype.update = function (options) {
        if (isNotEmpty(options)) {
            this.options = $.extend({}, this.options, options);

            this.container.find('.assessment-footer').remove();

            this.isRendered = false;
            this.render();
        }
    };

    AssessmentFooter.prototype.hide = function () {
        this.$element.hide()
    };

    AssessmentFooter.prototype.show = function () {
        this.$element.show()
    };

    AssessmentFooter.prototype.setVisible = function (isVisible) {
        if (isVisible) this.$element.css('visibility', 'hidden');
        else this.$element.css('visibility', 'visible');
    };

    AssessmentFooter.prototype.render = function () {
        if (!this.isRendered) {
            var opts = this.options;

            if (opts.container) {
                var tmpl = {
                    '<>': 'div',
                    'class': 'assessment-footer ' + this.options.className,
                    'html': []
                };

                var hasTopBorder = opts.hasTopBorder;

                if (hasTopBorder) {
                    tmpl['html'].push({
                        '<>': 'div',
                        'class': 'assessment-footer__top-border'
                    })
                }

                var hasBackBtn = opts.hasBackBtn;
                var hasNextBtn = opts.hasNextBtn;

                var hasLeftArrow = opts.hasLeftArrow;
                var hasRightArrow = opts.hasRightArrow;

                if (hasLeftArrow || hasBackBtn || hasNextBtn || hasRightArrow) {
                    var leftButtonsTmpl = {
                        '<>': 'div',
                        'class': 'assessment-footer__left-buttons',
                        'html': []
                    };

                    tmpl['html'].push(leftButtonsTmpl);

                    if (opts.currentPage > 0) {
                        if (hasLeftArrow) {
                            leftButtonsTmpl['html'].push({
                                '<>': 'a',
                                'class': 'assessment-footer__left-arrow',
                                'onclick': goBack(this)
                            });
                        }

                        if (hasBackBtn) {
                            leftButtonsTmpl['html'].push({
                                '<>': 'a',
                                'class': 'assessment-footer__back-btn',
                                'text': opts.backBtnText,
                                'onclick': goBack(this)
                            });
                        }
                    }

                    if (opts.currentPage < opts.totalPages - 1) {

                        if (hasNextBtn) {
                            leftButtonsTmpl['html'].push({
                                '<>': 'a',
                                'class': 'assessment-footer__next-btn',
                                'text': opts.nextBtnText,
                                'onclick': goNext(this)
                            });
                        }

                        if (hasRightArrow) {
                            leftButtonsTmpl['html'].push({
                                '<>': 'a',
                                'class': 'assessment-footer__right-arrow',
                                'onclick': goNext(this)
                            });
                        }
                    }
                }

                var rightButtons = opts.rightButtons;

                if (rightButtons) {
                    var rightButtonsTmpl = {
                        '<>': 'div',
                        'class': 'assessment-footer__right-buttons',
                        'html': []
                    };

                    tmpl['html'].push(rightButtonsTmpl);

                    rightButtonsTmpl['html'] = $.map(rightButtons, function (btn) {
                        var btnTmpl = {
                            '<>': 'a',
                            'class': 'assessment-footer__right-btn ' + btn.cssClass,
                            'style': btn.cssStyle,
                            'onclick': btn.onClick
                        };

                        if (btn.text) btnTmpl['text'] = btn.text;
                        if (btn.html) btnTmpl['html'] = btn.html;

                        return btnTmpl
                    });
                }

                this.container.json2html({}, tmpl);

                this.$element = this.container.find('.assessment-footer');
                this.isRendered = true
            }
        }
    };

    return AssessmentFooter;
})($);