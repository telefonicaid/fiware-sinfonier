/**
 * The wiring editor is overriden to add a button "RUN" to the control bar
 * @class webhookit.WiringEditor
 * @extends WireIt.ComposableWiringEditor
 */
webhookit.WiringEditor = function (options) {
  webhookit.WiringEditor.superclass.constructor.call(this, options);
};
var readURLParams = function () {
  var p = window.location.search.substr(1).split('&');
  var oP = {};
  for (var i = 0; i < p.length; i++) {
    var v = p[i].split('=');
    oP[v[0]] = window.decodeURIComponent(v[1]);
  }
  return oP;
};
var util = YAHOO.util,
  lang = YAHOO.lang,
  Dom = util.Dom;

YAHOO.lang.extend(webhookit.WiringEditor, WireIt.ComposableWiringEditor, {

  composedCategory: "My Topologies",
  checkAutoloadOnce: false,
  firstTime: true,
  urlParams: readURLParams(),
  scale: 1,

  /**
   * Customize the load success handler for the composed module list
   */
  onLoadSuccess: function (topologies) {

    // Reset the internal structure
    this.pipes = topologies;
    this.pipesByName = {};

    // Build the "pipesByName" index
    for (var i = 0; i < this.pipes.length; i++) {
      this.pipesByName[this.pipes[i].name] = this.pipes[i];
    }

    //  Customize to display composed module in the left list
    this.updateComposedModuleList();

    if (topologies.length > 0) {
      this.updateLoadPanelList();
    }

    // Check for autoload param and display the loadPanel otherwise
    if (!this.checkAutoLoad() && topologies.length > 0) {
      this.loadPanel.show();
      if (this.firstTime) {
        if (this.urlParams.new) {
          this.loadPanel.hide();
        }
      }
    }

    this.firstTime = false;
  },

  /**
   * All the saved topologies are reusable modules :
   */
  updateComposedModuleList: function () {

    // Remove all previous module with the ComposedModule class
    var el = YAHOO.util.Dom.get("module-category-Composed");
    if (el) {
      // Purge element (remove listeners on el and childNodes recursively)
      YAHOO.util.Event.purgeElement(el, true);
      el.innerHTML = "";
    }

    /*
     We do not load the composed topologies in the editor, but we have made the call.
     TODO: Avoid to make the call for loading composed modules
     */
    /*
     if(YAHOO.lang.isArray(this.pipes)) {
     for(var i = 0 ; i < this.pipes.length ; i++) {
     var module = this.pipes[i];

     var m = {
     name: module.name,
     category: this.composedCategory,
     container: {
     "xtype": "WireIt.ComposedContainer",
     "title": module.name,
     "wiring": this.pipes[i]
     }
     };
     YAHOO.lang.augmentObject(m, this.pipes[i]);

     this.modulesByName[module.name] = m;

     this.addModuleToList(m);
     }
     }
     */

  },

  /**
   * Start the loading of the pipes using the adapter
   * @method load
   */
  load: function () {
    var opts = {language: this.options.languageName};
    if (this.urlParams.template) {
      opts.template = this.urlParams.template;
    }
    this.adapter.listWirings(opts, {
      success: function (result) {
        this.onLoadSuccess(result);
      },
      failure: function (errorStr) {
        this.alert("Unable to load the topologies: " + errorStr);
      },
      scope: this
    });

  },

  /**
   * render the modules accordion in the left panel
   */
  renderModulesAccordion: function () {
    var Dom = YAHOO.util.Dom;

    var buildPanelElem = function (name) {
      var li = WireIt.cn('li');
      li.appendChild(WireIt.cn('h2', null, null, name));
      var d = WireIt.cn('div');
      d.appendChild(WireIt.cn('div', {id: "module-category-" + name}));
      li.appendChild(d);
      return li;
    };
    // Create the modules accordion DOM if not found
    if (!Dom.get('modulesAccordionView')) {
      Dom.get('left').appendChild(WireIt.cn('ul', {id: 'modulesAccordionView'}));
      var ul = Dom.get('modulesAccordionView');
      ul.appendChild(buildPanelElem("Spouts"));
      ul.appendChild(buildPanelElem("Bolts"));
      ul.appendChild(buildPanelElem("Drains"));
      ul.appendChild(buildPanelElem("Operators"));
      ul.appendChild(buildPanelElem("Utils"));
    }

    this.modulesAccordionView = new YAHOO.widget.AccordionView('modulesAccordionView', this.options.modulesAccordionViewParams);

    // Open all panels
    for (var l = 1, n = this.modulesAccordionView.getPanels().length; l < n; l++) {
      this.modulesAccordionView.openPanel(l);
    }
  },

  /**
   * Create a help panel
   * @method onHelp
   */
  onHelp: function () {
    window.open('http://sinfonier-project.net/features.php', '_blank');
    //this.helpPanel.show();
  },

  /**
   * Create a help panel
   * @method onHelp
   */
  zoomOut: function () {
    this.scale -= this.scale * .1;

    if (Math.abs(this.scale * 1000 - 1000) < 50) {
      $(".WireIt-Layer").css("overflow", "scroll");
      $("#center").css("overflow", "inherit");
      $(".WireIt-Layer").css("transform", "none");
      $(".WireIt-Layer").css("width", "100%");
      $(".WireIt-Layer").css("height", "100%");
      this.scale = 1;
    }
    else {
      $(".WireIt-Layer").css("transform-origin", "left top");
      $(".WireIt-Layer").css("transform", "scale(" + this.scale + "," + this.scale + ")");
      $(".WireIt-Layer").css("width", "" + (100 * (1 / this.scale)) + "%");
      $(".WireIt-Layer").css("height", "" + (100 * (1 / this.scale)) + "%");
    }
    $(".WireIt-Layer").data("scale", this.scale);
  },

  /**
   * Create a help panel
   * @method onHelp
   */
  zoomIn: function () {
    this.scale += (this.scale + .1) * .1;

    if (this.scale > 1 || Math.abs(this.scale * 1000 - 1000) < 50) {
      $(".WireIt-Layer").css("overflow", "scroll");
      $("#center").css("overflow", "inherit");
      $(".WireIt-Layer").css("transform", "none");
      $(".WireIt-Layer").css("width", "100%");
      $(".WireIt-Layer").css("height", "100%");
      this.scale = 1;
    }
    else {
      $(".WireIt-Layer").css("transform-origin", "left top");
      $(".WireIt-Layer").css("transform", "scale(" + this.scale + "," + this.scale + ")");
      $(".WireIt-Layer").css("width", "" + (100 * (1 / this.scale)) + "%");
      $(".WireIt-Layer").css("height", "" + (100 * (1 / this.scale)) + "%");

    }
    $(".WireIt-Layer").data("scale", this.scale);
    this.layer.eventChanged.fire(this.layer);
  },

  /**
   * save the current module
   * @method saveModule
   */
  save: function () {
    var value = this.getValue();

    if (value.name === "") {
      this.alert("Please choose a name");
      return;
    }

    if(!/\w+/i.test(value.name)) {
      this.alert("Please choose a valid name");
      return;
    }

    value.name = _.upperFirst(_.camelCase(_.deburr(value.name)));

    if (document.getElementsByClassName("inputEx-invalid").length > 0) {
      this.alert("Review required fields on modules");
      return;
    }

    var previous = document.getElementById("previous").value;
    var template_id = document.getElementById("templateid").value;

    this.tempSavedWiring = {
      prev: previous,
      template_id: template_id,
      name: value.name,
      description: value.description,
      sharing: value.sharing,
      working: value.working,
      language: this.options.languageName
    };

    this.adapter.saveWiring(this.tempSavedWiring, {
      success: this.saveModuleSuccessId,
      failure: this.saveModuleFailure,
      scope: this
    });

  },

  saveModuleSuccessId: function (o) {
    this.saveModuleSuccess(o);
    // TODO: store the id instead of working with name
  },

  /**
   * @method getPipeByName
   * @param {String} name Pipe's name or Pipe's id
   * @return {Object} return the evaled json pipe configuration
   */
  getPipeByName: function (name) {
    var n = this.pipes.length, ret;
    for (var i = 0; i < n; i++) {
      if (this.pipes[i].name == name || this.pipes[i].id == name) {
        return this.pipes[i];
      }
    }
    return null;
  },

  /**
   * Add Some buttons
   */
  renderButtons: function () {
    webhookit.WiringEditor.superclass.renderButtons.call(this);

    var toolbar = YAHOO.util.Dom.get('toolbar');

    /*
     var editTemplateButton = new YAHOO.widget.Button({ label:"Edit template", id:"WiringEditor-templateButton", container: toolbar });
     editTemplateButton.on("click", webhookit.editTemplateButton, webhookit, true);
     */
    // "Run" button
    var runButton = new YAHOO.widget.Button({label: "Run", id: "WiringEditor-runButton", container: toolbar});
    runButton.on("click", webhookit.run, webhookit, true);
    var zoomInButton = new YAHOO.widget.Button({label: "Zoom in", id: "WiringEditor-zoomInButton", container: toolbar});
    zoomInButton.on("click", this.zoomIn, this, true);
    var zoomOutButton = new YAHOO.widget.Button({
      label: "Zoom out",
      id: "WiringEditor-zoomOutButton",
      container: toolbar
    });
    zoomOutButton.on("click", this.zoomOut, this, true);

  },

  /**
   * checkAutoLoad looks for the "autoload" URL parameter and open the pipe.
   * returns true if it loads a Pipe
   * @method checkAutoLoad
   */
  checkAutoLoad: function () {
    if (!this.checkAutoLoadOnce) {
      var oP = this.urlParams;
      if (oP.autoload) {
        this.loadPipe(oP.autoload);
        this.checkAutoLoadOnce = true;
        return true;
      }
      if (oP.id) {
        this.loadPipe(oP.id);
        this.checkAutoLoadOnce = true;
        return true;
      }
      if (oP.template) {
        this.loadPipe("template");
        this.checkAutoLoadOnce = true;
        return true;
      }
    }
    return false;
  },

  /**
   * Build the left menu on the left
   * @method buildModulesList
   */
  buildModulesList: function () {
    var modules = this.modules;

    for (var i = 0; i < modules.length; i++) {
      this.addModuleToList(modules[i]);
    }

    // Make the layer a drag drop target
    if (!this.ddTarget) {
      this.ddTarget = new YAHOO.util.DDTarget(this.layer.el, "module");
      this.ddTarget._layer = this.layer;
    }
  },

  /**
   * Add a module definition to the left list
   */
  addModuleToList: function (module) {
    try {
      var div = WireIt.cn('div', {className: "WiringEditor-module"});

      if (module.description) {
        div.title = module.description;
      }

      if (module.type == 'spout') {
        if (module.authorId == userId) {
          div.appendChild(WireIt.cn('img', {src: '/public/images/icons/spout-user.png'}));
        } else {
          div.appendChild(WireIt.cn('img', {src: '/public/images/icons/spout.png'}));
        }
      } else if (module.type == 'bolt') {
        if (module.authorId == userId) {
          div.appendChild(WireIt.cn('img', {src: '/public/images/icons/bolt-user.png'}));
        } else {
          div.appendChild(WireIt.cn('img', {src: '/public/images/icons/bolt.png'}));
        }
      } else if (module.type == 'drain') {
        if (module.authorId == userId) {
          div.appendChild(WireIt.cn('img', {src: '/public/images/icons/drain-user.png'}));
        } else {
          div.appendChild(WireIt.cn('img', {src: '/public/images/icons/drain.png'}));
        }
      } else if (module.type == 'variable' || module.type == 'comment' || module.type == 'operator') {
        div.appendChild(WireIt.cn('img', {src: module.container.icon}));
      }
      div.appendChild(WireIt.cn('span', null, null, module.container.label));

      var ddProxy = new WireIt.ModuleProxy(div, this);
      ddProxy._module = module;

      // Get the category element in the accordion or create a new one
      var category = this.getCategory(module.container.type) || "main";
      var el = Dom.get("module-category-" + category);
      if (!el) {
        this.modulesAccordionView.addPanel({
          label: category,
          content: "<div id='module-category-" + category + "'></div>"
        });
        this.modulesAccordionView.openPanel(this.modulesAccordionView._panels.length - 1);
        el = Dom.get("module-category-" + category);
      }

      el.appendChild(div);
    } catch (ex) {
      console.log(ex);
    }
  },

  /**
   * This function will be called when a user drop a module over target (main canvas)
   */
  addModule: function (module, pos) {
    pos = pos.map(function (num) {
      return num / this.scale;
    }, this);

    try {
      var containerConfig = module.container, temp = this;
      containerConfig.position = pos;
      containerConfig.title = containerConfig.label;
      if (module.ticktuple) {
        containerConfig.fields = module.fields.concat(module.ticktuple);
      } else {
        containerConfig.fields = module.fields; 
      }

      containerConfig.getGrouper = function () {
        return temp.getCurrentGrouper(temp);
      };

      var container = this.layer.addContainer(containerConfig);
      container.form.validate();

      // Adding the category CSS class name
      var category = this.getCategory(module.container.type) || "main";
      Dom.addClass(container.el, "WiringEditor-module-category-" + category.replace(/ /g, '-'));

      // Adding the module CSS class name
      Dom.addClass(container.el, "WiringEditor-module-" + module.container.label.replace(/ /g, '-'));

    }
    catch (ex) {
      this.alert("Error Layer.addContainer: " + ex.message);
      if (window.console && YAHOO.lang.isFunction(console.log)) {
        console.log(ex);
      }
    }
  },

  /**
   * @method onNew
   */
  onNew: function () {

    if (!this.isSaved()) {
      if (!confirm("Warning: Your work is not saved yet ! Press ok to continue anyway.")) {
        return;
      }
    }

    document.getElementById("previous").value = "";
    document.getElementById("templateid").value = "";
    $("input[name=name]").attr('readonly', false);
    this.preventLayerChangedEvent = true;

    this.layer.clear();

    this.propertiesForm.clear(false); // false to tell inputEx to NOT send the updatedEvt

    this.markSaved();

    this.preventLayerChangedEvent = false;
  },

  /**
   * @method loadPipe
   * @param {String} name Pipe name
   */
  loadPipe: function (name) {

    if (!this.isSaved()) {
      if (!confirm("Warning: Your work is not saved yet ! Press ok to continue anyway.")) {
        return;
      }
    }

    try {
      this.preventLayerChangedEvent = true;
      this.loadPanel.hide();

      var pipe = this.getPipeByName(name);

      if (!pipe) {
        this.alert("The topology '" + name + "' was not found.");
        return;
      }

      document.getElementById("previous").value = pipe.name;
      try {
        document.getElementById("templateid").value = pipe.working.properties.templateid || '';
      } catch (ex) {
        pipe.working.properties = {};
        document.getElementById("templateid").value = ''
      }

      this.layer.clear();

      // Add to properties name and description for normalize
      pipe.working.properties['name'] = pipe.name;
      pipe.working.properties['description'] = pipe.description;

      this.propertiesForm.setValue(pipe.working.properties, false); // the false tells inputEx to NOT fire the updatedEvt
      if (pipe.name) // When loading a template not set readonly
        $("input[name=name]").attr('readonly', true);

      // When loading a template not set readonly
      if (pipe.name) $("input[name=name]").attr('readonly', true);

      if (!pipe.working.modules) pipe.working.modules = [];
      if (YAHOO.lang.isArray(pipe.working.modules)) {

        var notFound = [];
        var searched = {};

        for (var i = 0; i < pipe.working.modules.length; i++) {
          var m = pipe.working.modules[i];
          var module_key;

          if (!m.versionCode) {
            module_key = m.name;
          } else {
            module_key = m.name + ' (' + m.versionCode + ')'
          }

          if (!this.modulesByName[module_key]) {
            if (!searched[module_key]) {
              notFound.push(this.adapter.loadModule(m.name, m.versionCode, this.modulesByName));
              searched[module_key] = true;
            }
          }
        }

        var self = this;
        $.when.apply(this.adapter, notFound)
          .done(function () {
            // Containers
            for (i = 0; i < pipe.working.modules.length; i++) {
              var m = pipe.working.modules[i];
              var module_key;

              if (!m.versionCode) {
                module_key = m.name;
              } else {
                module_key = m.name + ' (' + m.versionCode + ')'
              }

              if (self.modulesByName[module_key]) {
                var baseContainerConfig = self.modulesByName[module_key].container;
                YAHOO.lang.augmentObject(m.config, baseContainerConfig);               
                m.config.title = baseContainerConfig.label;
                var container = self.layer.addContainer(m.config);
                YAHOO.util.Dom.addClass(container.el, "WiringEditor-module-" + m.name);
                container.setValue(m.value, m.parallelism);
              }
              else {
                throw new Error("WiringEditor: module '" + m.name + "' not found !");
              }
            }

            // Wires
            if (YAHOO.lang.isArray(pipe.working.wires)) {
              for (i = 0; i < pipe.working.wires.length; i++) {
                // On doit chercher dans la liste des terminaux de chacun des modules l'index des terminaux...
                self.layer.addWire(pipe.working.wires[i]);
              }
            }

            self.markSaved();
            self.preventLayerChangedEvent = false;
          })
          .fail(function (moduleName) {
            throw new Error("WiringEditor: module '" + m.name + "' not found !");
          });

      } else {
        this.markSaved();
        this.preventLayerChangedEvent = false;
      }
    }
    catch (ex) {
      this.alert(ex);
      if (window.console && YAHOO.lang.isFunction(console.log)) {
        console.log(ex);
      }
    }
  },

  getCategory: function (type) {
    if (type == 'variable' || type == 'comment') {
      return 'Utils';
    } else {
      return type.charAt(0).toUpperCase() + type.slice(1) + "s";
    }

  }
});
