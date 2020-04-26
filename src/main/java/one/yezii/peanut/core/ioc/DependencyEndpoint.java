package one.yezii.peanut.core.ioc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DependencyEndpoint {
    private String name;
    private List<String> nextList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public DependencyEndpoint setName(String name) {
        this.name = name;
        return this;
    }

    public DependencyEndpoint addNext(List<String> epName) {
        nextList.addAll(epName);
        return this;
    }

    public List<String> getNextListReadOnly() {
        return Collections.unmodifiableList(nextList);
    }


}
