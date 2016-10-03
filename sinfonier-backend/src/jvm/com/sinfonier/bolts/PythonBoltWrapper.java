package com.sinfonier.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.ShellBolt;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PythonBoltWrapper extends ShellBolt implements IRichBolt {

    private static final String PYTHON_INTERPRETER = "python";
    private String configParamsStr;

    public PythonBoltWrapper(String config) {
        super(PYTHON_INTERPRETER, new JSONObject(config).getString("pyscript"));
        this.configParamsStr = new JSONObject(config).getJSONObject("params").toString();
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        Map<String, Object> stormConfMod = new HashMap<String, Object>(stormConf);
        stormConfMod.put("sinfonier.module.params", (String)this.configParamsStr);
        super.prepare(stormConfMod, context, collector);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("map"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

}