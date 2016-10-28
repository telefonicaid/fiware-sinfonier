from bson.objectid import ObjectId


class Error(dict):
    def __init__(self, msg):
        super(Error,self).__init__()
        self.setdefault('message', msg)


class RequestHandler:
    @staticmethod
    def get_topology_log(req, params):
        return RequestHandler._topology_id_start_length(req, params)

    @staticmethod
    def get_topology_log_sizes(req, params):
        return RequestHandler._topology_id(params)

    @staticmethod
    def get_topology_status(req, params):
        return RequestHandler._topology_id(params)

    @staticmethod
    def launch_topology(req, params):
        return RequestHandler._topology_id(params)

    @staticmethod
    def stop_topology(req, params):
        return RequestHandler._topology_id(params)

    @staticmethod
    def delete_module(req, params):
        return RequestHandler._module_id_and_version_id(params)

    @staticmethod
    def upload_module(req, params):
        return RequestHandler._module_id_and_version_id(params)

    @staticmethod
    def validate_module(req, params):
        return RequestHandler._module_id_and_version_id(params)

    @staticmethod
    def api_status():
        return {}, None

    @staticmethod
    def _topology_id(params):
        _id = params.get('id')
        return ({'id': _id}, None) if _id and ObjectId.is_valid(_id) else (None, Error('Invalid topology\'s id'))

    @staticmethod
    def _module_id_and_version_id(params):
        context, error = RequestHandler._module_id(params)

        if error:
            return context, error

        version_code_context, version_code_error = RequestHandler._module_version_id(params)

        if version_code_error:
            return version_code_context, version_code_error

        context.update(version_code_context)
        return context, None

    @staticmethod
    def _module_id(params):
        # type: (object, object) -> tuple
        _id = params.get('id')
        return ({'id': _id}, None) if _id and ObjectId.is_valid(_id) else (None, Error('Invalid module\' id'))

    @staticmethod
    def _module_version_id(params):
        # type: (object, object) -> tuple
        _id = params.get('version_id')
        return ({'version_id': _id}, None) if _id and ObjectId.is_valid(_id) else (None, Error('Invalid module\' version_id'))


    @staticmethod
    def _topology_id_start_length(req,params):
        _id = params.get('id')
        try:
            _start = req.params.get('start').split(',')
            _length = req.params.get('length').split(',')
            Error('message')
        except Exception as ex:
            return (None, Error(ex.message))
        return ({'id': _id,'start':_start,'length':_length}, None) if _id and ObjectId.is_valid(_id) else (None, Error('Invalid topology\'s id'))
