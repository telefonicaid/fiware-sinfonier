import os


def running_server():
    from config.Routes import Routes
    _routes = Routes()
    return _routes.api


if __name__ == '__main__':
    import argparse
    from utils.SinfonierConstants import Environment as EnvConst

    parser = argparse.ArgumentParser(description='Sinfonier API configuration')
    parser.add_argument('--production', action='store_true')
    parser.add_argument('--docker', action='store_true')
    args = parser.parse_args()


    if args.production:
        os.environ[EnvConst.SINFONIER_ENV_KEY] = EnvConst.PROD_ENVIRONMENT
    elif args.docker:
        os.environ[EnvConst.SINFONIER_ENV_KEY] = EnvConst.DOCKER_ENVIRONMENT
    else:
        os.environ[EnvConst.SINFONIER_ENV_KEY] = EnvConst.DEVELOP_ENVIRONMENT

    from config.config import conf
    from config.Routes import Routes
    from logger.Logger import logger

    routes = Routes()
    api = routes.api

    from wsgiref import simple_server

    httpd = simple_server.make_server(conf.SINFONIER_API_HOST, conf.SINFONIER_API_PORT, api)
    logger.info('Server up! running in ' + conf.SINFONIER_API_HOST + ':' + str(conf.SINFONIER_API_PORT))
    logger.info(os.environ[EnvConst.SINFONIER_ENV_KEY].upper() + ' mode')
    httpd.serve_forever()
else:
    from utils.SinfonierConstants import Environment
    os.environ[Environment.SINFONIER_ENV_KEY] = Environment.PROD_ENVIRONMENT
    app = running_server()
