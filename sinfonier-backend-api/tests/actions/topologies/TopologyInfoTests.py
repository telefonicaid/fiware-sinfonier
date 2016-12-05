from mock.mock import MagicMock

from clustering.storm.LogViewer import LogViewer
from clustering.storm.StormUI import StormUI
from config.environmentConfig.Test import conf as config
from tests.actions.ApiBaseTest import ApiBaseTestCase
from utils.SinfonierConstants import Topology as TopologyConst


class GetTopologyInfoTestCase(ApiBaseTestCase):
    URL = '/topologies/{id}/info'

    @classmethod
    def setUpClass(cls):
        cls.upload_collection(TopologyConst.COLLECTION_NAME, config.TOPOLOGIES_JSON_FILE, parser=cls.parse_topology)

    def setUp(self):
        super(GetTopologyInfoTestCase, self).setUp()
        with open(config.TOPOLOGY_INFO_JSON_FILE) as file:
            StormUI.getTopologyByName= MagicMock(return_value=file.read())

    def test_log_01(self):
        topology_id = '5805f292b5aa21915447aaf9'
        result = self.simulate_get(self.URL.format(id=topology_id))
        self.assertTrue(result.status_code == 200, 'status code should be 200')
        self.assertIsNotNone(result.text, 'should be defined')
        self.assertTrue('Ola1' in result.text, 'should contains Ola1')

    @classmethod
    def tearDownClass(cls):
        cls.drop_collection(TopologyConst.COLLECTION_NAME)


