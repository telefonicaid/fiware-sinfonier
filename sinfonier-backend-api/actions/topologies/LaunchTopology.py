import os

import falcon

from actions.ApiBase import ApiBase
from apiRequests.ResponsesHandler import ResponsesHandler
from clustering.mongo.MongoHandler import MongodbFactory
from clustering.storm.StormProcess import StormProcess
from clustering.storm.StormUI import StormUI
from clustering.storm.TopologyBuilder import TopologyBuilder
from error.ErrorHandler import TopologyNotInCluster, Error
from logger.Logger import logger
from utils.CommandExecutor import CommandException
from utils.SinfonierConstants import Topology as TopologyConst


class LaunchTopology(ApiBase):
    @falcon.before(ApiBase.validate_params)
    def on_post(self, req, resp, id):
        topology_id = self.validated_params['id']
        builder = None
        try:
            topology = MongodbFactory.get_topology(topology_id)
            if not topology:
                return ResponsesHandler.handle_404(resp, 'Topology not found')

            topology_name = topology[TopologyConst.FIELD_NAME]
            try:
                pass
                # info = StormUI.killTopologyByName(topology_name, 0)
            except TopologyNotInCluster:
                info = None

            logger.debug('Creating topology project...')
            builder = TopologyBuilder(topology)
            logger.debug('Building topology jar...')
            builder.build_jar_with_dependencies()
            logger.debug('Uploading topology to storm...')
            result = StormProcess.launch_topology_cmd(topology_name, os.path.join(builder.base_path, 'target', builder.jar_name))
            logger.debug(result.stdout)

            MongodbFactory.update_running_state(topology_id)
            return ResponsesHandler.handle_200(resp, {'id': topology_id, 'status': TopologyConst.STATUS_RUNNING})

        except CommandException as Ex:
            logger.error(Ex.message)
            logger.debug(Ex.log())
            return ResponsesHandler.handle_400(resp)
        except Error as Ex:
            logger.error(Ex.message)
            return ResponsesHandler.handle_400(resp)
        except Exception as e:
            logger.critical(e.message)
            return ResponsesHandler.handle_500(resp)
        finally:
            if builder is not None:
                builder.remove()
