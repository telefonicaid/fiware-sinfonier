import logging
import os


class Config:
    # SinfonierAPI
    SINFONIER_API_HOST = os.getenv('SINFONIER_API_HOST','0.0.0.0')
    SINFONIER_API_PORT = int(os.getenv('SINFONIER_API_PORT','4000'))

    # StormUI AP
    STORM_UI_HOST = os.getenv('STORM_UI_HOST',"stormui")
    STORM_UI_PORT = os.getenv('STORM_UI_PORT',"8080")

    # Gist Credentials
    GIST_USERNAME = os.getenv('GIST_USERNAME','')
    GIST_TOKEN = os.getenv('GIST_TOKEN','')

    # Mongodb's Config
    MONGO_HOST = os.getenv('MONGO_HOST','mongo')
    MONGO_PORT = int(os.getenv('MONGO_PORT','27017'))
    MONGO_DATABASE = os.getenv('MONGO_DATABASE','sinfonier')
    MONGO_AUTH = os.getenv('MONGO_AUTH','false') in ('true','1','yes')
    MONGO_USER = os.getenv('MONGO_USER','sinfonier')
    MONGO_PASSWORD = os.getenv('MONGO_PASSWORD','sinfonier')

    STORM_HOST = os.getenv('STORM_HOST','nimbus')
    if os.environ.__contains__('STORM_HOME'):
        STORM_BINARY = os.path.normpath(os.path.join(os.environ['STORM_HOME'], 'bin/storm'))
    else:
        STORM_BINARY = os.getenv('STORM_BINARY','storm')
    SINFONIER_LAST_JAR = 'Base.jar'
    STORM_TOPOLOGY_PATH = '/tmp/topology'
    STORM_TOPOLOGY_CONFIG_PATH = '/tmp/topology/config'
    STORM_VERSION = '0.10.1'

    MAVEN_BINARY = os.getenv('MAVEN_BINARY','/usr/bin/mvn')

    ROOT_PATH = os.path.normpath(os.path.join(os.path.abspath(__file__), '..', '..', '..'))
    WORKING_PATH = os.path.normpath(os.path.join('/var/storm/src', 'sinfonier-backend'))
    SRC_BASE = 'src/jvm/com/sinfonier/'
    SRC_MULTI_LANG_BASE = 'multilang/resources/'
    SIMPLE_PROJECT_BASE_CLASS_PATH = 'utils/compiler/simple_project/base_classes/'
    SIMPLE_PROJECT_MULTI_LANG_RESOURCES_BASE_CLASS_PATH = 'utils/compiler/simple_project/multilang/resources/'
    SIMPLE_PROJECT_POM_TEMPLATE = 'utils/compiler/simple_project/pom_template.xml'
    JAVA_PATH_BOLTS = os.path.normpath(os.path.join(SRC_BASE, 'bolts'))
    JAVA_PATH_DRAINS = os.path.normpath(os.path.join(SRC_BASE, 'drains'))
    JAVA_PATH_SPOUTS = os.path.normpath(os.path.join(SRC_BASE, 'spouts'))

    TEMPLATES_PATH = 'utils/templates/'
    POM_TEMP = os.path.normpath(os.path.join(TEMPLATES_PATH, 'pom.xml'))

    CLASSES_FINAL_PATH = '/tmp/classes'

    CLASSES_TMP_PATH = '/tmp'
    CLASSES_TMP_PATH_MULTILANG = '/tmp'
    JAVA_JAR_BIN = '/tmp/topology.jar'

    LOGGER_LEVEL = logging.DEBUG
    LOGGER_NAME = 'sinfonier_api_logger'
    LOGGER_FILE = 'log/sinfonier-api.log'
    LOGGER_MAX_BYTES = 50000
    LOGGER_BACKUP_COUNT = 20

    TOPOLOGY_MESSAGE_TIMEOUT = 30
    TOPOLOGY_MAX_SPOUT_PENDING = 5
    TOPOLOGY_NUM_WORKERS = 1

    INTERNAL_MVN_REPOSITORY = os.getenv('INTERNAL_MVN_REPOSITORY','true') in ('true','1','yes')
    MVN_REPOSITORY_ID = os.getenv('MVN_REPOSITORY_ID','central')
    MVN_REPOSITORY_URL = os.getenv('MVN_REPOSITORY_URL','http://artifactory:8081/artifactory/libs-release-local')
conf = Config()
