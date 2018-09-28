### PictureInPicture（画中画）

效果：

[效果一](https://github.com/Wzhixiang/PictureInPicture/blob/master/ScreenCapture/device-2018-09-28-161203.png)
[效果二](https://github.com/Wzhixiang/PictureInPicture/blob/master/ScreenCapture/device-2018-09-28-161237.png)

如何实现画中画？
 
1、在Manifest.xml中添加支持画中画模式(supportsPictureInPicture)

        <activity
            android:name=".MainActivity"
            android:configChanges="screenSize|smallestScreenSize|screenLayout|orientation"
            android:supportsPictureInPicture="true" />
            
2、在module中引入相关支持包：
        
        implementation 'com.android.support:support-media-compat:$support_version'
            
3、在声明支持的Activity中进入画中画模式enterPictureInPictureMode(PictureInPictureParams)

        val mPictureInPictureParamsBuilder = PictureInPictureParams.Builder()
        enterPictureInPictureMode(mPictureInPictureParamsBuilder.build())
        
关于[PictureInPictureParams.Builder详细介绍](https://developer.android.google.cn/reference/android/app/PictureInPictureParams.Builder?hl=zh-tw)：
        
        * setAspectRatio：设置宽高比
        * setActions：设置用户操作行为
        * setSourceRectHint：设置源边界提示
        
注意事项：
        
        * 需要在版本Android 8.0 26上才能使用
        * 进入画中画模式后，生命周期变化 ->onPause， 重新回到MainActivity，生命周期->onResume
        
参考：
        
[视频播放器](https://github.com/lipangit/JiaoZiVideoPlayer)

[PictureInPicture](https://github.com/googlesamples/android-PictureInPicture)
        