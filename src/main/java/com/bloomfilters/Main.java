package com.bloomfilters;

import com.bloomfilters.core.BloomFilter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BloomFilter bloomFilter = new BloomFilter(5, 0.000001);
        List<String> items = List.of("Java", "Python", "JavaScript", "Go", "PHP");

        for (String item : items) {
            bloomFilter.put(item);
        }

        for (String item : items) {
            System.out.println(bloomFilter.contains(item));
        }

        System.out.println(bloomFilter.contains("Rust"));
        System.out.println(bloomFilter.contains("Scala"));
    }
}