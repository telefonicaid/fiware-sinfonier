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