package com.sinfonier.bolts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
 * Conditional Bolt. Divide the default stream depending on condition.
 * <p>
 * XML Options:<br/>
 * <ul>
 * <li><b>{@code <sources> <source> <sourceId></sourceId> 
 * <grouping field="field"></grouping> </source> ... </sources>}</b> - Needed.
 * Sources where this bolt must receive tuples.</li>
 * <li><b>{@code <url></url>}</b> -Needed. Url from RSS you want retrieve.</li>
 * <li><b>{@code <field></field>}</b> - Needed. Field of entity where we can
 * find full URL.</li>
 * <li><b>{@code <operator></operator>}</b> - Needed. Operator to compare field
 * and value.</li>
 * <li><b>{@code <value></value>}</b> - Needed. Value to be compared to.</li>
 * <li><b>{@code <entity></entity>}</b> - Optional. Entity, in case you want to
 * change it.</li>
 * <li><b>{@code <numTasks></numTasks>}</b> - Needed. Num tasks of this bolt.
 * </li>
 * <li><b>{@code <paralellism>1</paralellism>}</b> - Needed. Parallelism.</li>
 * </ul>
 */
// @formatter:on
public class ConditionalFields extends BaseBasicBolt {

	private static final long serialVersionUID = -2124724236957307080L;
	private ObjectMapper mapper;
	private BasicOutputCollector _collector;

	protected static Logger LOG = Logger.getLogger(BaseSinfonierBolt.class);
	private Map<String, Object> json = new HashMap<String, Object>();
	private JSONObject configParams;
	private String config;
	private Map<String, Object> configMap;
	private Map<String, Object> mapParams;

	private String field;
	private String operator;
	private String field2;

	private String entity;

	public ConditionalFields(String config) {
		this.config = config;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		mapper = new ObjectMapper();
		this.configParams = (JSONObject) new JSONObject(this.config).get("params");
        try {
			this.configMap = mapper.readValue(this.config,new TypeReference<Map<String, Object>>() {});
			mapParams = (Map<String,Object>) configMap.get("params");
		} catch (IOException e) {
			e.printStackTrace();
		}

		field = getParam("field1", true);
		operator = getParam("operator", true);
		field2 = getParam("field2", true);
		entity = getParam("entity");

	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		_collector = collector;
		if (isTickTuple(input)) {

		} else {
			try {
				this.json = mapper.readValue(input.getStringByField("map"), new TypeReference<Map<String, Object>>() {
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		boolean comparisonResult = doComparation();
		String jsonstr = "";
		try {
			jsonstr = mapper.writeValueAsString(this.json);
		} catch (Exception e) {
			e.printStackTrace();
		}

		_collector.emit(comparisonResult ? "yes":"no", new Values(jsonstr));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("yes", new Fields( "map"));
		declarer.declareStream("no", new Fields( "map"));
	}

	private boolean doComparation() {
		boolean result = false;
		Object jsonField = null;
		String jsonField2 = "";
		try {
			jsonField = getField(this.field);
			jsonField2 = (String) getField(this.field2);
		} catch (Exception e) {
			System.out.println("Field not found on json map received. Check bolt params");
		}
		if (jsonField == null) {
			return false;
		}

		switch (operator) {
		case "<":
			result = compare(jsonField, jsonField2) < 0 ? true : false;
			break;
		case "<=":
			result = compare(jsonField, jsonField2) <= 0 ? true : false;
			break;
		case ">":
			result = compare(jsonField, jsonField2) > 0 ? true : false;
			break;
		case ">=":
			result = compare(jsonField, jsonField2) >= 0 ? true : false;
			break;
		case "==":
			result = jsonField.equals(jsonField2) ? true : false;
			break;
		case "!=":
			result = jsonField.equals(jsonField2) ? false : true;
			break;
		case "RegexExpression":
			result = Pattern.matches(String.valueOf(jsonField), (CharSequence) jsonField2);
			break;
		}

		return result;
	}

	public int compare(Object field, String value) {
		return new Double(String.valueOf(field)).compareTo(Double.parseDouble(value));
	}

	public String getMandatoryParam(String param) {
		if (this.configParams.has(param)) {
			return (String) this.configParams.get(param);
		} else {
			throw new SinfonierException("Param error: '" + param + "' not found.");
		}
	}

	public String getParam(String param) {
		return (String) this.mapParams.get(param);
	}

	public String getParam(String param, boolean checkNotNull) {
		return checkNotNull ? this.getMandatoryParam(param) : getParam(param);
	}

	public Object getField(String key) {
		if (key.indexOf(".") >= 0) {
			return getNestedField(key);
		}
		return json.get(key);
	}

	private static boolean isTickTuple(Tuple tuple) {
		return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
				&& tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
	}

	@SuppressWarnings("unchecked")
	private Object getNestedField(String key) {
		String[] parts = key.split("\\.");
		Map<String, Object> value = null;
		for (int i = 0; i < parts.length - 1; i++) {
			value = (Map<String, Object>) json.get(parts[i]);
		}
		return value.get(parts[parts.length - 1]);
	}
}
