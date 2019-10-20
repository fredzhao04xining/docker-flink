package com.flink.demo;

import org.apache.flink.api.common.functions.FlatMapFunction;

import org.apache.flink.api.java.DataSet;

import org.apache.flink.api.java.ExecutionEnvironment;

import org.apache.flink.api.java.aggregation.Aggregations;

import org.apache.flink.api.java.tuple.Tuple2;

import org.apache.flink.api.java.utils.ParameterTool;

import org.apache.flink.util.Collector;

public class ReadLog {
    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(params);

//        DataSet<String> text = env.readTextFile("/Users/fred/Downloads/access_log");
        DataSet<String> text = env.readTextFile(params.get("input"));

        DataSet<Tuple2<String, Integer>> counts = text.flatMap(new MySplitter())
                .groupBy(0).aggregate(Aggregations.SUM, 1);// group by the tuple field "0" and sum up tuple field "1"
        counts.print();

    }


    public static class MySplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            String[] tokens = value.split("\"");
            String[] lines = tokens[1].split(" ");
            if(lines.length > 1){
                out.collect(new Tuple2<String, Integer>(lines[0] + " " + lines[1], 1));
            }
        }
    }

}
