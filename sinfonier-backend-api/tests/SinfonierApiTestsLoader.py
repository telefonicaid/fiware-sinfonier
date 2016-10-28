# coding: utf8

import os
import sys
import unittest

_abspath = os.path.abspath(__file__)
_dname = os.path.dirname(_abspath)
_workspace = os.path.normpath(os.path.join(_dname, '..'))

if sys.path.__contains__(_dname):
    sys.path.remove(_dname)

if not sys.path.__contains__(_workspace):
    sys.path.insert(0, _workspace)

from utils.SinfonierConstants import Environment as Env

os.environ[Env.SINFONIER_ENV_KEY] = os.getenv(Env.SINFONIER_ENV_KEY, Env.TEST_ENVIRONMENT)

suite = unittest.TestLoader().discover('.', pattern="*Tests.py", top_level_dir=_workspace)
unittest.TextTestRunner(verbosity=2).run(suite)
