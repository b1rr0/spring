package ronin.ua.balance.processor.util

import spock.lang.Specification
import org.antlr.v4.runtime.misc.Pair;

import java.util.Map
import java.util.UUID

import ronin.ua.balance.processor.service.util.PairCreator

class PairCreatorTest extends Specification {

    def "should create pair from map"() {
        given:
        def uuid1 = UUID.randomUUID()
        def uuid2 = UUID.randomUUID()
        def map = [(uuid1): 1.1d, (uuid2): 2.2d]

        when:
        Pair<UUID[], Double[]> pair = PairCreator.createPair(map)

        then:
        pair.a == [uuid1, uuid2] as UUID[]
        pair.b == [1.1d, 2.2d] as Double[]
    }

    def "should create pair from list of map entries"() {
        given:
        def uuid1 = UUID.randomUUID()
        def uuid2 = UUID.randomUUID()
        def entry1 = new AbstractMap.SimpleEntry(uuid1, 1.1d)
        def entry2 = new AbstractMap.SimpleEntry(uuid2, 2.2d)

        def list = [entry1, entry2]

        when:
        Pair<UUID[], Double[]> pair = PairCreator.createPair(list)

        then:
        pair.a == [uuid1, uuid2] as UUID[]
        pair.b == [1.1d, 2.2d] as Double[]
    }

    def "should handle empty map"() {
        given:
        def map = [:]

        when:
        Pair<UUID[], Double[]> pair = PairCreator.createPair(map)

        then:
        pair.a == [] as UUID[]
        pair.b == [] as Double[]
    }

    def "should handle empty list"() {
        given:
        def list = []

        when:
        Pair<UUID[], Double[]> pair = PairCreator.createPair(list)

        then:
        pair.a == [] as UUID[]
        pair.b == [] as Double[]
    }
}
