package eu.proszkie.adventofcode.day21

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class GameSnapshotBuilder {

    Map<Integer, Integer> playersPositions = [:]
    DiceState dice = new DeterministicDiceState(1000, 0)

    static GameSnapshot gameSnapshot(@DelegatesTo(GameSnapshotBuilder) Closure definition) {
        GameSnapshotBuilder builder = new GameSnapshotBuilder()
        builder.with(definition)
        return builder.build()
    }

    GameSnapshot build() {
        return new GameSnapshot(
                new Board(playersPositions.collectEntries { [new PlayerId(it.key), new Space(it.value)] }),
                playersPositions.keySet().collectEntries { [new PlayerId(it), new Score(0L)] },
                playersPositions.collect { new PlayerId(it.key) },
                dice,
        )
    }
}
