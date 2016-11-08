# -*- coding: utf-8 -*-

from behave import step
from toolium.utils.configuration import map_param
import time


@step(u'an invalid credential error is shown with message "{message}"')
def invalid_credential_error(context, message):
    context.page.element_error.wait_until_visible()

    expected_message = map_param(message)
    error_text = context.page.element_error.text

    print"error_text: "+error_text +"| expected_message: "+ expected_message


    assert error_text == expected_message, \
    "Expected message: {}; Error message: {}".format(expected_message, error_text)


@step(u'a valid "{success_message}" message is shown')
def valid_success(context, success_message):
    context.page.element_success.wait_until_visible()

    expected_message = map_param(success_message)
    success_text = context.page.element_success.text

    assert success_text == expected_message, \
        "Expected message: {}; Success message: {}".format(expected_message, success_text)


