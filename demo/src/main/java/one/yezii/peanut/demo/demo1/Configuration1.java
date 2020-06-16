package one.yezii.peanut.demo.demo1;

import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Bean;
import one.yezii.peanut.core.annotation.Configuration;
import one.yezii.peanut.core.annotation.DependOn;

import java.util.HashMap;
import java.util.List;

@Configuration
public class Configuration1 {
    @Autowired
    HashMap<String, String> map;

    @Bean
    public HashMap<String, String> map() {
        HashMap<String, String> map = new HashMap<>();
        map.put("hhhhh", "???");
        return map;
    }

    @DependOn({"map"})
    @Bean
    public String a(List<String> list2) {
        System.out.println(map.toString());
        System.out.println(list2.size());
        return "";
    }
}
