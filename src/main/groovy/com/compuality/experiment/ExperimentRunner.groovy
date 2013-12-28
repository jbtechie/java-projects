package com.compuality.experiment

import com.compuality.cpu.CPU
import com.compuality.experiment.ExperimentReport.GenerationReport
import com.compuality.experiment.ExperimentReport.LifeReport
import rx.Observable

class ExperimentRunner {

  static Observable<ExperimentReport> experiments(Observable<Map<String, Object>> argsSource) {
    return argsSource.map({ args ->
      UUID id = UUID.randomUUID()
      return new ExperimentReport([id:id, args:args, generations:generations(id)])
    })
  }

  private static Observable<GenerationReport> generations(UUID experimentId) {
    return Observable.create({ generationObserver ->
      (1..1000).each { genIndex ->
        Observable<LifeReport> lives = lives(experimentId, genIndex)
        GenerationReport generation = new GenerationReport([experimentId:experimentId, index:genIndex, lives:lives])
        generationObserver.onNext()
      }
      generationObserver.onCompleted()
    })
  }

  private static Observable<LifeReport> lives(UUID experimentId, long generationIndex) {
    return Observable.create({ lifeObserver ->
      (1..100).each {
        CPU cpu = new CPU(6)
        cpu.randMem()
        cpu.sim(cpu.WORD_SIZE)
        lifeObserver.onNext(new LifeReport([experimentId:experimentId, generationIndex:generationIndex]))
      }
      lifeObserver.onCompleted()
    })
  }
}
