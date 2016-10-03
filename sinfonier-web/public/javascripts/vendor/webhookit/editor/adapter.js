/**
 * Ajax Adapter using a REST interface. Expect JSON response for all queries.
 * @static
 */
webhookit.language.adapter = {

  init: function () {
    YAHOO.util.Connect.setDefaultPostHeader('application/json; charset=utf-8');
  },
  saveWiring: function (val, callbacks) {
    try {
      var prev = webhookit.editor.pipesByName[val.prev];
      var url = '/topologies/save', topology = {};
      
      if (val.prev != val.name) {
        var newName = webhookit.editor.pipesByName[val.name];
        if (newName) {
          callbacks.failure.call(callbacks.scope, "Topology with that name already exists");
          return
        }
      }
      
      if (prev) {
        YAHOO.lang.augmentObject({}, prev);
        url = '/topologies/' + prev.id + '/save';
      } else {
        if (val.template_id) {
          topology.template_id = val.template_id;
        }
      }

      topology.name = val.name;
      topology.description = val.description;
      topology.config = val.working;
      topology.sharing = val.sharing;
      if (prev) {
        topology.id = prev.id; 
      }

      var postData = YAHOO.lang.JSON.stringify({"topology": topology});

      YAHOO.util.Connect.asyncRequest('POST', url, {
        success: function (o) {
          var r, s;
          if (!prev) {
            s = o.responseText;
            r = YAHOO.lang.JSON.parse(s);
            document.getElementById("previous").value = r.name;
            webhookit.editor.pipesByName[r.name] = r;
          }

          callbacks.success.call(callbacks.scope, r);
        },
        failure: function (o) {
          // send error from body response
          s = o.responseText;
          r = YAHOO.lang.JSON.parse(s);
          callbacks.failure.call(callbacks.scope, r.data.message);
        }
      }, postData);
    } catch (ex) {
      console.log(ex);
    }
  },
  deleteWiring: function (val, callbacks) {
    var topology = webhookit.editor.pipesByName[val.name];
    if (!topology) {
      callbacks.failure.call(callbacks.scope, "Topology not saved yet!");
    }
    var url = '/topologies/' + topology.id + '.json';
    YAHOO.util.Connect.asyncRequest('DELETE', url, {
      success: function (o) {
        callbacks.success.call(callbacks.scope, {});
      },
      failure: function (o) {
        var error = o.status + " " + o.statusText;
        callbacks.failure.call(callbacks.scope, error);
      }
    });
  },
  listWirings: function (val, callbacks) {
    var url = '/topologies';
    if (val.template) {
      url = url + "?template=" + val.template;
    }
    YAHOO.util.Connect.asyncRequest('GET', url, {
      success: function (o) {
        var s = o.responseText;
        var v = YAHOO.lang.JSON.parse(s);
        var p = [];
        for (var i = 0; i < v.length; i++) {
          p.push({
            id: v[i].id,
            name: v[i].name,
            description: v[i].description,
            sharing: v[i].sharing,
            working: v[i].config
          });
        }
        callbacks.success.call(callbacks.scope, p);
      },
      failure: function (o) {
        var error = o.status + " " + o.statusText;
        callbacks.failure.call(callbacks.scope, error);
      }
    });
  },
  loadModule: function (moduleName, versionCode, store) {
    var deferred = $.Deferred();

    $.getJSON("/drawer/modules/" + moduleName + "/versions/" + versionCode + "/json")
      .done(function (module) {
        var module_key;  
        if (!versionCode) {
          module_key = moduleName;
        } else {
          module_key = moduleName + ' (' + versionCode + ')'
        }  
        store[module_key] = module;
        deferred.resolve();
      })
      .fail(function () {
        alert("failed");
        deferred.reject(moduleName);
      });

    return deferred.promise();
  }
};
