package org.example.rabbitmq.producer.partitioner;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ProductDataPartitioner implements Partitioner {

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> result = new HashMap<>(gridSize);

		// Divida o arquivo CSV em várias partições
		for (int i = 0; i < gridSize; i++) {
			ExecutionContext context = new ExecutionContext();
			context.put("startLine", i * 10);  // Início da partição
			context.put("endLine", (i + 1) * 10);  // Fim da partição
			result.put("partition" + i, context);
		}
		return result;
	}

//	@Override
//	public Map<String, ExecutionContext> partition(int gridSize) {
//		// Define the range of the data
//		int min = 1;
//
//		// Hardcoded for the Part-2; In real-world, this should be calculated dynamically
//		int max = 30; // 12461
//		int targetSize = (max - min) / gridSize + 1;//500
//
//		// Map to store the partitions
//		Map<String, ExecutionContext> result = new HashMap<>();
//
//		// Initialize the partition index and starting point
//		int partitionIndex = 0;
//		int currentStart = min;
//		int currentEnd = currentStart + targetSize - 1;
//		while (currentStart <= max) {
//			// Create a new ExecutionContext for the current partition
//			ExecutionContext value = new ExecutionContext();
//			result.put("partition" + partitionIndex, value);
//
//			if (currentEnd >= max) {
//				currentEnd = max;
//			}
//
//			// Store the partition details
//			value.putInt("minValue", currentStart);
//			value.putInt("maxValue", currentEnd);
//			log.debug("Partition {}: start={}, end={}", partitionIndex, currentStart, currentEnd);
//
//			// Update the start point for the next partition
//			currentStart += targetSize;
//			currentEnd += targetSize;
//			partitionIndex++;
//		}
//		return result;
//	}
}
