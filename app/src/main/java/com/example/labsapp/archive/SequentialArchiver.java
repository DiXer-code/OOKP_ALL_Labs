package com.example.labsapp.archive;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SequentialArchiver {

    public static ArchiveResult compress(File inputFile, File outputFile) throws Exception {

        long start = System.currentTimeMillis();

        long originalSize = inputFile.length();

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            ZipEntry entry = new ZipEntry(inputFile.getName());
            zos.putNextEntry(entry);

            byte[] buffer = new byte[4096];
            int length;

            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }

        long end = System.currentTimeMillis();
        long compressedSize = outputFile.length();

        return new ArchiveResult(end - start, originalSize, compressedSize);
    }
}