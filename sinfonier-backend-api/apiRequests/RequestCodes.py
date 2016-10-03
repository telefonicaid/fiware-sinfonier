class Code(dict):
    def __init__(self, code, status, data=None, msg=None):
        super(Code, self).__init__()
        self.setdefault('code', code)
        self.setdefault('status', status)

        if msg:
            self.setdefault('message', msg)

        if data:
            self.setdefault('data', data)


class Code200(Code):
    def __init__(self, data=None, msg=None):
        super(Code200, self).__init__(200, 'success', data, msg)


class Code400(Code):
    def __init__(self, msg='BAD REQUEST'):
        super(Code400, self).__init__(400, 'error', msg=msg)


class Code404(Code):
    def __init__(self, msg='NOT FOUND'):
        super(Code404, self).__init__(404, 'error', msg=msg)


class Code409(Code):
    def __init__(self, msg='CONFLICT'):
        super(Code409, self).__init__(409, 'error', msg=msg)


class Code412(Code):
    def __init__(self, msg='UNPROCESSABLE ENTITY'):
        super(Code412, self).__init__(412, 'error', msg=msg)


class Code500(Code):
    def __init__(self, msg='INTERNAL SERVER ERROR'):
        super(Code500, self).__init__(500, 'error', msg=msg)
