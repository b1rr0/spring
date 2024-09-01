package ronin.ua.balance.processor.service.util;

import org.antlr.v4.runtime.misc.Pair;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PairCreator {

    public static Pair<UUID[], Double[]> createPair(Map<UUID, Double> map) {
        return new Pair<>(map.keySet().toArray(new UUID[]{}), map.values().toArray(new Double[]{}));
    }


    public static Pair<UUID[], Double[]> createPair(List<Map.Entry<UUID, Double>> list) {
        UUID[] uuids = new UUID[list.size()];
        Double[] doubles = new Double[list.size()];

        for (int i = 0; i < list.size(); i++) {
            var entry = list.get(i);
            uuids[i] = entry.getKey();
            doubles[i] = entry.getValue();
        }
        return new Pair<>(uuids, doubles);
    }

}
