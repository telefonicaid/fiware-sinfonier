import os
import sys
from utils.SinfonierConstants import Environment as EnvConst

SINFONIER_API_NAME = os.environ[EnvConst.SINFONIER_ENV_KEY]

if SINFONIER_API_NAME == EnvConst.DEVELOP_ENVIRONMENT:
    from environmentConfig.Develop import *
elif SINFONIER_API_NAME == EnvConst.PROD_ENVIRONMENT:
    from environmentConfig.Production import *
elif SINFONIER_API_NAME == EnvConst.DOCKER_ENVIRONMENT:
    from environmentConfig.Docker import *
else:
    sys.exit('ERROR: Environment not found: ' + EnvConst.SINFONIER_ENV_KEY)
