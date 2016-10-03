package com.sinfonier.bolts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;

import com.sinfonier.exception.SinfonierException;

import backtype.storm.Constants;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

//@formatter:off
/**
* Exists Bolt. Divide the default stream depending on existence of field.
* <p> XML Options:<br/>
* <ul>
* <li> <b>{@code <sources> <source> <sourceId></sourceId> <grouping field="field"></grouping> </source> ... </sources>}</b> - Needed. Sources where this bolt must receive tuples. </li>
* <li> <b>{@code <url></url>}</b> - Needed. Url from RSS you want retrieve.</li>
* <li> <b>{@code <field></field>}</b> - Needed. Field of entity where we can find full URL. </li>
* <li> <b>{@code <entity></entity>}</b> - Optional. Entity, in case you want to change it. </li>
* <li> <b>{@code <numTasks></numTasks>}</b> - Needed. Num tasks of this bolt.</li>
* <li> <b>{@code <paralellism>1</paralellism>}</b> - Needed. Parallelism.</li>
* </ul>
*/
//@formatter:on
public class Exists extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;
    private ObjectMapper mapper;
    private BasicOutputCollector _collector;
    
    protected static Logger LOG = Logger.getLogger(BaseSinfonierBolt.class);
    private Map<String, Object> json = new HashMap<String, Object>();
    private JSONObject configParams;
    private String config;
    private Map<String,Object> configMap;
    private Map<String,Object> mapParams;

    private String field;
    private String entity;

    
    public Exists(String config) {
        this.config = config;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
    	mapper = new ObjectMapper();
        this.configParams = (JSONObject)new JSONObject(this.config).get("params");
        try {
			this.configMap = mapper.readValue(this.config,new TypeReference<Map<String, Object>>() {});
			mapParams = (Map<String,Object>) configMap.get("params");
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        field = getParam("field", true);
    }

    @Override
    public void execute(Tuple input,BasicOutputCollector collector) {
    	_collector = collector;
    	if (isTickTuple(input)) {
            
        } else {
        try {
            this.json = mapper.readValue(input.getStringByField("map"),
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        }

        boolean existResult = (json.get(field) != null);

        String jsonstr = "";
        try {
            jsonstr = mapper.writeValueAsString(this.json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        _collector.emit(existResult ? "yes":"no", new Values(jsonstr));
     }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("yes", new Fields( "map"));
        declarer.declareStream("no", new Fields("map"));
    }
    
    public String getMandatoryParam(String param) {
        if (this.configParams.has(param)){
            return (String)this.configParams.get(param);
        } else {
            throw new SinfonierException("Param error: '"+param+"' not found.");
        }
    }

    public String getParam(String param) {
    	return (String)this.mapParams.get(param);
    }
    public String getParam(String param, boolean checkNotNull) {
    	return checkNotNull ? this.getMandatoryParam(param) : getParam(param);
    }

    private static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

}
