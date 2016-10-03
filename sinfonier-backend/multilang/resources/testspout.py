#!/usr/bin/env python
# -*- coding: utf-8 -*-


from apscheduler.schedulers.background import BackgroundScheduler
from collections import deque
import basesinfonierspout
import json
import time
import datetime

class DummyListSpout(basesinfonierspout.BaseSinfonierSpout):

    def useropen(self):

        # Using deque as a queue
        self.queue = deque()

        self.listfield = self.getParam("listfield")
        self.items = self.getParam("items")
        self.frequency = int(self.getParam("frequency"))

        # This scheduler launches self.job function every X minutes
        self.sched = BackgroundScheduler()
        startime = datetime.datetime.now() + datetime.timedelta(seconds=30)
        self.sched.add_job(self.job, "interval", minutes=self.frequency, id="spoutlistpy", next_run_time=startime)
        self.sched.start()

    def usernextTuple(self):

        # If there are items in self.queue, get the first one (.popleft()), do what you want with it and emit the tuple
        if self.queue:
            self.d = self.queue.popleft()
            self.emit()
        else:
            time.sleep(0.05)


    def job(self):

        try:
            if isinstance(self.items, str):
                self.items = [self.items]
            jsemit = {self.listfield : self.items}
            self.queue.append(jsemit)

        except Exception, e:
            self.log("[SpoutList] Exception: " + str(e))


DummyListSpout().run()
