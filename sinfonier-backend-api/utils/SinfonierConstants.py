class Topology:
    COLLECTION_NAME = 'topologies'
    FIELD_ID = '_id'
    FIELD_NAME = 'name'
    FIELD_STATUS = 'status'
    FIELD_UPDATED = 'updated_at'
    FIELD_STARTED = 'started_at'
    FIELD_STOPPED = 'stopped_at'
    STATUS_RUNNING = 'running'
    STATUS_STOPPED = 'stopped'
    STATUS_ACTIVE = 'active'
    FIELD_CONFIG = 'config'
    FIELD_MODULES = 'modules'
    PROP_TIMEOUT = 'topologyMessageTimeout'
    PROP_MAX_SPOUT_PENDING = 'maxSpoutPending'
    PROP_NUM_WORKERS = 'numWorkers'


class Module:
    COLLECTION_NAME = 'modules'
    FIELD_ID = '_id'
    FIELD_UPDATE_AT = 'updated_at'
    FIELD_NAME = 'name'
    FIELD_LANG = 'language'
    FIELD_TYPE = 'type'
    FIELD_MODULE_ID = 'module_id'
    FIELD_MODULE_VERSION_ID = 'module_version_id'
    FIELD_VERSION_CODE = 'versionCode'


class ModuleVersions:
    COLLECTION_NAME = 'moduleVersions'
    FIELD_ID = '_id'
    FIELD_MODULE_ID = 'module_id'
    FIELD_SOURCE_TYPE = 'sourceType'
    FIELD_VERSION_CODE = 'versionCode'
    FIELD_SOURCE_CODE = 'sourceCode'
    FIELD_SOURCE_CODE_URL = 'sourceCodeUrl'
    FIELD_BUILD_STATUS = 'buildStatus'
    FIELD_LIBRARIES = 'libraries'
    FIELD_LIBRARY_URL = 'url'
    SOURCE_TYPE_GIST = 'gist'
    BUILD_STATUS_SUCCESS = 'success'
    BUILD_STATUS_FAILURE = 'failure'


class Environment:
    SINFONIER_ENV_KEY = 'SINFONIER_API_NAME'
    PROD_ENVIRONMENT = 'production'
    DEVELOP_ENVIRONMENT = 'develop'
    DOCKER_ENVIRONMENT = 'docker'
