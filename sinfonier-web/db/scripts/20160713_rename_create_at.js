//From mongo shell, use "load('20160713_rename_create_at.js')"
db = db.getSiblingDB('sinfonier');
db.modules.updateMany({},{$rename:{'create_at': 'created_at'}});
db.topologies.updateMany({},{$rename:{'create_at': 'created_at'}});