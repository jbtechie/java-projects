package com.compuality.experiment
import com.compuality.cpu.CPU
import com.compuality.experiment.ExperimentReport.GenerationReport
import com.compuality.experiment.ExperimentReport.LifeReport
import com.compuality.experiment.ExperimentReport.Loader
import rx.Observable

class ExperimentRunner {

  void run(Loader loader) {
    Observable<ExperimentReport> experiments = Observable.create({ experimentObserver ->
      (1..10).each {
        Observable<GenerationReport> generations = Observable.create({ generationObserver ->
          (1..1000).each {
            GenerationReport generationReport = new GenerationReport()

            Observable<LifeReport> lives = Observable.create({ lifeObserver ->
              (1..100).each {
                CPU cpu = new CPU(6)
                cpu.randMem()
                cpu.sim(cpu.WORD_SIZE)
                lifeObserver.onNext(new LifeReport([experimentId:generationReport.experimentId, generationIndex:generationReport.index]))
              }
              lifeObserver.onCompleted()
            })

            loader.loadLives(lives)

            generationObserver.onNext(generationReport)
          }
          generationObserver.onCompleted()
        })

        loader.loadGenerations(generations)

        experimentObserver.onNext()
      }

      experimentObserver.onCompleted()
    })

    loader.loadExperiments(experiments)
  }

  Observable<ExperimentReport> prepareExperiments() {
    return Observable.create({ experimentObserver ->
      (1..10).each {
        Observable<GenerationReport> generations = Observable.create({ generationObserver ->
          (1..1000).each {
            GenerationReport generationReport = new GenerationReport()

            Observable<LifeReport> lives = Observable.create({ lifeObserver ->
              (1..100).each {
                CPU cpu = new CPU(6)
                cpu.randMem()
                cpu.sim(cpu.WORD_SIZE)
                lifeObserver.onNext(new LifeReport([experimentId:generationReport.experimentId, generationIndex:generationReport.index]))
              }
              lifeObserver.onCompleted()
            })

            generationObserver.onNext(generationReport)
          }
          generationObserver.onCompleted()
        })

        experimentObserver.onNext()
      }

      experimentObserver.onCompleted()
    })
  }

  Observable<ExperimentReport> experiments(Observable<Map<String, Object>> argsSource) {
    return argsSource.map({ args ->
      UUID experimentId = UUID.randomUUID()
      Observable<GenerationReport> generations = Observable.create({ generationObserver ->
        (1..1000).each { genIndex ->
          Observable<LifeReport> lives = Observable.create({ lifeObserver ->
            (1..100).each {
              CPU cpu = new CPU(6)
              cpu.randMem()
              cpu.sim(cpu.WORD_SIZE)
              lifeObserver.onNext(new LifeReport([experimentId:experimentId, generationIndex:genIndex]))
            }
            lifeObserver.onCompleted()
          })

          generationObserver.onNext(new GenerationReport([experimentId:experimentId, index:genIndex, lives:lives]))
        }
        generationObserver.onCompleted()
      })

      return new ExperimentReport([id:experimentId, args:args, generations:generations])
    })
  }
}
