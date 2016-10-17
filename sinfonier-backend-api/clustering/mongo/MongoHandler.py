import datetime

from bson.objectid import ObjectId
from pymongo import MongoClient
from pymongo.errors import OperationFailure

from config.config import conf as config
from error.ErrorHandler import MongodbInvalidDatabase, MongodbInvalidCollection, MongodbInvalidCredentials, TopologyInvalidId, \
    ModuleInvalidId, ModuleVersionInvalidId, MissingMandatoryFields, ModuleException
from logger.Logger import logger
from utils.SinfonierConstants import Topology as TopologyConsts, Module as ModuleConst, \
    ModuleVersions as ModVersionsConst


class MongodbHandler(object):
    def __init__(self, host='localhost', port=27017, db_name=None, user=None, pwd=None):
        if not db_name:
            raise MongodbInvalidDatabase()

        self.client = MongoClient('mongodb://' + host + ':' + str(port))

        if user and pwd:
            try:
                is_logged = self.client[db_name].authenticate(user, pwd)
            except OperationFailure as e:
                logger.error(e.message)
                is_logged = False

            if not is_logged:
                raise MongodbInvalidCredentials()

        self.db = self.client[db_name]

    def find(self, collection, *args, **kwargs):
        if not collection:
            raise MongodbInvalidCollection()

        return self.db[collection].find(*args, **kwargs)

    def find_one(self, collection, filter):
        """"
        :type collection: str
        """
        if not collection:
            raise MongodbInvalidCollection()

        return self.db[collection].find_one(filter)

    def insert_one(self, collection, doc):
        if not collection:
            raise MongodbInvalidCollection()

        return self.db[collection].insert_one(doc)

    def insert_many(self, collection, docs, ordered=True):
        if not collection:
            raise MongodbInvalidCollection()

        return self.db[collection].insert_many(docs, ordered)

    def update_one(self, collection, filter, document, upsert=True):
        if not collection:
            raise MongodbInvalidCollection()

        return self.db[collection].update_one(filter=filter, update=document, upsert=upsert)

    def update_many(self, collection, documents, upsert=True):
        if not collection:
            raise MongodbInvalidCollection()

        return self.db[collection].update_many(filter=filter, update=documents, upsert=upsert)


# Mongodb's config
_host = config.MONGO_HOST
_port = config.MONGO_PORT
_bd_name = config.MONGO_DATABASE

if config.MONGO_AUTH:
    _user = config.MONGO_USER
    _pwd = config.MONGO_PASSWORD
else:
    _user = _pwd = None

instance = MongodbHandler(host=_host, port=_port, db_name=_bd_name, user=_user, pwd=_pwd)


class MongodbFactory:
    @staticmethod
    def update_topology_state(id, status):
        if not ObjectId.is_valid(id):
            raise TopologyInvalidId()

        return instance.update_one(TopologyConsts.COLLECTION_NAME, {TopologyConsts.FIELD_ID: ObjectId(id)},
                                   {'$set': {TopologyConsts.FIELD_STATUS: status}}, upsert=False)

    @staticmethod
    def update_start_at(id):
        if not ObjectId.is_valid(id):
            raise TopologyInvalidId()

        return instance.update_one(TopologyConsts.COLLECTION_NAME, {TopologyConsts.FIELD_ID: id},
                                   {'$set': {TopologyConsts.FIELD_STARTED: datetime.datetime.utcnow()}}, upsert=False)

    @staticmethod
    def update_stop_at(id):
        if not ObjectId.is_valid(id):
            raise TopologyInvalidId()

        return instance.update_one(TopologyConsts.COLLECTION_NAME, {TopologyConsts.FIELD_ID: ObjectId(id)},
                                   {'$set': {TopologyConsts.FIELD_STOPPED: datetime.datetime.utcnow()}}, upsert=False)

    @staticmethod
    def update_running_state(id):
        if not ObjectId.is_valid(id):
            raise TopologyInvalidId()

        return instance.update_one(TopologyConsts.COLLECTION_NAME,
                                   {TopologyConsts.FIELD_ID: ObjectId(id)},
                                   {'$set': {TopologyConsts.FIELD_STATUS: TopologyConsts.STATUS_RUNNING,
                                             TopologyConsts.FIELD_STARTED: datetime.datetime.utcnow(),
                                             TopologyConsts.FIELD_UPDATED: datetime.datetime.utcnow()}}, upsert=False)

    @staticmethod
    def update_stopped_state(id):
        if not ObjectId.is_valid(id):
            raise TopologyInvalidId()

        return instance.update_one(TopologyConsts.COLLECTION_NAME,
                                   {TopologyConsts.FIELD_ID: ObjectId(id)},
                                   {'$set': {TopologyConsts.FIELD_STATUS: TopologyConsts.STATUS_STOPPED,
                                             TopologyConsts.FIELD_STOPPED: datetime.datetime.utcnow(),
                                             TopologyConsts.FIELD_UPDATED: datetime.datetime.utcnow()}}, upsert=False)

    @staticmethod
    def get_topology(id):
        if not ObjectId.is_valid(id):
            raise TopologyInvalidId()

        top = instance.find_one(TopologyConsts.COLLECTION_NAME, {TopologyConsts.FIELD_ID: ObjectId(id)})
        MongodbFactory.validate_topology_params(top)
        return top

    @staticmethod
    def get_module(id):
        if not ObjectId.is_valid(id):
            raise ModuleInvalidId()

        mod = instance.find_one(ModuleConst.COLLECTION_NAME, {ModuleConst.FIELD_ID: ObjectId(id)})
        MongodbFactory.validate_module_params(mod)
        return mod

    @staticmethod
    def get_modules(ids):
        if reduce(lambda x, y: not x or not y, map(lambda id: ObjectId.is_valid(id), ids)):
            raise ModuleInvalidId()

        _ids = map(lambda id: ObjectId(id), ids)
        modules = instance.find(ModuleConst.COLLECTION_NAME, {ModuleConst.FIELD_ID: {'$in': _ids}})
        _modules = list()

        for m in modules:
            if not MongodbFactory.validate_module_params(m):
                raise ModuleException('The module ' + m.__str__() + ' are not valid.')
            else:
                _modules.append(m)

        return _modules

    @staticmethod
    def get_module_version(version_id):
        if not ObjectId.is_valid(version_id):
            raise ModuleVersionInvalidId()

        module_version = instance.find_one(ModVersionsConst.COLLECTION_NAME, {ModVersionsConst.FIELD_ID: ObjectId(version_id)})
        MongodbFactory.validate_module_version_params(module_version)
        return module_version

    @staticmethod
    def save_module_source(version_id, src_code):
        if not ObjectId.is_valid(version_id):
            raise ModuleInvalidId()

        return instance.update_one(ModVersionsConst.COLLECTION_NAME, {ModVersionsConst.FIELD_ID: ObjectId(version_id)},
                                   {'$set': {ModVersionsConst.FIELD_SOURCE_CODE: src_code}}, upsert=False)

    @staticmethod
    def update_status_builder_module(version_id, status):
        if not ObjectId.is_valid(version_id):
            raise ModuleInvalidId()

        return instance.update_one(ModVersionsConst.COLLECTION_NAME,
                                   {ModVersionsConst.FIELD_ID: ObjectId(version_id)},
                                   {'$set': {ModVersionsConst.FIELD_BUILD_STATUS: status}}, upsert=False)

    @staticmethod
    def update_module_last_modify(id):
        if not ObjectId.is_valid(id):
            raise ModuleInvalidId()

        return instance.update_one(ModuleConst.COLLECTION_NAME, {ModuleConst.FIELD_ID: ObjectId(id)},
                                   {'$set': {ModuleConst.FIELD_UPDATE_AT: datetime.datetime.utcnow()}}, upsert=False)

    @staticmethod
    def validate_module_params(module):
        if module is not None:
            fields = {ModuleConst.FIELD_ID, ModuleConst.FIELD_NAME, ModuleConst.FIELD_TYPE, ModuleConst.FIELD_LANG}
            return MongodbFactory.validate_mandatory_params('module', module, fields)

    @staticmethod
    def validate_module_version_params(module_version):
        if module_version is not None:
            fields = {ModVersionsConst.FIELD_ID, ModVersionsConst.FIELD_SOURCE_CODE_URL,
                      ModVersionsConst.FIELD_SOURCE_CODE,
                      ModVersionsConst.FIELD_SOURCE_TYPE}
            MongodbFactory.validate_mandatory_params('module version', module_version, fields)

    @staticmethod
    def validate_topology_params(topology):
        if topology is not None:
            fields = {TopologyConsts.FIELD_ID, TopologyConsts.FIELD_STATUS, TopologyConsts.FIELD_NAME,
                      TopologyConsts.FIELD_CONFIG}
            MongodbFactory.validate_mandatory_params('topology', topology, fields)

    @staticmethod
    def validate_mandatory_params(name, obj, fields):
        if obj is not None:
            missing = []
            keys = set(obj)
            for field in fields:
                if field not in keys:
                    missing.append(field)

            if missing:
                raise MissingMandatoryFields('Missing mandatory fields in ' + name + ': ' + ' '.join(missing))
            return True
