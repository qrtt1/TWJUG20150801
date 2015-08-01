package org.qty.aws.lambda.video;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.qty.aws.lambda.util.FFMpeg;
import org.qty.aws.lambda.util.S3Helper;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class VideoPreviewTaskGenerator {

    public void dispatchJobs(S3Event input, Context context) throws Exception {
        FFMpeg ffmpeg = new FFMpeg();
        for (S3EventNotificationRecord record : input.getRecords()) {
            generateJobs(record, ffmpeg.getMediaDuration(S3Helper.getURLFromS3Entity(record.getS3())));
            context.getLogger().log("generated s3://" 
                + record.getS3().getBucket().getName() + "/"
                + record.getS3().getObject().getKey());
        }
    }

    private void generateJobs(S3EventNotificationRecord record, int seconds) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            for (int offset = 0, index = 0; offset < seconds; offset += 30, index++) {
                generateJobFile(record, executor, offset, index);
            }
        } finally {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    private void generateJobFile(S3EventNotificationRecord record, ExecutorService executor, int offset, int index)
            throws JSONException {

        String key = record.getS3().getObject().getKey();
        String job = new JSONObject().put("bucket", record.getS3().getBucket().getName()).put("key", key)
                .put("offset", offset).put("image_key", toImageKey(key, index)).toString();

        CompletableFuture.runAsync(() -> {
            S3Helper.generateJobObject(record.getS3().getBucket().getName(), job);
        }, executor);
    }

    private String toImageKey(String key, int index) {
        String name = StringUtils.substringAfterLast(key, "/");
        return String.format("images/%s.%s.png", name, index);
    }

}
