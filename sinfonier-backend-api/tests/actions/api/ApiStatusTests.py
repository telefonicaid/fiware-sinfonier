from tests.actions.ApiBaseTest import ApiBaseTestCase


class ApiStatusTestCase(ApiBaseTestCase):
    def test_api_status(self):
        result = self.simulate_post(path='/')
        self.assertTrue(result.status_code == 200, 'Should be 200')

        result = self.simulate_get(path='/')
        self.assertTrue(result.status_code == 400, 'Should be 400')

        result = self.simulate_patch(path='/')
        self.assertTrue(result.status_code == 400, 'Should be 400')

        result = self.simulate_put(path='/')
        self.assertTrue(result.status_code == 400, 'Should be 400')

        result = self.simulate_head(path='/')
        self.assertTrue(result.status_code == 400, 'Should be 400')

        result = self.simulate_delete(path='/')
        self.assertTrue(result.status_code == 400, 'Should be 400')

        result = self.simulate_options(path='/')
        self.assertTrue(result.status_code == 400, 'Should be 400')
