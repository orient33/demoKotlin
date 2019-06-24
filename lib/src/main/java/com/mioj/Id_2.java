package com.mioj;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Id_2 {
    public static void main(String... args) {
        Scanner scan = new Scanner(System.in);
        String line;
        while (scan.hasNextLine()) {
            line = scan.nextLine().trim();
            // please write your code here
            showOneLine(line);
//            showOneLine("89 23 12 74 67 89 23 67 12");
            // System.out.println("answer");
        }
    }

    private static void showOneLine(String line) {
        String number;
        short num;
        int index;
        List<Short> bytes = new ArrayList<>();
        int sepIndex;
        while (line.length() > 0) {
            sepIndex = line.indexOf(' ');
            if (sepIndex != -1) {
                number = line.substring(0, sepIndex);
            } else {
                number = line;
            }
            try {
                num = Short.valueOf(number);
                if ((index = bytes.indexOf(num)) != -1) { // has num already
                    bytes.remove(index);
                } else { // has no num
                    bytes.add(num);
                }
            } catch (NumberFormatException e) {
                System.out.println("number not short! " + e);
                break;
            }
            if (sepIndex == -1) {
                break;
            }
            if (sepIndex + 1 >= line.length()) {
                System.out.println(" sepIndex + 1 >= length.");
                break;
            }
            line = line.substring(sepIndex + 1);
        }
        if (bytes.size() == 1) {
            System.out.println(bytes.get(0));
        } else {
            System.out.println(" more than one single.." + bytes.size());
        }
    }
}
