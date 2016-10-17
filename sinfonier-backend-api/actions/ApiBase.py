import inflection as inflection

from apiRequests.RequestHandler import RequestHandler
from apiRequests.ResponsesHandler import ResponsesHandler
from error.ErrorHandler import HTTPBadParams


class ApiBase:
    def __init__(self):
        self.validated_params = {}

    def on_post(self, req, res, **params):
        ResponsesHandler.handle_400(res)

    def on_get(self, req, res, **params):
        ResponsesHandler.handle_400(res)

    def on_put(self, req, res, **params):
        ResponsesHandler.handle_400(res)

    def on_head(self, req, res, **params):
        ResponsesHandler.handle_400(res)

    def on_delete(self, req, res, **params):
        ResponsesHandler.handle_400(res)

    def on_options(self, req, res, **params):
        ResponsesHandler.handle_400(res)

    def on_patch(self, req, res, **params):
        ResponsesHandler.handle_400(res)

    def set_validated_params(self, params):
        self.validated_params = params

    @staticmethod
    def validate_params(req, res, resource, params):
        (context, error) = getattr(RequestHandler, inflection.underscore(resource.__class__.__name__))(req, params)
        if not error:
            resource.set_validated_params(context)
        else:
            raise HTTPBadParams(msg=error['message'])
