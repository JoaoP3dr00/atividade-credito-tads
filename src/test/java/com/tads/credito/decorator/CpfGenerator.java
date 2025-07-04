package com.tads.credito.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CpfGenerator {
    private static final Random random = new Random();

    public static List<String> generateCpfList(int quantity) {
        List<String> cpfs = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            cpfs.add(generateCpf());
        }
        return cpfs;
    }

    public static String generateCpf() {
        int n1 = random.nextInt(10);
        int n2 = random.nextInt(10);
        int n3 = random.nextInt(10);
        int n4 = random.nextInt(10);
        int n5 = random.nextInt(10);
        int n6 = random.nextInt(10);
        int n7 = random.nextInt(10);
        int n8 = random.nextInt(10);
        int n9 = random.nextInt(10);

        int d1 = calcDigit(n1, n2, n3, n4, n5, n6, n7, n8, n9);
        int d2 = calcDigit(n1, n2, n3, n4, n5, n6, n7, n8, n9, d1);

        return String.format("%d%d%d.%d%d%d.%d%d%d-%d%d",
                n1, n2, n3, n4, n5, n6, n7, n8, n9, d1, d2);
    }

    private static int calcDigit(int... nums) {
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i] * ((nums.length + 1) - i);
        }
        int mod = sum % 11;
        return mod < 2 ? 0 : 11 - mod;
    }
}
