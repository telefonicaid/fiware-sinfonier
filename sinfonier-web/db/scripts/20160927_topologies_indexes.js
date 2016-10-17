//From mongo shell, use "load('20160927_topologies_indexes.js')"
db = db.getSiblingDB('sinfonier');
db.topologies.ensureIndex({name:1},{unique:true});