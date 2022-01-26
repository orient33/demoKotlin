package com.example;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipDemo {

    public static void main(String... arg) {
        String zipPath = "/home/dun/文档/tmp-0s/0feabce3-1e5c-4b80-ba7d-5592a74b3100.mtz";
        String targetFolder = "/home/dun/文档/tmp-0s/temp";

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipPath);
            @SuppressWarnings("unchecked")
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();
            File root = new File(targetFolder);
            root.mkdirs();
            System.out.println("print file path "+zipFile.getName());
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
//                String path = targetFolder + name;
                if (entry.isDirectory()) {
                    System.out.println(" dir " + name);
//                    new File(path).mkdirs();
                } else {
                    //偶尔 file与dir顺序无法保证 (部分zip文件)
                    System.out.println(" file " + entry);
//                    Files.copy(zipFile.getInputStream(entry), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
                    //                    String hash = writeTo(zipFile.getInputStream(entry), path, listner != null);
                }
            }
        } catch (Exception e) {
            //            Log.i(TAG, e.toString());
            //            System.out.println("");
            e.printStackTrace(System.out);
        } finally {
            //            Utils.closeQuietly(zipFile);
        }
    }

}
