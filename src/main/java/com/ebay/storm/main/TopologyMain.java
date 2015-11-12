package com.ebay.storm.main;
import javax.annotation.Resource;

import com.ebay.storm.spouts.WordReader;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import com.ebay.storm.bolts.WordCounter;
import com.ebay.storm.bolts.WordNormalizer;


public class TopologyMain {
	public static void main(String[] args) throws InterruptedException {
         
        //Topology definition
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("word-reader",new WordReader());
		builder.setBolt("word-normalizer", new WordNormalizer())
			.shuffleGrouping("word-reader");
		builder.setBolt("word-counter", new WordCounter(),1)
			.fieldsGrouping("word-normalizer", new Fields("words"));
		
        //Configuration
		Config conf = new Config();
		
		conf.put("wordsFile", args[0]);
		conf.setDebug(true);
        //Topology run
		conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
		/*LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());
		Thread.sleep(1000);
		cluster.shutdown();*/
		StormSubmitter stormSubmitter = new StormSubmitter();// storm集群执行  
        try {
			stormSubmitter.submitTopology(TopologyMain.class.getSimpleName(), conf, builder.createTopology());
		} catch (AlreadyAliveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTopologyException e) {
			// TODO Auto-generated catch block
			System.out.println("InvalidTopologyException...");
			e.printStackTrace();
		}  
	}
}
