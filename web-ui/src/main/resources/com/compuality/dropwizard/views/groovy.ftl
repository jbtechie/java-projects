<#-- @ftlvariable name="" type="com.example.views.PersonView" -->
<html>
<body>
<script src="/js/raphael-min.js"></script>
<script src="/js/g.raphael-min.js"></script>
<script src="/js/g.pie-min.js"></script>
<script>
    // Creates canvas 640 × 480 at 10, 50
    var r = Raphael(10, 50, 640, 480);
    // Creates pie chart at with center at 320, 200,
    // radius 100 and data: [55, 20, 13, 32, 5, 1, 2]
    r.piechart(320, 240, 100, [55, 20, 13, 32, 5, 1, 2]);
</script>
</body>
</html>