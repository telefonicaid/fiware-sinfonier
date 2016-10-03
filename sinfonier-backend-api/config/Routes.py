import falcon
from actions.api import *
from actions.modules import *
from actions.topologies import *
from apiRequests.ResponsesHandler import ResponsesHandler


class Routes:
    def __init__(self):
        self.api = falcon.API()

        self.api.add_route('/', ApiStatus.ApiStatus())

        self.api.add_route('/modules/{id}/{version_id}', UploadModule.UploadModule())
        self.api.add_route('/modules/{id}/{version_id}/validate', ValidateModule.ValidateModule())
        self.api.add_route('/modules/{id}/{version_id}/delete', DeleteModule.DeleteModule())

        self.api.add_route('/topologies/{id}/launch', LaunchTopology.LaunchTopology())
        self.api.add_route('/topologies/{id}/stop', StopTopology.StopTopology())
        self.api.add_route('/topologies/{id}/log', GetTopologyLog.GetTopologyLog())

        # Otherwise return a 404
        self.api.add_sink(ResponsesHandler.handle_otherwise, '')
