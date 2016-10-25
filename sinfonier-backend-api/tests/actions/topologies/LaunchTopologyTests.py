from mock.mock import MagicMock

from clustering.mongo.MongoHandler import MongodbFactory
from clustering.mvn.Mvn import Mvn
from clustering.storm.StormProcess import StormProcess
from clustering.storm.StormUI import StormUI
from config.environmentConfig.Test import conf as config
from tests.actions.ApiBaseTest import ApiBaseTestCase
from utils.CommandExecutor import ProcessResult
from utils.SinfonierConstants import Topology as TopologyConst


class LaunchTopologyTestCase(ApiBaseTestCase):
    URL = '/topologies/{id}/launch'

    @classmethod
    def setUpClass(cls):
        cls.upload_collection(TopologyConst.COLLECTION_NAME, config.TOPOLOGIES_JSON_FILE, parser=cls.parse_topology)

    def setUp(self):
        super(LaunchTopologyTestCase, self).setUp()
        StormUI.kill_topology_by_name = MagicMock(return_value={})
        Mvn.build_topology_jar = MagicMock(return_value=ProcessResult('Mock output for Mvn', None, 0))
        StormProcess.launch_topology_cmd = MagicMock(return_value=ProcessResult('Mock output for StormProcess', None, 0))

    def test_launch_01(self):
        topology_id = '5805f292b5aa21915447aaf9'
        result = self.simulate_post(self.URL.format(id=topology_id))
        self.assertTrue(result.status_code == 200, 'status code should be 200')
        topology = MongodbFactory.get_topology(topology_id)
        self.assertIsNotNone(topology, 'should be defined')
        self.assertEqual(topology[TopologyConst.FIELD_STATUS], TopologyConst.STATUS_RUNNING, 'should be running')

    def test_launch_02(self):
        topology_id = '5809da0fb5aafbc4b62a4b2b'
        result = self.simulate_post(self.URL.format(id=topology_id))
        self.assertTrue(result.status_code == 200, 'status code should be 200')
        topology = MongodbFactory.get_topology(topology_id)
        self.assertIsNotNone(topology, 'should be defined')
        self.assertEqual(topology[TopologyConst.FIELD_STATUS], TopologyConst.STATUS_RUNNING, 'should be running')

    @classmethod
    def tearDownClass(cls):
        cls.drop_collection(TopologyConst.COLLECTION_NAME)
