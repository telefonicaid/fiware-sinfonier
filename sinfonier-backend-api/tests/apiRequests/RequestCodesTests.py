import unittest

from apiRequests.RequestCodes import Code, Code200, Code500


class CodeTestCase(unittest.TestCase):
    def test_generic_codes(self):
        my_data = {'myData': 'myData'}
        my_code1 = {'code': 1, 'status': 'ok'}
        my_code2 = {'code': 2, 'status': 'nook', 'data': my_data, 'message': 'hello'}

        code1 = Code(1, 'ok')
        code2 = Code(2, 'nook', my_data, 'hello')

        self.assertEqual(code1, my_code1)
        self.assertEqual(code2, my_code2)

    def test_code200(self):
        c200 = Code200({'myData': 'myDataValue'}, 'msg')
        self.assertEqual(c200.get('code'), 200, 'The code is not 200')
        self.assertEqual(c200.get('status'), 'success', 'The status is not success')
        self.assertIsInstance(c200.get('data'), dict, 'The data is not a dict')
        self.assertEqual(c200.get('message'), 'msg', 'The message is not \'msg\'')

    def test_code500(self):
        c500 = Code500()
        self.assertEqual(c500.get('code'), 500, 'The code is not 500')
        self.assertEqual(c500.get('status'), 'error', 'The status is not success')
        self.assertIsNone(c500.get('data'), 'Data should will be None')
        self.assertEqual(c500.get('message'), 'INTERNAL SERVER ERROR', 'invalid message')


if __name__ == '__main__':
    unittest.main()
