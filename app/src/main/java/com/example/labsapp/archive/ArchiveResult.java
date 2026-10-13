package com.example.labsapp.archive;

public class ArchiveResult {
    private final long timeMs;
    private final long originalSize;
    private final long compressedSize;

    public ArchiveResult(long timeMs, long originalSize, long compressedSize) {
        this.timeMs = timeMs;
        this.originalSize = originalSize;
        this.compressedSize = compressedSize;
    }

    public long getTimeMs() { return timeMs; }
    public long getOriginalSize() { return originalSize; }
    public long getCompressedSize() { return compressedSize; }
}