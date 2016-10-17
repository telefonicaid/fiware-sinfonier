import os

from config.config import conf
from logger.Logger import logger
from utils.CommandExecutor import CommandExecutor


class StormProcess:
    @staticmethod
    def launch_topology_cmd(topology_name, topology_jar):
        cmd_launch = [conf.STORM_BINARY,"-c","nimbus.host="+conf.STORM_HOST, " jar ", topology_jar, " com.sinfonier.DynamicTopology ",
                      str(topology_name)]
        return CommandExecutor.execute(cmd_launch, capture_out=True)

    @staticmethod
    def compile_python_module(module_name, module_type):
        cmd_launch = ["cd", conf.CLASSES_TMP_PATH + ";", conf.JAVA_JAR_BIN, "uf", conf.SINFONIER_LAST_JAR,
                      module_type + module_name + ".class"]
        os.remove(conf.CLASSES_TMP_PATH + module_type + module_name + ".class")
        CommandExecutor.execute(cmd_launch, capture_out=True)

        cmd_launch = ["cd", conf.CLASSES_TMP_PATH + ";", conf.JAVA_JAR_BIN, "uf", conf.SINFONIER_LAST_JAR,
                      conf.CLASSES_TMP_PATH_MULTILANG + module_name.lower() + ".py"]
        os.remove(conf.CLASSES_TMP_PATH + conf.CLASSES_TMP_PATH_MULTILANG + module_name + ".class")
        return CommandExecutor.execute(cmd_launch, capture_out=True)

    @staticmethod
    def add_java_module(module_name, module_type):
        cmd_launch = ["cd", conf.CLASSES_TMP_PATH + ";", conf.JAVA_JAR_BIN, "uf", conf.SINFONIER_LAST_JAR,
                      module_type + module_name + ".class"]
        os.remove(conf.CLASSES_TMP_PATH + module_type + module_name + ".class")
        result = CommandExecutor.execute(cmd_launch, capture_out=True)
        f = open(conf.CLASSES_TMP_PATH + module_type + module_name + ".info")
        listclasses = f.read().splitlines()
        f.close()
        for classjava in listclasses:
            logger.info("Updating JAR with class " + classjava)
            cmd_launch = ["cd", conf.CLASSES_TMP_PATH + ";", conf.JAVA_JAR_BIN, "uf", conf.SINFONIER_LAST_JAR,
                          module_type + classjava.replace("$", "\$")]
            CommandExecutor.execute(cmd_launch, capture_out=True)
            os.remove(conf.CLASSES_TMP_PATH + module_type + classjava)
        os.remove(conf.CLASSES_TMP_PATH + module_type + module_name + ".info")
