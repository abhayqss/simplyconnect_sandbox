/**
 * Created by stsiushkevich on 25.09.18.
 */

$.validator.prototype.checkForm = function() {
    //overriden in a specific page
    this.prepareForm();
    for (var i = 0, elements = (this.currentElements = this.elements()); elements[i]; i++) {
        var result = this.findByName(elements[i].name);

        if (result.length !== undefined && result.length > 1) {
            for (var cnt = 0; cnt < result.length; cnt++) {
                this.check(result[cnt]);
            }
        } else {
            this.check(elements[i]);
        }
    }
    return this.valid();
};