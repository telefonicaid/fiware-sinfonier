class RequestHandler:
    @staticmethod
    def get_topology_log(req, params):
        return RequestHandler._topology_id(req, params)

    @staticmethod
    def get_topology_status(req, params):
        return RequestHandler._topology_id(req, params)

    @staticmethod
    def launch_topology(req, params):
        return RequestHandler._topology_id(req, params)

    @staticmethod
    def stop_topology(req, params):
        return RequestHandler._topology_id(req, params)

    @staticmethod
    def delete_module(req, params):
        return RequestHandler._module_id_and_version_id(req, params)

    @staticmethod
    def upload_module(req, params):
        return RequestHandler._module_id_and_version_id(req, params)

    @staticmethod
    def validate_module(req, params):
        return RequestHandler._module_id_and_version_id(req, params)

    @staticmethod
    def api_status(req, params):
        return ({}, None)

    @staticmethod
    def _topology_id(req, params):
        id = params.get('id', req.get_param('id'))
        return ({'id': id}, None) if id is not None else (None, {'message': 'The topology id is required'})

    @staticmethod
    def _module_id_and_version_id(req, params):
        id_context, id_message = RequestHandler._module_id(req, params)

        if not id_context:
            return id_context, id_message

        version_code_context, version_code_message = RequestHandler._module_version_id(req, params)

        if not version_code_context:
            return version_code_context, version_code_message

        id_context.update(version_code_context)

        return id_context, None

    @staticmethod
    def _module_id(req, params):
        # type: (object, object) -> tuple
        id = params.get('id')
        return ({'id': id}, None) if id else (None, {'message': 'Invalid module\''})

    @staticmethod
    def _module_version_id(req, params):
        # type: (object, object) -> tuple
        try:
            version_code = params.get('version_id')
            return ({'version_id': version_code}, None) if version_code else (None, 'Invalid module\' version_id')
        except ValueError as e:
            return None, 'Invalid version format'

    @staticmethod
    def _module_name(req, params):
        id = params.get('name', req.get_param('name'))
        return ({"name": id}, None) if id is not None else (None, {"message": "Invalid module name"})
