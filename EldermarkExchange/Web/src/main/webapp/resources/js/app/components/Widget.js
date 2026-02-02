/**
 * Created by stsiushkevich on 11.09.18.
 */

function Widget () {
    Component.apply(this, arguments);
}

Widget.prototype = Object.create(Component.prototype);
Widget.prototype.constructor = Widget;