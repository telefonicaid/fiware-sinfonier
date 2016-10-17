import logging.handlers
import os

import sys

from config.config import conf
from utils.SinfonierConstants import Environment as EnvConst

logger = logging.getLogger(conf.LOGGER_NAME)
logger.setLevel(conf.LOGGER_LEVEL)
formatter = logging.Formatter('%(asctime)s::[%(levelname)s] - %(message)s')

if conf.LOGGER_FILE:
    rotatingFileHandler = logging.handlers.RotatingFileHandler(conf.LOGGER_FILE, maxBytes=conf.LOGGER_MAX_BYTES,
                                                               backupCount=conf.LOGGER_BACKUP_COUNT)
    rotatingFileHandler.setFormatter(formatter)
    logger.addHandler(rotatingFileHandler)

if os.environ[EnvConst.SINFONIER_ENV_KEY] == EnvConst.DEVELOP_ENVIRONMENT or not conf.LOGGER_FILE:
    consoleHandler = logging.StreamHandler(sys.stdout)
    consoleHandler.setFormatter(formatter)
    logger.addHandler(consoleHandler)
