package com.arxality.common.logging;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

public class Batcher<E> {

  private final Executor executor = Executors.newCachedThreadPool(new DaemonThreadFactor());
  private final int size;
  private final Consumer<Collection<E>> batchFull;
  
  // Keep it simple and stick with two for now
  private transient Stack<Collection<E>> batches = new Stack<>();
  
  
  Batcher(int batchSize, Consumer<Collection<E>> batchFull) {
    this.size = batchSize;
    this.batchFull = batchFull;
    batches.push(new LinkedList<E>());
  }

  synchronized void add(E e) {
    batches.peek().add(e);
    
    if(batches.peek().size() >= this.size) {
      executor.execute(new BatchDrain(batches.pop()));
      batches.push(new LinkedList<E>());
    }
  }
  
  private static class DaemonThreadFactor implements ThreadFactory {

    private static int COUNTER = 0;
    
    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(r, "BatcherThread-"+(++COUNTER));
      t.setDaemon(true);
      return t;
    }
    
  }
  
  private class BatchDrain implements Runnable {
    
    private Collection<E> batch;
    
    private BatchDrain(Collection<E> batch) {
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
