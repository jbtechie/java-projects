package com.compuality.experiment
import com.google.common.collect.ImmutableMap
import rx.Observable
import rx.Observer

class ExperimentReport {

  UUID id
  Map<String, Object> args
  Observable<GenerationReport> generations

  static class CreateRequest extends ExperimentReport {}

  static class GenerationReport {

    UUID experimentId
    long index

    static class CreateRequest extends GenerationReport {}

    static class Builder {

    }
  }

  static class LifeReport {

    UUID experimentId
    long generationIndex
    double error

    static class CreateRequest extends LifeReport {}
  }

  static interface DAO {

    Observable<ExperimentReport> getAll()

    ExperimentReport get(UUID id)

    Observable<GenerationReport> getGenerationReports(UUID experimentId)

    Observable<LifeReport> getLifeReports(UUID experimentId, long generationIndex)

    Observable<ExperimentReport> createExperiments(Observable<CreateRequest> request)

    Observable<GenerationReport> createGenerations(Observable<GenerationReport.CreateRequest> request)

    Observable<LifeReport> createLives(Observable<LifeReport.CreateRequest> request)
  }

  static interface Loader {

    void loadExperiments(Observable<ExperimentReport> reports)
    void loadGenerations(Observable<GenerationReport> reports)
    void loadLives(Observable<LifeReport> reports)
  }

  static class LifeReportObserver implements Observer<LifeReport> {
    void onCompleted() {

    }

    void onError(Throwable e) {

    }

    void onNext(LifeReport args) {

    }
  }

  public static Observable<ExperimentReport> experiment(Observable<Map<String, Object>> argsSource) {
    return argsSource.map({ args ->
      //...do experiment...

      return new ExperimentReport([id:UUID.randomUUID(), args:args, generations:generations(id)])
    })
  }

  public static Observable<GenerationReport> generations(UUID id) {
    return Observable.create({ observer ->
      Random rand = new Random()
      long index = 0
      def prevGen = null
      while(rand.nextInt() != 0) {
        prevGen = gen(prevGen)
        def genReport = prevGen.report()
        genReport.lives.doOnCompleted({ })
        observer.onNext(genReport)
//        observer.onNext(GenerationReport([id:id, index:index, lives:lives(id, index)]))
      }
    })
  }

  def gen(def prevGen) {

  }

  public static Observable<LifeReport> lives(UUID id, long index) {

  }

  def foo() {
    experiment(Observable.from([arg1:"val1"])).subscribe({ ExperimentReport experiment ->
      dao.addExperiment(experiment)
      experiment.generations.subscribe({ GenerationReport generation ->
        dao.addGeneration(generation)
      })
    })
  }

  static class Builder {

    final UUID id
    final Map<String, Object> args
    final List<GenerationReport.Builder> genBuilders

    Builder(UUID id, Map<String, Object> args) {
      this.id = id
      this.args = ImmutableMap.copyOf(args)
    }

    GenerationReport.Builder addGeneration() {
      def builder = new GenerationReport.Builder()
      genBuilders.add(builder)
      return builder
    }
  }
}
