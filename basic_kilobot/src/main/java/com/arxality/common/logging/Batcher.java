package com.arxality.common.logging;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

public class Batcher<E> {

  private final ExecutorService executor = Executors.newCachedThreadPool(new DaemonThreadFactory());
  private final int size;
  private final Consumer<List<E>> batchFull;
  
  private transient List<E> batches = new LinkedList<>();
  
  /**
   * Frustratingly, the Mongo API requires a List, not a Collection
   * and so we use the same here to prevent having to convert between
   * more/less specific.
   * 
   * @param batchSize
   * @param batchFull
   */
  Batcher(int batchSize, Consumer<List<E>> batchFull) {
    this.size = batchSize;
    this.batchFull = batchFull;
  }

  synchronized void add(E e) {
    batches.add(e);
    
    
    
    if(batches.size() >= this.size) {
      List<E> old = batches;
      executor.execute(new BatchDrain(old));
      batches = new LinkedList<E>();
    }
  }
  
  public void stop() {
    executor.shutdownNow();
  }
  
  private static class DaemonThreadFactory implements ThreadFactory {

    private ThreadFactory defaultFactory =  Executors.defaultThreadFactory();
    
    @Override
    public Thread newThread(Runnable r) {
      Thread t = defaultFactory.newThread(r);
      t.setDaemon(true);
      return t;
    }
    
  }
  
  private class BatchDrain implements Runnable {
    
    private List<E> batch;
    
    private BatchDrain(List<E> batch) {
      this.batch = batch;
    }
    
    @Override
    public void run() {
      try {
        batchFull.accept(batch);
      } catch (Exception e) {
        // Intended to be used within the logging sub-system, so just sysout stuff
        System.err.println("Exception handing batch:" + e.getMessage());
        e.printStackTrace();
      }
    }
  }
}
