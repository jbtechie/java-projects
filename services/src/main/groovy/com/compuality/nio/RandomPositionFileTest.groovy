package com.compuality.nio
import com.yammer.metrics.core.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

public class RandomPositionFileTest {

  private static final Logger log = LoggerFactory.getLogger(RandomPositionFileTest)

  private static final int PAGE_SIZE = 4096

  private final Random rand = new Random()

  @Inject
  public RandomPositionFileTest() {
    for(int i=0; i < 6; ++i) {
      test(2**i * PAGE_SIZE)
    }
  }

  private void test(final int CHUNK_SIZE) {
    FileChannel fc = (FileChannel)Files.newByteChannel(Paths.get('test.dat'), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)

    final long SAMPLES = 100000
    final int COUNT = 1

    final byte[] chunk = new byte[CHUNK_SIZE]

    Clock clock = Clock.defaultClock()
    final long start = clock.tick()

    try {
      for(int i=0; i < SAMPLES; ++i) {
        testRandomWrite(fc, chunk, COUNT)
      }

    } finally {
      if(fc) {
        fc.close()
      }
    }

    final double duration = (clock.tick() - start)/1e9
    final long totalBytesWritten = SAMPLES * COUNT * chunk.length

    log.debug('Performed {} page aligned random writes totaling {} bytes in {} seconds ({} mb/s with a chunk size of {} bytes)', SAMPLES, totalBytesWritten, duration, totalBytesWritten/1e6/duration, chunk.length)
  }

//  private void testSequentialWrite(MappedByteBuffer buf, int chunkSize) {
//    Clock clock = Clock.defaultClock()
//    final long start = clock.tick()
//
//    for(int i=0; i < ((Integer.MAX_VALUE - chunkSize) / chunkSize); ++i) {
//      rand.nextBytes(chunk)
//      buf.put(chunk)
//    }
//
//    final double duration = (clock.tick() - start)/1e9
//
//    log.debug('Wrote {} bytes sequentially in {} seconds ({} mb/s with a chunk size of {} bytes', Integer.MAX_VALUE, duration, Integer.MAX_VALUE/1e6/duration, chunkSize)
//  }

  private void testRandomWrite(FileChannel fc, byte[] chunk, int count=((fc.size() - chunk.length) / chunk.length)) {
//    byte[] chunk = new byte[chunkSize]
//
//    Clock clock = Clock.defaultClock()
//    final long start = clock.tick()

    for(int i=0; i < count; ++i) {
      rand.nextBytes(chunk)
      fc.position(chunk.length * rand.nextInt((int)((fc.size() - chunk.length) / chunk.length)))
      fc.write(ByteBuffer.wrap(chunk))
    }

//    final double duration = (clock.tick() - start)/1e9

//    log.debug('Wrote {} bytes randomly in {} seconds ({} mb/s with a chunk size of {} bytes', Integer.MAX_VALUE, duration, Integer.MAX_VALUE/1e6/duration, chunkSize)
  }
}
