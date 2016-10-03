"use strict";

var webhookit = {

  language: {
    languageName: "webhookit",
    propertiesFields: [
      {"type": "string", inputParams: {"name": "name", label: "Topology Name", typeInvite: "(mandatory)"}},
      {"type": "text", inputParams: {"name": "description", label: "Description", cols: 30, rows: 3}}
    ],
    layoutOptions: {
      units: ([{
        position: 'bottom',
        height: 170,
        body: 'bottom',
        resize: true,
        header: '<button id="refreshDebug">Debug</button><button id="refreshXML">XML</button>',
        scroll: true,
        collapse: true,
        gutter: '5px'
      }]).concat(WireIt.BaseEditor.defaultOptions.layoutOptions.units)
    },
    required: true,
    modules: []
  },

  init: function () {
    try {
      this.editor = new webhookit.WiringEditor(this.language);

      YAHOO.util.Dom.setStyle('app', 'display', '');
      YAHOO.util.Dom.setStyle('appLoading', 'display', 'none');

      YAHOO.util.Event.addListener('refreshDebug', 'click', function () {
        this.refreshDebug(this.lastSelectedContainerIndex);
      }, this, true);
      YAHOO.util.Event.addListener('refreshXML', 'click', function () {
        this.refreshXML(this.lastSelectedContainerIndex);
      }, this, true);

      this.editor.accordionView.openPanel(1);
    } catch (ex) {
      console.log(ex);
    }
  },
  editTemplateButton: function () {
    var value = this.editor.getValue();
    // TODO: save it first

    // Get the id
    var prev = this.editor.pipesByName[value.name];

    if (!prev || !prev.id) {
      this.editor.alert("Open a topology first.");
      return;
    }

    window.location = "/topologies/" + prev.id + "/edit-template";
  },
  run: function () {
    var value = this.editor.getValue();
    if (!this.editor.isSaved()) {
      this.editor.save();

      if (!this.editor.isSaved()) {
        return;
      }
    }

    // Get the id
    var prev = this.editor.pipesByName[value.name];

    if (!prev || !prev.id) {
      this.editor.alert("Open a topology first.");
      return;
    }
    var _editor = this.editor;
    YAHOO.util.Connect.asyncRequest('POST', '/topology/' + prev.id + '/launch', {
      success: function (o) {
        var s = o.responseText, r = YAHOO.lang.JSON.parse(s);
        window.location.pathname = 'topologies/' + r.data.name;
      },
      failure: function (o) {
        if (!o) {
          console.error('Something was wrong');
          return;
        }

        var s = o.responseText, r = YAHOO.lang.JSON.parse(s);
        _editor.alert("Unable to launch the topology : " + r.data.message);
      }
    });
  },
  refreshDebug: function (index) {

    var value = this.editor.getValue();

    YAHOO.util.Dom.get('bottom').innerHTML = "<img src='/images/spinner.gif' />";

    YAHOO.util.Connect.asyncRequest('POST', '/editor/debug', {
      success: function (o) {
        var s = o.responseText,
          r = YAHOO.lang.JSON.parse(s);
        this.lastDebugRun = r;
        YAHOO.util.Dom.get('bottom').innerHTML = "";
        new inputEx.widget.JsonTreeInspector('bottom', r[index]);
      },
      failure: function (o) {
        try {
          var s = o.responseText,
            r = YAHOO.lang.JSON.parse(s);
        } catch (ex) {
          YAHOO.util.Dom.get('bottom').innerHTML = "Server error";
          return;
        }

        YAHOO.util.Dom.get('bottom').innerHTML = r.error;
      },
      scope: this
    }, YAHOO.lang.JSON.stringify(value));

  },
  refreshXML: function (index) {

    var value = this.editor.getValue();

    YAHOO.util.Dom.get('bottom').innerHTML = "<img src='/images/spinner.gif' />";

    YAHOO.util.Connect.asyncRequest('POST', '/editor/xml.json', {
      success: function (o) {
        var s = o.responseText,
          r = YAHOO.lang.JSON.parse(s);
        this.lastDebugRun = r;
        YAHOO.util.Dom.get('bottom').innerHTML = "";
        new inputEx.widget.JsonTreeInspector('bottom', r[index]);
      },
      failure: function (o) {
        try {
          var s = o.responseText,
            r = YAHOO.lang.JSON.parse(s);
        } catch (ex) {
          YAHOO.util.Dom.get('bottom').innerHTML = "Server error";
          return;
        }

        YAHOO.util.Dom.get('bottom').innerHTML = r.error;
      },
      scope: this
    }, YAHOO.lang.JSON.stringify(value));

  },
  updateDebugPanel: function (index) {

    if (!this.lastDebugRun) {
      YAHOO.util.Dom.get('bottom').innerHTML = "Run it first...";
      return;
    }
    else {
      YAHOO.util.Dom.get('bottom').innerHTML = "";
      new inputEx.widget.JsonTreeInspector('bottom', this.lastDebugRun[index]);
    }

  },
  updateInfoPanel: function (index, elem) {

    var text = elem.description || "Sinfonier - Not documented";
    this.editor.accordionView.openPanel(2);
    document.getElementById("description-paragraph").innerHTML = text;

  }
};

YAHOO.util.Event.onDOMReady(webhookit.init, webhookit, true);

/**
 * For the debugger so he knows the last selected container to display debug infos
 */
WireIt.Container.prototype.setFocus = function () {
  if (!YAHOO.util.Dom.hasClass(this.el, "WireIt-Container-focused")) {
    webhookit.lastSelectedContainerIndex = WireIt.indexOf(this, webhookit.editor.layer.containers);
    webhookit.updateDebugPanel(webhookit.lastSelectedContainerIndex);
    webhookit.updateInfoPanel(webhookit.lastSelectedContainerIndex, this);
  }
  YAHOO.util.Dom.addClass(this.el, "WireIt-Container-focused");
};

