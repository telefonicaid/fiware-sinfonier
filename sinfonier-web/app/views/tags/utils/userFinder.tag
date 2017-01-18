*{This view has been overridden because a known bug has not been fixed in darwin. }*
<div class="row">
    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
        #{form _action, class:'form-inline'}
            <div class="form-group">
                <select class="selectpicker" name="finder.field">
                    #{list items:_fieldsAllowed, as:'field' }
                        <option value="${field?.escapeHtml()?.raw()}" #{if _model != null && _model.field != null && _model.field.equals(field)}selected#{/if}>&{'finder.' + field}</option>
                    #{/list}
                </select>
            </div>
            <div class="form-group">
                <div class="input-icon">
                    <i class="fa fa-search"></i>
                    <input class="form-control" type="text" value="#{if _model != null && _model.value != null}${_model.value?.escapeHtml()?.raw()}#{/if}" placeholder="" name="finder.value">
                </div>
            </div>
            <div class="form-group">
                <select class="selectpicker small-input" name="finder.activeState">
                    <option value="${models.finder.UserFinder.ActiveState.BOTH}" #{if _model != null && _model.activeState != null && models.finder.UserFinder.ActiveState.BOTH.equals(_model.activeState)}selected#{/if}>&{'finder.all'}</option>
                    <option value="${models.finder.UserFinder.ActiveState.ACTIVE}" #{if _model != null && _model.activeState != null && models.finder.UserFinder.ActiveState.ACTIVE.equals(_model.activeState)}selected#{/if}>&{'finder.active'}</option>
                    <option value="${models.finder.UserFinder.ActiveState.INACTIVE}" #{if _model != null && _model.activeState != null && models.finder.UserFinder.ActiveState.INACTIVE.equals(_model.activeState)}selected#{/if}>&{'finder.inactive'}</option>
                </select>
            </div>
            <button type="submit" class="submit"><i class="material-icons">search</i></button>
        #{/form}
    </div>
</div>
