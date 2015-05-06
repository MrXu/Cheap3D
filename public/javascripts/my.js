$(document).ready(function() {
  $.ajax({
    "url": "wheel.stl",
    "success": function(ret) {
      buildGeometry(ret, {name: 'file.stl'});
    }
  });
});