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