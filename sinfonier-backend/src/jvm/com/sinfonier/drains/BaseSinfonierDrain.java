package com.sinfonier.drains;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backtype.storm.topology.BasicOutputCollector;
import com.sinfonier.exception.SinfonierException;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import backtype.storm.Constants;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import org.json.JSONArray;
import org.json.JSONObject;


public abstract class BaseSinfonierDrain extends BaseBasicBolt {

    private ObjectMapper mapper;
    private BasicOutputCollector _collector;
    private static final long serialVersionUID = 1L;
    protected static Logger LOG = Logger.getLogger(BaseSinfonierDrain.class);
    private Map<String, Object> json = new HashMap<String, Object>();
    private String rawJson;
    private JSONObject configParams;
    private String config;


    public BaseSinfonierDrain(String config) {
        this.config = config;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public final void prepare(Map stormConf, TopologyContext context) {
        mapper = new ObjectMapper();
        this.configParams = (JSONObject)new JSONObject(this.config).get("params");
        this.userprepare();
    }

    public abstract void userprepare();

    @Override
    public final void execute(Tuple input, BasicOutputCollector collector) {

        _collector = collector;

        if (isTickTuple(input)) {
            tickTupleCase();
        } else {
            try {
                this.rawJson = input.getStringByField("map");
                this.json = mapper.readValue(rawJson, new TypeReference<Map<String, Object>>() {});
                this.userexecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void userexecute();

    @Override
    public final void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public final void cleanup() {
        this.usercleanup();
    }

    public abstract void usercleanup();

    public void tickTupleCase() {
    }

    private static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

    public String getParam(String param) {
        if (this.configParams.has(param)){
            return (String)this.configParams.get(param);
        } else {
            throw new SinfonierException("Param error: '"+param+"' not found.");
        }
    }

    public String getParam(String param, boolean checkNotNull) {
        return this.getParam(param);
    }

    public List<Object> getParamList(String param) {
        if (this.configParams.has(param)){
            JSONArray jsonArray = this.configParams.getJSONArray(param);
            List<String> list = new ArrayList<String>();
            for (int i=0; i<jsonArray.length(); i++) {
                list.add( jsonArray.getString(i) );
            }
            return (List)list;
        } else {
            throw new SinfonierException("Param error: '"+param+"' not found.");
        }
    }


    public List<Map<String, String>> getComplexProperty(String param) {
        if (this.configParams.has(param)){
            JSONArray jsonArray = this.configParams.getJSONArray(param);
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject item = (JSONObject) jsonArray.get(i);
                Map<String, String> itemToMap = null;
                try {
                    itemToMap = (Map<String, String>)mapper.readValue(item.toString(), HashMap.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.add(itemToMap);

            }
            return (List<Map<String, String>>)list;
        } else {
            throw new SinfonierException("Param error: '"+param+"' not found.");
        }
    }

    public void addField(String key, Object value) {
        if (key.indexOf(".") >= 0) {
            addNestedField(key, value);
        } else {
            json.put(key, value);
        }
    }

    public void addNestedField(String key, Object value) {
        String[] parts = key.split("\\.");
        Map<String, Object> jsonCopy = json;
        for (int i = 0; i < parts.length - 1; i++) {
            if (jsonCopy.get(parts[i]) == null) {
                jsonCopy.put(parts[i], new HashMap<String, Object>());
            }
            jsonCopy = (Map<String, Object>) jsonCopy.get(parts[i]);
        }
        jsonCopy.put(parts[parts.length - 1], value);
    }

    public Object getField(String key) {
        if (key.indexOf(".") >= 0) {
            return getNestedField(key);
        }
        return json.get(key);
    }

    private Object getNestedField(String key) {
        String[] parts = key.split("\\.");
        Map<String, Object> value = json;
        for (int i = 0; i < parts.length - 1; i++) {
            value = (Map<String, Object>) value.get(parts[i]);
        }
        return value.get(parts[parts.length - 1]);
    }

    public void removeField(String key) {
        if (key.indexOf(".") >= 0) {
            removeNestedField(key);
        }
        json.remove(key);
    }

    private Object removeNestedField(String key) {
        String[] parts = key.split("\\.");
        Map<String, Object> value = json;
        for (int i = 0; i < parts.length - 1; i++) {
            value = (Map<String, Object>) value.get(parts[i]);
        }
        return value.remove(parts[parts.length - 1]);
    }

    public boolean existsField(String key) {
        return json.containsKey((String) key);
    }

    public Map<String, Object> getJson() {
        return json;
    }

    public String getRawJson() {
        return rawJson;
    }

    public static Logger getLog() {
        return LOG;
    }

}
