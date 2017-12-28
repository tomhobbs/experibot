package com.arxality.common.logging;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.bson.Document;
import org.slf4j.MDC;

import com.arxality.experibot.logging.Loggable;
import com.arxality.experibot.simulator.Robot;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneOptions;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;

import scala.collection.mutable.WrappedArray;

public class MongoDbAppender<E> extends AppenderBase<E> {

  private String uri;
  private int batchSize;
  private int socketTimeout;
  
  private MongoClient mongo;
  private MongoClientURI connectionString;
  private MongoCollection<Document> logs;
  private Batcher<Document> batcher;
  
  private volatile boolean shuttingDown = false;

  @Override
  public void start() {
    super.start();
    
    MongoClientOptions.Builder opts = new MongoClientOptions.Builder()
          .socketTimeout(socketTimeout);
    
    connectionString = new MongoClientURI(uri, opts);
    mongo = new MongoClient(connectionString);
    batcher = new Batcher<Document>(batchSize, this::saveBatch);
    
    System.out.println("Connecting to "+uri);
    
    logs = mongo
      .getDatabase(connectionString.getDatabase())
      .withWriteConcern(WriteConcern.ACKNOWLEDGED)
      .getCollection(connectionString.getCollection());
    
  }

  @Override
  public void stop() {
    try {
      shuttingDown = true;
      batcher.stop();
      if (null != mongo) mongo.close();
    } finally {
      super.stop();
    }
  }

  @Override
  protected void append(E eventObject) {
    if(eventObject instanceof LoggingEvent) {
      LoggingEvent le = (LoggingEvent) eventObject;
      
      Document log = new Document();
      
      addIfInMDC("robot_id", log);
      addIfInMDC("robot_role", log);
      
      log.put("timestamp", System.currentTimeMillis());
      log.put("timestamp_nano", System.nanoTime());
      log.put("msg", le.getFormattedMessage());
      
      if(null != le && null != le.getArgumentArray()) {
        
        for(Object arg : le.getArgumentArray()) {
          if(arg instanceof Robot) {
            Document doc = clean(((Loggable) arg).toDocument());
            errorIfRobotIdsMismatch(doc.get("robot_id"), MDC.get("robot_id"));
            log.putAll(doc);
          }
        }
      }
      
      batcher.add(log);
    } 
   
  }

  private void errorIfRobotIdsMismatch(Object fromDoc, Object fromMdc) {
    if(Objects.equals(fromDoc,  fromMdc)) return;
    else {
      throw new IllegalStateException("robot_ids in MDC and doc did not match: MDC=["+fromMdc+"], Doc=["+fromDoc+"]");
    }
  }

  private void addIfInMDC(String key, Map<String, Object> log) {
    if(null != MDC.get(key)) log.put(key, MDC.get(key));
  }
  
  @SuppressWarnings("unchecked")
  private void saveBatch(List<Document> docs) {
    try {
      logs.insertMany(docs);
    } catch (com.mongodb.MongoWaitQueueFullException e) {
      if(!shuttingDown) e.printStackTrace();
    } catch (com.mongodb.MongoSocketReadException e) {
      if(!shuttingDown) e.printStackTrace();
    } catch (com.mongodb.MongoInterruptedException e) {
      if(!shuttingDown) e.printStackTrace();
    }
  }

  private Document clean(Document dirty) {
    Document clean = new Document();
    
    dirty.forEach((k,v) -> {
      if(v instanceof WrappedArray) {
        final List<Object> vs = scala.collection.JavaConversions.seqAsJavaList((WrappedArray)v);
        clean.append(k, vs);
      } else if(v instanceof Document) {
        clean.append(k, clean((Document)v));
      } else if(v instanceof scala.Tuple3) {
        final List<Object> vs = new LinkedList<>();
        scala.Tuple3 v3 = (scala.Tuple3) v;
        vs.add(v3._1());
        vs.add(v3._2());
        vs.add(v3._3());
        clean.append(k, vs);
      } else {
        clean.append(k, v);
      }
    }); 
    
    return clean;
  }
  
  public void setUri(String uri) {
    this.uri = uri;
  }
  
  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }
  
  public void setSocketTimeout(int timeout) {
    this.socketTimeout = timeout;
  }
  
}
