
function AssessmentWizard (props) {
    this.props = props;

    this.isUpScrollerShowed = false;
}

AssessmentWizard.prototype.init = function () {
    this.$element = $(this.props.dom);

    var me = this;
    var $content = this.$element.find('.wizard-content');

    $content.on('scroll', function (e) {
        var top = e.target.scrollTop;
        var h = e.target.clientHeight;

        if (top > h/2) {
            !me.isUpScrollerShowed && me.showUpScroller();
        } else {
            me.isUpScrollerShowed && me.hideUpScroller();
        }
    });

    this.$element.find('.up-scroller').on('click', function () {
        $content.scrollTo(0, 500);
    });
};

AssessmentWizard.prototype.showUpScroller = function () {
    this.isUpScrollerShowed = true;
    this.$element.find('.up-scroller').show();
};

AssessmentWizard.prototype.hideUpScroller = function () {
    this.isUpScrollerShowed = false;
    this.$element.find('.up-scroller').hide();
};