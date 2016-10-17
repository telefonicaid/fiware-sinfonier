package com.sinfonier.bolts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backtype.storm.topology.BasicOutputCollector;
import com.sinfonier.exception.SinfonierException;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import backtype.storm.Constants;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.json.JSONArray;
import org.json.JSONObject;


public abstract class BaseSinfonierBolt extends BaseBasicBolt {

    private ObjectMapper mapper;
    private BasicOutputCollector _collector;
    private static final long serialVersionUID = 1L;
    protected static Logger LOG = Logger.getLogger(BaseSinfonierBolt.class);
    private Map<String, Object> json = new HashMap<String, Object>();
    private JSONObject configParams;
    private Map<String,Object> configMap;
    private Map<String,Object> mapParams;
    private String config;
    private String rawJson;


    public BaseSinfonierBolt(String config) {
        this.config = config;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public final void prepare(Map stormConf, TopologyContext context) {
        mapper = new ObjectMapper();
        this.configParams = (JSONObject)new JSONObject(this.config).get("params");
        try {
			this.configMap = mapper.readValue(this.config,new TypeReference<Map<String, Object>>() {});
			mapParams = (Map<String,Object>) configMap.get("params");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
                this.json = mapper.readValue(input.getStringByField("map"),new TypeReference<Map<String, Object>>() {});
                this.userexecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void userexecute();

    public final void emit() {
        String jsonstr = "";
        try {
            jsonstr = mapper.writeValueAsString(this.json);
        } catch (JsonGenerationException e) {
            LOG.warn("Can't generate JSON. Failed writeValueAsString.");
            e.printStackTrace();
        } catch (JsonMappingException e) {
            LOG.warn("Can't generate JSON. JSON Mapping Exception.");
            e.printStackTrace();
        } catch (IOException e) {
            LOG.warn("Can't generate JSON. IOException.");
            e.printStackTrace();
        }

        _collector.emit(new Values(jsonstr));
    }

    @Override
    public final void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("map"));
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

    public void setJSon(Map<String, Object> json) {
        this.json = json;
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

    @SuppressWarnings("unchecked")
	public List<Object> getParamList(String param) {
    	
        if (this.configParams.has(param)){
        	return (List<Object>) mapParams.get(param);
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
        } else {
            json.remove(key);
        }
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
        return json.containsKey(key);
    }

    public Map<String, Object> getJson() {
        return json;
    }

    public String getRawJson() {
        return rawJson;
    }


}
