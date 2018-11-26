package com.example;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;

import static com.example.MessyCode.isMessyCode;

public class StringCodeUtil {
    static void test() throws UnsupportedEncodingException {

        byte[] bbb = new byte[]{68, 70, -17, -65, -67, -17, -65, -67, -17, -65, -67, -17, -65, -67};
        String label = new String(bbb);//"DF����";

        String[] set1 = {"-", "ASCII", "big5", "UTF-8", "GBK", "GB2312", "GB18030",
                "US-ASCII", "ISO_8859_1",
                "CP936", "UTF-16", "UTF-16BE", "UTF-16LE"};

        Collection<String> setAll = Charset.availableCharsets().keySet();
//        label = "ch车和家"; "chM-hM-=M-&M-eM-^RM-^LM-eM-.M-6"
        for (String s1 : setAll) {
            for (String s2 : setAll) {
//                if (s1.equals(s2)) continue;
                try {
                    String v = change(label, s1, s2);
                    if (isMessyCode(v)) continue;
                    if (!v.startsWith("DF")) continue;
                    log(String.format("  %s -> %s : %b ", s1, s2, isMessyCode(v)) + v);
                    if (v.equals("DF东方")) {
                        log("终于找到了. " + s1 + s2);
                        return;
                    }
                } catch (UnsupportedEncodingException | UnsupportedOperationException e) {
                    log(s1 + s2 + ": " + e);
                }
            }
        }

        log("diff byte :" + byte2Str("DF东方".getBytes()));
        log("diff byte :" + byte2Str("DF����".getBytes("GBK")));
    }


    private static String byte2Str(byte[] b) {
        StringBuilder s = new StringBuilder();
        for (byte bb : b) {
            s.append(bb).append(',');
        }
        return s.toString();
    }

    private static String change(String value, String set1, String set2) throws UnsupportedEncodingException {
        byte[] b = "-".equals(set1) ? value.getBytes() : value.getBytes(set1);
        return "-".equals(set2) ? new String(b) : new String(b, set2);
    }

    private static void log(String s) {
        System.out.println(s);
    }
}
