//From mongo shell, use "load('20160908_update_module_name_in_topology.js')"
db = db.getSiblingDB('sinfonier');

var _COL = 'topologies';

var cursor = db[_COL].find({});
while (cursor.hasNext()) {
  var topology = cursor.next();

  for (var i = 0; i < topology['config']['modules'].length; i++) {
    var _module = topology['config']['modules'][i];

    try {
      var _m_name = _module.name.trim().split(' (');

      if (_m_name.length > 0) _module.name = _m_name[0];

      var module = db['modules'].find({'name': _module.name});
      module = module[0];
      var version_id;
      for (var k = 0; k < module.versions.length; k++) {
        if (module.versions[k]['versionCode'] == _module.versionCode) {
          version_id = module.versions[k]['versionId'];
          break;
        }
      }

      if (module == null || version_id == null) continue;

      _module['module_id'] = module['_id'];
      _module['module_version_id'] = version_id;
    } catch (E) {
      continue;
    }

    topology['config']['modules'][i] = _module;
  }

  db[_COL].remove({_id: topology._id});
  delete topology._id;
  db[_COL].insert(topology);
}