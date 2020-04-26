package one.yezii.peanut.core.ioc;

import java.util.*;

/**
 * directed graph
 * the class is used to describe the relation between beans
 * actually,the cycles are not allowed in the dependencies directed graph
 * so it's must be a forest
 */
public class InjectOrderProducer {
    private Map<String, DependencyEndpoint> restEndpoints = new HashMap<>();
    private LinkedHashMap<String, DependencyEndpoint> injectedEndpoints = new LinkedHashMap<>();

    /**
     * every endpoint before add only know the next endpoints
     * but prev endpoints
     * so addEndpoint method will inject the prev endpoints in the endpoint
     */
    public void addEndPoints(List<DependencyEndpoint> endpoints) {
        for (DependencyEndpoint ep : endpoints) {
            if (restEndpoints.containsKey(ep.getName())) {
                throw new RuntimeException("Key " + ep.getName() + " already exist!");
            }
            ep.getNextListReadOnly().stream().filter(restEndpoints::containsKey).map(restEndpoints::get)
                    .forEach(DependencyEndpoint::addPrev);
            restEndpoints.put(ep.getName(), ep);
        }
    }

    //todo
    public List<String> getInjectOrder() {
        //todo add dependency cycle test
        //todo add not found dependency test
        while (!restEndpoints.isEmpty()) {
            restEndpoints.forEach((epName, ep) -> {
                if (ep.nextEmpty() || ep.getNextListReadOnly().stream().allMatch(injectedEndpoints::containsKey)) {
                    injectedEndpoints.put(epName, ep);
                }
                //todo
            });
            injectedEndpoints.keySet().forEach(restEndpoints::remove);
        }
        return new ArrayList<>(injectedEndpoints.keySet());
    }
}
