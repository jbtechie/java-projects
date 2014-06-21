$(document).ready(function() {
  var doMathOnServer = function(iterationCount, outputFunc) {
    $.ajax({
      type: 'POST',
      url: '/services/ui/math',
      contentType: 'application/json',
      data: JSON.stringify(iterationCount),
      dataType: 'text',
      success: function(sum, textStatus, jqXhr) {
        outputFunc(sum);
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
      success: function(sum, textStatus, jqXhr) {
        outputFunc(sum);
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
    var sum = 0;
    for(i=0; i < iterationCount; ++i) {
      sum += elems[i];
    }
    outputFunc(sum);
  };

  var timeMath = function(mathFunc) {
    var iterationCount = 10000000;
    var startTime = new Date().getTime();

    mathFunc(iterationCount, function(sum) {
      var duration = new Date().getTime() - startTime;
      $('<div/>', {
        text: 'function=' + mathFunc.name + ', duration=' + duration + ', sum=' + sum
      }).appendTo('#results');
    });
  }

  timeMath(function browser(iterationCount, outputFunc) {
    doMathInBrowser(iterationCount, outputFunc);
    timeMath(function groovy(iterationCount, outputFunc) {
      doMathOnServer(iterationCount, outputFunc);
        timeMath(function java(iterationCount, outputFunc) {
        doMathOnJavaServer(iterationCount, outputFunc);
      });
    });
  });
});