package com.iminer.memoryinfo;

public final class MemoryInfo {
    /**
     * 最大堆内存大小，单位字节
     */
    public long maxSize;
    /**
     * 空闲堆内存大小，单位字节
     */
    public long freeSize;
    /**
     * 当前已用堆内存大小，单位字节
     */
    public long totalSize;
    public long nativeHeapAllocatedSize;
    public long nativeHeapFressSize;
    public long nativeHeapSize;
}
