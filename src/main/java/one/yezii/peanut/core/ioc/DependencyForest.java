package one.yezii.peanut.core.ioc;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyForest {
    List<Map<String, DependencyEndpoint>> forest = new ArrayList<>();
    List<DependencyEndpoint> endpoints = new ArrayList<>();

    private boolean forestContains(String epName) {
        return forest.stream().anyMatch(map -> map.containsKey(epName));
    }

    public DependencyForest addEndpoints(List<DependencyEndpoint> endpoints) {
        this.endpoints.addAll(endpoints);
        return this;
    }

    public DependencyForest generate() {
        checkDuplicatedDependencies();
        for (int currentLevel = 0; !endpoints.isEmpty(); currentLevel++) {
            if (prevLevelEmpty(currentLevel)) {
                checkInvalidDependenciesAndThrow();
            }
            createNewLevel(currentLevel);
        }
        return this;
    }

    private void createNewLevel(int level) {
        forest.add(level, new HashMap<>());
        List<DependencyEndpoint> reAddList = new ArrayList<>();
        while (!endpoints.isEmpty()) {
            DependencyEndpoint endpoint = endpoints.remove(0);
            if (isZeroLevel(endpoint) || nextDependenciesAllInForest(endpoint)) {
                putEndpointInLevel(level, endpoint);
            } else {
                reAddList.add(endpoint);
            }
        }
        endpoints.addAll(reAddList);
    }

    private void putEndpointInLevel(int level, DependencyEndpoint endpoint) {
        forest.get(level).put(endpoint.getName(), endpoint);
    }

    private boolean isZeroLevel(DependencyEndpoint endpoint) {
        return endpoint.getNextListReadOnly().isEmpty();
    }

    private boolean nextDependenciesAllInForest(DependencyEndpoint endpoint) {
        return endpoint.getNextListReadOnly().stream().allMatch(this::forestContains);
    }

    private void checkInvalidDependenciesAndThrow() {
        DependencyForest forest = new DependencyForest().addEndpoints(endpoints);
        for (DependencyEndpoint endpoint : endpoints) {
            for (String epName : endpoint.getNextListReadOnly()) {
                if (!forest.forestContains(epName) && !this.forestContains(epName)) {
                    throw new RuntimeException("dependency not found in [" + endpoint.getName() + "->" + epName + "]");
                }
            }
        }
        throw new RuntimeException("circular dependencies between " +
                endpoints.stream().map(DependencyEndpoint::getName).collect(Collectors.joining(",")));
    }

    private void checkDuplicatedDependencies() {
        Set<String> epNames = new HashSet<>();
        for (DependencyEndpoint endpoint : endpoints) {
            if (epNames.contains(endpoint.getName())) {
                throw new RuntimeException("duplicated endpoint [" + endpoint.getName() + "]");
            }
            epNames.add(endpoint.getName());
        }
    }

    private boolean prevLevelEmpty(int currentLevel) {
        return currentLevel != 0 && forest.get(currentLevel - 1).isEmpty();
    }


    public List<List<String>> getForestLevels() {
        return forest.stream().map(Map::keySet).map(ArrayList::new).collect(Collectors.toList());
    }

    public List<String> getInjectOrders() {
        return getForestLevels().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
