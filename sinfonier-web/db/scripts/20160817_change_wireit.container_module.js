//From mongo shell, use "load('20160817_change_wireit.container_module.js')"
db = db.getSiblingDB('sinfonier');

// Update container xtype 'WireIt.FormContainer' in moduleVersions
var cursor = db.moduleVersions.find({});
while(cursor.hasNext()) {
  var version = cursor.next();
  
  if (version.container) {
    if (version.container.xtype == "WireIt.Container") {
      db.moduleVersions.update({_id: version._id}, {$set: {'container.xtype': 'WireIt.FormContainer'}}, false, true);
    }
  }
}

//Update container xtype 'WireIt.FormContainer' in topologies
var cursor2 = db.topologies.find({});
while(cursor2.hasNext()) {
  var topology = cursor2.next();
  
  if (topology.config) {
    if (topology.config.modules) {
      for (i=0; i < topology.config.modules.length; i++) {
        if (topology.config.modules[i].config.xtype == "WireIt.Container") {  
          topology.config.modules[i].config.xtype = "WireIt.FormContainer";
        }
      }
    
      db.topologies.update({_id: topology._id}, {$set: {'config.modules': topology.config.modules}}, false, true);
    }
    
  }
}
