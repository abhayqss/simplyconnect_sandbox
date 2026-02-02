$(document).ready(function(){
    $('#navbar-list-cda').height($(window).height()-100);
});
$(window).resize(function(){
    $('#navbar-list-cda').height($(window).height()-100);
});

$(document).ready(function(){
    $('#navbar-list-cda').height($(window).height()-100);
});

$(window).resize(function(){
    $('#navbar-list-cda').height($(window).height()-100);
});

$(document).ready(function(){
    $('.cda-render a[href*="#"]:not([href="#"])').bind('click.smoothscroll',function (e) {
        e.preventDefault();

        var target = this.hash,
            $target = $(target);

        $('html, body').stop().animate({
            'scrollTop': $target.offset().top
        }, 1000, 'swing', function () {
            window.location.hash = target;

            // lets add a div in the background
            $('<div />').css({'background':'#336b7a'}).prependTo($target).fadeIn('fast', function(){
                $(this).fadeOut('fast', function(){
                    $(this).remove();
                });
            });

        });
    });
});

$( function() {
    $( "#navbar-list-cda-sortable" ).sortable();
    $( "#navbar-list-cda-sortable" ).disableSelection();
} );

$( function( ) {
    var $nav = $( '#navbar-list-cda-sortable' );
    var $content = $( '#doc-clinical-info' );
    var $originalContent = $content.clone( );
    $nav.sortable( {
        update: function ( e ) {
            $content.empty( );
            $nav.find( 'a' ).each( function ( ) {
                $content.append( $originalContent.clone( ).find( $( this ).attr( 'href' ) ).parent ( ) );
            } );

            $('[data-spy="scroll"]').each(function () {
                var $spy = $(this).scrollspy('refresh')
            })
        }
    } );
} );