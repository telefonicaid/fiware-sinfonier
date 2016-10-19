var extraConf = {};

function parseExtraConfig(extraConfField) {
  var keyPattern = /^([a-zA-Z0-9]+)*$/i;
  var valuePattern = /^([a-zA-Z0-9_\-\.:/@]+){1,25}([,][\s]*([a-zA-Z0-9_\-\.:/@]+){1,25})*$/i;
  
  extraConf = {};
  var rows = extraConfField.value.split("\n");
  for (var i=0; i<rows.length ; i++) {
    if (rows[i].length == 0) {
      continue;
    }

    var values = rows[i].split("=");
    if (values.length != 2 || values[0].trim().length == 0 || values[1].trim().length == 0) {
      continue;
    }
    
    if (keyPattern.test(values[0]) && valuePattern.test(values[1])) {
      this.extraConf[values[0]] = values[1];
    }
  }
}

function revalidateRelatedFields() {
  var topologyConfKeyRefPattern = /^\[\$([a-zA-Z0-9]+)\]*$/i;
  for (var i = 0; i< webhookit.editor.layer.containers.length; i++) {
    var container = webhookit.editor.layer.containers[i];
    for (var j = 0; j < container.form.inputs.length; j++) {
      if (topologyConfKeyRefPattern.test(container.form.inputs[j].getValue())) {
        container.form.inputs[j].setClassFromState();
      }
    }
  }	
}

function regenerateModulesDD() {
  var layerPos = Dom.getXY(webhookit.editor.layer.el);
  for (var i=0; i<webhookit.editor.layer.containers.length; i++) {
    webhookit.editor.layer.containers[i].dd = new WireIt.util.DD(webhookit.editor.layer.containers[i].terminals,webhookit.editor.layer.containers[i].el);
    var containerPos = Dom.getXY(webhookit.editor.layer.containers[i].el);
    webhookit.editor.layer.containers[i].dd.setXConstraint(containerPos[0] - layerPos[0]);
    webhookit.editor.layer.containers[i].dd.setYConstraint(containerPos[1] - layerPos[1]);
    if(webhookit.editor.layer.containers[i].ddHandle) {
      webhookit.editor.layer.containers[i].dd.setHandleElId(webhookit.editor.layer.containers[i].ddHandle);
    }
  }	  
}

function adviseTopologyNotSaved(event) {
  if (!webhookit.editor.isSaved()) {
    if (!confirm("Warning: Your work is not saved yet! Press ok to continue anyway.")) {
      event.preventDefault();
      return;
    }
  }    
}