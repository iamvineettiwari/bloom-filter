package com.bloomfilters.core;

import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BloomFilterTest {

    @Test
    public void testPutAndContains() {
        BloomFilter bloomFilter = new BloomFilter(1000, 0.000001);
        List<String> insertItems = List.of("java", "python", "php", "go", "");
        List<String> doNotInsertItems = List.of("javascript", "rust", "scala");

        for (String item : insertItems) {
            bloomFilter.put(item);
        }

        for (String item: insertItems) {
            assertTrue(bloomFilter.contains(item), "Item " + item + " should exist");
        }

        for (String item : doNotInsertItems) {
            assertFalse(bloomFilter.contains(item), "Item " + item + " should not exist");
        }
    }

    @Test
    public void testFalsePositiveRateAccuracy() {
        int n = 100;
        double expectedFalsePositiveRate = 0.5;
        int testSampleSize = 100000;

        BloomFilter bloomFilter = new BloomFilter(n, expectedFalsePositiveRate);

        for (int i = 0; i < n; i++) {
            String item = "inserted-" + i;
            bloomFilter.put(item);
        }

        int falsePositives = 0;
        int totalTests = 0;

        for (int i = 0; i < testSampleSize; i++) {
            String testItem = "noninserted-" + i;

            if (bloomFilter.contains(testItem)) {
                falsePositives++;
            }

            totalTests++;
        }

        double actualFalsePositives = (double) falsePositives / totalTests;

        System.out.println("Expected False Positive Rate: " + expectedFalsePositiveRate);
        System.out.println("Actual False Positive Rate: " + actualFalsePositives);

        double allowedDeviation = 0.1;

        assertTrue(
                Math.abs(actualFalsePositives - expectedFalsePositiveRate) < allowedDeviation,
                "False positive rate is outside acceptable range. Expected: " + expectedFalsePositiveRate + ", Actual: " + actualFalsePositives
        );
    }
}
