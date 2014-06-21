$(document).ready(function() {
  var repeatTimeCheck = function() {
    $.ajax({
      url: '/services/ui/view/time',
      dataType: 'text',
      success: function(current_time, textStatus, jqXhr) {
        $('<div/>', {
          text: current_time
        }).appendTo('#time')

        repeatTimeCheck();
      },
      error: function(jqXhr, textStatus, errorThrown) {
        console.log(textStatus);
        console.log(errorThrown);
      }
    });
  }

  repeatTimeCheck();
});