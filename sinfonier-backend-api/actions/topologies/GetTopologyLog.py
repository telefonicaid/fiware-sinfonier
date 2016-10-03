import falcon

from actions.ApiBase import ApiBase
from apiRequests.ResponsesHandler import ResponsesHandler
from clustering.mongo.MongoHandler import MongodbFactory
from clustering.storm.LogViewer import LogViewer
from error.ErrorHandler import TopologyInvalidId, MissingMandatoryFields, Error
from logger.Logger import logger
from utils.SinfonierConstants import Topology as TopologyConst


class GetTopologyLog(ApiBase):
    @falcon.before(ApiBase.validate_params)
    def on_get(self, req, resp, id):
        topology_id = self.validated_params['id']
        try:
            topology = MongodbFactory.get_topology(topology_id)
            if topology is None:
                return ResponsesHandler.handle_404(resp, 'Topology not found')

            text = LogViewer.get_log(topology[TopologyConst.FIELD_NAME])
            return ResponsesHandler.handle_200(resp, {'id': topology_id, 'log': text})

        except TopologyInvalidId as Ex:
            logger.error(Ex.message)
            return ResponsesHandler.handle_400(resp, Ex.message)
        except MissingMandatoryFields as Ex:
            logger.error(Ex.message)
            return ResponsesHandler.handle_400(resp)
        except Error as Ex:
            logger.critical(Ex.message)
            return ResponsesHandler.handle_400(resp)
        except Exception as Ex:
            logger.critical(Ex.message)
            return ResponsesHandler.handle_500(resp)
