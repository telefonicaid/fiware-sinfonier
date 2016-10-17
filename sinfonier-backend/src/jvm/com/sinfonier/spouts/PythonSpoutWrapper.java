package com.sinfonier.spouts;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.spout.ShellSpout;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.topology.IRichSpout;
import org.json.JSONObject;

public class PythonSpoutWrapper extends ShellSpout implements IRichSpout {

    private static final String PYTHON_INTERPRETER = "python"; // TBC
    private String configParamsStr;

    public PythonSpoutWrapper(String legacy, String config) {
        super(PYTHON_INTERPRETER, new JSONObject(config).getString("pyscript"));
        this.configParamsStr = new JSONObject(config).getJSONObject("params").toString();
    }

    public void open(Map stormConf, TopologyContext context, SpoutOutputCollector collector) {
        Map<String, Object> stormConfMod = new HashMap<String, Object>(stormConf);
        stormConfMod.put("sinfonier.module.params", (String)this.configParamsStr);
        super.open(stormConfMod, context, collector);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("map"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public final void close() {

        super.close();

    }
}