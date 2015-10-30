package net.rabraffe.lazyalarmclock.activities;

import android.app.KeyguardManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import net.rabraffe.lazyalarmclock.R;
import net.rabraffe.lazyalarmclock.entities.AlarmScheme;

import java.io.IOException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlarmActivity extends AppCompatActivity {
    @Bind(R.id.btnStopVibrate)
    Button btnStopVibrate;

    private Vibrator vibrator;                          //震动控制器
    private SensorManager sensorManager;                //传感器管理
    private PowerManager powerManager;                  //电源管理
    private PowerManager.WakeLock wakeLock;             //屏幕锁
    private KeyguardManager keyguardManager;            //屏幕锁管理
    private SensorValueListener listener;               //传感器事件
    private Sensor sensor;                              //传感器对象
    private Uri uriAlarm;                               //闹钟铃声的URI
    private MediaPlayer mediaPlayer;                    //媒体播放器
    private AudioManager audioManager;                  //音频管理服务
    private WindowManager windowManager;                //Window服务
    private boolean isClear;                            //是否释放.

    private Date dtStart;                               //闹钟开始的时间
    private Date dtEnd;                                 //闹钟结束的时间

    @OnClick(R.id.btnStopVibrate)
    public void btnStopVibrateClick(View view) {
        alarmClockOff();
    }

    /**
     * 关闭闹钟
     */
    private void alarmClockOff() {
        if (!isClear) {
            dtEnd = new Date();
            long lTimeDiff = dtEnd.getTime() - dtStart.getTime();
            float dSeconds = lTimeDiff / 1000.0f;
            vibrator.cancel();
            mediaPlayer.stop();
            //关闭传感器
            sensorManager.unregisterListener(listener);
            wakeLock.release();
            //设置下一个闹钟
            AlarmScheme.getInstance().setNextAlarm();
            isClear = true;
            this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        initView();
        alarmClockOn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭传感器
        alarmClockOff();
    }

    //初始化控件
    private void initView() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);         //获取重力感应器
        listener = new SensorValueListener();
        uriAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);   //获取默认的铃声URI
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, uriAlarm);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //打开闹钟
    private void alarmClockOn() {
        //关闭当前闹钟
//        Alarms.getInstance().disableAlarm(getIntent().getStringExtra("uuid"));
        //开始计时
        dtStart = new Date();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] fVibrate = new long[]{1000, 2000};
        vibrator.vibrate(fVibrate, 0);
        //启动铃声
        mediaPlayer.start();
        //启动感应器
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        //亮屏并且解锁
        keyguardManager.newKeyguardLock("").disableKeyguard();
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "lock");
        wakeLock.acquire();
        //按钮震动
    }

    //传感器监听类
    private class SensorValueListener implements SensorEventListener {
        float mLast;
        float value;
        boolean isUp;

        @Override
        public void onSensorChanged(SensorEvent event) {
            value = event.values[2];        //取重力的值
            if (value > 0 && mLast == 0.0f) {
                isUp = true;
            }
            if (isUp && mLast < -6.0f) {
                alarmClockOff();
            } else if (!isUp && mLast > 6.0f) {
                alarmClockOff();
            }
            mLast = value;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
