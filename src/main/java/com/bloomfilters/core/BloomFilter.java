package com.bloomfilters.core;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class BloomFilter {

    private final int totalItems;
    private final double falsePositiveRate;
    private final byte[] bucket;
    private final int numBits;
    private final int numHashFunctions;

    public BloomFilter(int totalItems, double falsePositiveRate) {
        this.totalItems = totalItems;
        this.falsePositiveRate = falsePositiveRate;

        this.numBits = calculateNumberOfBits();
        this.numHashFunctions = calculateNumberOfHashFunctions();

        int bucketSize = (int) Math.ceil(numBits / 8.0);
        this.bucket = new byte[bucketSize];
    }

    public void put(String item) {
        int[] bitIndexes = getBitIndexes(item);

        for (int bitIndex : bitIndexes) {
            int nthBucket = bitIndex / 8;
            int nthBit = bitIndex % 8;
            setNthBit(nthBucket, nthBit);
        }
    }

    public boolean contains(String item) {
        int[] bitIndexes = getBitIndexes(item);

        for (int bitIndex : bitIndexes) {
            int nthBucket = bitIndex / 8;
            int nthBit = bitIndex % 8;

            if (!isNthBitSet(nthBucket, nthBit)) {
                return false;
            }
        }

        return true;
    }

    private void setNthBit(int nthBucket, int nthBit) {
        this.bucket[nthBucket] = (byte) (this.bucket[nthBucket] | (1 << nthBit));
    }

    private boolean isNthBitSet(int nthBucket, int nthBit) {
        return (this.bucket[nthBucket] & (1 << nthBit)) != 0;
    }

    private int calculateNumberOfBits() {
        return (int) Math.ceil(-(totalItems * Math.log(falsePositiveRate)) / (Math.pow(Math.log(2), 2)));
    }

    private int calculateNumberOfHashFunctions() {
        return (int) Math.round((numBits / (double) totalItems) * Math.log(2));
    }

    private int[] getBitIndexes(String key) {
        int[] indexes = new int[numHashFunctions];

        byte[] hash = getHash(key);

        long hash1 = getLong(hash, 0);
        long hash2 = getLong(hash, 1);

        for (int i = 0; i < numHashFunctions; i++) {
            long curHash = hash1 + i * hash2;
            indexes[i] = Math.floorMod(curHash, numBits);
        }

        return indexes;
    }

    private byte[] getHash(String key) {
        return Hashing.murmur3_128().hashString(key, StandardCharsets.UTF_8).asBytes();
    }

    private long getLong(byte[] bytes, int offset) {
        return ((long) bytes[offset] & 0xff)
                | (((long) bytes[offset + 1] & 0xff) << 8)
                | (((long) bytes[offset + 2] & 0xff) << 16)
                | (((long) bytes[offset + 3] & 0xff) << 24)
                | (((long) bytes[offset + 4] & 0xff) << 32)
                | (((long) bytes[offset + 5] & 0xff) << 40)
                | (((long) bytes[offset + 6] & 0xff) << 48)
                | (((long) bytes[offset + 7] & 0xff) << 56);
    }
}