import re
from clustering.storm.StormUI import StormUI
from bs4 import BeautifulSoup

from logger.Logger import logger


class LogViewer:

    @staticmethod
    def get_log(topology_name, num_lines=10):
        filenames = StormUI.getWorkersByTopologyName(topology_name)
        lines = ""
        if filenames:
            logger.debug("Filenames for topology " + topology_name + " -> " + str(filenames))
            for filename in filenames:
                logger.info("Topology :" + topology_name + " - LogFilename: " + filename)
                # get log file from storm cluster
                n_lines = int(num_lines) * 100
                content = StormUI.getFile(filename, n_lines)
                try:
                    # Remove HTML tags from Storm Log 8000 port
                    logcontent = BeautifulSoup(content, "lxml").find("pre", {"id": "logContent"})
                    if logcontent:
                        lines += "\n###################################################\n"
                        (hostname, port, name) = LogViewer.parse_worker_info(filename)
                        lines += "Log from worker: " + hostname + " - " + name + "\n"
                        lines += "###################################################\n"
                        lines += logcontent.getText()
                        logger.debug("Getting " + str(len(lines.splitlines())) + " lines.")
                except Exception as e:
                    return "Error parsing data from Storm UI:" + str(e)
        return lines

    @staticmethod
    def parse_worker_info(filename):
        '''

        :param filename:
        :return:
        >>> LogViewer.parse_worker_info("http://example.com:8000?par=3sd&file=mifilename&das=adfsd")
        ('example.com', '8000', 'mifilename')
        >>> LogViewer.parse_worker_info("http://example.com:8000?par=3sd&otroparam=mifilename&das=adfsd")
        ('example.com', '8000', '')
        >>> LogViewer.parse_worker_info("invalid filename")
        ('', '', '')
       '''
        pattern = re.compile("http[s]?://([^:]*):?([0-9]+)?\?(?:[^\&]*\&)*(?:file=([^\&]*))(?:.*)")
        res = pattern.match(filename)
        if res is not None:
            return tuple(item or '' for item in res.groups())
        else:
            pattern = re.compile("http[s]?://([^:]*):?([0-9]+)?\?(?:.*)")
            res = pattern.match(filename)
            if res is not None:
                (host, port) = res.groups()
                return tuple(item or '' for item in (host, port, ''))
            else:
                return '', '', ''
