package org.qty.aws.lambda.video;

import java.io.File;

import org.qty.aws.lambda.util.FFMpeg;
import org.qty.aws.lambda.util.S3Helper;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.util.json.JSONObject;

public class VideoPreviewGenerator {

    public void createPreviewImage(S3Event input, Context context) throws Exception {

        FFMpeg ffmpeg = new FFMpeg();
        for (S3EventNotificationRecord record : input.getRecords()) {
            String bucket = record.getS3().getBucket().getName();
            String object = record.getS3().getObject().getKey();

            JSONObject json = S3Helper.getObjectAsJsonObject(bucket, object);
            File outputFile = ffmpeg.captureImage(S3Helper.getURLFromJsonObject(json), json.getInt("offset"));
            S3Helper.putObjectWithPublicRead(bucket, json.getString("image_key"), outputFile);
            S3Helper.removeObject(bucket, object);
        }
    }

}
