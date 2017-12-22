package com.arxality.common.logging;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.Document;
import org.slf4j.MDC;

import com.arxality.experibot.simulator.Robot;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class MongoDbAppender<E> extends AppenderBase<E> {

  private String uri;
  private int batchSize;
  
  private MongoClient mongo;
  private MongoClientURI connectionString;
  private MongoCollection logs;
  private Batcher<Map<String, Object>> batcher;

  @Override
  public void start() {
    super.start();
    connectionString = new MongoClientURI(uri);
    mongo = new MongoClient(connectionString);
    batcher = new Batcher<Map<String, Object>>(batchSize, this::saveBatch);
    
    logs = mongo
      .getDatabase(connectionString.getDatabase())
      .getCollection(connectionString.getCollection());
  }

  @Override
  public void stop() {
    try {
      if (null != mongo) mongo.close();
    } finally {
      super.stop();
    }
  }

  @Override
  protected void append(E eventObject) {
    if(eventObject instanceof LoggingEvent) {
      LoggingEvent le = (LoggingEvent) eventObject;
      
      Map<String, Object> log = new HashMap<>();
      addIfInMDC("robot_id", log);
      addIfInMDC("robot_role", log);
      
      log.put("timestamp", System.currentTimeMillis());
      log.put("timestamp_nano", System.nanoTime());
      
      if(null != le && null != le.getArgumentArray()) {
        for(Object arg : le.getArgumentArray()) {
          if(arg instanceof Robot) {
            ((Robot)arg).appendData(log);
          }
        }
      }
      
      batcher.add(log);
    } 
   
  }

  private void addIfInMDC(String key, Map<String, Object> log) {
    if(null != MDC.get(key)) log.put(key, MDC.get(key));
  }
  
  @SuppressWarnings("unchecked")
  private void saveBatch(Collection<Map<String, Object>> es) {
    System.out.println("SAVING>>>"+ es.size());
    List<Document> docs = convert(es);
    logs.insertMany(docs);
  }
  
  private List<Document> convert(Collection<Map<String, Object>> es) {
   return 
        es
          .stream()
          .map((Map<String,Object> m) -> { 
            Document doc = new Document();
            doc.putAll(m);
            return doc; 
          })
          .collect(Collectors.toList())
          ;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }
  
  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }
}
