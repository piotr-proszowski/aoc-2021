package eu.proszkie.adventofcode.day24.builders

import eu.proszkie.adventofcode.day24.RegistryName
import eu.proszkie.adventofcode.day24.RegistryState
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class RegistryStateBuilder {
    int x = 0
    int w = 0
    int z = 0
    int y = 0

    static RegistryState registryState(@DelegatesTo(RegistryStateBuilder) Closure definition) {
        RegistryStateBuilder builder = new RegistryStateBuilder()
        builder.with(definition)
        return builder.build()
    }

    private RegistryState build() {
        return new RegistryState([
                new RegistryName("x" as char): x,
                new RegistryName("y" as char): y,
                new RegistryName("z" as char): z,
                new RegistryName("w" as char): w,
        ])
    }
}
