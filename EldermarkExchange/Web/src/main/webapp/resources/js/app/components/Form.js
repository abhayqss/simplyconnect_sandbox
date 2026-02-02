/**
 * Created by stsiushkevich on 11.09.18.
 */

function Form () {
    Widget.apply(this, arguments)
}

Form.prototype = Object.create(Widget.prototype);
Form.prototype.constructor = Form;

Form.prototype.componentDidMount = function () {
    this.$element = $('[cmp-id="'+ this.$$id +'"]');
    this.dom = this.$element.get(0);
};

// API

Form.prototype.reset = function () {
    this.dom.reset()
};

Form.prototype.focusOnElement = function (name) {
    $(this.dom.elements[name]).focus();
};

Form.prototype.getElementValue = function (name) {
    return $(this.dom.elements[name]).val();
};

Form.prototype.setElementValue = function (name, val) {
    return $(this.dom.elements[name]).val(val);
};

Form.prototype.getElement = function (name) {
    return this.dom.elements[name];
};

Form.prototype.isElementChecked = function (name) {
    return $(this.dom.elements[name]).is(':checked');
};

Form.prototype.getAttr = function (attrName) {
    return this.$element.attr(attrName);
};

Form.prototype.setAttr = function (attrName, attrVal) {
    return this.$element.attr(attrName, attrVal);
};

Form.prototype.fill = function (data) {
    var names = this.getElementNames();
    for (var i = 0; i < names.length; i++) {
        if(data[names[i]]){
            this.setElementValue(names[i], data[names[i]]);
        }
    }
};

Form.prototype.getElementNames = function () {
    var names = [];
    var keys = Object.getOwnPropertyNames(this.dom.elements);
    for(var i=0; i<keys.length; i++){
        if(!isFinite(keys[i])) names.push(keys[i]);
    }
    return names;
};

Form.prototype.getData = function () {
    var data = {};

    $.map(this.$element.serializeArray(), function(item, i) {
        var lb = item.name.indexOf('[');
        if (lb > -1) {
            var o = data;
            var name = item.name.replace(/\]/gi, '').split('[');

            for (var j=0, len = name.length; j<len; j++) {
                if (j == len-1) {
                    if (o[name[j]]) {
                        if (typeof o[name[j]] == 'string') {
                            o[name[j]] = [o[name[j]]];
                        }
                        if (item.value) o[name[j]].push(item.value);
                    }

                    else if (item.value) o[name[j]] = item.value;
                }
                else o = o[name[j]] = o[name[j]] || {};
            }
        }

        else {
            if (data[item.name] !== undefined) {
                if (!data[item.name].push) {
                    data[item.name] = [data[item.name]];
                }

                data[item.name].push(item.value || '');
            }

            else if (item.value) data[item.name] = item.value;
        }
    });

    return data;
};

Form.prototype.onSubmit = function (handler) {
    this.$element.submit(handler);
};

Form.prototype.submit = function () {
    this.$element.submit();
};

Form.prototype.setVisible = function (isVisible) {
    this.$element.css('visibility', isVisible ? 'visible' : 'hidden');
};

Form.prototype.show = function () {
    this.$element.show();
};

Form.prototype.hide = function () {
    this.$element.hide();
};



