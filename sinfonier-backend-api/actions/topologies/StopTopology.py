import falcon

from actions.ApiBase import ApiBase
from apiRequests.ResponsesHandler import ResponsesHandler
from clustering.mongo.MongoHandler import MongodbFactory
from clustering.storm.StormUI import StormUI
from error.ErrorHandler import TopologyInvalidId, TopologyNotInCluster, MissingMandatoryFields, Error
from logger.Logger import logger
from utils.SinfonierConstants import Topology as TopologyConst


class StopTopology(ApiBase):
    @falcon.before(ApiBase.validate_params)
    def on_post(self, req, resp, id):
        topology_id = self.validated_params['id']

        try:
            topology = MongodbFactory.get_topology(topology_id)

            if not topology:
                return ResponsesHandler.handle_404(resp, 'Topology not found')

            info = StormUI.killTopologyByName(topology[TopologyConst.FIELD_NAME])

            if info['status'] == 'success':
                MongodbFactory.update_stopped_state(topology_id)
                return ResponsesHandler.handle_200(resp, {'id': topology_id, 'status': TopologyConst.STATUS_STOPPED})

            return ResponsesHandler.handle_409(resp, {'id': topology_id, 'status': info['status']})

        except TopologyInvalidId as Ex:
            logger.error(Ex.message)
            return ResponsesHandler.handle_400(resp)
        except MissingMandatoryFields as Ex:
            logger.error(Ex.message)
            return ResponsesHandler.handle_400(resp)
        except TopologyNotInCluster as Ex:
            MongodbFactory.update_stopped_state(topology_id)
            logger.error(Ex.message)
            return ResponsesHandler.handle_200(resp, {'id': topology_id, 'status': TopologyConst.STATUS_STOPPED})
        except Error as Ex:
            logger.error(Ex.message)
            return ResponsesHandler.handle_400(resp)
        except Exception as Ex:
            logger.critical(Ex.message)
            return ResponsesHandler.handle_500(resp)
