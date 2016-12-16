var FormTools = function (f) {
  var that = this;

  this.reset = function () {
    $(f)
      .find('input, select')
      .each(function (index, item) {
        if (item.tagName.toLowerCase() === 'input') {
          $(item).val('');
        } else if (item.tagName.toLowerCase() === 'select') {
          $(item).val($(item).children().first().val());
        }

      });
  };

  this.validate = function () {
    $(f).validate();
    return $(f).valid();
  };

  this.addFilter = function (filter) {
    if (!filter || typeof filter !== 'function') {
      return;
    }

    // Do more stuff;
  };

  return this;
};

function checkField(field, requiredMessage, patternMessage) {
  if (field.validity.valid || (field.offsetWidth == 0 && field.offsetHeight == 0))
    return false;
  if (field.validity.valueMissing) {
    field.setCustomValidity(requiredMessage);
  } else if ((field.validity.patternMismatch || field.validity.typeMismatch) && patternMessage) {
    field.setCustomValidity(patternMessage);
  } else {
	field.setCustomValidity('');  
  }
  return true;
}