package test;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import java.util.*;

public class Datafilter {
    private static String accessKey = "89189D3CBF9AB0869CAF";
    private static String secretKey = "W0Y4RkVEOEE3M0FEMTc5RTNDMjI3OTY3MEM0RUJE";
    private static String endpoint = "http://scuts3.depts.bingosoft.net:29997";
    private static String bucket = "kongqing";
    private static String key = "demo.txt";
    private static String topic = "data_test_kongqing";
    private static int period = 7500;
    private static String bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";
    private static S3Writer s3Writer = new S3Writer(accessKey, secretKey, endpoint, bucket, "storage/", 5000);
    private static String[] filtersHotel = { "东方宾馆", "珠岛宾馆", "越秀宾馆", "华泰宾馆", "白云宾馆", "君达华海宾馆"};
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("acks", "all");
        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("group.id", "flink-group");
        FlinkKafkaConsumer010<String> consumer = new FlinkKafkaConsumer010<String>(topic, new SimpleStringSchema(), properties);
        consumer.setCommitOffsetsOnCheckpoints(true);
        DataStream<String> stream = env.addSource(consumer);
        List<DataStream<String>> streams = new ArrayList<>();
        for (String hotel : filtersHotel) {
            DataStream<String> tempStream = stream
                    .filter(new FilterFunction<String>() {
                        public boolean filter(String s) throws Exception {
                            String cityt = s.split(",")[2];
                            String city = cityt.substring(1, cityt.length() - 1);
                            return city.equals(hotel);
                        }
                    })
                    .flatMap((String line, Collector<String> collector) -> {
                        System.out.println("the data with the hotel:" + hotel + ": " + line);
                        collector.collect(line);
                    })
                    .returns(Types.STRING);
            tempStream.writeUsingOutputFormat(new S3Writer(accessKey, secretKey, endpoint, bucket, "storage/" + hotel + "/", period));
            streams.add(tempStream);
        }
        DataStream<String> tempStream = stream
                .filter(new FilterFunction<String>() {
                    public boolean filter(String s) throws Exception {
                        String cityt = s.split(",")[2];
                        String city = cityt.substring(1, cityt.length() - 1);
                        return Arrays.asList(filtersHotel).indexOf(city) == -1;
                    }
                })
                .flatMap((String line, Collector<String> collector) -> {
                    System.out.println("switch into others:" + line);
                    collector.collect(line);
                })
                .returns(Types.STRING);
        tempStream.writeUsingOutputFormat(new S3Writer(accessKey, secretKey, endpoint, bucket, "storage/others/", period));
        env.execute("appear times:");
    }
}
