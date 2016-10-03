//From mongo shell, use "load('20160804_change_module_version.js')"
db = db.getSiblingDB('sinfonier');
db.modules.find({}).forEach(function(x) {
  // Create moduleVersions
  var doc = {
      "versionTag" : "1.0",
      "versionCode" : NumberInt(1),
      "status" : x.status,
      "sourceType" : x.sourceType,
      "sourceCode" : x.sourceCode,
      "sourceCodeUrl" : x.sourceCodeUrl,
      "description" : x.description,
      "created_at" : x.created_at,
      "updated_at" : x.updated_at,
      "singleton" : x.singleton,
      "topologies_count" : NumberInt(x.topologies_count),
      "ticktuple" : x.ticktuple,
      "fields" : x.fields,
      "libraries" : x.libraries,
      "container" : {
          "xtype" : x.container.xtype,
          "type" : x.container.type,
          "icon" : x.container.icon,
          "attributes" : x.container.attributes,
          "terminals" : x.container.terminals
      },
      "my_tools" : x.my_tools
  };
  db.moduleVersions.insert(doc);
  db.moduleVersions.find({}).sort({_id:-1}).limit(1).forEach(function(y) {
    var tag = "1.0";
    var isDeleted = false;
    var isVisible = true;
    if (y.status == "developing") tag = null;
    if (y.status == "deleted") isDeleted = true;
    if (y.status == "private") isVisible = false;
    db.modules.update({ _id: x._id }, {$set: {
      versions:[{
        "versionTag": tag,
        "versionCode": NumberInt(1),
        "versionId": y._id,
        "isDeleted": isDeleted,
        "isVisible": isVisible
      }]
    }},false,true);
  });
  
  db.modules.update({ _id: x._id }, {$unset: {sourceType:1}},false,true);
  db.modules.update({ _id: x._id }, {$unset: {sourceCode:1}},false,true);
  db.modules.update({ _id: x._id }, {$unset: {sourceCodeUrl:1}},false,true);
  db.modules.update({ _id: x._id }, {$unset: {description:1}},false,true);
  db.modules.update({ _id: x._id }, {$unset: {singleton:1}},false,true);
  db.modules.update({ _id: x._id }, {$unset: {ticktuple:1}},false,true);
  db.modules.update({ _id: x._id }, {$unset: {fields:1}},false,true);
  db.modules.update({ _id: x._id }, {$unset: {libraries:1}},false,true);
  db.modules.update({ _id: x._id }, {$unset: {container:1}},false,true);
});

// Update topologies
//Change author for authorId in ratings, complains and my_tools
var cursor = db.topologies.find({});
while(cursor.hasNext()) {
    var topology = cursor.next();
    
    if (topology.config) {
      if (topology.config.modules) {
        for (i=0; i < topology.config.modules.length; i++) {
          if (topology.config.modules[i].type != "comment" && topology.config.modules[i].type != "variable" &&
              topology.config.modules[i].type != "operator") {	
            topology.config.modules[i].name = topology.config.modules[i].name+" (1.0)";
            topology.config.modules[i].versionCode = NumberInt(1);
          }
        }
      
        db.topologies.update({_id: topology._id}, {$set: {'config': topology.config}}, false, true);
      }
      
    }
}

