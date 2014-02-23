package com.compuality.nio

import com.yammer.metrics.core.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

public class MemMappedFileTest {

  private static final Logger log = LoggerFactory.getLogger(MemMappedFileTest)

  private static final int PAGE_SIZE = 4096

  @Inject
  public MemMappedFileTest() {
    FileChannel fc = (FileChannel)Files.newByteChannel(Paths.get('test.dat'), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
    try {
      MappedByteBuffer buf = fc.map(FileChannel.MapMode.READ_WRITE, 0, Integer.MAX_VALUE)

      final int SAMPLES = 5

      for(int i=0; i < SAMPLES; ++i) {
        buf.rewind()
        testSequentialWrite(buf, (int)(PAGE_SIZE * 8))
      }
      for(int i=0; i < SAMPLES; ++i) {
        buf.rewind()
        testSequentialWrite(buf, (int)(PAGE_SIZE * 16))
      }
      for(int i=0; i < SAMPLES; ++i) {
        buf.rewind()
        testSequentialWrite(buf, (int)(PAGE_SIZE * 32))
      }

      for(int i=0; i < SAMPLES; ++i) {
        buf.rewind()
        testRandomWrite(buf, (int)(PAGE_SIZE * 8))
      }
      for(int i=0; i < SAMPLES; ++i) {
        buf.rewind()
        testRandomWrite(buf, (int)(PAGE_SIZE * 16))
      }
      for(int i=0; i < SAMPLES; ++i) {
        buf.rewind()
        testRandomWrite(buf, (int)(PAGE_SIZE * 32))
      }

    } finally {
      if(fc) {
        fc.close()
      }
    }
  }

  private static void testSequentialWrite(MappedByteBuffer buf, int chunkSize) {
    byte[] chunk = new byte[chunkSize]
    Random rand = new Random()

    Clock clock = Clock.defaultClock()
    final long start = clock.tick()

    for(int i=0; i < ((Integer.MAX_VALUE - chunkSize) / chunkSize); ++i) {
      rand.nextBytes(chunk)
      buf.put(chunk)
    }

    final double duration = (clock.tick() - start)/1e9

    log.debug('Wrote {} bytes sequentially in {} seconds ({} mb/s with a chunk size of {} bytes', Integer.MAX_VALUE, duration, Integer.MAX_VALUE/1e6/duration, chunkSize)
  }

  private static void testRandomWrite(MappedByteBuffer buf, int chunkSize) {
    byte[] chunk = new byte[chunkSize]
    Random rand = new Random()

    Clock clock = Clock.defaultClock()
    final long start = clock.tick()

    for(int i=0; i < ((Integer.MAX_VALUE - chunkSize) / chunkSize); ++i) {
      rand.nextBytes(chunk)
      buf.position(chunkSize * rand.nextInt((int)((Integer.MAX_VALUE - chunkSize) / chunkSize)))
      buf.put(chunk)
    }

    final double duration = (clock.tick() - start)/1e9

    log.debug('Wrote {} bytes randomly in {} seconds ({} mb/s with a chunk size of {} bytes', Integer.MAX_VALUE, duration, Integer.MAX_VALUE/1e6/duration, chunkSize)
  }
}
