package com.sinfonier.spouts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sinfonier.exception.SinfonierException;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class BaseSinfonierSpout extends BaseRichSpout {

    private ObjectMapper mapper;
    private SpoutOutputCollector _collector;
    private static final long serialVersionUID = 1L;
    private static Logger LOG = Logger.getLogger(BaseSinfonierSpout.class);
    private Map<String, Object> json = new HashMap<String, Object>();
    private JSONObject configParams;
    private Map<String,Object> configMap;
    private Map<String,Object> mapParams;
    private String config;


    public BaseSinfonierSpout(String legacy, String config) {
        this.config = config;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public final void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        mapper = new ObjectMapper();
        _collector = collector;
        this.configParams = (JSONObject)new JSONObject(this.config).get("params");
        try {
			this.configMap = mapper.readValue(this.config,new TypeReference<Map<String, Object>>() {});
			mapParams = (Map<String,Object>) configMap.get("params");
		} catch (IOException e) {
			e.printStackTrace();
		}
        this.useropen();
    }

    public abstract void useropen();

    @Override
    public final void nextTuple() {
        this.usernextTuple();
        this.json = new HashMap<String, Object>();
    }

    public abstract void usernextTuple();

    @Override
    public final void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("map"));
    }

    @Override
    public final void close() {
        this.userclose();
    }

    public abstract void userclose();

    public final void emit() {
        String jsonstr = "";
        try {
            jsonstr = mapper.writeValueAsString(this.json);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        _collector.emit(new Values(jsonstr));
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
        	return (List<Object>) mapParams.get(param);
        } else {
            throw new SinfonierException("Param error: '"+param+"' not found.");
        }
    }

    @SuppressWarnings("unchecked")
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

    private void addNestedField(String key, Object value) {
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
        return json.containsKey(key);
    }

    public void setJson(String json) {
        try {
            this.json = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
