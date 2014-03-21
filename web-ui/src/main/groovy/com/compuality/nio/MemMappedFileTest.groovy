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

  private final Random rand = new Random()

  @Inject
  public MemMappedFileTest() {
    FileChannel fc = (FileChannel)Files.newByteChannel(Paths.get('test.dat'), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)

    MappedByteBuffer[] bufs = new MappedByteBuffer[1]
    for(long i=0; i < bufs.length; ++i) {
      bufs[i] = fc.map(FileChannel.MapMode.READ_WRITE, 0, Integer.MAX_VALUE - PAGE_SIZE + 1)
    }

    final int SAMPLES = 100000
    final int COUNT = 1
    final int CHUNK_SIZE = PAGE_SIZE

    final byte[] chunk = new byte[CHUNK_SIZE]

    Clock clock = Clock.defaultClock()
    final long start = clock.tick()

    try {

      for(int i=0; i < SAMPLES; ++i) {
        testRandomWrite(bufs[rand.nextInt(bufs.length)], chunk, COUNT)
      }
    } finally {
      if(bufs) {
        bufs.each { it.force() }
      }
      if(fc) {
        fc.close()
      }
    }

    final double duration = (clock.tick() - start)/1e9
    final int totalBytesWritten = SAMPLES * COUNT * chunk.length

    log.debug('Performed {} page aligned random writes totaling {} bytes in {} seconds ({} mb/s with a chunk size of {} bytes with {} different cached maps)', SAMPLES, totalBytesWritten, duration, totalBytesWritten/1e6/duration, chunk.length, 1)
  }

  private void testSequentialWrite(MappedByteBuffer buf, int chunkSize) {
    Clock clock = Clock.defaultClock()
    final long start = clock.tick()

    for(int i=0; i < ((Integer.MAX_VALUE - chunkSize) / chunkSize); ++i) {
      rand.nextBytes(chunk)
      buf.put(chunk)
    }

    final double duration = (clock.tick() - start)/1e9

    log.debug('Wrote {} bytes sequentially in {} seconds ({} mb/s with a chunk size of {} bytes', Integer.MAX_VALUE, duration, Integer.MAX_VALUE/1e6/duration, chunkSize)
  }

  private void testRandomWrite(MappedByteBuffer buf, byte[] chunk, int count=((buf.limit() - chunk.length) / chunk.length)) {
//    byte[] chunk = new byte[chunkSize]
//
//    Clock clock = Clock.defaultClock()
//    final long start = clock.tick()

    for(int i=0; i < count; ++i) {
      rand.nextBytes(chunk)
      buf.position(chunk.length * rand.nextInt((int)((buf.limit() - chunk.length) / chunk.length)))
      buf.put(chunk)
    }

//    final double duration = (clock.tick() - start)/1e9

//    log.debug('Wrote {} bytes randomly in {} seconds ({} mb/s with a chunk size of {} bytes', Integer.MAX_VALUE, duration, Integer.MAX_VALUE/1e6/duration, chunkSize)
  }
}
