var editor;

function ImportModuleProcesses(e) {
  var result, that = this;
  var _parseOldModule = function (input) {
    var output = _.merge(input, input['container']);
    delete output.container;
    return output;
  };

  this.registerRemoveEvent = function () {
    return $('form#module-form #library-forms .library-form span.remove, form#module-form #field-forms .field-form span.remove').on('click', function () {
      $(this)
        .parent()
        .parent()
        .parent()
        .parent()
        .parent()
        .remove();
    });
  };

  this.initFields = function (fields) {
	$('#field-forms').html('');
    if (!fields || fields.length === 0) {
      return;
    }

    for (var i = 0; i < fields.length; i++) {
      var form = handleAddField();
      $(form).find('input#name-field').val(fields[i]['name']);
      $(form).find('input#label-field').val(fields[i]['label']);
      $(form).find('select#type-field').val(fields[i]['type']);
      $(form).find('select#required-field').val(Boolean(fields[i]['required']).toString());
      $(form).find('select#var-field').val(Boolean(fields[i]['wirable']).toString());
      if (fields[i]['elementType'] != null) {
        $(form).find('select#elementType-field').val(fields[i]['elementType']['name']);
      }
      $(form).find('select#type-field').trigger('change');
    }

    that.registerRemoveEvent();

    return 0;
  };
  this.initLibraries = function (libraries) {
	$('#library-forms').html('');
    if (!libraries || libraries.length === 0) {
      return;
    }

    for (var i = 0; i < libraries.length; i++) {
      var form = handleAddLibrary();
      $(form).find('input#name-library').val(libraries[i]['name']);
      $(form).find('input#url-library').val(libraries[i]['url']);
    }

    that.registerRemoveEvent();

    return 0;
  };

  this.moduleProcessor = function (input) {
    $('form#module-form input#versionTag').val(input.versionTag);
    $('form#module-form input#name').val(input.name);
    $('form#module-form select#type').val(input.type);
    if (input.sourceCodeUrl != null) {
      $('form#module-form input#code-url').val(input.sourceCodeUrl);	
    } else {
      $('form#module-form input#code-url').val(input.codeURL);	
    }
    
    $('form#module-form textarea#description').val(input.description);
    if (input.language) {
      $('form#module-form select#language').val(input.language.toLowerCase());
    }
    $('form#module-form textarea#code').val(input.sourceCode);
    
    if (input.sourceType) {
      input.sourceType = input.sourceType.toLowerCase() == "local" ? "template" : input.sourceType;
      $('form#module-form select#source-type').val(input.sourceType.toLowerCase());
    }
    
    window.ModuleInit.code = { code: input.sourceCode };
    $('form#module-form select#source-type').trigger('change');
    
    $('form#module-form input#singleton').prop('checked',
      Boolean(input.singleton instanceof String ? !Boolean(input.singleton) : input.singleton) ? false : 'checked');

    if (input.ticktuple) {
      $('form#module-form #ticktuple-form input#name-field').val(input.ticktuple.name);
      $('form#module-form #ticktuple-form input#label-tuple').val(input.ticktuple.label);
      $('form#module-form #ticktuple-form select#type-field').val(input.ticktuple.type);
      $('form#module-form #ticktuple-form select#required-field').val(input.ticktuple.required.toString());
      $('form#module-form #ticktuple-form select#var-tuple').val(input.ticktuple.wirable);
      $('#ticktuple-collapse').collapse('show');
    } else {
      $('form#module-form #ticktuple-form input#name-field').val('');
      $('form#module-form #ticktuple-form input#label-tuple').val('');
      $('form#module-form #ticktuple-form select#type-field').val('string');
      $('form#module-form #ticktuple-form select#required-field').val('false');
      $('form#module-form #ticktuple-form select#var-tuple').val('false');
      $('#ticktuple-collapse').collapse('hide');
    }

    // Update Icon
    handleIconModule();

    // Fields
    that.initFields(input.fields);

    // Libraries
    that.initLibraries(input.libraries);
  };

  this.process = function (e) {
    try {
      result = JSON.parse(e.target.result);
      if (result['container']) {
        result = _parseOldModule(result);
      }

      that.moduleProcessor(result);

    } catch (err) {
      console.error(err);
    }
  };

  if (e != undefined) {
    this.process(e)
  }

  return this;
}

function changeVersion(name) {
  var selected = $('#version').val();
  window.location = $('#version option[value="'+selected+'"').attr('data-url');
}

function handleFileSelect(file) {
  var fReader = new FileReader();
  var moduleProcess = new ImportModuleProcesses();
  file = $(file)[0]['files'][0];
  fReader.onload = moduleProcess.process;
  fReader.readAsText(file);
}

function handleAddField() {
  var _setName = function (form, index) {
    return $(form)
      .attr('name', $(form).attr('name').replace(/\[X\]/, '[' + index + ']'));
  };

  // NOTE: Get Index before to update it. this is importar because the htm code is appended by js.
  var index = $('#help-zone #data').data('fields-size');
  $('#help-zone #data').data('fields-size', parseInt(index, 10) + 1);


  // Copy original form and change form's id.
  var form = $('div#help-zone #form-model-field')
    .clone()
    .attr('id', 'field-' + index);


  // Set it the index into array
  form
    .find('input, select')
    .each(function () {
      _setName(this, index);
    });

  $('form#module-form #field-forms').append(form);

  new ImportModuleProcesses().registerRemoveEvent();

  return form;
}

function handleAddLibrary() {
  var _setName = function (form, index) {
    return $(form)
      .attr('name', $(form).attr('name').replace(/\[X\]/, '[' + index + ']'));
  };

  // NOTE: Get Index before to update it. this is importar because the htm code is appended by js.
  var index = $('#help-zone #data').data('libraries-size');
  $('#help-zone #data').data('libraries-size', parseInt(index, 10) + 1);

  var form = $('div#help-zone #form-model-library')
    .clone()
    .attr('id', 'library-' + index);

  form
    .find('input, select')
    .each(function () {
      _setName(this, index);
    });

  $('form#module-form #library-forms').append(form);

  new ImportModuleProcesses().registerRemoveEvent();

  return form;
}

function handleIconModule(file) {
  var basePath = '/public/images/modules/';
  
  if (file != null) {
    // Read icon file
	  var fReader = new FileReader();
	  file = $(file)[0]['files'][0];
	  fReader.onload = function(e) {
	    $('form#module-form #icon-preview img').attr('src', e.target.result);
	  }
	  fReader.readAsDataURL(file);
  } else if ($('form#module-form #icon-preview img').size() > 0 &&
      $('form#module-form #icon-preview img').attr('src').match("^"+basePath)) {
    // Set example icon
    switch ($('form#module-form select#type').val()) {
      case 'drain':
        $('form#module-form #icon-preview img').attr('src', basePath + 'drain-big.png');
        break;
      case 'bolt':
        $('form#module-form #icon-preview img').attr('src', basePath + 'bolt-big.png');
        break;
      default:
        $('form#module-form #icon-preview img').attr('src', basePath + 'spout-big.png');
    }
  }
}

function setStarVote(rate, color) {
  var stars = $('li[data-value] i');
  for (i=0; i < stars.length; i++) {
    if ((i+1) <= rate) {
      $(stars[i]).html('star');
      if (color) $(stars[i]).addClass('star-vote-color');
      else $(stars[i]).removeClass('star-vote-color');
    } else if ((i+0.5) <= rate) {
      $(stars[i]).html('star_half');
      if (color) $(stars[i]).addClass('star-vote-color');
      else $(stars[i]).removeClass('star-vote-color');
    } else {
      $(stars[i]).html('star_border');
      $(stars[i]).removeClass('star-vote-color');
    }
  }
}

function handleVote(elem, rate) {
  // Reset class for the user have been selected something before.
  var orig = $(elem).parent().attr('data-value');
  setStarVote(orig, false);

  if (!$(elem).parent().attr('data-orig')) {
    $(elem).parent().attr('data-orig', orig);
  }
  $(elem).parent().attr('data-value', rate);
  
  // Mark all stars that have a value less and equal than current rate.
  setStarVote(rate, true);

  // Show the form for add a message.
  $('form.form-vote')
    .show()
    .find('input.rate')
    .val(rate);
}

function cancelVote() {
  $('form.form-vote').hide();
  // Reset class for the user have been selected something before.
  var orig = $('.vote-section ul').attr('data-orig');
  setStarVote(orig, false);
  $('.vote-section ul').attr('data-value', orig);
  $('.vote-section ul').removeAttr('data-orig');
}

function handleComplain() {
  // Show the form for add a message.
  $('form.form-complain').show();
}

function cancelComplain() {
  $('form.form-complain').hide();
}

function handleModuleTemplate(elem, name, language, type, isReadOnly, code) {
  var id = $(elem).data('target');
  
  if (!this.editor) {
    initCodeMirror($('#code')[0]);  
  }
  
  this.editor.setOption('readOnly', isReadOnly);

  if(!name) name = $('form#module-form input#name').val();
  if(!type) type = $('form#module-form select#type').val();
  if(!language) language = $('form#module-form select#language').val();
  if(!name || !type || !language) return;

  try {
    if (window.ModuleInit.code.code && $('#module-form select#source-type').val() == 'template') {
      code = window.ModuleInit.code.code;
    }  
  } catch (e) {}
  
  if (!code) {
    _template = new TemplateCode(name, language, type, code);
    code = _template.code;
  } else {
    var decodedSourceCode = window.atob(code);
    code = decodedSourceCode;  
  }
  this.editor.setValue(code);
  var that = this;
  setTimeout(function() {
    that.editor.refresh();
  },1);
}

function handleSourceCode() {
  var source = $('#module-form select#source-type').val();
  var selector, isEdit;

  if (source === 'template') {
    isEdit = true;
    selector = '#button-template-edit';
    $('#collapseCode').collapse('show');
    $('#module-form').find(selector).show();
    $('#module-form #source-external-code').hide();
   
    $('#module-form #code-url').prop('required', '');
    $('#module-form #code-url').val('');
  } else {
    isEdit = false;
    selector = '#source-external-code';
    $('#collapseCode').collapse('hide');
    $('#module-form #button-template-edit').hide();
    $('#module-form').find(selector).show();
    
    $('#module-form #code-url').prop('required', 'required');
    $('#module-form #code').prop('required', '');
    $('#module-form #code').val('');
    if (this.editor) {
      this.editor.setValue('');
      var that = this;
      setTimeout(function() {
        that.editor.refresh();
      },1);
    }
  }
  
  //Init CodeMirror
  if ($('#code') && $('#code').is(':visible') && !this.editor) {
    initCodeMirror($('#code')[0]);  
  }
  if (source === 'template') {
    handleModuleTemplate(selector + ' button', undefined, undefined, undefined, !isEdit);	  
  }
}

function handleFieldType(select) {
  console.log($(select).val())
  if ($(select).val() == "list") {
    $(select).parent().parent().parent().find('.vars').hide();
    $(select).parent().parent().parent().find('.elementType').show();
  } else {
    $(select).parent().parent().parent().find('.vars').show();
    $(select).parent().parent().parent().find('.elementType').hide();
  }
}

function initCodeMirror(field) {
  this.editor = CodeMirror.fromTextArea(field, {
    lineNumbers: true,
    continuousScanning: 500,
    height: "350px",
    mode: "javascript"
  });
  var that = this;
  setTimeout(function() {
    that.editor.refresh();
  },1);	
}

/**
 * DOM is ready!
 */
(function () {
  $(document).ready(function () {
    $('form#module-form').submit(function (e) {
      if ($('#type').attr('disabled')) {
        $('#type').removeAttr('disabled');
      }
      if ($('#language').attr('disabled')) {
        $('#language').removeAttr('disabled');
      }	
      
      // Recalculate fields ids
      $('#field-forms>div').each(function(index) {
        $(this).attr('id', 'field-'+index);
        $(this).find('#name-field').attr('name','version.fields.fields['+index+'].name');
        $(this).find('#label-field').attr('name','version.fields.fields['+index+'].label');
        $(this).find('#type-field').attr('name','version.fields.fields['+index+'].type');
        $(this).find('#required-field').attr('name','version.fields.fields['+index+'].required');
        $(this).find('#var-field').attr('name','version.fields.fields['+index+'].wirable');
        $(this).find('#elementType-field').attr('name','version.fields.fields['+index+'].elementTypeEnum');
      });
      
      // Recalculate library ids
      $('#library-forms>div').each(function(index) {
        $(this).attr('id', 'library-'+index);
        $(this).find('#name-library').attr('name','version.libraries.libraries['+index+'].name');
        $(this).find('#url-library').attr('name','version.libraries.libraries['+index+'].url');
      });

      var f = new FormTools(this);

      if (!f.validate()) {
        e.preventDefault();
        e.stopPropagation();
      } 
    });

    /**
     * This is for show the button for upload the document to import.
     */
    $('button#import-btn').click(function () {
      $('#import-doc').toggle();
    });

    /**
     * Update template view.
     */
    $('#module-form input#name, #module-form select#type, #module-form select#language').change(function () {
      if ($('form#module-form #templates-section .terminal').is(':visible')) {
        var isEdit = $('form#module-form select#source-type').val() === 'gist';
        handleSourceCode(isEdit);
      }
    });

    /**
     * TickTuple
     */
    if ($('#ticktuple-form input#label-tuple').val() && $('#ticktuple-form input#label-tuple').val().length > 0) {
      $('#ticktuple-collapse').collapse('show');
    }
    // Events
    $('#ticktuple-collapse').on('hide.bs.collapse', function () {
      $('#ticktuple-form input#label-tuple').val('');
    });
    
    /**
     * NOTE: This is very important. Here the module would be init. This have a special importance when
     * module is edited!!!
     */
    handleSourceCode();

    handleIconModule();
    if (window.ModuleInit) {
      var moduleProcessor = new ImportModuleProcesses();
      moduleProcessor.initFields(ModuleInit.fields || []);
      moduleProcessor.initLibraries(ModuleInit.libraries || []);
    }
    
    /**
     * Vote star
     */
    $('.star-vote').hover(
        function(evt) {
          var index = $(evt.target).parent().attr('data-value');
          var stars = $('li[data-value] i');
          for (i=0; i < stars.length; i++) {
            if ((i+1) <= index) {
              $(stars[i]).html('star');
            }
          }
        },
        function(evt) {
          var rate = $(evt.target).parent().parent().attr('data-value');
          var orig = $(evt.target).parent().parent().attr('data-orig');
          setStarVote(rate, orig != null);
        }
    );
    setStarVote($('.vote-section ul').attr('data-value'), false);
    
    // Disable inputs in form
    $('#language').ready(function() {
      if ($('#name').attr('readonly')) {
        $('#language').attr('disabled', 'disabled');
      }
    });
    $('#type').ready(function() {
      if ($('#name').attr('readonly')) {
        $('#type').attr('disabled', 'disabled');
      }
    })
  });
})();
