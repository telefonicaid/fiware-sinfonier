import json

from config.config import conf
from error.ErrorHandler import TopologyNotInCluster
from utils.HTTPHandler import HTTPHandler


class StormUI:
    @staticmethod
    def baseurl():
        return "http://{}:{}".format(conf.STORM_UI_HOST, conf.STORM_UI_PORT)

    # GET Operations

    ######################################
    # /api/v1/cluster/configuration (GET)
    # Returns the cluster configuration.
    ######################################

    @staticmethod
    def getClusterConfiguration():
        url = StormUI.baseurl() + "/api/v1/cluster/configuration"
        return HTTPHandler.get_as_json(url)

    ######################################
    # /api/v1/cluster/summary (GET)
    # Returns cluster summary information such as nimbus uptime or number of supervisors.
    ######################################

    @staticmethod
    def getClusterSummary():
        url = StormUI.baseurl() + "/api/v1/cluster/summary"
        return HTTPHandler.get_as_json(url)

    ######################################
    # /api/v1/supervisor/summary (GET)
    # Returns summary information for all supervisors.
    ######################################

    @staticmethod
    def getSupervisorSummary():
        url = StormUI.baseurl() + "/api/v1/supervisor/summary"
        return HTTPHandler.get_as_json(url)

    ######################################
    # /api/v1/topology/summary (GET)
    # Returns summary information for all topologies.
    ######################################

    @staticmethod
    def getTopologySummary():
        url = StormUI.baseurl() + "/api/v1/topology/summary"
        return HTTPHandler.get_as_json(url)

    ######################################
    # /api/v1/topology/:id (GET)
    # Returns topology information and statistics. Substitute id with topology id.
    ######################################

    @staticmethod
    def getTopology(topologyid):
        url = StormUI.baseurl() + "/api/v1/topology/" + topologyid
        return HTTPHandler.get_as_json(url)

    ######################################
    # /api/v1/topology/:id/component/:component (GET)
    # Returns detailed metrics and executor information
    ######################################

    @staticmethod
    def getTopologyComponent(topologyid, componentid):
        url = StormUI.baseurl() + "/api/v1/topology/" + topologyid + "/component/" + componentid
        return HTTPHandler.get_as_json(url)

    # POST Operations

    ######################################
    # /api/v1/uploadTopology (POST)
    # uploads a topology.
    ######################################

    @staticmethod
    def uploadTopology(topologyConfig, topologyJar):
        '''
        >>> StormUI.uploadTopology("config","my.jar")
        'Not implemented yet in this version'
        '''
        return "Not implemented yet in this version"

    # url = StormUI.baseurl+"/api/v1/uploadTopology"
    # return HTTPHandler.get(url).json()

    ######################################
    # /api/v1/topology/:id/activate (POST)
    # Activates a topology.
    ######################################

    @staticmethod
    def activateTopology(topologyid):
        url = StormUI.baseurl() + "/api/v1/topology/" + topologyid + "/activate"
        return HTTPHandler.post_as_json(url)

    ######################################
    # /api/v1/topology/:id/activate (POST)
    # Activates a topology.
    ######################################

    @staticmethod
    def activateTopologyByName(topology_name):
        id = StormUI.get_topology_id_by_name(topology_name)
        if id is not None:
            return StormUI.activateTopology(id)

    ######################################
    # /api/v1/topology/:id/deactivate (POST)
    # Deactivates a topology.
    ######################################

    @staticmethod
    def deactivateTopology(topologyid):
        url = StormUI.baseurl() + "/api/v1/topology/" + topologyid + "/deactivate"
        return HTTPHandler.post_as_json(url)

    ######################################
    # /api/v1/topology/:id/deactivate (POST)
    # Deactivates a topology.
    ######################################

    @staticmethod
    def deactivateTopologyByName(topology_name):
        id = StormUI.get_topology_id_by_name(topology_name)
        return StormUI.deactivateTopology(id)

    ######################################
    # /api/v1/topology/:id/rebalance/:wait-time (POST)
    # Rebalances a topology.
    # rebalanceOptions = {"rebalanceOptions": {"numWorkers": 2, "executors": { "spout" : "5", "split": 7, "count": 5 }}, "callback":"foo"}
    ######################################

    @staticmethod
    def rebalanceTopology(topologyid, wait_time, rebalanced_options=None):
        if rebalanced_options is None:
            rebalanced_options = {}
        url = StormUI.baseurl() + "/api/v1/topology/" + topologyid + "/rebalance/" + wait_time
        headers = {"Content-Type": "application/json"}
        return HTTPHandler.post_as_json(url, data=json.dumps(rebalanced_options), headers=headers)

    ######################################
    # /api/v1/topology/:id/kill/:wait-time (POST)
    # Kills a topology.
    ######################################

    @staticmethod
    def killTopology(topologyid, wait_time):
        url = StormUI.baseurl() + "/api/v1/topology/" + topologyid + "/kill/" + str(wait_time)
        return HTTPHandler.post_as_json(url)

    ######################################
    # /api/v1/topology/:id/deactivate (POST)
    # Deactivates a topology.
    ######################################

    @staticmethod
    def kill_topology_by_name(topology_name, wait_time=0):
        id = StormUI.get_topology_id_by_name(topology_name)
        if id is not None:
            return StormUI.killTopology(id, wait_time)
        return None

    ######################################
    # /api/v1/topology/:id/visualization (GET)
    # Get topology visualization data.
    ######################################

    @staticmethod
    def getTopologyVisualization(topologyid):
        url = StormUI.baseurl() + "/api/v1/topology/" + topologyid + "/visualization"
        return HTTPHandler.get_as_json(url)

    ######################################


    ######################################
    # Get topology summary by name (GET)
    # This function makes 1 StormUI API query
    ######################################

    @staticmethod
    def getTopologySummaryByName(topologyname):
        response = StormUI.getTopologySummary()
        topologies = response["topologies"]
        for topo in topologies:
            if topo["name"] == topologyname:
                return topo
        return {}

    ######################################
    # Get topology detail by name (GET)
    # This function makes 2 StormUI API queries
    ######################################

    @staticmethod
    def getTopologyByName(topologyname):
        response = StormUI.getTopologySummary()
        topologies = response["topologies"]
        for topo in topologies:
            if topo["name"] == topologyname:
                response = StormUI.getTopology(topo["id"])
                return response
        raise TopologyNotInCluster

    ######################################
    # Get topology detail by name (GET)
    # This function makes 2 StormUI API queries
    ######################################

    @staticmethod
    def get_topology_id_by_name(topology_name):
        response = StormUI.getTopologySummary()
        topologies = response["topologies"]
        for topology in topologies:
            if topology["name"] == topology_name:
                return topology["id"]
        raise TopologyNotInCluster

    ######################################
    # Get worker by ID (GET)
    # This function makes 2 StormUI API queries
    ######################################

    ## Return workers list from all spouts and all executors of the topology. Without duplicates.

    @staticmethod
    def getWorkersByTopologyID(topologyid):
        topo = StormUI.getTopology(topologyid)
        return StormUI.getWorkersByTopology(topo)

    ######################################
    # Get worker by Name (GET)
    # This function makes 3 StormUI API queries
    ######################################

    ## Return workers list from all spouts and all executors of the topology. Without duplicates.

    @staticmethod
    def getWorkersByTopologyName(topologyname):
        topo = StormUI.getTopologyByName(topologyname)
        return StormUI.getWorkersByTopology(topo)

    ######################################
    # Get workers by Topology
    #
    ######################################
    ## Return workers list from all spouts and all executors of the topology. Without duplicates.

    @staticmethod
    def getWorkersByTopology(topo):
        spoutids = [spout["spoutId"] for spout in topo["spouts"]]
        workersLinks = list()
        for spoutid in spoutids:
            component = StormUI.getTopologyComponent(topo["id"], spoutid)
            for executor in component["executorStats"]:
                workersLinks.append(executor["workerLogLink"])
        return list(set(workersLinks))

    ######################################
    # Get worker by Name (GET)
    # This function makes 3 StormUI API queries
    ######################################

    ## Return workers list from all spouts and all executors of the topology. Without duplicates.

    @staticmethod
    def getWorkersByTopologyId(topology_id):
        topo = StormUI.getTopology(topology_id)
        spoutids = [spout["spoutId"] for spout in topo["spouts"]]
        workersLinks = list()
        for spoutid in spoutids:
            component = StormUI.getTopologyComponent(topo["id"], spoutid)
            for executor in component["executorStats"]:
                workersLinks.append(executor["workerLogLink"])
        return list(set(workersLinks))

    ######################################
    # Get error in topology by topology Name (GET)
    # This function makes 2 StormUI API queries
    ######################################

    @staticmethod
    def getErrorInTopologyByName(topologyname):
        topo = StormUI.getTopologyByName(topologyname)
        if topo:
            # Return True if there is an error in any module of the topology and False if not
            return any(module["lastError"] for module in (topo["spouts"] + topo["bolts"]))

            ######################################
            # Get error details in topology by topology Name (GET)
            # This function makes 2 StormUI API queries
            ######################################

    @staticmethod
    def getErrorDetailsInTopologyByName(topologyname):
        topo = StormUI.getTopologyByName(topologyname)
        return [{module["spoutId"]: module["lastError"]} for module in topo["spouts"]] + [
            {module["boltId"]: module["lastError"]} for module in topo["bolts"]] if topo else None

    ######################################
    # Get topology visualization by name (GET)
    # This function makes 1 StormUI API query
    ######################################

    @staticmethod
    def getTopologyVisualizationByName(topologyname):
        response = StormUI.getTopologySummary()
        topologies = response["topologies"]
        for topo in topologies:
            if topo["name"] == topologyname:
                response = StormUI.getTopologyVisualization(topo["id"])
                return response
        return {}

    @staticmethod
    def getFile(file_name, start = 0, length=5000):
        if start == -1:
            url = file_name + "&tail="  + str(length)
        else:
            url = file_name + "&start="+str(start)+"&length=" + str(length)
        return HTTPHandler.get(url)
