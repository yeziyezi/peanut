package one.yezii.peanut.demo.demo2;

import one.yezii.peanut.core.annotation.Bean;
import one.yezii.peanut.core.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class Configuration2 {
    @Bean
    public List<String> list2(List<String> list3) {
        return Collections.emptyList();
    }
}
