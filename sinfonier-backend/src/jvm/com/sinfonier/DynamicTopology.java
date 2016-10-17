package com.sinfonier;

import backtype.storm.topology.*;
import com.sinfonier.util.JSONProperties;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

import com.sinfonier.exception.SinfonierException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;


public class DynamicTopology {

    private static final Logger LOG = Logger.getLogger(DynamicTopology.class);

    public static void main(String[] args) throws Exception {

        LOG.info("Reading JSON file configuration...");
        JSONProperties config = new JSONProperties("/topology.json");
        TopologyBuilder builder = new TopologyBuilder();

        /* Spout Configuration */
        JSONArray spouts = config.getSpouts();
        configureSpouts(builder, spouts);

        /* Bolt Configuration */
        JSONArray bolts = config.getBolts();
        configureBolts(builder, bolts);

        /* Drain Configuration */
        JSONArray drains = config.getDrains();
        configureDrains(builder, drains);

        /* Configure more Storm options */
        Config conf = setTopologyStormConfig(config.getProperties());


        if(config.getProperty("name") != null){
            StormSubmitter.submitTopology((String)config.getProperty("name"), conf, builder.createTopology());
        } else {
            conf.setDebug(true);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", conf, builder.createTopology());
            Utils.sleep(1000000); // Alive for 100 seconds = 100000 ms
            cluster.killTopology("test");
            cluster.shutdown();
        }

    }

    private static Config setTopologyStormConfig(JSONObject topologyProperties) throws ConfigurationException {

        Config conf = new Config();

        Iterator<?> keys = topologyProperties.keys();
        while(keys.hasNext()){
            String stormProperty = (String) keys.next();
            conf.put(stormProperty, topologyProperties.get(stormProperty));
        }

        return conf;
    }


    private static void configureSpouts(TopologyBuilder builder, JSONArray spouts) throws Exception {

        if (spouts == null || spouts.length() == 0) {
            throw new SinfonierException("There is no spouts. Add at least one spout.");
        }

        for (int i=0; i < spouts.length(); i++){

            JSONObject spout = spouts.getJSONObject(i);

            LOG.info("Creating spout with id:"+ spout.getString("abstractionId"));

            Object spoutInstance = new Object();
            SpoutDeclarer spoutDeclarer = null;

            try{
                spoutInstance = Class.forName(spout.getString("class"))
                        .getConstructor(String.class, String.class).newInstance("", spout.toString());

                spoutDeclarer = builder.setSpout(
                        spout.getString("abstractionId"),
                        (IRichSpout) spoutInstance, spout.getInt("parallelism"));

            } catch (Exception e){
                LOG.error(e.toString());
            }

            if (spout.has("numTasks") && spoutDeclarer != null) {
                spoutDeclarer.setNumTasks(spout.getInt("numTasks"));
            }

            if (spout.has("tickTuple") && spoutDeclarer != null) {
                spoutDeclarer.addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, spout.getInt("tickTuple"));
            }
        }

    }


    private static void configureBolts(TopologyBuilder builder, JSONArray bolts) throws Exception {

        for (int i=0; i < bolts.length(); i++) {

            JSONObject bolt = bolts.getJSONObject(i);

            LOG.info("Creating bolt with id:"+ bolt.getString("abstractionId"));

            Object boltInstance = new Object();
            BoltDeclarer boltDeclarer = null;

            try{
                boltInstance = Class.forName(bolt.getString("class")).getConstructor(String.class).newInstance(bolt.toString());

                if (boltInstance instanceof IBasicBolt){
                    boltDeclarer = builder.setBolt(
                            bolt.getString("abstractionId"),
                            (IBasicBolt) boltInstance, bolt.getInt("parallelism"));
                } else if (boltInstance instanceof IRichBolt){
                    boltDeclarer = builder.setBolt(
                            bolt.getString("abstractionId"),
                            (IRichBolt) boltInstance, bolt.getInt("parallelism"));
                }


            } catch (Exception e){
                LOG.error(e.toString());
            }

            JSONArray sources = bolt.getJSONArray("sources");
            readSourcesFromBoltsAndDrains(boltDeclarer, sources);

            if (bolt.has("numTasks") && boltDeclarer != null) {
                boltDeclarer.setNumTasks(bolt.getInt("numTasks"));
            }

            if (bolt.has("tickTuple") && boltDeclarer != null) {
                boltDeclarer.addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, bolt.getInt("tickTuple"));
            }
        }
    }


    private static void configureDrains(TopologyBuilder builder, JSONArray drains) throws Exception {

        if (drains == null || drains.length() == 0) {
            throw new SinfonierException("There is no drains. Add at least one spout.");
        }

        for (int i=0; i < drains.length(); i++) {

            JSONObject drain = drains.getJSONObject(i);

            LOG.info("Creating drain with id:"+ drain.getString("abstractionId"));

            Object drainInstance = new Object();
            BoltDeclarer drainDeclarer = null;

            try{
                drainInstance = Class.forName(drain.getString("class")).getConstructor(String.class).newInstance(drain.toString());

                if (drainInstance instanceof IBasicBolt){
                    drainDeclarer = builder.setBolt(
                            drain.getString("abstractionId"),
                            (IBasicBolt) drainInstance, drain.getInt("parallelism"));
                } else if (drainInstance instanceof IRichBolt) {
                    drainDeclarer = builder.setBolt(
                            drain.getString("abstractionId"),
                            (IRichBolt) drainInstance, drain.getInt("parallelism"));
                }

            } catch (Exception e){
                LOG.error(e.toString());
            }

            JSONArray sources = drain.getJSONArray("sources");
            readSourcesFromBoltsAndDrains(drainDeclarer, sources);

            if (drain.has("numTasks") && drainDeclarer != null) {
                drainDeclarer.setNumTasks(drain.getInt("numTasks"));
            }

            if (drain.has("tickTuple") && drainDeclarer != null) {
                drainDeclarer.addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, drain.getInt("tickTuple"));
            }
        }
    }

    private static void readSourcesFromBoltsAndDrains(BoltDeclarer boltDeclarer, JSONArray sources) {

        if (sources.length() == 0) {
            throw new SinfonierException("Error in Topology Configuration: Not sources found for bolt");
        }

        for (int i=0; i < sources.length(); i++) {

            JSONObject source = sources.getJSONObject(i);

            switch (source.getString("grouping").toLowerCase()) {

                case "shuffle":
                    if (source.has("streamId")) {
                        boltDeclarer = boltDeclarer.shuffleGrouping(source.getString("sourceId"), source.getString("streamId"));
                    } else {
                        boltDeclarer = boltDeclarer.shuffleGrouping(source.getString("sourceId"));
                    }
                    break;
                case "field":
                    String field = source.getString("field");
                    if (source.has("streamId")) {
                        boltDeclarer.fieldsGrouping(source.getString("sourceId"),source.getString("streamId"), new Fields(field));
                    } else {
                        boltDeclarer = boltDeclarer.fieldsGrouping(source.getString("sourceId"), new Fields(field));
                    }
                    break;
                case "global":
                    if (source.has("streamId")) {
                        boltDeclarer = boltDeclarer.globalGrouping(source.getString("sourceId"), source.getString("streamId"));
                    } else {
                        boltDeclarer = boltDeclarer.globalGrouping(source.getString("sourceId"));
                    }
                    break;
            }
        }
    }
}
