//From mongo shell, use "load('20161213_users_init_or_update_created_at.js')"
db = db.getSiblingDB('sinfonier');
var cursor = db.users.find();
while(cursor.hasNext()) {
    var user = cursor.next();
    if (!user.t || (user.t && user.t > user.passChange)){
    	db.users.update({_id: user._id}, {$set: {t: user.passChange}}, false, true);
    }
}
