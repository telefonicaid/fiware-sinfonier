from clustering.mongo.MongoHandler import MongodbFactory
from config.environmentConfig.Test import conf as config
from tests.actions.ApiBaseTest import ApiBaseTestCase
from utils.SinfonierConstants import Module as ModuleConst, ModuleVersions as ModVersionConst


class UploadModuleTestCase(ApiBaseTestCase):
    URL = '/modules/{id}/{version_id}'

    @classmethod
    def setUpClass(cls):
        cls.upload_collection(ModuleConst.COLLECTION_NAME, config.MODULE_JSON_FILE, parser=cls.parse_module)
        cls.upload_collection(ModVersionConst.COLLECTION_NAME, config.MODULE_VERSION_JSON_FILE, parser=cls.parse_module_version)

    def test_uploaded_success_01(self):
        module_id = '57ff8856b5aad378bac5abda'
        version_id = '57ff8856b5aad378bac5abd9'
        result = self.simulate_post(self.URL.format(id=module_id, version_id=version_id))

        self.assertTrue(result.status_code == 200, 'status code should be 200')

        module = MongodbFactory.get_module(module_id)
        version = MongodbFactory.get_module_version(version_id)

        self.assertIsNotNone(module, 'module should not be a none')
        self.assertIsNotNone(version, 'version should not be a none')

        self.assertEqual(version[ModVersionConst.FIELD_BUILD_STATUS], ModVersionConst.BUILD_STATUS_SUCCESS,
                         'build status should be success')
        self.assertTrue(len(version[ModVersionConst.FIELD_SOURCE_CODE]) > 0, 'the code from gist should be saved on BBDD')

    def test_uploaded_success_02(self):
        module_id = '57ff98ffb5aa858d11776185'
        version_id = '57ff98ffb5aa858d11776184'
        result = self.simulate_post(self.URL.format(id=module_id, version_id=version_id))

        self.assertTrue(result.status_code == 200, 'status code should be 200')

        module = MongodbFactory.get_module(module_id)
        version = MongodbFactory.get_module_version(version_id)

        self.assertIsNotNone(module, 'module should not be a none')
        self.assertIsNotNone(version, 'version should not be a none')

        self.assertEqual(version[ModVersionConst.FIELD_BUILD_STATUS], ModVersionConst.BUILD_STATUS_SUCCESS,
                         'build status should be success')

    def test_uploaded_success_03(self):
        module_id = '57ff9930b5aa858d11776187'
        version_id = '57ff9930b5aa858d11776186'
        result = self.simulate_post(self.URL.format(id=module_id, version_id=version_id))

        self.assertTrue(result.status_code == 200, 'status code should be 200')

        module = MongodbFactory.get_module(module_id)
        version = MongodbFactory.get_module_version(version_id)

        self.assertIsNotNone(module, 'module should not be a none')
        self.assertIsNotNone(version, 'version should not be a none')

        self.assertEqual(version[ModVersionConst.FIELD_BUILD_STATUS], ModVersionConst.BUILD_STATUS_SUCCESS,
                         'build status should be success')
        self.assertTrue(len(version[ModVersionConst.FIELD_SOURCE_CODE]) > 0, 'the code from gist should be saved on BBDD')

    def test_uploaded_success_04(self):
        module_id = '57ff9ea7b5aa858d11776189'
        version_id = '57ff9ea7b5aa858d11776188'
        result = self.simulate_post(self.URL.format(id=module_id, version_id=version_id))

        self.assertTrue(result.status_code == 200, 'status code should be 200')

        module = MongodbFactory.get_module(module_id)
        version = MongodbFactory.get_module_version(version_id)

        self.assertIsNotNone(module, 'module should not be a none')
        self.assertIsNotNone(version, 'version should not be a none')

        self.assertEqual(version[ModVersionConst.FIELD_BUILD_STATUS], ModVersionConst.BUILD_STATUS_SUCCESS,
                         'build status should be success')
        self.assertTrue(len(version[ModVersionConst.FIELD_SOURCE_CODE]) > 0, 'the code from gist should be saved on BBDD')

    def test_uploaded_success_05(self):
        module_id = '57ffa120b5aa858d1177618b'
        version_id = '57ffa120b5aa858d1177618a'
        result = self.simulate_post(self.URL.format(id=module_id, version_id=version_id))

        self.assertTrue(result.status_code == 200, 'status code should be 200')

        module = MongodbFactory.get_module(module_id)
        version = MongodbFactory.get_module_version(version_id)

        self.assertIsNotNone(module, 'module should not be a none')
        self.assertIsNotNone(version, 'version should not be a none')

        self.assertEqual(version[ModVersionConst.FIELD_BUILD_STATUS], ModVersionConst.BUILD_STATUS_SUCCESS,
                         'build status should be success')
        self.assertTrue(len(version[ModVersionConst.FIELD_SOURCE_CODE]) > 0, 'the code from gist should be saved on BBDD')

    def test_uploaded_success_06(self):
        module_id = '57ffa120b5aa858d1177618b'
        version_id = '5800851aba789ab538a62263'
        result = self.simulate_post(self.URL.format(id=module_id, version_id=version_id))

        self.assertTrue(result.status_code == 200, 'status code should be 200')

        module = MongodbFactory.get_module(module_id)
        version = MongodbFactory.get_module_version(version_id)

        self.assertIsNotNone(module, 'module should not be a none')
        self.assertIsNotNone(version, 'version should not be a none')

        self.assertEqual(version[ModVersionConst.FIELD_BUILD_STATUS], ModVersionConst.BUILD_STATUS_SUCCESS,
                         'build status should be success')

    @classmethod
    def tearDownClass(cls):
        cls.drop_collection(ModuleConst.COLLECTION_NAME)
        cls.drop_collection(ModVersionConst.COLLECTION_NAME)
