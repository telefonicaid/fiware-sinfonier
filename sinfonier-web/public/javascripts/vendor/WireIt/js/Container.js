/*global YAHOO,WireIt,window */
(function() {
   
   var util = YAHOO.util;
   var Dom = util.Dom, Event = util.Event, CSS_PREFIX = "WireIt-";
   
/**
 * Visual module that contains terminals. The wires are updated when the module is dragged around.
 * @class Container
 * @namespace WireIt
 * @constructor
 * @param {Object}   options      Configuration object (see options property)
 * @param {WireIt.Layer}   layer The WireIt.Layer (or subclass) instance that contains this container
 */
WireIt.Container = function(options, layer) {
   
   // Set the options
   this.setOptions(options);
   
   /**
    * the WireIt.Layer object that schould contain this container
    * @property layer
    * @type {WireIt.Layer}
    */
   this.layer = layer;
   
   /**
    * List of the terminals 
    * @property terminals
    * @type {Array}
    */
   this.terminals = [];
   
   /**
    * List of all the wires connected to this container terminals
    * @property wires
    * @type {Array}
    */
   this.wires = [];
   
   /**
    * Container DOM element
    * @property el
    * @type {HTMLElement}
    */
   this.el = null;
   
   /**
    * Body element
    * @property bodyEl
    * @type {HTMLElement}
    */
   this.bodyEl = null;

    /**
     * Body element
     * @property bodyEl
     * @type {HTMLElement}
     */
    this.parallEl = null;

  /**
    * Event that is fired when a wire is added
    * You can register this event with myTerminal.eventAddWire.subscribe(function(e,params) { var wire=params[0];}, scope);
    * @event eventAddWire
    */
   this.eventAddWire = new util.CustomEvent("eventAddWire");
   
   /**
    * Event that is fired when a wire is removed
    * You can register this event with myTerminal.eventRemoveWire.subscribe(function(e,params) { var wire=params[0];}, scope);
    * @event eventRemoveWire
    */
   this.eventRemoveWire = new util.CustomEvent("eventRemoveWire");
   
   this.eventFocus = new util.CustomEvent("eventFocus");
   
   this.eventBlur = new util.CustomEvent("eventBlur");
   
   // Render the div object
   this.render();
   
   // Init the terminals
	if( options.terminals ) {
		this.initTerminals( options.terminals);
	}
   
	// Make the container draggable
	if(this.draggable) {
		   
	   if(this.resizable) {
			// Make resizeable   
			this.ddResize = new WireIt.util.DDResize(this);
			this.ddResize.eventResize.subscribe(this.onResize, this, true);
	   }
	   
	   // Use the drag'n drop utility to make the container draggable
	   this.dd = new WireIt.util.DD(this.terminals,this.el);
	
		// Set minimum constraint on Drag Drop to the top left corner of the layer (minimum position is 0,0)
		this.dd.setXConstraint(this.position[0]);
		this.dd.setYConstraint(this.position[1]);
	   
	   // Sets ddHandle as the drag'n drop handle
	   if(this.ddHandle) {
			this.dd.setHandleElId(this.ddHandle);
	   }
	   
	   // Mark the resize handle as an invalid drag'n drop handle and vice versa
	   if(this.resizable) {
			this.dd.addInvalidHandleId(this.ddResizeHandle);
			this.ddResize.addInvalidHandleId(this.ddHandle);
	   }
   }
   
};

WireIt.Container.maxParallel = 5;
WireIt.Container.useParallel = true;

WireIt.Container.prototype = {
   
	/** 
    * @property xtype
    * @description String representing this class for exporting as JSON
    * @default "WireIt.Container"
    * @type String
    */
   xtype: "WireIt.Container",

	/** 
    * @property draggable
    * @description boolean that enables drag'n drop on this container
    * @default true
    * @type Boolean
    */
	draggable: true,
	
	/** 
    * @property position
    * @description initial position of the container
    * @default [100,100]
    * @type Array
    */
	position: [100,100],

	/** 
    * @property className
    * @description CSS class name for the container element
    * @default "WireIt-Container"
    * @type String
    */
	className: CSS_PREFIX+"Container",

	/** 
    * @property ddHandle
    * @description (only if draggable) boolean indicating we use a handle for drag'n drop
    * @default true
    * @type Boolean
    */
	ddHandle: true,
	
	/** 
    * @property ddHandleClassName
    * @description CSS class name for the drag'n drop handle
    * @default "WireIt-Container-ddhandle"
    * @type String
    */
	ddHandleClassName: CSS_PREFIX+"Container-ddhandle",

	/** 
    * @property resizable
    * @description boolean that makes the container resizable
    * @default true
    * @type Boolean
    */
	resizable: true,

	/** 
    * @property resizeHandleClassName
    * @description CSS class name for the resize handle
    * @default "WireIt-Container-resizehandle"
    * @type String
    */
	resizeHandleClassName: CSS_PREFIX+"Container-resizehandle",

	/** 
    * @property close
    * @description display a button to close the container
    * @default true
    * @type Boolean
    */
	close: true,
	
	/** 
    * @property closeButtonClassName
    * @description CSS class name for the close button
    * @default "WireIt-Container-closebutton"
    * @type String
    */
	closeButtonClassName: CSS_PREFIX+"Container-closebutton",
  /**
   * @property parallUpButtonClassName
   * @description CSS class name for the parallel increase button
   * @default "WireIt-Container-parallupbutton"
   * @type String
   */
  parallUpButtonClassName: CSS_PREFIX+"Container-parallupbutton",
  /**
   * @property parallDownButtonClassName
   * @description CSS class name for the parallel decrease button
   * @default "WireIt-Container-paralldownbutton"
   * @type String
   */
  parallDownButtonClassName: CSS_PREFIX+"Container-paralldownbutton",

  /**
   * @property parallControlClassName
   * @description CSS class name for the parallel controller
   * @default "WireIt-Container-paralldownbutton"
   * @type String
   */
  parallControlClassName: CSS_PREFIX+"Container-parallcontrol",


  /**
    * @property groupable
    * @description option to add the grouping button
    * @default true
    * @type Boolean
    */
	groupable: true,
	
	/** 
    * @property preventSelfWiring
    * @description option to prevent connections between terminals of this same container
    * @default true
    * @type Boolean
    */
   preventSelfWiring: true,

	/** 
    * @property title
    * @description text that will appear in the module header
    * @default null
    * @type String
    */
	title: null,

	/** 
    * @property icon
    * @description image url to be displayed in the module header
    * @default null
    * @type String
    */
	icon: null,

	/** 
    * @property width
    * @description initial width of the container
    * @default null
    * @type Integer
    */
	width: null,
	
	/** 
    * @property height
    * @description initial height of the container
    * @default null
    * @type Integer
    */
	height: null,

  /**
   *
   */
  singleton:false,

  shadowColor:"#6699ee",

   /**
    * Set the options by putting them in this (so it overrides the prototype default)
    * @method setOptions
    */
   setOptions: function(options) {
      for(var k in options) {
			if( options.hasOwnProperty(k) ) {
				this[k] = options[k];
			}
		}
   },

   /**
    * Function called when the container is being resized.
    * It sets the size of the body element of the container
    * @method onResize
    */
   onResize: function(event, args) {
     var size = args[0];
     if (args.length == 1) {
     // TODO: do not hardcode those sizes !!
     WireIt.sn(this.bodyEl, null, {width: (size[0] - 14) + "px", height: (size[1] - ( this.ddHandle ? 44 : 14) ) + "px"});
     }
   },

   /**
    * Render the dom of the container
    * @method render
    */
   render: function() {
   
      // Create the element
      this.el = WireIt.cn('div', {className: this.className});
   
      if(this.width) {
         this.el.style.width = this.width+"px";
      }
      if(this.height) {
         this.el.style.height = this.height+"px";
      }
   
      // Adds a handler for mousedown so we can notice the layer
      Event.addListener(this.el, "mousedown", this.onMouseDown, this, true);
   
      if(this.ddHandle) {
         // Create the drag/drop handle
			this.ddHandle = WireIt.cn('div', {className: this.ddHandleClassName});
			this.el.appendChild(this.ddHandle);

         // Icon
         if (this.icon) {
            var iconCn = WireIt.cn('img', {src: this.icon, className: 'WireIt-Container-icon'});
            this.ddHandle.appendChild(iconCn);
         }

         // Set title
         if(this.title) {
            this.ddHandle.appendChild( WireIt.cn('span', {className: 'floatleft'}, null, this.title) );
         }

        if (!this.singleton && this.xtype === "WireIt.FormContainer" && WireIt.Container.useParallel)
        {
          this.parallEl = WireIt.cn('input', {disabled:"true", title:"parallelism",type:"text", "value":"2", className: "parallInput","size":"2"});
          this.el.style.boxShadow = this.calcShadow(2);
          this.parallUpButton = WireIt.cn('div', {className: this.parallUpButtonClassName} );
          this.parallDownButton = WireIt.cn('div', {className: this.parallDownButtonClassName} );
          this.parallAll = WireIt.cn('div', {className: this.parallControlClassName} );
          this.ddHandle.appendChild(this.parallAll);
          this.parallAll.appendChild(this.parallEl);
          this.parallAll.appendChild(this.parallUpButton);
          this.parallAll.appendChild(this.parallDownButton);
          Event.addListener(this.parallUpButton, "click", this.onParallUpButton, this, true);
          Event.addListener(this.parallDownButton, "click", this.onParallDownButton, this, true);
        }

      }
      // Create the body element
      this.bodyEl = WireIt.cn('div', {className: "body"});
      this.el.appendChild(this.bodyEl);
   
      if(this.resizable) {
         // Create the resize handle
			this.ddResizeHandle = WireIt.cn('div', {className: this.resizeHandleClassName} );
			this.el.appendChild(this.ddResizeHandle);
      }

      if(this.close) {
         // Close button
         this.closeButton = WireIt.cn('div', {className: this.closeButtonClassName} );
			if (this.ddHandle) {
				this.ddHandle.appendChild(this.closeButton);
			}
			else {
				this.el.appendChild(this.closeButton);
			}
         Event.addListener(this.closeButton, "click", this.onCloseButton, this, true);
      }
      
      if(this.groupable && this.ddHandle) {
         this.groupButton = WireIt.cn('div', {className: 'WireIt-Container-groupbutton'} );
			this.ddHandle.appendChild(this.groupButton);
         Event.addListener(this.groupButton, "click", this.onGroupButton, this, true);
      }   
      // Append to the layer element
      this.layer.el.appendChild(this.el);
   
		// Set the position
		this.el.style.left = this.position[0]+"px";
		this.el.style.top = this.position[1]+"px";
   },

   /**
    * Sets the content of the body element
    * @method setBody
    * @param {String or HTMLElement} content
    */
   setBody: function(content) {
      if(typeof content == "string") {
         this.bodyEl.innerHTML = content;
      }
      else {
         this.bodyEl.innerHTML = "";
         this.bodyEl.appendChild(content);
      }
   },

   /**
    * Called when the user made a mouse down on the container and sets the focus to this container (only if within a Layer)
    * @method onMouseDown
    */
   onMouseDown: function(event) {
      if(this.layer) {
         if(this.layer.focusedContainer && this.layer.focusedContainer != this) {
            this.layer.focusedContainer.removeFocus();
         }
         this.setFocus();
         this.layer.focusedContainer = this;
      }
   },

   /**
    * Adds the class that shows the container as "focused"
    * @method setFocus
    */
   setFocus: function() {
      Dom.addClass(this.el, CSS_PREFIX+"Container-focused");
      
      this.eventFocus.fire(this);
   },

   /**
    * Remove the class that shows the container as "focused"
    * @method removeFocus
    */
   removeFocus: function() {
      Dom.removeClass(this.el, CSS_PREFIX+"Container-focused");
      
      this.eventBlur.fire(this);
   },

   /**
    * Called when the user clicked on the close button
    * @method onCloseButton
    */
   onCloseButton: function(e, args) {
      Event.stopEvent(e);
      this.layer.removeContainer(this);
   },

  shadeColor1 : function (color, percent) {
    var num = parseInt(color.slice(1),16), amt = Math.round(2.55 * percent), R = (num >> 16) + amt, G = (num >> 8 & 0x00FF) + amt, B = (num & 0x0000FF) + amt;
    return "#" + (0x1000000 + (R<255?R<1?0:R:255)*0x10000 + (G<255?G<1?0:G:255)*0x100 + (B<255?B<1?0:B:255)).toString(16).slice(1);
  },

   calcShadow: function(level)
   {
     var strShadow = "none";
     for (var i=1;i<level;i++)
     {
       strShadow = (i === 1) ? "" : (strShadow +", ");
       var pos = i*5;
       strShadow = strShadow+String(pos)+"px "+String(pos)+"px "+this.shadeColor1(this.shadowColor,pos);
     }
     return strShadow;
   },

  /**
   * Called when the user clicked on the close button
   * @method onCloseButton
   */
  onParallUpButton: function(e, args) {
    Event.stopEvent(e);
    var num = parseInt(this.parallEl.value);
    if (num < WireIt.Container.maxParallel)
    {
      this.parallEl.value = String(num+1);
      this.el.style.boxShadow = this.calcShadow(num+1);
    }
  },
  /**
   * Called when the user clicked on the close button
   * @method onCloseButton
   */
  onParallDownButton: function(e, args) {
    Event.stopEvent(e);
    var num = parseInt(this.parallEl.value);
    if (num > 1)
    {
      this.parallEl.value = String(num-1);
      this.el.style.boxShadow = this.calcShadow(num-1);
    }
  },

  /**
	 * TODO
	 */
   highlight: function() {
		this.el.style.border = "2px solid blue";
   },

	/**
	 * TODO
	 */
   dehighlight: function() {
		this.el.style.border = "";
   },
   
 	/**
 	 * TODO
    */
   superHighlight: function() {
		this.el.style.border = "4px outset blue";
    },
  

   /**
    * Remove this container from the dom
    * @method remove
    */
   remove: function() {
      // Remove the terminals (and thus remove the wires)
      this.removeAllTerminals();
   
      // Remove from the dom
      this.layer.el.removeChild(this.el);
      
      // Remove all event listeners
      Event.purgeElement(this.el);
   },

   /**
    * Call the addTerminal method for each terminal configuration.
    * @method initTerminals
    */
   initTerminals: function(terminalConfigs) {
      for(var i = 0 ; i < terminalConfigs.length ; i++) {
         this.addTerminal(terminalConfigs[i]);
      }
   },


   /**
    * Instanciate the terminal from the class pointer "xtype" (default WireIt.Terminal)
    * @method addTerminal
    * @return {WireIt.Terminal}  terminal Created terminal
    */
   addTerminal: function(terminalConfig) {

   	var klass = WireIt.terminalClassFromXtype(terminalConfig.xtype);

      // Instanciate the terminal
      var term = new klass(this.el, terminalConfig, this);
   
      // Add the terminal to the list
      this.terminals.push( term );
   
      // Event listeners
      term.eventAddWire.subscribe(this.onAddWire, this, true);
      term.eventRemoveWire.subscribe(this.onRemoveWire, this, true);
   
      return term;
   },

   /**
    * This method is called when a wire is added to one of the terminals
    * @method onAddWire
    * @param {Event} event The eventAddWire event fired by the terminal
    * @param {Array} args This array contains a single element args[0] which is the added Wire instance
    */
   onAddWire: function(event, args) {
      var wire = args[0];
      // add the wire to the list if it isn't in
      if( WireIt.indexOf(wire, this.wires) == -1 ) {
         this.wires.push(wire);
         this.eventAddWire.fire(wire);
      } 
   },

   /**
    * This method is called when a wire is removed from one of the terminals
    * @method onRemoveWire
    * @param {Event} event The eventRemoveWire event fired by the terminal
    * @param {Array} args This array contains a single element args[0] which is the removed Wire instance
    */
   onRemoveWire: function(event, args) {
      var wire = args[0];
      var index = WireIt.indexOf(wire, this.wires);
      if( index != -1 ) {
         this.eventRemoveWire.fire(wire);
         this.wires[index] = null;
      }
      this.wires = WireIt.compact(this.wires);
   },

   /**
    * Remove all terminals
    * @method removeAllTerminals
    */
   removeAllTerminals: function() {
      for(var i = 0 ; i < this.terminals.length ; i++) {
         this.terminals[i].remove();
      }
      this.terminals = [];
   },

   /**
    * Redraw all the wires connected to the terminals of this container
    * @method redrawAllTerminals
    */
   redrawAllWires: function() {
      for(var i = 0 ; i < this.terminals.length ; i++) {
         this.terminals[i].redrawAllWires();
      }
   },

	/**
	 * Get the position relative to the layer (if any)
	 * @method getXY
	 * @return Array position
	 */
	getXY: function() {
		var position = Dom.getXY(this.el);
      if(this.layer) {
         // remove the layer position to the container position
         var layerPos = Dom.getXY(this.layer.el);
         position[0] -= layerPos[0];
         position[1] -= layerPos[1];
         // add the scroll position of the layer to the container position
         position[0] += this.layer.el.scrollLeft;
         position[1] += this.layer.el.scrollTop;
      }

		return position;
	},

   /**
    * Return the config of this container.
    * @method getConfig
    */
   getConfig: function() {   
      return {
			position: this.getXY(),
			xtype: this.xtype
		};
   },
   
   /**
    * Subclasses should override this method.
    * @method getValue
    * @return {Object} value
    */
   getValue: function() {
      return {};
   },

   /**
    * Subclasses should override this method.
    * @method setValue
    * @param {Any} val Value 
    */
   setValue: function(val) {
   },
   
   
   /**
    * @method getTerminal
    */
   getTerminal: function(name) {
      var term;
      for(var i = 0 ; i < this.terminals.length ; i++) {
         term = this.terminals[i];
         if(term.name == name) {
            return term;
         }
      }
      return null;
   }

};

})();