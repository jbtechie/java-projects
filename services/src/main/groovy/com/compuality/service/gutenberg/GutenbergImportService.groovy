package com.compuality.service.gutenberg
import com.compuality.concurrency.BoundedExecutor
import com.compuality.datastore.Datastore
import com.compuality.inject.WebService
import com.google.common.base.Optional
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.yammer.metrics.core.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@Path('gutenberg')
@Produces(MediaType.APPLICATION_JSON)
class GutenbergImportService implements WebService {

  private final Logger log = LoggerFactory.getLogger(GutenbergImportService)

  private final Datastore datastore
  private final Random rand

  @Inject
  GutenbergImportService(Datastore datastore, Random rand) {
    this.datastore = datastore
    this.rand = rand
  }

  @GET
  @Path('import')
  @SuppressWarnings('unused')
  ImportResult importBooks(final @QueryParam('root_dir') File rootDir,
                           final @QueryParam('threads') Optional<Integer> threadsParam,
                           final @QueryParam('buffer_size') Optional<Integer> bufferSizeParam) {

    log.debug('Root directory: {}', rootDir)

    final int threads = threadsParam.isPresent() ? threadsParam.get() : 1
    final long bufferSize = bufferSizeParam.isPresent() ? bufferSizeParam.get() : 100e6
    final long threadBufferSize = (bufferSize / threads).longValue()

    String threadNameFormat = "${GutenbergImportService} (${rand.nextInt(Integer.MAX_VALUE)}) - #%d"
    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(threadNameFormat).build()
    BoundedExecutor executor = new BoundedExecutor(threads, Executors.newFixedThreadPool(threads, threadFactory))

    Clock clock = Clock.defaultClock()
    final long startTime = clock.tick()

    AtomicLong storedCount = new AtomicLong()
    AtomicLong storedSize = new AtomicLong()
    List<GutenbergBook> batch = []
    long threadBufferRemaining = threadBufferSize

    try {
      rootDir.eachDirRecurse { d ->
        d.eachFileMatch(~/[0-9]+[.]zip/) { f ->
          ZipFile zipFile = null

          try {
            zipFile = new ZipFile(f)

            // there is only a single entry in each zip file
            ZipEntry zipEntry = zipFile.entries().nextElement()

            if(zipEntry.size > threadBufferRemaining) {
              if(!batch.isEmpty()) {
                log.debug('Batch size: {}', batch.size())
                List<GutenbergBook> closureBatch = batch
                executor.execute({
                  datastore.store(closureBatch)
                  storedCount.addAndGet(closureBatch.size())
                  closureBatch.each {
                    storedSize.addAndGet(it.text.length())
                  }
                })
                batch = []
                threadBufferRemaining = threadBufferSize
              }

              if(zipEntry.size > threadBufferRemaining) {
                log.warn('Skipping file that is too large ({} bytes): {}', zipEntry.size, f)
                return
              }
            }

            String bookText = zipFile.getInputStream(zipEntry).text
            batch.add(new GutenbergBook(sourceFilePath: f.path, text: bookText))
            threadBufferRemaining -= zipEntry.size

          } catch(IOException e) {
            log.error('Error reading zip file: ${f}. Skipping.', e)
          } finally {
            zipFile?.close()
          }
        }
      }

      if(!batch.isEmpty()) {
        executor.execute({
          datastore.store(batch)
          storedCount.addAndGet(batch.size())
          batch.each {
            storedSize.addAndGet(it.text.length())
          }
        })
      }
    } finally {
      executor.shutdownAndWait()
    }

    final double duration = (clock.tick() - startTime) / 1e9

    log.info('Stored {} books in {} seconds ({} b/s).', storedCount.get(), duration, storedCount.get()/duration)
    log.info('Stored {} MB in {} seconds ({} MB/s).', storedSize.get(), duration, storedSize.get()/duration)

    return new ImportResult(processed:storedCount.get())
  }

  static class GutenbergBook {

    String sourceFilePath

    String text
  }

  static class ImportResult {

    int processed
  }
}
