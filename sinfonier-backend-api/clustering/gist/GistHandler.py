import simplegist
from config.config import *
from error.ErrorHandler import GistHandlerUrlException, GistHandlerIdInvalid
from logger.Logger import logger


class GistHandler:
    @staticmethod
    def get_code(gistid):
        """
        Get code from gist.github.com given a Gist ID
        >>> GistHandler.get_code('477f340d7a21074de2cff9e63454364f')
        u'testing api - - -'
        """

        try:
            logger.debug("gist preparation")
            gist = simplegist.Simplegist(username=conf.GIST_USERNAME, api_token=conf.GIST_TOKEN)
            code = gist.profile().content(id=gistid)
            logger.debug("gist obtained")
        except Exception:
            raise GistHandlerIdInvalid()

        return code

    @staticmethod
    def get_code_from_url(url):
        """
        Get code from gist.github.com given a Gist ID
        >>> GistHandler.get_code_from_url('https://gist.github.com/ffr4nz/6d0a0e825c033e4e5cbc')
        u'testing api - - -'
        """

        if not GistHandler.is_from_gist(url):
            raise GistHandlerUrlException()

        id = url.split("/")[-1]
        return GistHandler.get_code(id)

    @staticmethod
    def is_from_gist(url):
        return True if 'gist.github.com' in url else False
