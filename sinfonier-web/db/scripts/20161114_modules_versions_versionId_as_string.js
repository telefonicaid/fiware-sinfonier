//From mongo shell, use "load('20161114_modules_versions_versionId_as_string.js')"
db = db.getSiblingDB('sinfonier');
var cursor = db.modules.find({"versions.versionId": {$type: "objectId"}},{"versions.versionId": true});
while(cursor.hasNext()) {
    var module = cursor.next();
    db.modules.update({_id: module._id, "versions.versionId": {$type: "objectId"}}, {$set: {"versions.$.versionId": module.versions[0]["versionId"].str}}, false, true);
}
