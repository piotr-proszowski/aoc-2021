package eu.proszkie.adventofcode.day18

class SnailfishNumberBuilder {
    static SnailfishNumber snailfish(int raw) {
        return new PlainNumber(raw)
    }

    static SnailfishNumber snailfish(List numbers) {
        assert numbers.size() == 2: 'There should be two numbers in list'
        return new TwoSnailfishNumbers(snailfish(numbers[0]), snailfish(numbers[1]))
    }

    static SnailfishNumber snailfish(String snailfishNumber) {
        return snailfish(Eval.me(snailfishNumber) as List)
    }
}
