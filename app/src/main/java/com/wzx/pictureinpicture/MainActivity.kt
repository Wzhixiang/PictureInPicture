package com.wzx.pictureinpicture

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Rational
import cn.jzvd.Jzvd
import kotlinx.android.synthetic.main.activity_main.*

/**
 * PictureInPicture（画中画）
 *
 * Google关于PictureInPicturedemo：
 * https://github.com/googlesamples/android-PictureInPicture
 */
class MainActivity : AppCompatActivity() {

    companion object {
        /** Intent action for media controls from Picture-in-Picture mode.  */
        private val ACTION_MEDIA_CONTROL = "media_control"

        /** Intent extra for media controls from Picture-in-Picture mode.  */
        private val EXTRA_CONTROL_TYPE = "control_type"

        /** The request code for play action PendingIntent.  */
        private val REQUEST_PLAY = 1

        /** The request code for pause action PendingIntent.  */
        private val REQUEST_PAUSE = 2

        /** The intent extra value for play action.  */
        private val CONTROL_TYPE_PLAY = 1

        /** The intent extra value for pause action.  */
        private val CONTROL_TYPE_PAUSE = 2

    }

    private val mPictureInPictureParamsBuilder = PictureInPictureParams.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log("onCreate")

        videoPlayer.setUp("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4", "Jazz Durms", Jzvd.SCREEN_WINDOW_NORMAL)

        pictureInPictureMode.setOnClickListener {
            minimize()
        }

    }

    /**
     * 更新画中画操作行为
     */
    fun updateActions(@DrawableRes iconId: Int, @NonNull title: String, @Nullable description: String?,
                      @NonNull requestCode: Int, @NonNull controlCode: Int) {
        var actions = arrayListOf<RemoteAction>()

        val intent = PendingIntent.getBroadcast(this@MainActivity,
                requestCode, Intent(ACTION_MEDIA_CONTROL).putExtra(EXTRA_CONTROL_TYPE, controlCode),
                0)

        actions.add(RemoteAction(
                Icon.createWithResource(this@MainActivity, iconId),
                title,
                description ?: title,
                intent))

        mPictureInPictureParamsBuilder.setActions(actions)
        setPictureInPictureParams(mPictureInPictureParamsBuilder.build())
    }

    /**
     * 启用画中画
     */
    fun minimize() {
        //设置宽高比
        mPictureInPictureParamsBuilder.setAspectRatio(Rational(videoPlayer.width, videoPlayer.height))
        enterPictureInPictureMode(mPictureInPictureParamsBuilder.build())
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    /**
     * 画中画模式变化时回调方法
     */
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            //全屏
            videoPlayer.startWindowFullscreen()
            registerReceiver(mReceiver, IntentFilter(ACTION_MEDIA_CONTROL))
        } else {
            //退出全屏
            Jzvd.quitFullscreenOrTinyWindow()
            unregisterReceiver(mReceiver)
        }
    }

    /**
     * 接收播放行为
     */
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            intent?.let {
                if (intent?.action != ACTION_MEDIA_CONTROL) {
                    return
                }

                val controlType = intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)
                when (controlType) {
                    CONTROL_TYPE_PLAY -> {
                        Jzvd.goOnPlayOnResume()
                        //更新画中画的行为控件
                        updateActions(R.drawable.ic_stop_white_24dp, "stop", null, REQUEST_PAUSE, CONTROL_TYPE_PAUSE)
                    }
                    CONTROL_TYPE_PAUSE -> {
                        Jzvd.goOnPlayOnPause()
                        //更新画中画的行为控件
                        updateActions(R.drawable.ic_play_arrow_white_24dp, "play", null, REQUEST_PLAY, CONTROL_TYPE_PLAY)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
        videoPlayer.startVideo()
        updateActions(R.drawable.ic_stop_white_24dp, "stop", null, REQUEST_PAUSE, CONTROL_TYPE_PAUSE)
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onRestart() {
        super.onRestart()
        log("onRestart")
    }

    override fun onPause() {
        super.onPause()
        log("onPause")
        if (!isInPictureInPictureMode) {
            Jzvd.releaseAllVideos()
        }
    }

    override fun onStop() {
        log("onStop")
        super.onStop()
    }

    override fun onDestroy() {
        log("onDestroy")
        super.onDestroy()
    }

    fun log(msg: String) {
        Log.i("MainActivity", msg)
    }
}
