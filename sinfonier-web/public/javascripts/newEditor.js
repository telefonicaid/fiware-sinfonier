(function () {
  $(document).ready(function () {
    $('#properties-form #properties-name').change(function () {
      var workspace = RED.workspaces.active();
      if (workspace) {
    	  RED.nodes.workspace(RED.workspaces.active()).label = $(this).val(); 
    	  RED.workspaces.refresh();
      }
    });
  });
})();

function getCategory(type) {
  if (type == 'variable' || type == 'comment') {
    return 'util';
  } else {
    return type;
  }	  
}

//TODO calculate inputs number when a modules has wired fields
function getInputs(type) {
  switch (type) {
    case 'spout':
    case 'variable':
    case 'comment':
      return 0;
    case 'bolt':
    case 'drain':
    case 'operator':
      return 1;
  } 	  
}

function getOutputs(type) {
  switch (type) {
    case 'comment':	
    case 'drain':
  	  return 0;	
    case 'spout':
    case 'variable':
    case 'bolt':	
      return 1;
    case 'operator':
      return 2;
  }	  
}

function getColor(type) {
  switch (type) {
    case 'spout':
      return '#E6BBB8';
    case 'variable':
    case 'comment':
      return '#E6EFEC';
    case 'bolt':
      return '#C0E2EF';
    case 'drain':
      return '#CEC8C8';
    case 'operator':
      return '#C7F9C2';
  } 	  
}

function getIcon(type) {
  switch (type) {
    case 'spout':
      return 'spout.png';
    case 'variable':
      return 'globe.png';	
    case 'comment':
      return 'note_edit.png';
    case 'bolt':
      return 'bolt.png';
    case 'drain':
      return 'drain.png';
    case 'operator':
      return 'operator.png';
  } 	  
}

REDSINF = {}

REDSINF.form = {
  oneditprepare : function() {
    var node = this;
    var fields = node._def.fields;
    for (var key  in fields) {
      var def = fields[key]
      if (def.type == "list"){
        REDSINF.form.prepare_list(node,key,def);
      }
    }
  },
  oneditsave : function() {
    var node = this;
    var fields = node._def.fields;
    for (var key  in fields) {
      var def = fields[key]
      if (def.type == "list"){
        REDSINF.form.save_list(node,key,def);
      }
    }
  },
  oneditresize : function(size) {
    var rows = $("#dialog-form>div:not(.node-input-rule-container-row)");
  },
  
  prepare_list : function(node,key,def) {
    
    $("#node-input-list-"+key).css('min-height', '200px').css('min-width', '450px').editableList({
      addItem : function(container, i, opt) {
        var value = [];
        var placeholder = def.labels;
        if (placeholder.length == 0){
           placeholder.push('');
        }
        if (typeof opt == "string") {
          value = [opt];
        } else if ( Object.prototype.toString.call( opt ) === '[object Array]' ) {
          value = opt;
        }
        else {
          for (var i=0; i<def.numElements;i++ ){
            value.push('');
          }
        }
        for (var i=0; i<value.length;i++ ){
          $('<input/>',{class:"node-input-list-"+key+"-value",type:"text",style:"width:"+(90/value.length)+"%;margin-left: 5px;",value:value[i], placeholder:placeholder[i]}).appendTo(container);
        }
      },
      removable : true
    });

    for (var i = 0; i < node[key].length; i++) {
      var value = node[key][i];
      $("#node-input-list-"+key).editableList('addItem', value);
    }

  },

  save_list : function(node,key,def) {
    
    var valueList = $("#node-input-list-"+key).editableList('items');
    node[key] = [];
    valueList.each(function(i) { 
      var values = $(this);
      var value = [];
      values.find("input").each(function(){
        value.push($(this).val());
      });
      node[key].push(value);
    });

  }


}