import json
import os
import types
from datetime import datetime

from bson.objectid import ObjectId
from falcon.testing import TestCase

from clustering.mongo.MongoHandler import MongodbHandler
from config.Routes import Routes
from config.environmentConfig.Test import conf
from error.ErrorHandler import PathException, ParseException
from logger.Logger import logger
from utils.SinfonierConstants import Module as ModuleConst, ModuleVersions as ModVersionConst, Topology as TopologyConst


class ApiBaseTestCase(TestCase):
    def setUp(self):
        super(ApiBaseTestCase, self).setUp()
        routes = Routes()
        self.api_class = routes.api
        self.api = self.api_class

    @staticmethod
    def upload_collection(collection_name, json_file, parser=None):
        logger.info('Uploading data to ' + conf.MONGO_DATABASE + ' database.')

        mongodb = MongodbHandler(db_name=conf.MONGO_DATABASE)
        if not os.path.exists(json_file) or not os.path.isfile(json_file):
            raise PathException('We cannot find this file: ' + str(json_file))

        with open(json_file) as json_data:
            data = json.load(json_data)

            if parser and isinstance(parser, types.FunctionType):
                data = map(parser, data)
            else:
                logger.warning('You should probably to pass it a parse function.')

        mongodb.db[collection_name].drop()
        mongodb.db[collection_name].insert_many(data)
        mongodb.close()

    @staticmethod
    def drop_collection(collection_name):
        mongodb = MongodbHandler(db_name=conf.MONGO_DATABASE)
        mongodb.db[collection_name].drop()
        mongodb.close()

    @staticmethod
    def parse_module(json_module):
        date_format = '%Y-%m-%dT%H:%M:%S'

        if not json_module:
            raise ParseException('We cannot parse the module')

        try:
            json_module[ModVersionConst.FIELD_ID] = ObjectId(json_module[ModVersionConst.FIELD_ID])
            json_module[ModVersionConst.FIELD_CREATED_AT] = datetime.strptime(
                json_module[ModVersionConst.FIELD_CREATED_AT][0:19],
                date_format
            )
            json_module[ModVersionConst.FIELD_UPDATED_AT] = datetime.strptime(
                json_module[ModVersionConst.FIELD_UPDATED_AT][0:19],
                date_format
            )
        except Exception as Ex:
            logger.error(Ex.message)
            raise ParseException('We cannot parse the module because we probably missing some mandatory field.')

        return json_module

    @staticmethod
    def parse_module_version(json_version):
        date_format = '%Y-%m-%dT%H:%M:%S'

        if not json_version:
            raise ParseException('We cannot parse the module\' version')

        try:
            json_version[ModuleConst.FIELD_ID] = ObjectId(json_version[ModuleConst.FIELD_ID])
            json_version[ModuleConst.FIELD_CREATED_AT] = datetime.strptime(
                json_version[ModuleConst.FIELD_CREATED_AT][0:19],
                date_format
            )
            json_version[ModuleConst.FIELD_UPDATED_AT] = datetime.strptime(
                json_version[ModuleConst.FIELD_UPDATED_AT][0:19],
                date_format
            )
        except Exception as Ex:
            logger.error(Ex.message)
            raise ParseException('We cannot parse the module\'s version because we probably missing some mandatory field.')

        return json_version

    @staticmethod
    def parse_topology(json_topology):
        date_format = '%Y-%m-%dT%H:%M:%S'

        if not json_topology:
            raise ParseException('We cannot parse the topology\' version')

        try:
            json_topology[TopologyConst.FIELD_ID] = ObjectId(json_topology[TopologyConst.FIELD_ID])
            json_topology[TopologyConst.FIELD_CREATED_AT] = datetime.strptime(
                json_topology[TopologyConst.FIELD_CREATED_AT][0:19],
                date_format
            )
            json_topology[TopologyConst.FIELD_UPDATED] = datetime.strptime(
                json_topology[TopologyConst.FIELD_UPDATED][0:19],
                date_format
            )
        except Exception as Ex:
            logger.error(Ex.message)
            raise ParseException('We cannot parse the module\'s version because we probably missing some mandatory field.')

        return json_topology
