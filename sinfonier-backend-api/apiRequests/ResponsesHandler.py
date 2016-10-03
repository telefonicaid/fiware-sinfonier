import json

import falcon

from apiRequests.RequestCodes import Code200, Code400, Code404, Code409, Code500


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
    def handle_otherwise(req, resp):
        resp.status = falcon.HTTP_404
        resp.body = json.dumps(Code404())
