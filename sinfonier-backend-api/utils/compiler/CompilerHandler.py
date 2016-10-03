import os
import re
import shutil
import tempfile
import unicodedata
import xml.etree.cElementTree as ET

from clustering.mvn.Mvn import Mvn
from config.config import conf
from error.ErrorHandler import ModuleWriteException, ModuleInvalidLanguage, ModuleErrorCompilingException, \
    TemplateNotFound, PathException, GeneratePomFileException, GenerateMainClassException
from logger.Logger import logger
from utils.CommandExecutor import CommandException


class MvnDependency(object):
    def __init__(self, group_id, artifact_id, version=None):
        self.groupId = group_id
        self.artifactId = artifact_id
        self.version = version


class PomFile(object):
    _DEFAULT_FILE = conf.POM_TEMP

    def __init__(self, file=None):
        if not file:
            file = self._DEFAULT_FILE
        elif not os.path.exists(file):
            raise PathException('We can not find this path: ' + str(file))

        ET.register_namespace('', 'http://maven.apache.org/POM/4.0.0')
        self._tree = ET.parse(file)

    def get_base(self):
        return self._tree


class Compiler(object):
    LANG_PYTHON = 'python'
    LANG_JAVA = 'java'

    TYPE_BOLT = 'bolt'
    TYPE_SPOUT = 'spout'
    TYPE_DRAIN = 'drain'

    def __init__(self, module_name, module_version, module_type, module_language, module_code, params=None, dependencies=list(),
                 install=False):
        """

        :param module_name: Module's name
        :param module_version: Module's version to compile
        :param module_type: Module's type
        :param module_language: Module's language
        :param module_code: Module's code
        :param install: if this compilation is definitive for saving with the other modules

        :raise ModuleInvalidType: if the type is invalid
        :raise ModuleInvalidLanguage: if the language is invalid.
        :raise ModuleWriteException: when something was wrong trying to create a file
        :raise ModuleErrorCompilingException: If the compilation gets some errors
        :raise ModuleWarningsCompilingException: If the compilation gets some warns
        :raise TemplateNotFound
        """

        global is_ok, log
        self.code = module_code
        self.type = module_type
        self.language = module_language
        self.install = install
        self.name = module_name
        self.version = module_version
        self.dependencies = dependencies
        self.params = params if params else dict()
        self._package = self.type + 's'
        self._is_installed = False
        self.base_path = None
        self.jar_file = None

        # create a workspace
        self.base_path = self.generate_basic_workspace()
        # code file path
        if self.language == self.LANG_PYTHON:
            file_name = os.path.join(self.base_path, conf.SRC_MULTI_LANG_BASE, self.name.lower() + '.py')
        elif self.language == self.LANG_JAVA:
            file_name = os.path.join(self.base_path, conf.SRC_BASE, self._package, self.name + '.java')
        else:
            raise ModuleInvalidLanguage()

        # security remove
        try:
            os.remove(file_name)
        except OSError:
            pass

        try:
            # write in file code
            self._write_file(file_name)

            self.generate_pom_file(base_path=self.base_path, name=self.name, version=self.version,
                                   dependencies=self.dependencies)

            if self.language == self.LANG_JAVA:
                is_ok, log = self.compile(self.base_path)
                if not is_ok:
                    raise ModuleErrorCompilingException(str(log))


        except ModuleWriteException as Ex:
            raise Ex
        except ModuleErrorCompilingException as Ex:
            raise Ex

    def remove(self):
        if self.base_path:
            self.remove_dir(self.base_path)

    def _get_target_path(self, base_path):
        if self.type == self.TYPE_BOLT:
            return os.path.normpath(os.path.join(base_path, conf.JAVA_PATH_BOLTS))
        elif self.type == self.TYPE_DRAIN:
            return os.path.normpath(os.path.join(base_path, conf.JAVA_PATH_DRAINS))
        else:
            return os.path.normpath(os.path.join(base_path, conf.JAVA_PATH_SPOUTS))

    def _write_file(self, file_name):

        try:
            if not os.path.exists(os.path.dirname(file_name)):
                os.makedirs(os.path.dirname(file_name))
            with open(file_name, 'w') as text_file:
                text_file.write(unicodedata.normalize('NFKD', self.code).encode('ascii', 'ignore'))
        except OSError as e:
            raise ModuleWriteException(e.message)

    @staticmethod
    def generate_basic_workspace(base_path=None):
        # type: (str) -> str
        if not base_path:
            base_path = tempfile.mkdtemp()
        else:
            Compiler.remove_dir(base_path)

        path_src = os.path.normpath(os.path.join(base_path, conf.SRC_BASE))
        path_multi_lang_src = os.path.normpath(os.path.join(base_path, conf.SRC_MULTI_LANG_BASE))

        try:
            # create main directories
            os.makedirs(path_src)
            os.makedirs(path_multi_lang_src)

        except TemplateNotFound as Ex:
            raise Ex
        except GenerateMainClassException as Ex:
            raise Ex
        except IOError as Ex:
            raise Ex

        return base_path

    @staticmethod
    def remove_dir(path=None):
        if os.path.exists(path):
            shutil.rmtree(path)

    @staticmethod
    def generate_pom_file(base_path, name, version, dependencies=list()):
        """
        Generate a pom file for compiling with mvn
        NOTE: the package it's only temporally
        :raise GeneratePomFileException
        :type base_path: str
        :type name: str
        :type version: int
        :type dependencies: list
        :return: path to pom.xml file
        """

        if not os.path.exists(base_path):
            raise PathException('The path: ' + base_path + ' not exists and it\'s required')

        pom = PomFile()
        pom_tree = pom.get_base()
        for child in pom_tree.getroot():
            if 'artifactId' in child.tag:
                child.text = name
                continue
            if 'version' in child.tag:
                child.text = version
                continue
            if 'name' in child.tag:
                child.text = 'Module ' + name + ' version ' + str(version)
                continue
            if 'description' in child.tag:
                child.text = 'Module' + name + ' - ' + str(version) + ' for sinfonier backend'
                continue

            if 'dependencies' in child.tag:
                for dependency in dependencies:
                    d = ET.Element('dependency')
                    g = ET.SubElement(d, 'groupId')
                    g.text = dependency.groupId
                    a = ET.SubElement(d, 'artifactId')
                    a.text = dependency.artifactId
                    if dependency.version:
                        v = ET.SubElement(d, 'version')
                        v.text = dependency.version
                    child.append(d)
                continue

        pom_file = os.path.normpath(os.path.join(base_path, 'pom.xml'))

        try:
            pom_tree.write(pom_file)
        except OSError as Ex:
            raise GeneratePomFileException(Ex.message)

        return pom_file

    @staticmethod
    def compile(base_path, quiet_mode=True):
        try:
            result = Mvn.compile_module(base_path, quiet_mode)
        except CommandException as e:
            result = e
        logger.debug('Command output: ' + str(result.log()))
        return Mvn.parse_process_result(result)

    def build_module_jar(self):
        execution = Mvn.build_module_jar(self.base_path, self.name, self.version)

        output = execution.stdout
        logger.debug('Command output: ' + str(output))
        self.jar_file = os.path.join(self.base_path, 'target', self.name + '.' + self.version + '.jar')

    def install_jar(self):
        if self.jar_file is None:
            self.build_module_jar()
        execution = Mvn.install_jar(self.base_path, self.jar_file, self.name, self.version)

        output = execution.stdout
        logger.debug('Command output: ' + str(output))
        return

    @staticmethod
    def remove_jar(base_path):
        execution = Mvn.remove_local_jar(base_path)
        output = execution.stdout
        logger.debug('Command output: ' + str(output))
        return

    @staticmethod
    def delete_module(module_name, module_version):
        base_path = Compiler.generate_basic_workspace()
        try:
            Compiler.fake_pom_file(base_path=base_path, name=module_name, version=module_version)
            Compiler.remove_jar(base_path)
        except ModuleWriteException as Ex:
            raise Ex
        except ModuleErrorCompilingException as Ex:
            raise Ex
        finally:
            if base_path is not None:
                Compiler.remove_dir(base_path)

    @staticmethod
    def fake_pom_file(base_path, name, version):
        if not os.path.exists(base_path):
            raise PathException('The path: ' + base_path + ' not exists and it\'s required')

        pom_file_base = Compiler.generate_pom_file(base_path, name, version)
        pom_file = os.path.normpath(os.path.join(base_path, 'pom.xml'))
        pom_base = PomFile(pom_file_base)
        pom_tree = pom_base.get_base()

        for child in pom_tree.getroot():
            if 'artifactId' in child.tag:
                child.text = 'delete-' + name
                continue
            if 'profiles' in child.tag:
                child.clear()
                continue
            if 'build' in child.tag:
                plugin_str = '<plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-dependency-plugin</artifactId>' \
                             '<version>2.10</version><executions><execution><id>purge-local-dependencies</id><phase>process-sources</phase>' \
                             '<goals><goal>purge-local-repository</goal></goals><configuration><excludes></excludes></configuration>' \
                             '</execution></executions></plugin>'
                plugin_element = ET.fromstring(plugin_str)
                plugins_element = child.find('plugins')
                if plugins_element:
                    plugins_element.append(plugin_element)

                child.remove(child.find('resources'))
                continue
            if 'dependencies' in child.tag:
                child.clear()
                d = ET.Element('dependency')
                g = ET.SubElement(d, 'groupId')
                g.text = 'com.sinfonier-api'
                a = ET.SubElement(d, 'artifactId')
                a.text = name
                v = ET.SubElement(d, 'version')
                v.text = version
                child.append(d)
                continue

        try:
            pom_tree.write(pom_file)
        except OSError as Ex:
            raise GeneratePomFileException(Ex.message)

        return pom_file

    @staticmethod
    def build_mvn_dependency_from_url(url):
        pattern = re.compile("http[s]?://mvnrepository\.com/artifact/([^/]*)/([^/]*)/(.*)")
        res = pattern.match(url)
        if res is not None:
            (groupId, artifactId, version) = res.groups()
            return MvnDependency(groupId, artifactId, version)
