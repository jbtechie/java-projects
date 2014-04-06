package com.compuality.service.gutenberg
import com.compuality.concurrency.BoundedExecutor
import com.compuality.datastore.Datastore
import com.compuality.inject.WebService
import com.google.common.base.Optional
import com.google.common.util.concurrent.ThreadFactoryBuilder
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
import java.util.zip.ZipFile

@Path('gutenberg')
@Produces(MediaType.APPLICATION_JSON)
class GutenbergImportService implements WebService {

  private static final Logger log = LoggerFactory.getLogger(GutenbergImportService)

  private final Datastore datastore
  private final Random rand

  @Inject
  GutenbergImportService(Datastore datastore, Random rand) {
    this.datastore = datastore
    this.rand = rand
  }

  @GET
  @Path('import')
  ImportResult importBooks(final @QueryParam('root_dir') File rootDir,
                           final @QueryParam('threads') Optional<Integer> threadsParam,
                           final @QueryParam('batch_size') Optional<Integer> batchSizeParam) {

    log.debug('Root directory: {}', rootDir)

    final int threads = threadsParam.isPresent() ? threadsParam.get() : 1
    final int batchSize = batchSizeParam.isPresent() ? batchSizeParam.get() : 100

    String threadNameFormat = "${GutenbergImportService} (${rand.nextInt(Integer.MAX_VALUE)}) - #%d"
    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(threadNameFormat).build()
    BoundedExecutor executor = new BoundedExecutor(threads, Executors.newFixedThreadPool(threads, threadFactory))

    long count = 0

    try {
      List<GutenbergBook> batch = []

      rootDir.eachDirRecurse { d ->
        d.eachFileMatch(~/[0-9]+[.]zip/) { f ->
          ZipFile zf = new ZipFile(f)

          zf.entries().nextElement().with { entry ->
            String bookText = zf.getInputStream(entry).text
  //          Matcher m = (bookText =~ /Title: (.*)/)
  //          println(m[0][1])
            batch.add(new GutenbergBook(sourceFilePath:f.path, text:bookText))

            if(batch.size() == batchSize) {
              executor.execute({
                datastore.store(batch)
              })
              batch = []
            }
          }

          ++count
        }
      }

      if(!batch.isEmpty()) {
        executor.execute({
          datastore.store(batch)
        })
      }
    } finally {
      executor.shutdownAndWait()
    }

    log.debug('Processed {} books.', count)

    return new ImportResult(processed:count)
  }

  static class GutenbergBook {

    String sourceFilePath

    String text
  }

  static class ImportResult {

    int processed
  }
}
