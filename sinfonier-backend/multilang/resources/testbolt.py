import basesinfonierbolt

class AddTimestamp(basesinfonierbolt.BaseSinfonierBolt):

    def userprepare(self):

        pass

    def userprocess(self):

        self.log("timestamp-111111111")
        self.log(self.getParam("key"))
        self.emit()

AddTimestamp().run()

