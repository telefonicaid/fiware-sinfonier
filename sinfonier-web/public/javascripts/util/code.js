function TemplateCode(name, language, type, code) {
  var that = this;
  this.name = name;
  this.type = type;
  this.language = language;

  var __initJavaCode = function () {
    var code = '/**The MIT License (MIT) \n\nCopyright (c) ' + new Date().getFullYear() + ' sinfonier-project\n\n';
    code += 'Permission is hereby granted, free of charge, to any person obtaining a copy\n' +
      'of this software and associated documentation files (the "Software"), to deal\n' +
      'in the Software without restriction, including without limitation the rights\n' +
      'to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n' +
      'copies of the Software, and to permit persons to whom the Software is\n' +
      'furnished to do so, subject to the following conditions:\n\nThe above copyright notice and this permission notice shall be included in\n' +
      'all copies or substantial portions of the Software.\n\n' +
      'THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n' +
      'IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n' +
      'FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE\n' +
      'AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n' +
      'LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n' +
      'OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN\n' +
      'THE SOFTWARE.*/\n\n';

    if (that.type === 'bolt' || that.type === 'drain') {
      code += 'package com.sinfonier.' + that.type + 's;\n\n' +
        'public class ' + _.capitalize(_.camelCase(that.name)) + ' extends BaseSinfonier' + _.capitalize(that.type) + ' {\n' +
        '\t//TO-DO: Declare variables\n' +
        '\tprivate String var;\n' +
        '\tprivate String name;\n' +
        '\tpublic ' + _.capitalize(_.camelCase(that.name)) + ' (String xmlFile) {\n' +
        '\t\tsuper(xmlFile);\n' +
        '\t}\n\n' +
        '\t@Override\n' +
        '\tpublic void userprepare() {\n' +
        '\t\t// TO-DO: Init values. Code here runs once\n' +
        '\t\tthis.var = "value";\n' +
        '\t\t// Get Param (get value of "param_name" from input box)\n' +
        '\t\tthis.name = (String)this.getParam("param_name");\n' +
        '\t}\n\n' +
        '\t@Override\n' +
        '\tpublic void userexecute() {\n' +
        '\t\t//TO-DO: Write your code here. This code runs once by each input tuple\n' +
        '\t\t// You can use the following functions to process it\n\n' +
        '\t\t// Add field\n' +
        '\t\tthis.addField("name","Peter");\n\n' +
        '\t\t// Get field (return a String)\n' +
        '\t\tthis.getField("country");\n\n' +
        '\t\t// Remove field\n' +
        '\t\tthis.removeField(this.var);\n\n' +
        '\t\t// Exists field (return Boolean)\n' +
        '\t\tthis.existsField("lastname");\n\n';
      code += that.type === 'bolt' ? '\t\t// Mandatory. Emit the tuple to the next bolt\n\t\tthis.emit();\n' : '';
      code += '\t}\n\n' +
        '\tpublic void usercleanup() {}\n}';

      return code;

    } else {
      code += 'package com.sinfonier.' + that.type + 's;\n\n' +
        'import java.util.concurrent.LinkedBlockingQueue;\n' +
        'import org.json.JSONObject;\n' +
        'import org.quartz.Job;\n' +
        'import org.quartz.JobBuilder;\n' +
        'import org.quartz.JobDetail;\n' +
        'import org.quartz.JobExecutionContext;\n' +
        'import org.quartz.JobExecutionException;\n' +
        'import org.quartz.Scheduler;\n' +
        'import org.quartz.SchedulerContext;\n' +
        'import org.quartz.SchedulerException;\n' +
        'import org.quartz.SimpleScheduleBuilder;\n' +
        'import org.quartz.Trigger;\n' +
        'import org.quartz.TriggerBuilder;\n' +
        'import org.quartz.impl.StdSchedulerFactory;\n' +
        'import backtype.storm.utils.Utils;\n' +
        'import java.security.SecureRandom;\n' +
        'import java.math.BigInteger;\n\n' +
        'public class ' + _.capitalize(_.camelCase(that.name)) + ' extends BaseSinfonierSpout {\n\n' +
        '\tprivate String name;\n' +
        '\tprivate int age;\n' +
        '\tprivate LinkedBlockingQueue\<String\> queue = null;\n' +
        '\tprivate int frequency = 300;\n' +
        '\tprivate SecureRandom random = new SecureRandom();\n\n' +
        '\tpublic ' + _.capitalize(_.camelCase(that.name)) + ' (String spoutName, String xmlPath) {\n' +
        '\t\tsuper(spoutName, xmlPath);\n' +
        '\t}\n\n' +
        '\tpublic void useropen() {\n' +
        '\t\t// TO-DO: Init values. Code here runs once.\n' +
        '\t\t// Get params from module\n' +
        '\t\ttry {\n\t\t\tname = getParam("name");\n' +
        '\t\t\tage = Integer.parseInt(getParam("age",true));\n' +
        '\t\t\tfrequency = Integer.parseInt(getParam("frequency",true));\n' +
        '\t\t} catch (Exception e) {\n' +
        '\t\t\te.printStackTrace();\n' +
        '\t\t}\n' +
        '\t\tqueue = new LinkedBlockingQueue\<String\>(1000);\n' +
        '\t\tJobDetail job = JobBuilder.newJob(JobClass.class)\n' +
        '\t\t\t.withIdentity("dummyJobName", "group1").build();\n\n' +
        '\t\tTrigger trigger = TriggerBuilder\n' +
        '\t\t\t.newTrigger()\n' +
        '\t\t\t.withIdentity("Generate Items")\n' +
        '\t\t\t.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(frequency).repeatForever())\n' +
        '\t\t\t.build();\n' +
        '\t\tScheduler scheduler = null;\n' +
        '\t\ttry {\n\t\t\tString schedulerName = new BigInteger(130, random).toString(32);\n' +
        '\t\t\tSystem.setProperty("org.quartz.scheduler.instanceName", schedulerName);\n' +
        '\t\t\tStdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();\n' +
        '\t\t\tstdSchedulerFactory.initialize();\n' +
        '\t\t\tscheduler = stdSchedulerFactory.getScheduler();\n' +
        '\t\t} catch (Exception e) {\n' +
        '\t\t\ttry {\n' +
        '\t\t\t\tString schedulerName = new BigInteger(130, random).toString(32);\n' +
        '\t\t\t\tSystem.setProperty("org.quartz.scheduler.instanceName", schedulerName);\n' +
        '\t\t\t\tStdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();\n' +
        '\t\t\t\tstdSchedulerFactory.initialize();\n' +
        '\t\t\t\tscheduler = stdSchedulerFactory.getScheduler();\n' +
        '\t\t\t} catch (Exception u) {\n' +
        '\t\t\t\tu.printStackTrace();\n' +
        '\t\t\t\tSystem.out.println("ERROR - Second try scheduler error.");\n' +
        '\t\t\t}\n' +
        '\t\t}\n' +
        '\t\tif (scheduler != null){\n' +
        '\t\t\ttry {\n' +
        '\t\t\t\tscheduler.getContext().put("queue", queue);\n' +
        '\t\t\t\tscheduler.getContext().put("name", name);\n' +
        '\t\t\t\tscheduler.getContext().put("age", age);\n' +
        '\t\t\t\tscheduler.getContext().put("frequency", frequency);\n\n' +
        '\t\t\t\tscheduler.start();\n' +
        '\t\t\t\tscheduler.scheduleJob(job, trigger);\n' +
        '\t\t\t} catch (SchedulerException e) {\n' +
        '\t\t\t\te.printStackTrace();\n' +
        '\t\t\t}\n' +
        '\t\t}\n' +
        '\t}\n\n' +
        '\tpublic void usernextTuple(){\n' +
        '\t\tif (!queue.isEmpty()) {\n' +
        '\t\t\tString json = queue.poll();\n' +
        '\t\t\tthis.setJson(json);\n' +
        '\t\t\tthis.emit();\n' +
        '\t\t} else {\n' +
        '\t\t\tUtils.sleep(50);\n' +
        '\t\t}\n' +
        '\t}\n\n' +
        '\tpublic void userclose() {}\n\n' +
        '\tpublic static class JobClass implements Job {\n\n' +
        '\t\t@Override\n' +
        '\t\tpublic void execute(JobExecutionContext context) throws JobExecutionException {\n' +
        '\t\t\tSchedulerContext schedulerContext = null;\n\n' +
        '\t\t\tString name;\n' +
        '\t\t\tint age;\n\n' +
        '\t\t\ttry {\n' +
        '\t\t\t\tschedulerContext = context.getScheduler().getContext();\n' +
        '\t\t\t\t@SuppressWarnings("unchecked")\n' +
        '\t\t\t\tLinkedBlockingQueue\<String\> queue = (LinkedBlockingQueue\<String\>) schedulerContext\n' +
        '\t\t\t\t\t.get("queue");\n\n' +
        '\t\t\t\tname = (String) schedulerContext.get("name");\n' +
        '\t\t\t\tage = (Integer) schedulerContext.get("age");\n\n' +
        '\t\t\t\tJSONObject jobj = new JSONObject();\n' +
        '\t\t\t\tjobj.put("name",name);\n' +
        '\t\t\t\tjobj.put("age",age);\n\n' +
        '\t\t\t\tqueue.put(jobj.toString());\n\n' +
        '\t\t\t} catch (Exception e) {\n' +
        '\t\t\t\te.printStackTrace();\n' +
        '\t\t\t}\n' +
        '\t\t}\n' +
        '\t}\n}';

      return code;
    }
  };
  var __initPyCode = function () {
    var code = '#!/usr/bin/env python\n' +
      '# -*- coding: utf-8 -*-\n' +
      '"""The MIT License (MIT) \n\n' +
      'Copyright (c) ' + new Date().getFullYear() + ' sinfonier-project\n\n';

    code += 'Permission is hereby granted, free of charge, to any person obtaining a copy\n' +
      'of this software and associated documentation files (the "Software"), to deal\n' +
      'in the Software without restriction, including without limitation the rights\n' +
      'to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n' +
      'copies of the Software, and to permit persons to whom the Software is\n' +
      'furnished to do so, subject to the following conditions:\n\nThe above copyright notice and this permission notice shall be included in\n' +
      'all copies or substantial portions of the Software.\n\n' +
      'THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n' +
      'IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n' +
      'FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE\n' +
      'AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n' +
      'LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n' +
      'OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN\n' +
      'THE SOFTWARE."""\n\n';


    if (that.type === 'bolt' || that.type === 'drain') {
      code += 'import basesinfonier' + that.type + '\n\n' +
        'class ' + _.capitalize(_.camelCase(that.name)) + ' (basesinfonier' + _.capitalize(that.type) + '.BaseSinfonier' + _.capitalize(that.type) + '):\n\n' +
        '\tdef userprepare(self):\n' +
        '\t\t# TO-DO: Init values. Code here runs once\n' +
        '\t\tself.variable = "hello"\n\n' +
        '\t\t# Get Param (get value of "param_name" from input box)\n' +
        '\t\tself.name = self.getParam("param_name")\n\n' +
        '\tdef userprocess(self):\n' +
        '\t\t# TO-DO:\n' +
        '\t\t# Write your code here.\n' +
        '\t\t# This code runs once by each input tuple\n' +
        '\t\t# You can use the following functions to process it\n\n' +
        '\t\t# Add field\n' +
        '\t\tself.addField("name","Peter")\n' +
        '\t\tself.addField("name2",self.name)\n\n' +
        '\t\t# Get field (return a String)\n' +
        '\t\tself.getField("country")\n\n' +
        '\t\t# Remove field\n' +
        '\t\tself.removeField("age")\n\n' +
        '\t\t# Exists field (return bool)\n' +
        '\t\tself.existsField("lastname")\n';

      code += that.type === 'bolt' ? '\n\t\t# Mandatory: Emit the tuple to the next bolt\n\t\tself.emit()\n\n' : '\n\n';
      code += _.capitalize(_.camelCase(that.name)) + '().run()\n';

      return code;
    } else {
      code += 'from apscheduler.schedulers.background import BackgroundScheduler\n' +
        'from collections import deque\n' +
        'import time\n' +
        'import basesinfonierspout\n' +
        'import json\n\n' +
        'class ' + _.capitalize(_.camelCase(that.name)) + '(basesinfonierspout.BaseSinfonierSpout):\n' +
        '\tdef useropen(self):\n' +
        '\t\t# TO-DO: Init values. Code here runs once.\n' +
        '\t\t# Using deque as a queue\n' +
        '\t\tself.queue = deque()\n' +
        '\t\tself.frequency = int(self.getParam("frequency"))\n\n' +
        '\t\t# This scheduler launches self.job function every X seconds\n' +
        '\t\tself.sched = BackgroundScheduler()\n' +
        '\t\tself.sched.add_job(self.job, "interval", seconds=self.frequency, id="testpy")\n' +
        '\t\tself.sched.start()\n\n' +
        '\tdef usernextTuple(self):\n' +
        '\t\t# If there are items in self.queue, get the first one (.popleft()), do what you want with it and emit the tuple\n' +
        '\t\tif self.queue:\n' +
        '\t\t\tself.addField("timestamp",self.queue.popleft())\n' +
        '\t\t\tself.emit()\n\n' +
        '\tdef userclose(self):\n' +
        '\t\tpass\n\n' +
        '\t\tdef job(self):\n' +
        '\t\tself.queue.append(str(int(time.time())))';

      return code;
    }
  };

  var _initCode = function () {
    if (that.language === 'python') {
      return __initPyCode()
    } else {
      return __initJavaCode();
    }
  };

  if (code) {
    this.code = code;
  } else {
    if (!this.name || !this.type || !this.language) {
      throw new TypeError('name, type and language must be defined');
    }

    that.code = _initCode();
  }

  return this;
}