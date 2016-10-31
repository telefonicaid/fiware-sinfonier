var topology=null;
function ImportTopology(e) {
  var result, that = this;

  this.prepareForm = function (input) {
    $('input#name').val(input.name);
    $('#description').val(input.description);
      
  };

  this.drawTopology = function(json) {
	  $(".topologies-import").each(function () {
		    var $diagram = $(this);
		    var totalWidth = $diagram.width();
		    var totalHeight = $diagram.height();
		    var c = $diagram.find('canvas');
		    if (c.length == 0) {
		      var element = document.createElement('canvas');

		      // IE compatibility
		      if (typeof(G_vmlCanvasManager) != 'undefined') element = G_vmlCanvasManager.initElement(element);
		      c = $(element);
		      c.attr("width", totalWidth);
		      c.attr("height", totalHeight);
		      $diagram.append(c);
		      c.attr("border", "2");
		    }
			    
		      
		      var ctxt = c.get(0).getContext("2d");

		      drawTopology(ctxt, json, totalWidth, totalHeight);
		  });
  }
  
  this.checkModules = function() {
	    if (topology) {
	      $("#topology-div-error").empty();
	      topology.config.modules.forEach(function(module){
	        $.ajax("/modules/check", {
	          data : JSON.stringify({module:module}),
	            contentType : 'application/json',
	            type : 'POST'
	          }).done(function(data){
	            //nothing to do
	          }).fail(function(jqXHR){
	            $("#topology-div-error").removeClass('hide');
	            $("#topology-div-error").fadeIn();
	            $("#topology-div-error").append("<p>" +module.name+": "+jqXHR.responseJSON.data.message+"</p>")
	          });
	      });
	    }
	}
  
  this.process = function (e) {
    try {
      topology = JSON.parse(e.target.result);

      that.prepareForm(topology);
      that.drawTopology(topology);
      that.checkModules(topology);
      
    } catch (err) {
      console.error(err);
    }
  };

  if (e != undefined) {
    this.process(e)
  }

  return this;
}


function prepareTopology(file) {
	  var fReader = new FileReader();
	  var topologyProcessor = new ImportTopology();
	  file = $(file)[0]['files'][0];
	  fReader.onload = topologyProcessor.process;
	  fReader.readAsText(file);
	}

$(function(){
  $("button#topology-do-import").click(function(){
    if (topology)	{
      topology.description = $("#description").val();
      topology.name = $("#name").val();
      $("#topology-div-error").fadeOut();
      if ($("#name")[0].checkValidity()) {
        $.ajax("/topologies/import", {
          data : JSON.stringify({topology:topology}),
            contentType : 'application/json',
            type : 'POST'
          }).done(function(data){
            window.location.href = '/topologies/'+ data["name"];
          }).fail(function(jqXHR){
            $("#topology-div-error").removeClass('hide');
            $("#topology-div-error").fadeIn();
            $("#topology-div-error").empty();
            $("#topology-div-error").append(jqXHR.responseJSON.data.message);
          });
      } else {
        $("#topology-div-error").removeClass('hide');
        $("#topology-div-error").fadeIn();
        $("#topology-div-error").empty();
        $("#topology-div-error").append(i18n('Topologies.error.import.name'));  
      }
    }
  });
});
