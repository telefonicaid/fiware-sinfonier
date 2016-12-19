//From mongo shell, use "load('20161219_init_versionCode_on_predefined_modules.js')"
db = db.getSiblingDB('sinfonier');
var cursor = db.topologies.find();
while(cursor.hasNext()) {
	var topology = cursor.next();
	for (var i = 0; i < topology['config']['modules'].length; i++) {
		var module = topology['config']['modules'][i];
    	if (!module.versionCode){
    		db.topologies.update({_id: topology._id, "config.modules.name": module.name}, {$set: {"config.modules.$.versionCode": NumberInt(0.0)}}, false, true);
    	}
	}
}
