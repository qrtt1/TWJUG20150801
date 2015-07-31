package org.qty.aws.lambda.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class FFMpeg {

    final static String FFMPEG_COMMAND_PATH = getApplicationPath("ffmpeg");
    final static String FFPROBE_COMMAND_PATH = getApplicationPath("ffprobe");

    private static String getApplicationPath(String application) {
        return new File(System.getenv("LAMBDA_TASK_ROOT"), application).getAbsolutePath();
    }

    public int getMediaDuration(String media) throws FileNotFoundException, IOException {
        String result = new ApplicationExecutor(FFPROBE_COMMAND_PATH, "-i", media).execute(0);
        String durationLine = StringUtils.trimToEmpty(StringUtils.substringBetween(result, "Duration:", ", start:"));
        return getDurationInSeconds(durationLine);
    }

    public File captureImage(String media, int offset) throws FileNotFoundException, IOException {
        File outputFile = new File("/tmp/" + UUID.randomUUID().toString() + ".png");
        outputFile.delete();

        new ApplicationExecutor(FFMPEG_COMMAND_PATH, 
                new String[] { 
                    "-ss", String.valueOf(offset), 
                    "-i", media, 
                    "-vframes", "1", 
                    "-f", "image2", outputFile.getAbsolutePath() }).execute(0);

        if (outputFile.exists() && outputFile.length() > 0) {
            return outputFile;
        }
        outputFile.delete();

        return null;
    }

    private int getDurationInSeconds(String durationString) {

        String[] d = durationString.split(":");
        if (d.length != 3) {
            return 0;
        }

        int hourToSec = NumberUtils.toInt(d[0], 0) * 60 * 60;
        int minToSec = NumberUtils.toInt(d[1], 0) * 60;

        String sec = d[2].split("[.]")[0];
        int secTosec = NumberUtils.toInt(sec, 0);

        return hourToSec + minToSec + secTosec;
    }
}
