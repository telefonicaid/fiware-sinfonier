//From mongo shell, use "load('20160826_remove_name_&_description_&_move_parallelism.js')"
db = db.getSiblingDB('sinfonier');


var _COL = 'topologies';

// Update container xtype 'WireIt.FormContainer' in moduleVersions
var cursor = db[_COL].find({});
while (cursor.hasNext()) {
  var topology = cursor.next();

  if (topology.config.properties.name) delete topology.config.properties.name;
  if (topology.config.properties.description) delete topology.config.properties.description;

  for (var i = 0; i < topology['config']['modules'].length; i++) {
    var parallelism = topology['config']['modules'][i]['value']['parallelism'];

    if (parallelism == undefined) {
      parallelism = 1;
    } else {
      delete topology['config']['modules'][i]['value']['parallelism'];
    }

    topology['config']['modules'][i]['parallelism'] = NumberInt(parallelism);
  }

  db[_COL].remove({_id: topology._id});
  delete topology._id;
  db[_COL].insert(topology);
}
