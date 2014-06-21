$(document).ready(function() {
  var doMathOnServer = function(iterationCount, outputFunc) {
    $.ajax({
      type: 'POST',
      url: '/services/ui/math',
      contentType: 'application/json',
      data: JSON.stringify(iterationCount),
      dataType: 'text',
      success: function(elems, textStatus, jqXhr) {
        outputFunc(JSON.parse(elems));
      },
      error: function(jqXhr, textStatus, errorThrown) {
        console.log(textStatus);
        console.log(errorThrown);
      }
    });
  };

  var doMathOnJavaServer = function(iterationCount, outputFunc) {
    $.ajax({
      type: 'POST',
      url: '/services/ui/math/java',
      contentType: 'application/json',
      data: JSON.stringify(iterationCount),
      dataType: 'text',
      success: function(elems, textStatus, jqXhr) {
        outputFunc(JSON.parse(elems));
      },
      error: function(jqXhr, textStatus, errorThrown) {
        console.log(textStatus);
        console.log(errorThrown);
      }
    });
  };

  var doMathInBrowser = function(iterationCount, outputFunc) {
    var elems = [];
    for(i=0; i < iterationCount; ++i) {
      elems[i] = Math.random();
    }
    outputFunc(elems);
  };

  var timeMath = function(mathFunc, nextFunc) {
    var iterationCount = 10000000;
    var startTime = new Date().getTime();

    mathFunc(iterationCount, function(elems) {
      var sum = 0;
      for(i=0; i < iterationCount; ++i) {
        sum += elems[i];
      }
      var duration = new Date().getTime() - startTime;
      $('<div/>', {
        text: 'function=' + mathFunc.name + ', duration=' + duration + ', sum=' + sum
      }).appendTo('#results');

      nextFunc();
    });
  }

  timeMath(doMathInBrowser, function() {
    timeMath(doMathOnServer, function() {
      timeMath(doMathOnJavaServer, function() {
      });
    });
  });
});