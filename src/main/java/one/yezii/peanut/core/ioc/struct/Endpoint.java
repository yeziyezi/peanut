package one.yezii.peanut.core.ioc.struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Endpoint {
    private int outDegree = 0;
    private int inDegree = 0;
    private List<Endpoint> nextList = new ArrayList<>();
    private List<Endpoint> prevList = new ArrayList<>();

    public Endpoint addNext(Endpoint... endpoints) {
        outDegree += endpoints.length;
        nextList.addAll(Arrays.asList(endpoints));
        return this;
    }

    public Endpoint addPrev(Endpoint... endpoints) {
        inDegree += endpoints.length;
        prevList.addAll(Arrays.asList(endpoints));
        return this;
    }

    public List<Endpoint> getNextList() {
        return nextList;
    }

    public List<Endpoint> getPrevList() {
        return prevList;
    }
}
