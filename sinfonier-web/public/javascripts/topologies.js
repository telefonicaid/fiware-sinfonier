var boxWidth = 300;
var boxHeight = 50;
var defaultStyle = "rgba(0, 90, 200, 0.5)";

var drawTopology = function (ctxt, topology, totalWidth, totalHeight, zoom, customStyle) {
  var theZoom = zoom || 1;
  var style = customStyle || defaultStyle;
  ctxt.strokeStyle = style;
  ctxt.fillStyle = style;
  ctxt.lineWidth = 2;
  ctxt.clearRect(0, 0, totalWidth, totalHeight);
  var modules = topology.config.modules;
  var xMin = 10000, xMax = 0;
  var yMin = 10000, yMax = 0;

  for (var i = 0; i < modules.length; i++) {
    var module = modules[i];
    xMin = Math.min(module.config.position[0], xMin);
    xMax = Math.max(module.config.position[0], xMax);
    yMin = Math.min(module.config.position[1], yMin);
    yMax = Math.max(module.config.position[1], yMax);
  }

  var hRatio = (totalWidth) / (xMax + boxWidth * theZoom + xMin);
  var vRatio = (totalHeight) / (yMax + boxHeight * theZoom + yMin);

  for (var i = 0; i < modules.length; i++) {
    drawModule(ctxt, modules[i], hRatio, vRatio, style);
  }

  var wires = topology.config.wires;

  for (var i = 0; i < wires.length; i++) {
    var wire = wires[i];
    var src = wire.src.moduleId,
      tgt = wire.tgt.moduleId;

    var srcX = modules[src].config.position[0] + boxWidth / 2;
    var srcY = modules[src].config.position[1] + boxHeight;

    var dOut = [0, 1];
    var dIn = [0, -1];
    var tgtX = modules[tgt].config.position[0] + boxWidth / 2;
    var tgtY = modules[tgt].config.position[1];
    if (modules[src].name == "Global Variable") {
      tgtX = modules[tgt].config.position[0];
      tgtY = modules[tgt].config.position[1] + boxHeight / 2;
      dIn = [-1, 0];
    }

    //Rect
    /*ctxt.beginPath();
     ctxt.moveTo(srcX*hRatio,srcY*vRatio);
     ctxt.lineTo(tgtX*hRatio,tgtY*vRatio);
     ctxt.closePath();
     ctxt.stroke();
     */

    var p1 = [srcX * hRatio, srcY * vRatio];
    var p2 = [tgtX * hRatio, tgtY * vRatio];
    // Coefficient multiplicateur de direction
    // 100 par defaut, si distance(p1,p2) < 100, on passe en distance/2
    var coeffMulDirection = 100;
    var distance = Math.sqrt(Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2));

    coeffMulDirection = distance / 2;

    // Vectores directores d1 et d2 :
    var d1 = [dOut[0] * coeffMulDirection,
      dOut[1] * coeffMulDirection];
    var d2 = [dIn[0] * coeffMulDirection,
      dIn[1] * coeffMulDirection];
    var bezierPoints = [];

    bezierPoints[0] = p1;
    bezierPoints[1] = [p1[0] + d1[0], p1[1] + d1[1]];
    bezierPoints[2] = [p2[0] + d2[0], p2[1] + d2[1]];
    bezierPoints[3] = p2;

    // Draw the inner bezier curve
    ctxt.strokeStyle = style;
    ctxt.beginPath();
    ctxt.moveTo(bezierPoints[0][0], bezierPoints[0][1]);
    ctxt.bezierCurveTo(bezierPoints[1][0], bezierPoints[1][1], bezierPoints[2][0], bezierPoints[2][1], bezierPoints[3][0], bezierPoints[3][1]);
    ctxt.stroke();
  }
};
var drawModule = function (ctxt, module, hRatio, vRatio, style) {
  var x = module.config.position[0], y = module.config.position[1];
  var fontSize = 11;
  var grey = "rgba(200, 200, 200, 0.8)";
  ctxt.font = " " + fontSize + "px Helvetica";
  var textWidth = 0;

  if (typeof(ctxt.measureText) != 'undefined') textWidth = ctxt.measureText(module.name).width;

  var fontWidth = textWidth + 4;
  var width = Math.max(boxWidth * hRatio, fontWidth), height = boxHeight * vRatio;

  ctxt.strokeStyle = grey;
  ctxt.fillStyle = grey;
  ctxt.fillRect(x * hRatio + 2, y * vRatio + 2, width + 2, height + 2);
  ctxt.strokeStyle = style;
  ctxt.fillStyle = style;
  ctxt.fillRect(x * hRatio, y * vRatio, width, height);
  ctxt.fillStyle = "white";
  ctxt.fillRect(x * hRatio + 1, y * vRatio + 1, width - 2, fontSize);
  ctxt.fillStyle = "black";

  if (typeof(ctxt.fillText) != 'undefined') ctxt.fillText(module.name, x * hRatio + 2, y * vRatio + fontSize);

  ctxt.strokeStyle = style;
  ctxt.fillStyle = style;
};
var deleteTopology = function (id, name) {
  var url = '/topologies/' + id + '.json';
  YAHOO.util.Connect.asyncRequest('DELETE', url, {
    success: function (o) {
      alert("successfully removed " + name);
      var $deleted = $("#__" + id + " .deleted");
      if ($deleted.size() > 0) {
        $("#__" + id + " .delete-topology").toggle(false);
        $("#__" + id + " .deleted").toggle(true);
      } else {
        $("#__" + id).toggle(false);
      }
    },
    failure: function (o) {
      var error = o.responseText;
      alert("cannot remove " + name + ". Error: " + error);
    }
  });
};
var launchTopology = function (id, name) {
  var url = '/topologies/' + id + '/launch.json';
  $("#__" + id + " .ajax-loading").fadeOut(100).fadeIn(500);

  $.post(url).done(function (data) {
    $("#__" + id + " .ajax-loading").hide();
    if (data.result == 'error') {
      getStatusTopology(id, name);
      alert("cannot launch " + name + ": " + data.description + "\n" + (data.detail || ""));
    }
    else {
      $("#__" + id + " .launch-topology").toggle(false);
      $("#__" + id + " .stop-topology").toggle(true);
      $("#__" + id + " .update-topology").toggle(true);
      alert("successfully launched " + name);
    }
  }).fail(function (data) {
    $("#__" + id + " .ajax-loading").hide();
    getStatusTopology(id, name);
    if (data && data.responseJSON) {
      alert(data.responseJSON.description + "\n" + (data.responseJSON.detail || ""));
    }
    else {
      alert("cannot launch " + name);
    }

  });

};
var getStatusTopology = function (id, name) {
  var url = '/topologies/' + id + '/status.json';
  //$("#__" + id + " .ajax-loading" ).fadeOut(100).fadeIn( 500 );

  $.get(url).done(function (data) {
    //$("#__" + id + " .ajax-loading" ).hide( );
    if (data.result == 'error') {

    }
    else {
      if (data.detail == "ACTIVE") {
        $("#__" + id + " .launch-topology").toggle(false);
        $("#__" + id + " .stop-topology").toggle(true);
        $("#__" + id + " .update-topology").toggle(true);
      }
      else {
        $("#__" + id + " .launch-topology").toggle(true);
        $("#__" + id + " .stop-topology").toggle(false);
        $("#__" + id + " .update-topology").toggle(false);
      }

    }
  }).fail(function (data) {

  });

};
var stopTopology = function (id, name) {
  var url = '/topologies/' + id + '/stop.json';
  $("#__" + id + " .ajax-loading").fadeOut(100).fadeIn(500);

  $.post(url).done(function (data) {
    $("#__" + id + " .ajax-loading").hide();
    if (data.result == 'error') {
      alert("cannot stop " + name);
    }
    else {
      $("#__" + id + " .launch-topology").toggle(true);
      $("#__" + id + " .stop-topology").toggle(false);
      $("#__" + id + " .update-topology").toggle(false);
      alert("successfully stopped " + name);
    }
  }).fail(function (data) {
    $("#__" + id + " .ajax-loading").hide();
    var error = data.status + " " + data.statusText;
    alert("cannot stop " + name);
  });

};
var updateTopology = function (id, name) {
  var url = '/topologies/' + id + '/update.json';
  $("#__" + id + " .ajax-loading").fadeOut(100).fadeIn(500);

  $.post(url).done(function (data) {
    $("#__" + id + " .ajax-loading").hide();
    if (data.result == 'error') {
      alert("cannot update " + name);
    }
    else {
      alert("successfully updated " + name);
    }
  }).fail(function (data) {
    $("#__" + id + " .ajax-loading").hide();
    var error = data.status + " " + data.statusText;
    alert("cannot update " + name);
  });

};
var getTopologyLog = function (id, $panel) {
  var url = '/topologies/' + id + '/log.json';
  $.get(url).done(function (data) {
    if (data.result == 'error') {
      $panel.html("cannot access log: " + data.description + "<br>" + data.detail);
    }
    else {
      $panel.html(data.description);
    }
    $panel.removeClass('requested');
  }).fail(function (data) {
    if (data.responseJSON) {
      $panel.html("cannot access log: " + data.responseJSON.description + "<br>" + data.responseJSON.detail);
    }
    else {
      var error = data.status + " " + data.statusText;
      $panel.html("cannot access log: " + data.status + "<br>" + data.statusText);
    }
    $panel.removeClass('requested');
  });

};
var publishTopology = function (id, cb) {
  $.ajax({url: "/topologies/" + id + "/publish.json", data: {}, method: "PATCH"}).done(function () {
    $("#__" + id + " .sharing-text").html('published');
    var $link = $("#__" + id + " .sharing-link");
    $link.data('sharing', 'published');
    $link.attr('title', 'Unpublish');
    $link.html('Unpublish');
    cb();
  }).fail(function (data) {
    if (data && data.responseJSON) {
      alert(data.responseJSON.message);
    }
    else {
      alert("unable to publish");
    }
  });
  return false;
};
var privatizeTopology = function (id, cb) {
  $.ajax({url: "/topologies/" + id + "/privatize.json", data: {}, method: "PATCH"}).done(function () {
    $("#__" + id + " .sharing-text").html('private');
    var $link = $("#__" + id + " .sharing-link");
    $link.data('sharing', 'private');
    $link.attr('title', 'Publish');
    $link.html('Publish');
    cb();
  }).fail(function () {
    alert("unable to unpublish");
  });
  return false;
};
var prepareClicks = function () {
  $(".log-info").unbind("click").click(function () {
    var $panel = $(this).next();
    $panel.toggle();
    if (!$panel.hasClass('requested') && $panel.is(":visible")) {
      $panel.addClass('requested');
      $panel.html('<div class="log-pending"></div>');
      getTopologyLog($(this).data("id"), $panel);
    }
    return false;
  });
  $(".delete-topology").unbind("click").click(function () {
    var id = $(this).data("id");
    var name = $(this).data("name");
    var confirmation = confirm("Do you want to remove topology " + name + "?");
    if (confirmation) {
      deleteTopology(id, name);
    }
    return false;
  });

  $(".launch-topology").unbind("click").click(function () {
    var id = $(this).data("id");
    var name = $(this).data("name");
    var confirmation = confirm("Do you want to launch topology " + name + "?");
    if (confirmation) {
      launchTopology(id, name);
    }
    return false;
  });

  $(".stop-topology").unbind("click").click(function () {
    var id = $(this).data("id");
    var name = $(this).data("name");
    var confirmation = confirm("Do you want to stop topology " + name + "?");
    if (confirmation) {
      stopTopology(id, name);
    }
    return false;
  });

  $(".update-topology").unbind("click").click(function () {
    var id = $(this).data("id");
    var name = $(this).data("name");
    var confirmation = confirm("Do you want to update topology " + name + "?");
    if (confirmation) {
      updateTopology(id, name);
    }
    return false;
  });

  $(".sharing-link").unbind('click').click(function () {
    var topologyId = $(this).closest("div.topology.detail").data("id");
    var sharing = $(this).data("sharing");
    return (sharing === 'published' ? privatizeTopology(topologyId) : publishTopology(topologyId));
  });

};
var topologyAdapter = function (topology) {
  if (!topology) return {};

  var reduce = {name: topology.name, config: topology.config};

  reduce.config.modules.forEach(function (module) {
    delete module.value;
  });

  return reduce;
};

$(function () {
  $(".topologies-diagram-list").each(function () {
    var $diagram = $(this);
    var $desc = $diagram.find(".topology-description");

    if ($diagram.find('canvas').length == 0) {
      var totalWidth = $diagram.width();
      var totalHeight = $diagram.height();
      var element = document.createElement('canvas');

      //IE compatibility
      if (typeof(G_vmlCanvasManager) != 'undefined') element = G_vmlCanvasManager.initElement(element);

      var c = $(element);
      c.attr("width", totalWidth);
      c.attr("height", totalHeight);
      $diagram.append(c);
      c.attr("border", "2");
      var ctxt = element.getContext("2d");
      var topologyAsText = JSON.parse($desc.text());
      var topologyDescription = topologyAdapter(JSON.parse(topologyAsText));

      drawTopology(ctxt, topologyDescription, totalWidth, totalHeight);
      //$desc.remove();
    }
  });
  $(".topologies-diagram-list")
    .mouseenter(function () {
      var $diagram = $(this);
      var $desc = $diagram.find(".topology-description");
      var $edit = $(this).closest(".box-row").find(".edit-link-cmd");
      if ($desc.size() > 0 && $edit.length > 0) {
        var totalWidth = $diagram.width();
        var totalHeight = $diagram.height();
        var c = $diagram.find('canvas');

        c.attr("border", "2");

        var ctxt = c.get(0).getContext("2d");
        var topologyAsText = JSON.parse($desc.text());
        var topologyDescription = topologyAdapter(JSON.parse(topologyAsText));

        c.data("zoom", "1");
        c.data("limit", ".96");
        c.data("increment", "-.01");
        c.data("step", "1");

        var topologyAnimation = function ($canvas) {
          var zoom = parseFloat($canvas.data("zoom"));
          var inc = parseFloat($canvas.data("increment"));
          var limit = parseFloat($canvas.data("limit"));
          var step = parseInt($canvas.data("step"));
          var showStyle = "rgba(0, 50, 100, 0.5)";
          if (Math.abs(inc) > 0.0) {
            drawTopology(ctxt, topologyDescription, totalWidth, totalHeight, zoom, showStyle);
            $canvas.data("zoom", "" + (zoom + inc));
            if (Math.abs(limit - zoom) > Math.abs(limit - (zoom + inc))) {
              setTimeout(function () {
                topologyAnimation($canvas);
              }, step);
            }
          }
        };

        topologyAnimation(c);
      }
    })
    .mouseleave(function () {
      var $diagram = $(this);
      var $desc = $diagram.find(".topology-description");
      var $edit = $(this).closest(".box-row").find(".edit-link-cmd");

      if ($desc.size() > 0 && $edit.length > 0) {
        var totalWidth = $diagram.width();
        var totalHeight = $diagram.height();
        var c = $diagram.find('canvas');

        c.attr("border", "2");

        var ctxt = c.get(0).getContext("2d");
        var topologyAsText = JSON.parse($desc.text());
        var topologyDescription = topologyAdapter(JSON.parse(topologyAsText));

        c.data("zoom", "1");
        c.data("increment", "0");
        drawTopology(ctxt, topologyDescription, totalWidth, totalHeight, 1, defaultStyle);
      }
    })
    .click(function () {
      var $edit = $(this).closest(".box-row").find(".edit-link-cmd");

      if ($edit.length > 0) {
        window.location = $edit.attr('href');
      }
    });

});
