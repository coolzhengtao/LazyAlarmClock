package net.rabraffe.lazyalarmclock.activities;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import net.rabraffe.lazyalarmclock.R;
import net.rabraffe.lazyalarmclock.entities.AlarmScheme;
import net.rabraffe.lazyalarmclock.events.AlarmUpdateEvent;
import net.rabraffe.lazyalarmclock.events.CloseAllActivityEvent;
import net.rabraffe.lazyalarmclock.utils.EventBus;

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
    private NotificationManager notificationManager;    //提醒服务
    private boolean isClear;                            //是否释放.

    private Date dtStart;                               //闹钟开始的时间
    private Date dtEnd;                                 //闹钟结束的时间

    private TimeOutTask timeOutTask = new TimeOutTask();        //超时任务
    private boolean isVibrateOn = true;                 //震动是否打开

    @OnClick(R.id.btnStopVibrate)
    public void btnStopVibrateClick(View view) {
        timeOutTask.cancel(true);
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
            if (isVibrateOn) {
                vibrator.cancel();
            }
            mediaPlayer.stop();
            //关闭传感器
            sensorManager.unregisterListener(listener);
            wakeLock.release();
            //判断是否是单次闹钟
            if (getIntent().getStringExtra("once").equals("1")) {
                //禁用单次闹钟
                AlarmScheme.getInstance().disableAlarm(getIntent().getStringExtra("uuid"));
                //刷新界面
                EventBus.getInstance().post(new AlarmUpdateEvent());
            }
            //设置下一个闹钟
            AlarmScheme.getInstance().setNextAlarm();
            isClear = true;
            EventBus.getInstance().post(new CloseAllActivityEvent());
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
        timeOutTask.cancel(true);
        alarmClockOff();
    }

    //初始化控件
    private void initView() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);         //获取加速度感应器
        listener = new SensorValueListener();
        uriAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);   //获取默认的铃声URI
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
        isVibrateOn = getIntent().getStringExtra("isVibrate").equals("1");
    }

    //打开闹钟
    private void alarmClockOn() {
        //启用超时
        timeOutTask.execute(60000);         //超时60秒自动关闭
        //开始计时
        dtStart = new Date();
        if (isVibrateOn) {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] fVibrate = new long[]{0, 2000, 1000};
            vibrator.vibrate(fVibrate, 0);
        }
        //启动铃声
        mediaPlayer.start();
        //启动感应器
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        //亮屏并且解锁
        keyguardManager.newKeyguardLock("").disableKeyguard();
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "lock");
        wakeLock.acquire();
        //提示Notifacation
        Notification notification = new NotificationCompat.Builder(this).setTicker("闹钟响了")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("闹钟响了").setContentTitle("懒人闹钟").build();
        notificationManager.notify(1, notification);
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
                timeOutTask.cancel(true);
                alarmClockOff();
            } else if (!isUp && mLast > 6.0f) {
                timeOutTask.cancel(true);
                alarmClockOff();
            }
            mLast = value;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    /**
     * 超时异步任务
     */
    private class TimeOutTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                //cancel task
                Log.e("线程中断", "");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            alarmClockOff();
        }
    }
}
