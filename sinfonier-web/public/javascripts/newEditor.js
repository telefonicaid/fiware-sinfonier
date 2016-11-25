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