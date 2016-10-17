import json

import falcon

from apiRequests.RequestCodes import Code200, Code400, Code404, Code409, Code500, HTTP_804, CodeError, HTTP_805, HTTP_803, HTTP_810, \
    HTTP_TOPOLOGY_ERROR, HTTP_GIST_ERROR


class ResponsesHandler:
    @staticmethod
    def handle_200(resp, data=None, msg=None):
        resp.body = json.dumps(Code200(data, msg))
        resp.status = falcon.HTTP_200

    @staticmethod
    def handle_400(resp, msg=None):
        if msg:
            resp.body = json.dumps(Code400(msg))
        else:
            resp.body = json.dumps(Code400())

        resp.status = falcon.HTTP_400

    @staticmethod
    def handle_404(resp, msg=None):
        if msg:
            resp.body = json.dumps(Code404(msg))
        else:
            resp.body = json.dumps(Code404())

        resp.status = falcon.HTTP_404

    @staticmethod
    def handle_409(resp, msg=None):
        if msg:
            resp.body = json.dumps(Code409(msg))
        else:
            resp.body = json.dumps(Code409())

        resp.status = falcon.HTTP_409

    @staticmethod
    def handle_500(res):
        res.status = falcon.HTTP_500
        res.body = json.dumps(Code500())

    @staticmethod
    def handle_803(res, msg=None, data=None):
        code = int(HTTP_803[:3])
        msg = HTTP_803[3:].strip()

        res.status = HTTP_803
        res.body = json.dumps(CodeError(code=code, msg=msg, data=data))

    @staticmethod
    def handle_804(res, msg=None, trace=None):
        code = int(HTTP_804[:3])
        msg = HTTP_804[3:].strip() if not msg else msg

        res.status = HTTP_804
        res.body = json.dumps(CodeError(code=code, msg=msg, data={'trace': trace} if trace else None))

    @staticmethod
    def handle_805(res, msg=None, data=None):
        code = int(HTTP_805[:3])
        msg = HTTP_805[3:].strip() if not msg else msg

        res.status = HTTP_GIST_ERROR
        res.body = json.dumps(CodeError(code=code, msg=msg, data=data))

    @staticmethod
    def handle_810(res, msg=None, data=None):
        code = int(HTTP_810[:3])
        msg = HTTP_810[3:].strip() if not msg else msg

        res.status = HTTP_TOPOLOGY_ERROR
        res.body = json.dumps(CodeError(code=code, msg=msg, data=data))

    @staticmethod
    def handle_xxx(res, status, code):
        res.status = status
        res.body = json.dumps(code)

    @staticmethod
    def handle_otherwise(req, resp):
        resp.status = falcon.HTTP_404
        resp.body = json.dumps(Code404())
