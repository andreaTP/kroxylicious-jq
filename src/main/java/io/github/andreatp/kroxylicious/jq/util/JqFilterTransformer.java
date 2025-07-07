package io.github.andreatp.kroxylicious.jq.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import io.github.andreatp.kroxylicious.jq.config.JqFilterConfig;
import io.roastedroot.jq4j.Jq;
import org.apache.kafka.common.compress.Compression;
import org.apache.kafka.common.message.FetchResponseData;
import org.apache.kafka.common.message.ProduceRequestData;
import org.apache.kafka.common.record.AbstractRecords;
import org.apache.kafka.common.record.MemoryRecordsBuilder;
import org.apache.kafka.common.record.Record;
import org.apache.kafka.common.record.RecordBatch;
import org.apache.kafka.common.utils.ByteBufferOutputStream;

import io.kroxylicious.proxy.filter.FilterContext;

/**
 * Transformer class for the jq filters. Provides static transform functions for find-and-replace
 * transformation of data in ProduceRequests and FetchResponses.
 */
public class JqFilterTransformer {

    /**
     * Transforms the given partition data according to the provided configuration.
     * @param partitionData the partition data to be transformed
     * @param context the context
     * @param config the transform configuration
     */
    public static void transform(ProduceRequestData.PartitionProduceData partitionData, FilterContext context, JqFilterConfig config) {
        partitionData.setRecords(transformPartitionRecords((AbstractRecords) partitionData.records(), context, config.getJqFilter()));
    }

    /**
     * Transforms the given partition data according to the provided configuration.
     * @param partitionData the partition data to be transformed
     * @param context the context
     * @param config the transform configuration
     */
    public static void transform(FetchResponseData.PartitionData partitionData, FilterContext context, JqFilterConfig config) {
        partitionData.setRecords(transformPartitionRecords((AbstractRecords) partitionData.records(), context, config.getJqFilter()));
    }

    /**
     * Performs find-and-replace transformations on the given partition records.
     * @param records the partition records to be transformed
     * @param context the context
     * @param replacerModule the wasm replacer module to be used
     * @return the transformed partition records
     */
    private static AbstractRecords transformPartitionRecords(AbstractRecords records, FilterContext context, String replacerModule) {
        if (records.batchIterator().hasNext()) {
            ByteBufferOutputStream stream = context.createByteBufferOutputStream(records.sizeInBytes());
            MemoryRecordsBuilder newRecords = createMemoryRecordsBuilder(stream, records.firstBatch());

            for (RecordBatch batch : records.batches()) {
                for (Record batchRecord : batch) {
                    newRecords.append(batchRecord.timestamp(), batchRecord.key(), transformRecord(batchRecord.value(), replacerModule),
                            batchRecord.headers());
                }
            }
            return newRecords.build();
        }
        return records;
    }

    /**
     * Performs a find-and-replace transformation of a given record value.
     * @param in the record value to be transformed
     * @param jqFilter the filter expression to be executed by Jq
     * @return the transformed record value
     */
    private static ByteBuffer transformRecord(ByteBuffer in, String jqFilter) {
        String originalString = new String(StandardCharsets.UTF_8.decode(in).array());

        var result = Jq.builder()
                .withArgs("-M", "--compact-output", jqFilter)
                .withStdin(originalString.getBytes(StandardCharsets.UTF_8))
                .run();

        if (result.success()) {
            return ByteBuffer.wrap(result.stdout());
        } else {
            result.printStdout();
            result.printStderr();
            throw new RuntimeException("Failed to execute jq command");
        }
    }

    /**
     * Instantiates a MemoryRecordsBuilder object using the given stream. This duplicates some of the
     * functionality in io.kroxylicious.proxy.internal, but we aren't supposed to import from there.
     */
    private static MemoryRecordsBuilder createMemoryRecordsBuilder(ByteBufferOutputStream stream, RecordBatch firstBatch) {
        return new MemoryRecordsBuilder(stream, firstBatch.magic(), Compression.of(firstBatch.compressionType()).build(), firstBatch.timestampType(), firstBatch.baseOffset(),
                firstBatch.maxTimestamp(), firstBatch.producerId(), firstBatch.producerEpoch(), firstBatch.baseSequence(), firstBatch.isTransactional(),
                firstBatch.isControlBatch(), firstBatch.partitionLeaderEpoch(), stream.remaining());
    }
}
