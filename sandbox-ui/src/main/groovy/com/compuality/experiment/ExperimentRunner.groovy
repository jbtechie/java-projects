package com.compuality.experiment

import com.compuality.cpu.CPU
import com.compuality.experiment.ExperimentReport.GenerationReport
import com.compuality.experiment.ExperimentReport.LifeReport
import rx.Observable

class ExperimentRunner {

  void run() {
    UUID id = UUID.randomUUID()
    (1..1000).each { generationIndex ->
      Observable<LifeReport> lives = lives(id, generationIndex)
      Observable<GenerationReport> generation = generation(id, generationIndex, lives)
    }
  }

  static Observable<ExperimentReport> experiments(Observable<Map<String, Object>> argsSource) {
    return argsSource.map({ args ->
      UUID id = UUID.randomUUID()
      return new ExperimentReport([id:id, args:args, generations:generations(id)])
    })
  }

  private static Observable<GenerationReport> generation(UUID experimentId, long generationIndex, Observable<LifeReport> lives) {
    return lives.reduce([totalError:0], { result, life ->
      result.totalError += life.error;
      return result
    })
    .map({ result ->
      return new GenerationReport([experimentId:experimentId, generationIndex:generationIndex, totalError:result.totalError])
    })
  }

  private static Observable<LifeReport> lives(UUID experimentId, long generationIndex) {
    return Observable.create({ lifeObserver ->
      (1..100).each {
        CPU cpu = new CPU(6)
        cpu.randMem()
        cpu.sim(cpu.WORD_SIZE)
        lifeObserver.onNext(new LifeReport([experimentId:experimentId, generationIndex:generationIndex, error:cpu.mem[0]]))
      }
      lifeObserver.onCompleted()
    })
  }

  void run2() {
    Observable.from((1..1000))
      .map({
        CPU cpu = new CPU(6)
        cpu.randMem()
        cpu.sim(cpu.WORD_SIZE)
        return cpu
      })
      .reduce([totalError:0], { result, cpu ->
        result.totalError += cpu.mem[0]
        return result
      })
      .toBlockingObservable()
      .single()
  }

  // init -> process -> analysis -> re-init ->
}
