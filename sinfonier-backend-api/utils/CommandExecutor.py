import subprocess

from logger.Logger import logger


class CommandExecutor:
    @staticmethod
    def execute(command, timeout=None, capture_out=True):
        """
        Executes a command shell process
        :param command: execution command. Can be a string with the full command or a list with the command and arguments
        :param timeout:
        :param capture_out: If true, the stdout and stderr are returned in result object
        :return: subprocess.CompletedProcess: it has stdout and stderr as strings, and result_code
        >>> CommandExecutor.execute('echo AAA').stdout
        'AAA\\n'
        """

        cmd = ' '.join(command) if isinstance(command, list) else command
        process = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        output, err = process.communicate()
        if process.returncode != 0:
            raise CommandException(output, err, process.returncode)

        return ProcessResult(output, err, process.returncode)


class ProcessResult:
    def __init__(self, output, err, return_code):
        self.stdout = output
        self.stderr = err
        self.return_code = return_code

    def log(self):
        logger.info(self.stdout)
        if self.stderr:
            logger.error(self.stderr)


class CommandException(Exception):
    def __init__(self, output, err, return_code):
        super(CommandException, self).__init__("Command executed with error " + str(return_code))
        self.stdout = output
        self.stderr = err
        self.return_code = return_code

    def log(self):
        logger.info(self.stdout)
        if self.stderr:
            logger.error(self.stderr)
