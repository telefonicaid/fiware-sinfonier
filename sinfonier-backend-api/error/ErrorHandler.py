from falcon.http_error import HTTPError
from falcon.status_codes import HTTP_409

from apiRequests.RequestCodes import Code


class Error(Exception):
    def __init__(self, code, message):
        super(Error, self).__init__({'code': code, 'message': message})


# Mongodb's errors 1 - 999
class MongodbConnectionFail(Error):
    _CODE = 1
    _DEFAULT_MSG = 'Something was wrong trying to connect to Mongodb'

    def __init__(self, message=_DEFAULT_MSG):
        super(MongodbConnectionFail, self).__init__(self._CODE, message)


class MongodbInvalidDatabase(Error):
    _CODE = 2
    _DEFAULT_MSG = 'Invalid database name.'

    def __init__(self, message=_DEFAULT_MSG):
        super(MongodbInvalidDatabase, self).__init__(self._CODE, message)


class MongodbInvalidCollection(Error):
    _CODE = 2
    _DEFAULT_MSG = 'Invalid database name.'

    def __init__(self, message=_DEFAULT_MSG):
        super(MongodbInvalidCollection, self).__init__(self._CODE, message)


class MongodbInvalidCredentials(Error):
    _CODE = 3
    _DEFAULT_MSG = 'The user or password is invalid.'

    def __init__(self, message=_DEFAULT_MSG):
        super(MongodbInvalidCredentials, self).__init__(self._CODE, message)


# Topology's errors 1000 - 1999
class TopologyInvalidId(Error):
    _CODE = 1000
    _DEFAULT_MSG = 'The topology\'s must be defined.'

    def __init__(self, message=_DEFAULT_MSG):
        super(TopologyInvalidId, self).__init__(self._CODE, message)


# Topology's errors 1000 - 1999
class TopologyNotInCluster(Error):
    _CODE = 1001
    _DEFAULT_MSG = 'The topology\'s is not in cluster.'

    def __init__(self, message=_DEFAULT_MSG):
        super(TopologyNotInCluster, self).__init__(self._CODE, message)


# Module's errors 2000 - 2999
class ModuleException(Error):
    _CODE = 2000
    _DEFAULT_MSG = 'The module\'s must be defined.'

    def __init__(self, message=_DEFAULT_MSG):
        super(ModuleException, self).__init__(self._CODE, message)


class ModuleVersionInvalidId(Error):
    _CODE = 2001
    _DEFAULT_MSG = 'The module\'s version code must be defined.'

    def __init__(self, message=_DEFAULT_MSG):
        super(ModuleVersionInvalidId, self).__init__(self._CODE, message)


class ModuleInvalidType(Error):
    _CODE = 2002
    _DEFAULT_MSG = 'Invalid module type. Allow type are spout, bolt and drain.'

    def __init__(self, message=_DEFAULT_MSG):
        super(ModuleInvalidType, self).__init__(self._CODE, message)


class ModuleInvalidLanguage(Error):
    _CODE = 2003
    _DEFAULT_MSG = 'Invalid module type. Allow type are python and java.'

    def __init__(self, message=_DEFAULT_MSG):
        super(ModuleInvalidLanguage, self).__init__(self._CODE, message)


class ModuleErrorCompilingException(Error):
    _CODE = 2004

    def __init__(self, trace):
        super(ModuleErrorCompilingException, self).__init__(self._CODE, 'Error compiling... Trace: ' + trace)


class ModuleWarningsCompilingException(Error):
    _CODE = 2005

    def __init__(self, trace):
        super(ModuleWarningsCompilingException, self).__init__(self._CODE, 'Warnings compiling... Trace: ' + trace)


class ModuleInvalidVersion(Error):
    _CODE = 2006
    _DEFAULT_MSG = 'The module\'s version must be defined or we can\'t find the version.'

    def __init__(self, message=_DEFAULT_MSG):
        super(ModuleInvalidVersion, self).__init__(self._CODE, message)


class ModuleWriteException(Error):
    _CODE = 2007
    _DEFAULT_MSG = 'Something was wrong trying to write the module in a file.'

    def __init__(self, message=_DEFAULT_MSG):
        super(ModuleWriteException, self).__init__(self._CODE, message)


class ModuleInvalidId(Error):
    _CODE = 2008
    _DEFAULT_MSG = 'The module\'s id is required'

    def __init__(self, message=_DEFAULT_MSG):
        super(ModuleInvalidId, self).__init__(self._CODE, message)


class ModuleInvalidName(Error):
    _CODE = 2009
    _DEFAULT_MSG = 'The module\'s name is required'

    def __init__(self, message=_DEFAULT_MSG):
        super(ModuleInvalidName, self).__init__(self._CODE, message)


class GistHandlerUrlException(Error):
    _CODE = 3000
    _DEFAULT_MSG = 'The url is not form gist.github.com'

    def __init__(self, message=_DEFAULT_MSG):
        super(GistHandlerUrlException, self).__init__(self._CODE, message)


class GistHandlerIdInvalid(Error):
    _CODE = 3001
    _DEFAULT_MSG = 'No such gist found'

    def __init__(self, message=_DEFAULT_MSG):
        super(GistHandlerIdInvalid, self).__init__(self._CODE, message)


class TemplateNotFound(Error):
    _CODE = 4000
    _DEFAULT_MSG = 'Template not found'

    def __init__(self, message=_DEFAULT_MSG):
        super(TemplateNotFound, self).__init__(self._CODE, message)


class PathException(Error):
    _CODE = 4001
    _DEFAULT_MSG = 'Path not exits'

    def __init__(self, message=_DEFAULT_MSG):
        super(PathException, self).__init__(self._CODE, message)


class GeneratePomFileException(Error):
    _CODE = 4002
    _DEFAULT_MSG = 'Error trying to generated pom.xml'

    def __init__(self, message=_DEFAULT_MSG):
        super(GeneratePomFileException, self).__init__(self._CODE, message)


class GenerateMainClassException(Error):
    _CODE = 4003
    _DEFAULT_MSG = 'Error trying to generated App.java'

    def __init__(self, message=_DEFAULT_MSG):
        super(GenerateMainClassException, self).__init__(self._CODE, message)


class TopologyWriteException(Error):
    _CODE = 5001
    _DEFAULT_MSG = 'Something was wrong trying to write the topology in a file.'

    def __init__(self, message=_DEFAULT_MSG):
        super(TopologyWriteException, self).__init__(self._CODE, message)


class MissingMandatoryFields(Error):
    _CODE = 6000
    _DEFAULT_MSG = 'Missing mandatory fields in object from db'

    def __init__(self, message=_DEFAULT_MSG):
        super(MissingMandatoryFields, self).__init__(self._CODE, message)


'''
HTTP Sinfonier errors
'''
class HTTPErrorsSinfonier(HTTPError):
    def __init__(self, status, code, message=None, params=None):
        super(HTTPErrorsSinfonier, self).__init__(status=status, code=code)
        self.message = message
        self.params = params

    def to_dict(self, obj_type=dict):
        return Code(self.code, self.status, self.params, self.message)


class HTTPBadParams(HTTPErrorsSinfonier):
    def __init__(self, msg, params=None):
        super(HTTPBadParams, self).__init__(HTTP_409, 409, msg, params)
