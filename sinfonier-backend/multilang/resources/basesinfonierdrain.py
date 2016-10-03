#!/usr/bin/env python
# -*- coding: utf-8 -*-

import storm
import json

class BaseSinfonierDrain(storm.BasicBolt):

    def log(self, msg):

        storm.log(msg)

    def getParam(self, param):

        return self.config[param] if param in self.config.keys() else ""

    def initialize(self, stormconf, context):

        self.d = dict()
        self.config = json.loads(stormconf["sinfonier.module.params"])
        self.userprepare()

    def userprocess(self):

        raise NotImplementedError("update: This method must be implemented in your class")

    def userprepare(self):

        raise NotImplementedError("update: This method must be implemented in your class")

    def process(self, tup):

        self.lastuple = tup
        self.d = json.loads(tup.values[0])

        self.userprocess()

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
