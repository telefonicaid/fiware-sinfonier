from datetime import datetime

from error.ErrorHandler import ModuleErrorCompilingException
from utils.CommandExecutor import CommandExecutor, ProcessResult, CommandException
from config.config import conf
import os


class Mvn:
    @staticmethod
    def parse_process_result(result):
        """
        :type result: ProcessResult
        :return: (is_ok, log)
        """

        if result.stderr:
            return False, result.stderr.split('\n')
        elif len(result.stdout) > 0 and reduce(lambda e, e1: e or e1, map(lambda line: '[ERROR]' in line, result.stdout.split('\n'))):
            return False, result.stdout.split('\n')
        else:
            return True, result.stdout.split('\n')

    @staticmethod
    def compile_module(base_path, quit_mode=False):
        """
        Clean and compile
        :param base_path: base path file
        :param quit_mode: it's like put -q to mvn
        :return: ProcessResult
        """
        pom_file = os.path.normpath(os.path.join(base_path, 'pom.xml'))
        cmd_launch = conf.MAVEN_BINARY + ' -q ' if quit_mode else ''
        cmd_launch += ' -Dstorm.version=' + conf.STORM_VERSION
        cmd_launch += ' -f ' + pom_file + ' clean compile'
        try:
            return CommandExecutor.execute(cmd_launch, capture_out=True)
        except CommandException as Ex:
            raise ModuleErrorCompilingException(Ex.stderr + Ex.stdout)

    @staticmethod
    def build_topology_jar(base_path, quit_mode=False):
        """
        Build complete jar with dependencies
        :param base_path: base path file
        :param quit_mode: it's like put -q to mvn
        :return: ProcessResult
        """
        pom_file = os.path.normpath(os.path.join(base_path, 'pom.xml'))
        cmd_launch = conf.MAVEN_BINARY + (' -q ' if quit_mode else '')
        cmd_launch += ' -Dstorm.version=' + conf.STORM_VERSION
        cmd_launch += ' -f ' + pom_file + ' clean compile assembly:single'
        cmd_launch = [cmd_launch]
        if conf.INTERNAL_MVN_REPOSITORY:
            cmd_launch.append('-DrepositoryId='+ conf.MVN_REPOSITORY_ID)
            cmd_launch.append('-Durl=' + conf.MVN_REPOSITORY_URL)

        return CommandExecutor.execute(cmd_launch, capture_out=True)

    @staticmethod
    def build_module_jar(base_path, module_name, version):
        cmd_launch = [conf.MAVEN_BINARY, ' -f ', os.path.normpath(os.path.join(base_path,'pom.xml')), 'package',
                      '-Djar.finalName=' + module_name + "." + version
                      , '-Dstorm.version=' + conf.STORM_VERSION]
        result = CommandExecutor.execute(cmd_launch, capture_out=True)
        result.log()
        return result

    @staticmethod
    def install_jar(base_path, file_name, module, version):
        target = 'deploy:deploy-file' if conf.INTERNAL_MVN_REPOSITORY else 'install:install-file'
        cmd_launch = [conf.MAVEN_BINARY, " -f ", os.path.normpath(os.path.join(base_path,'pom.xml')), target, '-Dfile=' + file_name,
                      '-DgroupId=com.sinfonier',
                      '-DartifactId=' + module, '-Dversion=' + version, '-Dpackaging=jar'
                    , '-Dstorm.version=' + conf.STORM_VERSION]
        if conf.INTERNAL_MVN_REPOSITORY:
            cmd_launch.append('-DrepositoryId='+ conf.MVN_REPOSITORY_ID)
            cmd_launch.append('-Durl=' + conf.MVN_REPOSITORY_URL)
        result = CommandExecutor.execute(cmd_launch, capture_out=True)
        result.log()
        return result

    @staticmethod
    def remove_local_jar(base_path):
        cmd_launch = [conf.MAVEN_BINARY, " -f ", os.path.normpath(os.path.join(base_path,'pom.xml')),
                        'dependency:purge-local-repository',
                        '-DactTransitively=false',
                        '-DreResolve=false']
        result = CommandExecutor.execute(cmd_launch, capture_out=True)
        result.log()
        return result

    @staticmethod
    def build_topology(workingPath, onlyCompile=False):
        cmd_launch = [conf.MAVEN_BINARY, "-f", workingPath + 'pom.xml', 'clean', 'compile', 'install']
        return CommandExecutor.execute(cmd_launch, capture_out=True)

    @staticmethod
    def compile_topology(topology_name):
        cmd_launch = [conf.MAVEN_BINARY, "-f", conf.WORKING_PATH + topology_name + '/pom.xml', 'compile']
        return CommandExecutor.execute(cmd_launch, capture_out=True)
