package com.compuality.concurrency

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class BoundedExecutor implements Executor {

  private final ExecutorService backing
  private final Semaphore semaphore

  BoundedExecutor(int capacity, ExecutorService backing) {
    this.backing = backing
    this.semaphore = new Semaphore(capacity)
  }

  @Override
  void execute(Runnable command) {
    try {
      semaphore.acquire()

      backing.execute({
        try {
          command.run()
        } finally {
          semaphore.release()
        }
      })

    } catch(RejectedExecutionException e) {
      semaphore.release()
    }
  }

  ExecutorService getBacking() {
    return backing
  }

  void shutdownAndWait() {
    backing.shutdown()
    backing.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)
  }
}
