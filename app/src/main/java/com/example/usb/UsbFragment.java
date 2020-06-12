package com.example.usb;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.TopUtilKt;
import com.example.kotlindemo.R;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author dundongfang on 2018/9/28.
 */
public class UsbFragment extends Fragment {

    private TextView text;

    private Context mContext;
    private Calendar mCalendar = Calendar.getInstance(TimeZone.getDefault(), Locale.UK);

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_usb, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        text = view.findViewById(R.id.text);
        text.setText("--------U盘相关action-----------");
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_BUTTON);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addAction(Intent.ACTION_MEDIA_NOFS);
        filter.addAction(Intent.ACTION_MEDIA_SHARED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");// 加上这个 使得USB广播不能收到.
        mContext.registerReceiver(mediaReceiver, filter);
        IntentFilter filter2 = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter2.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter2.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter2.addAction(Intent.ACTION_HEADSET_PLUG);//耳机插拔
        filter2.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);//耳机拔出? 包括蓝牙么
        mContext.registerReceiver(usbReceiver, filter2);
        appendMsg("\n 已注册相关广播. " + (filter.countActions() + filter2.countActions()));
        loadUSBfile();
        //load headset state.
        loadHeadset();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContext.unregisterReceiver(mediaReceiver);
        mContext.unregisterReceiver(usbReceiver);
    }

    private void appendMsg(String msg) {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        String time = mCalendar.get(Calendar.MINUTE) + " : " + mCalendar.get(Calendar.SECOND);
        text.append("\n" + msg + " . 时间: " + time);
        Log.i("df", msg + " . 时间: " + time);
    }

    private void scanDelay() {
        text.postDelayed(() -> {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    if (mUsb != null) {
                        appendMsg("发送扫描广播: " + mUsb.getPath());
                        intent.setData(Uri.fromFile(mUsb));
                    }
                    mContext.sendBroadcast(intent);
                },
                3000);
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
                if (intent.hasExtra("state")) {
                    int state = intent.getIntExtra("state", 0);
                    appendMsg("headset :" + (state == 1 ? "插入耳机" : "拔出耳机"));
                } else {
                    appendMsg("headset action. no extra!");
                }
            } else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())){
                appendMsg("ACTION_AUDIO_BECOMING_NOISY,");
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
                appendMsg("U盘已插入..");
                scanDelay();
            } else {
                appendMsg("接收到 U盘已拔出");
            }
        }
    };
    private final BroadcastReceiver mediaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            appendMsg("接收到 " + intent.getAction());
            if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
                loadUsbName(context);
            }
        }
    };

    @TargetApi(24)
    private void loadUsbName(Context c) {
        StorageManager sm = c.getSystemService(StorageManager.class);
        List<StorageVolume> list = sm.getStorageVolumes();
        StringBuilder sb = new StringBuilder("storage: " + list.size() + " :\n");
        for (StorageVolume sv : list) {
            if (sv.isPrimary() || sv.isEmulated()) continue;//忽略 emulated的
            sb.append(",state=").append(sv.getState())
                    .append(",primary=").append(sv.isPrimary())
                    .append(",emulated=").append(sv.isEmulated())
                    .append(",removable=").append(sv.isRemovable())
                    .append(",uuid=").append(sv.getUuid())
                    .append(",description=").append(sv.getDescription(c));//即为label,英文可正常读取.
        }
        appendMsg(sb.toString());
    }

    private void loadHeadset() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioManager am = mContext.getSystemService(AudioManager.class);
            appendMsg("耳机连接状态 : " + am.isWiredHeadsetOn());

            AudioDeviceInfo[] adi = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo ai : adi) {
                appendMsg(String.format(Locale.CHINA, "--info: id=%d, pro=%s,type=%d ,sink=%b,source=%b",
                        ai.getId(), ai.getProductName(), ai.getType(), ai.isSink(), ai.isSource()));
            }
        }
    }

    File mUsb;

    private void loadUSBfile() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> at = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                File storage = new File("/storage");
                if (storage.exists()) {
                    if (storage.canRead()) {
                        String sub[] = storage.list((dir, name) -> {
                            TopUtilKt.log("dir: " + dir + ",,, name:" + name, "USB");
                            return !"self".equals(name) && !"emulated".equals(name);
                        });
                        String subFileString = "";
                        if (sub.length > 0) {
                            File usb = new File("/storage", sub[0]);
                            mUsb = usb;
                            if (usb.canRead()) {
                                subFileString = array2String(usb.list());
                            } else {
                                subFileString = "usb不可读";
                            }
                        }
                        return array2String(sub) + "\n\n " + subFileString;
                    } else {
                        return "storage 不可读";
                    }
                } else {
                    return "storage 不存在";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                appendMsg("\nstorage子目录 : ---\n" + s);
            }
        };
        at.execute();
    }

    private String array2String(String[] s) {
        if (s == null) return "null";
        else if (s.length == 0) return "空";
        else {
            StringBuilder sb = new StringBuilder();
            for (String sub : s) {
                sb.append(sub).append(",");
            }
            return sb.toString();
        }
    }

}
