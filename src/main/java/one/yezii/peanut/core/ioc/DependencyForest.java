package one.yezii.peanut.core.ioc;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyForest {
    List<Map<String, DependencyEndpoint>> forest = new ArrayList<>();

    private boolean contains(String epName) {
        return forest.stream().anyMatch(map -> map.containsKey(epName));
    }

    public DependencyForest addEndpoints(List<DependencyEndpoint> endpoints) {
        Set<String> epNames = new HashSet<>();
        for (DependencyEndpoint endpoint : endpoints) {
            if (epNames.contains(endpoint.getName())) {
                throw new RuntimeException("duplicated endpoint [" + endpoint.getName() + "]");
            }
            epNames.add(endpoint.getName());
        }
        int levelLimit = endpoints.size();
        for (int level = 0; level < levelLimit; level++) {
            if (level != 0 && forest.get(level - 1).isEmpty()) {
                //todo
                throw new RuntimeException("dependency not found or circular dependencies");
            }
            forest.add(level, new HashMap<>());
            List<DependencyEndpoint> reAddList = new ArrayList<>();
            while (!endpoints.isEmpty()) {
                DependencyEndpoint endpoint = endpoints.remove(0);
                //if current level is zero,all bean with no dependencies will set in the zero level.
                //if current level is not zero and some bean's dependencies all contained in prev levels,
                //these beans will be set in the current level
                if ((level == 0 && endpoint.getNextListReadOnly().isEmpty())
                        || endpoint.getNextListReadOnly().stream().allMatch(this::contains)) {
                    forest.get(level).put(endpoint.getName(), endpoint);
                } else {
                    reAddList.add(endpoint);
                }
            }
            endpoints.addAll(reAddList);
        }
        return this;
    }

    public List<List<String>> getForestLevels() {
        return forest.stream().map(Map::keySet).map(ArrayList::new).collect(Collectors.toList());
    }

    public List<String> getInjectOrders() {
        return getForestLevels().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
