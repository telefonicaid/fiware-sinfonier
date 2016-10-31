from mock.mock import MagicMock

from clustering.storm.LogViewer import LogViewer
from clustering.storm.StormUI import StormUI
from config.environmentConfig.Test import conf as config
from tests.actions.ApiBaseTest import ApiBaseTestCase
from utils.SinfonierConstants import Topology as TopologyConst


class GetLogTopologyTestCase(ApiBaseTestCase):
    URL = '/topologies/{id}/log'

    @classmethod
    def setUpClass(cls):
        cls.upload_collection(TopologyConst.COLLECTION_NAME, config.TOPOLOGIES_JSON_FILE, parser=cls.parse_topology)

    def setUp(self):
        super(GetLogTopologyTestCase, self).setUp()
        LogViewer.get_log = MagicMock(return_value=['MOCK LOG. Lorem ipsum dolor sit amet, consectetuer adipiscing elit.'])

    def test_log_01(self):
        topology_id = '5805f292b5aa21915447aaf9'
        result = self.simulate_get(self.URL.format(id=topology_id),query_string="start=-1&length=100")
        self.assertTrue(result.status_code == 200, 'status code should be 200')
        self.assertIsNotNone(result.text, 'should be defined')
        self.assertTrue('MOCK' in result.text, 'should contains MOCK')

    def test_log_02(self):
        topology_id = '5809da0fb5aafbc4b62a4b2b'
        result = self.simulate_get(self.URL.format(id=topology_id),query_string="start=-1&length=100")
        self.assertTrue(result.status_code == 200, 'status code should be 200')
        self.assertIsNotNone(result.text, 'should be defined')
        self.assertTrue('MOCK' in result.text, 'should contains MOCK')

    @classmethod
    def tearDownClass(cls):
        cls.drop_collection(TopologyConst.COLLECTION_NAME)


class GetTopologyLogSizesTestCase(ApiBaseTestCase):
    URL = '/topologies/{id}/logsizes'

    @classmethod
    def setUpClass(cls):
        cls.upload_collection(TopologyConst.COLLECTION_NAME, config.TOPOLOGIES_JSON_FILE, parser=cls.parse_topology)

    def setUp(self):
        super(GetTopologyLogSizesTestCase, self).setUp()
        StormUI.getFile = MagicMock(return_value='<html><body><a href="http://localhost?start=100&length=1000">Next</a><body></html>')
        StormUI.getWorkersByTopologyName= MagicMock(return_value=['filename'])
    def test_log_01(self):
        topology_id = '5805f292b5aa21915447aaf9'
        result = self.simulate_get(self.URL.format(id=topology_id))
        self.assertTrue(result.status_code == 200, 'status code should be 200')
        self.assertTrue(result.json['data']['sizes'][0]['start'] == '100', 'size not valid')
        self.assertTrue(result.json['data']['sizes'][0]['length'] == '1000', 'size not valid')

    @classmethod
    def tearDownClass(cls):
        cls.drop_collection(TopologyConst.COLLECTION_NAME)
