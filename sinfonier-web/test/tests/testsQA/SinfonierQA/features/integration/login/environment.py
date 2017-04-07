# -*- coding: utf-8 -*-


from toolium.behave.environment_project_structure import before_scenario, \
    before_all as toolium_before_all, after_scenario, after_all


def before_all(context):
    """
    Initialization method that will be executed before all execution
    Variables added to Behave's context after this function execution:
        - context.driver_wrapper -> toolium.driver_wrapper.DriverWrapper
        - context.utils -> toolium.utils.Utils
        - context.toolium_config -> toolium.config_parser.ExtendedConfigParser
        - context.logger -> logging
        - context.config_files -> ConfigFiles
        - context.page_object_list_autoloaded: (list) PageObjectAutoloaded list
    Methods added to Behave's context after this function execution:
        - context.get_page_object(page_object_name): (function) to retrieve a PageObject by its name
        - context.get_message_property(key_string): (function) to retrieve the text message in the specified language
            from the language properties file, using a key_string like this: "home.button.login".
    :param context: behave context
    """

    toolium_before_all(context)




