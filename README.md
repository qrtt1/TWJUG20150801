[![works badge](https://cdn.rawgit.com/nikku/works-on-my-machine/v0.2.0/badge.svg)](https://github.com/nikku/works-on-my-machine)

## 活動網址

* 活動頁面 
 * kktix http://twjug.kktix.cc/events/twjug201508
 * meetup http://www.meetup.com/taiwanjug/events/224070599/

* 投影片 https://speakerdeck.com/qrtt1/introducing-aws-lambda-for-java-developer

* AWS 介紹影片
 * AWS re:Invent 2014 | Announcing AWS Lambda https://www.youtube.com/watch?v=9eHoyUVo-yg
 * AWS re:Invent 2014 | (MBL202) NEW LAUNCH: Getting Started with AWS Lambda https://www.youtube.com/watch?v=UFj27laTWQA

## Demo 1 HelloLambda

實作 1 個簡易的『外部命令』執行程式 [HelloLambda](https://github.com/qrtt1/TWJUG20150801/blob/master/src/main/java/org/qty/aws/lambda/HelloLambda.java)，透過它來查看 Lambda 執行環境的狀態。

在這個例子，展示了 RequestResponse 執行模式的 2 種呼叫方法：

1. 直接使用 API 呼叫 Lambda Function [LowLevelInvoker](https://github.com/qrtt1/TWJUG20150801/blob/master/src/main/java/org/qty/aws/lambda/invoker/LowLevelInvoker.java)
1. 使用 Service Interface 呼叫 Lambda Function [ServiceProxyInvoker](https://github.com/qrtt1/TWJUG20150801/blob/master/src/main/java/org/qty/aws/lambda/invoker/ServiceProxyInvoker.java)

最後，以 [PeepLambdaEnvironment](https://github.com/qrtt1/TWJUG20150801/blob/master/src/main/java/org/qty/aws/lambda/invoker/PeepLambdaEnvironment.java) 窺探 Lambda 執行環境。

## Demo 2 Lambda With Native Applications

### 打包注意事項

Lambda 允許使用者將 Native Library 或 Native Application 包進 deploy package 裡，唯一需要注意的是在 Linux 上，無論是執行程式本身或 Shared Object 都需設為可執行權限，可參考 [build.gradle](https://github.com/qrtt1/TWJUG20150801/blob/master/build.gradle) 的設定：

```groovy
task buildZip(type: Zip) {
    from compileJava
    from processResources              
    into('lib') {
        from configurations.runtime
    }           
    eachFile { 
        if (it.name.contains(".so.")) fileMode 0755
        if (it.name.contains("ffprobe")) fileMode 0755
        if (it.name.contains("ffmpeg")) fileMode 0755
    }
}
```

### 範例情境

1. 使用者上傳影片至 S3 Bucket 下的 `/videos` prefix。將 Object Creation Events 綁定 `/videos` prefix 與 `.mp4` 副檔名，指定給 `VideoPreviewTaskGenerator` Lambda Function
1. `VideoPreviewTaskGenerator` 收到通知後，啟動 ffprobe 取得影片長度，並產生 job file 至 `/jobs`
1. `VideoPreviewGenerator` Lambda Function 綁定 `/jobs` 下的 Objec Creation Events，並依 job file 產生縮圖至 `/images` prefix 內。

#### job file

job file 包含產截圖需要的資訊，例如 `offset` 是指定第幾秒，而 `image_key` 是決定存檔位置：

```json
{
    "bucket": "qty.lambda.video",
    "offset": 60,
    "image_key": "images/sample.mp4.2.png",
    "key": "videos/sample.mp4"
}
```
