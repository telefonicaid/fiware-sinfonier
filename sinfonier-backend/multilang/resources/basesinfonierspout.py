#!/usr/bin/env python
# -*- coding: utf-8 -*-

import storm
import json

class BaseSinfonierSpout(storm.Spout):

    def log(self, msg):

        storm.log(msg)

    def getParam(self, param):

        return self.config[param] if param in self.config.keys() else ""

    def initialize(self, conf, context):

        self.d = dict()
        self.config = json.loads(conf["sinfonier.module.params"])
        self.useropen()

    def emit(self):

        storm.emit([json.dumps(self.d)])

    def usernextTuple(self):

        raise NotImplementedError("update: This method must be implemented in your class")

    def useropen(self):

        raise NotImplementedError("update: This method must be implemented in your class")

    def nextTuple(self):

        self.usernextTuple()
        self.d = dict()

    def addField(self, s, o):
        self.d[s] = o

    def getField(self, s):
        return self.getNestedField(s) if "." in s else self.d[s]

    def removeField(self, s):
        del self.d[s]

    def getNestedField(self, s):

        value = self.d.copy()
        for part in s.split("."):
            if type(value[part]) == "dict":
                value = json.loads(value[part])
            else:
                value = value[part]
        return value

    def existsField(self, s):
        return True if s in self.d.keys() else None
