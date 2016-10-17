from actions.ApiBase import ApiBase

from apiRequests.ResponsesHandler import ResponsesHandler


class ApiStatus(ApiBase):
    def on_post(self, req, res):
        return ResponsesHandler.handle_200(res, msg='SinfonierAPI is running!')
