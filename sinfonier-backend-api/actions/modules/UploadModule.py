import falcon

from actions.ApiBase import ApiBase
from apiRequests.ResponsesHandler import ResponsesHandler
from clustering.gist.GistHandler import GistHandler
from clustering.mongo.MongoHandler import MongodbFactory
from error.ErrorHandler import Error, ModuleVersionInvalidId, ModuleException, ModuleErrorCompilingException, \
    MissingMandatoryFields
from logger.Logger import logger
from utils.CommandExecutor import CommandException
from utils.SinfonierConstants import ModuleVersions as ModuleVersionConst, Module as ModuleConst
from utils.compiler.CompilerHandler import Compiler


class UploadModule(ApiBase):
    @falcon.before(ApiBase.validate_params)
    def on_post(self, req, res, id, version_id):
        module_id = self.validated_params['id']
        module_version_id = self.validated_params['version_id']
        compiler = None
        try:
            module = MongodbFactory.get_module(module_id)
            module_version = MongodbFactory.get_module_version(module_version_id)

            if not module_version or not module:
                return ResponsesHandler.handle_404(res, 'Module not found')

            if not module_version[ModuleVersionConst.FIELD_SOURCE_TYPE]:
                raise ModuleException('The module\'s ' + ModuleVersionConst.FIELD_SOURCE_TYPE + ' is mandatory.')

            if module_version[ModuleVersionConst.FIELD_SOURCE_TYPE].strip().lower() == ModuleVersionConst.SOURCE_TYPE_GIST:
                code = GistHandler.get_code_from_url(module_version[ModuleVersionConst.FIELD_SOURCE_CODE_URL])
                result = MongodbFactory.save_module_source(module_version_id, code)
                if result.matched_count == 0:
                    return ResponsesHandler.handle_404(res, 'Module not found')
                else:
                    module_version[ModuleVersionConst.FIELD_SOURCE_CODE] = code

            m_name = module[ModuleConst.FIELD_NAME]
            m_type = module.get(ModuleConst.FIELD_TYPE)

            if not m_type and not module['container']:
                raise ModuleException('We can not found the module\'s type')
            elif not m_type:
                m_type = module['container'][ModuleConst.FIELD_TYPE]

            m_lang = module[ModuleConst.FIELD_LANG].lower()
            m_code = module_version[ModuleVersionConst.FIELD_SOURCE_CODE]
            m_version = str(module_version[ModuleVersionConst.FIELD_VERSION_CODE])
            m_libraries = module_version.get(ModuleVersionConst.FIELD_LIBRARIES)

            if m_libraries is not None:
                dependencies = list(
                    map(Compiler.build_mvn_dependency_from_url, [l[ModuleVersionConst.FIELD_LIBRARY_URL] for l in m_libraries]))
            else:
                dependencies = list()

            compiler = Compiler(m_name, m_version, m_type, m_lang, m_code, dependencies=dependencies)
            MongodbFactory.update_status_builder_module(module_version_id, ModuleVersionConst.BUILD_STATUS_SUCCESS)
            MongodbFactory.update_module_last_modify(module_id)

        except ModuleVersionInvalidId as Ex:
            logger.error(Ex.message)
            return ResponsesHandler.handle_404(res, msg=Ex.message)
        except MissingMandatoryFields as Ex:
            logger.error(Ex.message)
            return ResponsesHandler.handle_409(res)
        except ModuleErrorCompilingException as Ex:
            logger.error(Ex.message)
            MongodbFactory.update_status_builder_module(module_version_id, ModuleVersionConst.BUILD_STATUS_FAILURE)
            MongodbFactory.update_module_last_modify(module_id)
            return ResponsesHandler.handle_400(res)
        except CommandException as Ex:
            logger.error(Ex.log())
            return ResponsesHandler.handle_400(res)
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
