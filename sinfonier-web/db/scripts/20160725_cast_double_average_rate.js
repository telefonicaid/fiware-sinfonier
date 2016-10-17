//From mongo shell, use "load('20160725_cast_double_average_rate.js')"
db = db.getSiblingDB('sinfonier');
db.modules.find({'average_rate': {$type: 16}}).forEach(function(x) {   
  db.modules.update({ _id: x._id },
    {$set: {
        average_rate_NEW: Number(x.average_rate)
     }});
  db.modules.updateMany({ _id: x._id }, {$rename: {"average_rate": "average_rate_OLD"}});
  db.modules.updateMany({ _id: x._id }, {$rename: {"average_rate_NEW": "average_rate"}});
  db.modules.update({ _id: x._id }, {$unset: {average_rate_NEW:1}},false,true);
  db.modules.update({ _id: x._id }, {$unset: {average_rate_OLD:1}},false,true);
});