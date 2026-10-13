package com.example.labsapp.archive;

import java.io.File;

public class ParallelArchiver {

    public interface ProgressCallback {
        void onProgress(int percent);
        void onFinish(ArchiveResult result);
    }

    public static void compressAsync(File input, File output, ProgressCallback callback) {

        new Thread(() -> {
            try {
                long start = System.currentTimeMillis();
                long originalSize = input.length();

                java.io.FileInputStream fis = new java.io.FileInputStream(input);
                java.io.FileOutputStream fos = new java.io.FileOutputStream(output);
                java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(fos);

                zos.putNextEntry(new java.util.zip.ZipEntry(input.getName()));

                byte[] buffer = new byte[4096];
                int length;
                long totalRead = 0;

                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                    totalRead += length;

                    int percent = (int) ((totalRead * 100) / originalSize);
                    callback.onProgress(percent);
                }

                zos.closeEntry();
                zos.close();
                fis.close();

                long end = System.currentTimeMillis();

                ArchiveResult result =
                        new ArchiveResult(end - start, originalSize, output.length());

                callback.onFinish(result);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}