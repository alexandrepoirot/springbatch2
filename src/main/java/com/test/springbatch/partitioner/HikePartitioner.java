package com.test.springbatch.partitioner;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

public class HikePartitioner implements Partitioner {

    private final int minId;
    private final int maxId;
    private final int partitionSize;

    public HikePartitioner(int minId, int maxId, int partitionSize) {
        this.minId = minId;
        this.maxId = maxId;
        this.partitionSize = partitionSize;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();

        int start = minId;
        int end = start + partitionSize - 1;

        int partitionNumber = 0;
        while (start <= maxId) {
            ExecutionContext context = new ExecutionContext();
            context.putInt("minId", start);
            context.putInt("maxId", Math.min(end, maxId));
            partitions.put("partition" + partitionNumber, context);

            start += partitionSize;
            end += partitionSize;
            partitionNumber++;
        }

        return partitions;
    }
}