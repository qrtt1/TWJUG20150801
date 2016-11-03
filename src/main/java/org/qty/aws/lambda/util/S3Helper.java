package org.qty.aws.lambda.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class S3Helper {
    final static Gson gson = new Gson();
    final static JsonParser jsonParser = new JsonParser();
    final static AmazonS3 amazonS3 = new AmazonS3Client();

    public static String getURLFromS3Entity(S3Entity entity) {
        return amazonS3.generatePresignedUrl(entity.getBucket().getName(), entity.getObject().getKey(),
                expirationAfterOneHour()).toString().replace("https", "http");
    }

    public static void generateJobObject(String bucket, String content) {
        ObjectMetadata metadata = new ObjectMetadata();
        byte[] data = content.getBytes();
        metadata.setContentLength(data.length);

        amazonS3.putObject(bucket, "jobs/" + UUID.randomUUID().toString(), new ByteArrayInputStream(data), metadata);
    }

    public static JsonObject getObjectAsJsonObject(String bucket, String object) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = amazonS3.getObject(bucket, object).getObjectContent();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, output);

            return jsonParser.parse(new String(output.toByteArray())).getAsJsonObject();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

    public static String getURLFromJsonObject(JsonObject job) throws AmazonClientException {
        return amazonS3.generatePresignedUrl(job.get("bucket").getAsString(), job.get("key").getAsString(),
                expirationAfterOneHour()).toString().replace("https", "http");
    }

    public static void putObjectWithPublicRead(String bucket, String key, File file) {
        PutObjectRequest request = new PutObjectRequest(bucket, key, file);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3.putObject(request);
    }

    public static void removeObject(String bucket, String key) {
        amazonS3.deleteObject(bucket, key);
    }

    private static Date expirationAfterOneHour() {
        return Date.from(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC));
    }
}
