package jobs;

import models.factory.SinfonierFactory;
import play.jobs.*;
import play.libs.*;

@OnApplicationStart
public class Bootstrap extends Job {
    public void doJob() {
        new Init(new SinfonierFactory())
                .now()
                .onRedeem(new F.Action<F.Promise>() {
                    @Override
                    public void invoke(F.Promise result) {
                        //
                    }
                });
    }
}

