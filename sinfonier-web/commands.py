# -*- coding: utf-8 -*-

import sys

MODULE = 'sinfonier'

COMMANDS = ['sinfonier:']

HELP = {
    "sinfonier:": "Show help for the Sinfonier-web"
}


def execute(**kargs):
    command = kargs.get('command')
    app = kargs.get('app')
    args = kargs.get('args')
    env = kargs.get('env')

    sys.exit(0)
