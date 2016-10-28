from mock.mock import MagicMock

from clustering.mongo.MongoHandler import MongodbFactory
from clustering.storm.StormUI import StormUI
from config.environmentConfig.Test import conf as config
from tests.actions.ApiBaseTest import ApiBaseTestCase
from utils.SinfonierConstants import Topology as TopologyConst


class StopTopologyTestCase(ApiBaseTestCase):
    URL = '/topologies/{id}/stop'

    @classmethod
    def setUpClass(cls):
        cls.upload_collection(TopologyConst.COLLECTION_NAME, config.TOPOLOGIES_JSON_FILE, parser=cls.parse_topology)

    def setUp(self):
        super(StopTopologyTestCase, self).setUp()
        StormUI.kill_topology_by_name = MagicMock(return_value={'status': 'success'})

    def test_stop_01(self):
        topology_id = '5805f292b5aa21915447aaf9'
        result = self.simulate_post(self.URL.format(id=topology_id))
        self.assertTrue(result.status_code == 200, 'status code should be 200')
        topology = MongodbFactory.get_topology(topology_id)
        self.assertIsNotNone(topology, 'should be defined')
        self.assertEqual(topology[TopologyConst.FIELD_STATUS], TopologyConst.STATUS_STOPPED, 'should be stopped')

    def test_stop_02(self):
        topology_id = '5809da0fb5aafbc4b62a4b2b'
        result = self.simulate_post(self.URL.format(id=topology_id))
        self.assertTrue(result.status_code == 200, 'status code should be 200')
        topology = MongodbFactory.get_topology(topology_id)
        self.assertIsNotNone(topology, 'should be defined')
        self.assertEqual(topology[TopologyConst.FIELD_STATUS], TopologyConst.STATUS_STOPPED, 'should be stopped')

    @classmethod
    def tearDownClass(cls):
        cls.drop_collection(TopologyConst.COLLECTION_NAME)

    @staticmethod
    def parse_topology(json_topology):
        json_topology = super(StopTopologyTestCase, StopTopologyTestCase).parse_topology(json_topology)
        json_topology[TopologyConst.FIELD_STATUS] = TopologyConst.STATUS_RUNNING
        return json_topology
