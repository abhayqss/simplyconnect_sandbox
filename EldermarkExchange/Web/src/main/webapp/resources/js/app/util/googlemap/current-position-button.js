function addYourLocationButton(map, initLocation)
{
    var controlDiv = document.createElement('div');

    var firstChild = document.createElement('button');
    firstChild.style.border = 'none';
    firstChild.style.outline = 'none';
    firstChild.style.cursor = 'pointer';
    firstChild.style.marginRight = '8px';
    firstChild.style.padding = '0px';
    firstChild.title = 'Your Location';
    controlDiv.appendChild(firstChild);

    var secondChild = document.createElement('div');
    secondChild.style.width = '45px';
    secondChild.style.height = '45px';
    secondChild.style.backgroundImage = 'url("resources/images/ic_location.svg")';
    secondChild.style.backgroundRepeat = 'no-repeat';
    secondChild.style.backgroundPosition = '-4px -2px';
    secondChild.id = 'you_location_img';
    firstChild.appendChild(secondChild);

    firstChild.addEventListener('click', function() {
        map.setCenter(initLocation);
    });

    controlDiv.index = 1;
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(controlDiv);
}