//From mongo shell, use "load('20160729_change_author_model.js')"
db = db.getSiblingDB('sinfonier');
db.modules.find({}).forEach(function(x) {
  // Change author for authorId in module
  db.modules.update({ _id: x._id },
    {$set: {
        authorId: x.author.id
     }}
  );
  db.modules.update({ _id: x._id }, {$unset: {author:1}},false,true);
});

db.topologies.find({}).forEach(function(x) {
  // Change author for authorId in topology
  db.topologies.update({ _id: x._id },
    {$set: {
        authorId: x.author.id
     }}
  );
  db.topologies.update({ _id: x._id }, {$unset: {author:1}},false,true);
});

//Change author for authorId in ratings, complains and my_tools
var cursor = db.modules.find({});
while(cursor.hasNext()) {
    var module = cursor.next();
    
    if (module.ratings) {
      for (i=0; i < module.ratings.length; i++) {
        module.ratings[i].userId = module.ratings[i].author.id;
        delete module.ratings[i].author;
      }
    
      db.modules.update({_id: module._id}, {$set: {'ratings': module.ratings}}, false, true);
    }
    
    if (module.complains) {
      for (i=0; i < module.complains.length; i++) {
        module.complains[i].userId = module.complains[i].author.id;
        delete module.complains[i].author;
      }
    
      db.modules.update({_id: module._id}, {$set: {'complains': module.complains}}, false, true);
    }
    
    if (module.my_tools) {
      for (i=0; i < module.my_tools.length; i++) {
        module.my_tools[i].userId = module.my_tools[i].author.id;
        delete module.my_tools[i].author;
      }
    
      db.modules.update({_id: module._id}, {$set: {'my_tools': module.my_tools}}, false, true);
    }
}
