HTTP_803 = '803 Invalid module'
HTTP_INVALID_MODULE = HTTP_803
HTTP_804 = '804 Compiling failure'
HTTP_COMPILING_FAILURE = HTTP_804
HTTP_805 = '805 Gist error'
HTTP_GIST_ERROR = HTTP_805
HTTP_809 = '809 Invalid params'
HTTP_INVALID_PARAMS = HTTP_809
HTTP_810 = '810 Topology Error'
HTTP_TOPOLOGY_ERROR = HTTP_810


class Code(dict):
    def __init__(self, code, status, data=None, msg=None):
        super(Code, self).__init__()
        self.setdefault('code', code)
        self.setdefault('status', status)

        if msg:
            self.setdefault('message', msg)

        if data:
            self.setdefault('data', data)


class CodeError(Code):
    def __init__(self, data, msg, code=500):
        super(CodeError, self).__init__(code, 'error', data, msg)


class CodeSuccess(Code):
    def __init__(self, data, msg, code=200):
        super(CodeSuccess, self).__init__(code, 'success', data, msg)


class Code200(CodeSuccess):
    def __init__(self, data=None, msg=None):
        super(Code200, self).__init__(data=data, msg=msg)


class Code400(CodeError):
    def __init__(self, msg='BAD REQUEST'):
        super(Code400, self).__init__(code=400, msg=msg, data=None)


class Code404(CodeError):
    def __init__(self, msg='NOT FOUND'):
        super(Code404, self).__init__(code=404, msg=msg, data=None)


class Code409(CodeError):
    def __init__(self, msg='CONFLICT'):
        super(Code409, self).__init__(code=409, msg=msg, data=None)


class Code412(CodeError):
    def __init__(self, msg='UNPROCESSABLE ENTITY'):
        super(Code412, self).__init__(code=412, msg=msg, data=None)


class Code500(CodeError):
    def __init__(self, msg='INTERNAL SERVER ERROR'):
        super(Code500, self).__init__(msg=msg, data=None)
