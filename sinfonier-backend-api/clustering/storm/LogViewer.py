import re

from requests import HTTPError

from clustering.storm.StormUI import StormUI
from bs4 import BeautifulSoup

from logger.Logger import logger


class LogViewer:

    @staticmethod
    def get_log(topology_name, start=0, length=1000):
        filenames = StormUI.getWorkersByTopologyName(topology_name)
        logs = []
        if filenames:
            logger.debug("Filenames for topology " + topology_name + " -> " + str(filenames))
            for idx, filename in enumerate(filenames):
                logger.info("Topology :" + topology_name + " - LogFilename: " + filename)
                # get log file from storm cluster
                try:

                    start_param = start[idx] if type(start) in (tuple, list) else start
                    length_param = length[idx] if type(length) in (tuple, list) else length

                    content = StormUI.getFile(filename, start_param, length_param)
                    # Remove HTML tags from Storm Log 8000 port
                    soup = BeautifulSoup(content, "lxml")

                    logcontent = soup.find("pre", {"id": "logContent"})
                    if logcontent:
                        logs.append(logcontent.getText())
                    else:
                        logs.append('');
                except HTTPError as ex:
                        logger.error(ex.message)
                        logs.append('')
                except Exception as e:
                    logs.append("Error parsing data from Storm UI:" + str(e))
        return logs

    @staticmethod
    def get_log_sizes(topology_name, length=1000):
        filenames = StormUI.getWorkersByTopologyName(topology_name)
        sizes = []
        start_pattern = re.compile("(\?|\&)start\=([^&]+)")
        length_pattern = re.compile("(\?|\&)length\=([^&]+)")
        if filenames:
            for filename in filenames:
                content = StormUI.getFile(filename, -1, length)
                try:
                    soup = BeautifulSoup(content, "lxml")
                    for link in soup.find_all('a'):
                        if link.get_text() == 'Next':
                            info = {
                                'filename': filename,
                                'start': start_pattern.search(link.get('href')).groups()[1],
                                'length': length_pattern.search(link.get('href')).groups()[1]
                            }
                            sizes.append(info)
                            break
                except Exception as e:
                    return "Error parsing data from Storm UI:" + str(e)
        return sizes

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
