package ru.sinvic;

import com.google.common.collect.Lists;
import java.util.List;

@SuppressWarnings("java:S106")
public class App {
    public static void main(String... args) {
        List<String> elements = Lists.newArrayList("alpha", "beta", "gamma");

        for (String element : Lists.reverse(elements)) {
            System.out.println(element);
        }
    }
}
