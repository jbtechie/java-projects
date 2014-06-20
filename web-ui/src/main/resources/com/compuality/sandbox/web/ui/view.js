$(document).ready(function() {
    var r = Raphael('chart');
    $.ajax({
        url: '/services/ui/view',
        success: function(coords) {
            console.log(coords)
            var render = function() {
                // Creates canvas 640 × 480 at 10, 50
                r.clear();
                var w = $(window).width();
                var h = $(window).height();
                r.dotchart(0, 0, w, h, coords,
//                r.dotchart(0, 0, 620, 260, [76, 70, 67, 71, 69],
                                           [0, 1, 2, 10, 4],
                                           [100, 1000, 2500, 160, 500],
                        {max: 25, axisylabels: ['Mexico', 'Argentina', 'Cuba', 'Canada', 'United States of America'],
                            heat: true, axis: '0 0 1 1'})
            }
            render();
            $(window).resize(render);
        }
    });
});