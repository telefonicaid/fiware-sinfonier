//From mongo shell, use "load('20160902_avoid_modules_fields_int_values_saved_as_double.js')"
db = db.getSiblingDB('sinfonier');

var _COL = 'topologies';

// Update modules fields integer values saved as double
var cursor = db[_COL].find({});
while (cursor.hasNext()) {
  var topology = cursor.next();

  for (var i = 0; i < topology['config']['modules'].length; i++) {
    var keys = Object.keys(topology['config']['modules'][i]['value']);  
    for (var j = 0; j < keys.length; j++) {
      var value_key = keys[j];
      var value = topology['config']['modules'][i]['value'][value_key];
      if (typeof value == "number" && parseInt(Number(value)) == value) {    	  
        topology['config']['modules'][i]['value'][value_key] = NumberInt(value);  
      }
    }
  }

  db[_COL].remove({_id: topology._id});
  delete topology._id;
  db[_COL].insert(topology);
}
