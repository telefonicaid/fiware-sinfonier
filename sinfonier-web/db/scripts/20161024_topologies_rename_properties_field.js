//From mongo shell, use "load('20161024_topologies_rename_properties_field.js')"
db = db.getSiblingDB('sinfonier');
db.topologies.updateMany({}, {$rename: {"config.properties": "config.stormProperties"}});