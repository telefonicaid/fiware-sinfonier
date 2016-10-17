import falcon

from actions.ApiBase import ApiBase
from apiRequests.ResponsesHandler import ResponsesHandler
from clustering.mongo.MongoHandler import MongodbFactory
from error.ErrorHandler import Error, ModuleVersionInvalidId, MissingMandatoryFields
from logger.Logger import logger
from utils.SinfonierConstants import ModuleVersions as ModuleVersionConst, Module as ModuleConst
from utils.compiler.CompilerHandler import Compiler


class DeleteModule(ApiBase):
    @falcon.before(ApiBase.validate_params)
    def on_delete(self, req, res, id, version_id):
        module_id = self.validated_params['id']
        module_version_id = self.validated_params['version_id']
        compiler = None
        try:
            module = MongodbFactory.get_module(module_id)
            module_version = MongodbFactory.get_module_version(module_version_id)

            if not module or not module_version:
                return ResponsesHandler.handle_404(res, 'Module not found')

            m_name = module[ModuleConst.FIELD_NAME]
            m_version = module_version[ModuleVersionConst.FIELD_VERSION_CODE]
            compiler = Compiler.delete_module(m_name, m_version)

        except ModuleVersionInvalidId as Ex:
            logger.error(Ex.message)
            return ResponsesHandler.handle_404(res, msg=Ex.message)
        except MissingMandatoryFields as Ex:
            logger.error(Ex.message)
            return ResponsesHandler.handle_409(res)
        except Error as Ex:
            logger.error(Ex.message)
            return ResponsesHandler.handle_400(res)
        except Exception as e:
            logger.critical(e.message)
            return ResponsesHandler.handle_500(res)
        finally:
            if compiler:
                compiler.remove()

        return ResponsesHandler.handle_200(res)
