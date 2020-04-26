package one.yezii.peanut.core.ioc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DependencyEndpoint {
    private String name;
    private List<String> nextList = new ArrayList<>();
    private List<String> prevList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public DependencyEndpoint setName(String name) {
        this.name = name;
        return this;
    }

    public boolean nextEmpty() {
        return nextList.isEmpty();
    }

    public DependencyEndpoint addPrev(String... epName) {
        prevList.addAll(Arrays.asList(epName));
        return this;
    }

    public DependencyEndpoint addNext(List<String> epName) {
        prevList.addAll(epName);
        return this;
    }

    public DependencyEndpoint addNext(String... epName) {
        addNext(Arrays.asList(epName));
        return this;
    }

    public List<String> getNextListReadOnly() {
        return Collections.unmodifiableList(nextList);
    }

    public List<String> getPrevListReadOnly() {
        return Collections.unmodifiableList(prevList);
    }
}
