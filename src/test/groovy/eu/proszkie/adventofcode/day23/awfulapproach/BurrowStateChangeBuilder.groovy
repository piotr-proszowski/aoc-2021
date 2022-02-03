package eu.proszkie.adventofcode.day23.awfulapproach


import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class BurrowStateChangeBuilder {
    Coords from
    Coords to
    Movable movable

    static BurrowStateChange change(@DelegatesTo(BurrowStateChangeBuilder) Closure definition) {
        BurrowStateChangeBuilder builder = new BurrowStateChangeBuilder()
        builder.with(definition)
        return builder.build()
    }

    def from(@DelegatesTo(CoordsBuilder) Closure definition) {
        CoordsBuilder builder = new CoordsBuilder()
        builder.with(definition)
        this.from = builder.build()
        return this
    }

    def to(@DelegatesTo(CoordsBuilder) Closure definition) {
        CoordsBuilder builder = new CoordsBuilder()
        builder.with(definition)
        this.to = builder.build()
        return this
    }

    def element(String token) {
        this.movable = MovableFactory.INSTANCE.fromElement(Element.@Companion.getByToken(token as char))
        return this
    }

    BurrowStateChange build() {
        return new BurrowStateChange(movable, from, to)
    }

    static List<BurrowStateChange> triesToStopOnSpaceImmediatelyOutsideAnyRoom() {
        return [
                change {
                    from { x 3 y 2 }
                    to { x 3 y 1 }
                    element 'B'
                },
                change {
                    from { x 5 y 2 }
                    to { x 5 y 1 }
                    element 'B'
                },
        ]
    }

    static List<BurrowStateChange> triesToMoveIntoRoomInWhichThereIsOtherTypeOfAmphipod() {
        return [
                change {
                    from { x 3 y 2 }
                    to { x 3 y 1 }
                    element 'B'
                },
                change {
                    from { x 3 y 1 }
                    to { x 4 y 1 }
                    element 'B'
                },
                change {
                    from { x 5 y 2 }
                    to { x 5 y 1 }
                    element 'C'
                },
                change {
                    from { x 5 y 1 }
                    to { x 6 y 1 }
                    element 'C'
                },
                change {
                    from { x 4 y 1 }
                    to { x 5 y 1 }
                    element 'B'
                },
                change {
                    from { x 5 y 1 }
                    to { x 5 y 2 }
                    element 'B'
                },
        ]
    }

    static List<BurrowStateChange> triesToComebackToTheSpaceInWhichAlreadyItWasDuringSingleWalk() {
        return [
                change {
                    from { x 3 y 2 }
                    to { x 3 y 1 }
                    element 'B'
                },
                change {
                    from { x 3 y 1 }
                    to { x 4 y 1 }
                    element 'B'
                },
                change {
                    from { x 4 y 1 }
                    to { x 3 y 1 }
                    element 'B'
                },
        ]
    }
    static List<BurrowStateChange> triesToStopAndThenGoNotDirectlyToTheRoom() {
        return [
                change {
                    from { x 3 y 2 }
                    to { x 3 y 1 }
                    element 'B'
                },
                change {
                    from { x 3 y 1 }
                    to { x 4 y 1 }
                    element 'B'
                },
                change {
                    from { x 5 y 2 }
                    to { x 5 y 1 }
                    element 'C'
                },
                change {
                    from { x 5 y 1 }
                    to { x 6 y 1 }
                    element 'C'
                },
                change {
                    from { x 4 y 1 }
                    to { x 3 y 1 }
                    element 'B'
                },
                change {
                    from { x 3 y 1 }
                    to { x 2 y 1 }
                    element 'B'
                },
                change {
                    from { x 8 y 2 }
                    to { x 8 y 1 }
                    element 'D'
                },
        ]
    }

    static List<BurrowStateChange> triesToStopInsideTheRoom() {
        return [
                change {
                    from { x 3 y 2 }
                    to { x 3 y 1 }
                    element 'B'
                },
                change {
                    from { x 3 y 1 }
                    to { x 4 y 1 }
                    element 'B'
                },
                change {
                    from { x 3 y 3 }
                    to { x 3 y 2 }
                    element 'A'
                },
                change {
                    from { x 4 y 1 }
                    to { x 3 y 1 }
                    element 'C'
                },
        ]
    }

    static List<BurrowStateChange> triesToApplyTheSameChangeTwice() {
        return [
                change {
                    from { x 3 y 2 }
                    to { x 3 y 1 }
                    element 'B'
                },
                change {
                    from { x 3 y 1 }
                    to { x 4 y 1 }
                    element 'B'
                },
                change {
                    from { x 3 y 3 }
                    to { x 3 y 2 }
                    element 'A'
                },
                change {
                    from { x 3 y 2 }
                    to { x 3 y 1 }
                    element 'A'
                },
                change {
                    from { x 3 y 1 }
                    to { x 2 y 1 }
                    element 'A'
                },
                change {
                    from { x 4 y 1 }
                    to { x 3 y 1 }
                    element 'B'
                },
        ]
    }
}