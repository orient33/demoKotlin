package com.example.appinfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.TopUtilKt;
import com.example.kotlindemo.IActivity;
import com.example.kotlindemo.R;

import java.util.ArrayList;
import java.util.List;

// https://github.com/orient33/getAppInfo

@SuppressLint("NewApi")
public class AppListFragment extends Fragment {
    private static final int FLAG_DATA = 0;
    private static final int FLAG_SYSTEM = 1;
    private static final int FLAG_ALL = 2;
    View root;
    ListView mListView;
    Spinner mSpinner;

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return root.findViewById(id);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_applist, container, false);
        mSpinner = findViewById(R.id.spinner);
        mListView = findViewById(R.id.list_view);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        // new TestEventBus();
        LauncherApps la = mContext.getSystemService(LauncherApps.class);
        List<LauncherActivityInfo> lai = la
                .getActivityList(null, android.os.Process.myUserHandle());
        final AppAdapter aa = new AppAdapter(mContext, lai);
        mListView.setAdapter(aa);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 2) {
                    doFlag(position);
                    return;
                }
                aa.setFilter(position);
                TopUtilKt.toast(view.getContext(), "onItemSelected=" + position, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TopUtilKt.toast(parent.getContext(), "onNothingSelected", 0);
            }
        });
        /*
        final Intent target =  new Intent(this,MainActivity.class);
        Intent un = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        un.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
        un.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Test");
        sendBroadcast(un);
        //
        Runnable run = new Runnable() {
            public void run(){
                Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Test");
                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,target);
                sendBroadcast(intent);
            }
        };
        this.getWindow().getDecorView().postDelayed(run, 1000);
        PackageManager pm = getPackageManager();
        ComponentName cn = new ComponentName(this, TActivity.class);
        int state = pm.getComponentEnabledSetting(cn);
        final int newState;
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            newState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            Toast.makeText(this, "从禁用更改为启用", Toast.LENGTH_SHORT).show();
        } else {
            newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            Toast.makeText(this, "从启用更改为禁用", Toast.LENGTH_SHORT).show();
        }
        pm.setComponentEnabledSetting(cn, newState, PackageManager.DONT_KILL_APP);
        /*
         * // for壁纸 parter PackageManager pm = getPackageManager(); String action =
         * "com.android.launcher3.action.PARTNER_CUSTOMIZATION"; final Intent intent = new
         * Intent(action); String result="--"; for (ResolveInfo info :
         * pm.queryBroadcastReceivers(intent, 0)) { if (info.activityInfo != null &&
         * (info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) { final
         * String packageName = info.activityInfo.packageName; result = packageName +"========" +
         * info.activityInfo;//,Toast.LENGTH_LONG).show();// res); } } Toast.makeText(this, result,
         * Toast.LENGTH_LONG).show();
         */
        /*MTK 未读测试
        ComponentName cn = new ComponentName("com.android.email",
                "com.android.email.activity.Welcome");
        Intent intent = new Intent("com.mediatek.action.UNREAD_CHANGED");
        intent.putExtra("com.mediatek.intent.extra.UNREAD_NUMBER", 1);
        intent.putExtra("com.mediatek.intent.extra.UNREAD_COMPONENT", cn);
        sendBroadcast(intent);
        Toast.makeText(this, "send unread. " + cn, 0).show();
        */

        /*
        String p = getDownloadPathForPPTVPhone(this);
        Toast.makeText(this, "Default="+p, Toast.LENGTH_LONG).show(); */
        /*
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndTypeAndNormalize(Uri.parse("file://sdcard/a.mp4"), "video/*");
        List<ResolveInfo> ls = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo ri : ls) {
            android.util.Log.i("df", "" + ri.activityInfo.packageName +", "+ ri.activityInfo.name);
        }*/
        /*
        try {
            String s=String.format("?username=%s&from=%s&version=%s&format=%s",
                    URLEncoder.encode("_50934936%40qq", "UTF-8"), "aph", "aa", "xml");
            Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        } catch (UnsupportedEncodingException e) {
        }*/
        //

//        PackageManager pm = getPackageManager();
//        PackageInfo pi = pm.getPackageArchiveInfo("/sdcard/pptv_plugins/app-debug.apk",
//                PackageManager.GET_ACTIVITIES);
//        if (pi != null && pi.activities != null && pi.activities.length > 0) {
//            for (ActivityInfo ai : pi.activities) {
//                ai.theme = ai.getThemeResource();
//                Log.e("dd", ai.name + ", theme :" + Integer.toHexString(ai.theme));
//            }
//        }
        return root;
    }

    void doFlag(int position) {
//        startActivity(new Intent(this, OtherActivity.class));
    }

    class AppAdapter extends BaseAdapter implements View.OnClickListener {

        final Context context;
        final List<LauncherActivityInfo> data = new ArrayList<>();
        final List<LauncherActivityInfo> all;
        final int dm;

        AppAdapter(Context ctx, List<LauncherActivityInfo> d) {
            context = ctx;
            dm = ctx.getResources().getDisplayMetrics().densityDpi;
            all = d;
            setFilter(FLAG_DATA);
        }

        void setFilter(int flag) {
            if (flag < 0 || flag > 2) throw new RuntimeException("Flags error. ");
            data.clear();
            if (flag == FLAG_ALL) {
                data.addAll(all);
            } else {
                for (LauncherActivityInfo item : all) {
                    int flags = item.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM;
                    if ((flags != 0 && flag == FLAG_SYSTEM) ||
                            (flags == 0 && flag == FLAG_DATA)) {
                        data.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public LauncherActivityInfo getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_appinfo, null);
                convertView.setOnClickListener(this);
            }
            final LauncherActivityInfo info = getItem(position);
            Drawable drawable = info.getIcon(dm);
            String cnn = info.getComponentName().toShortString();
            ImageView icon = convertView.findViewById(R.id.icon);
            icon.setImageDrawable(drawable);
            TextView name = convertView.findViewById(R.id.name);
            name.setText(++position + ")  " + info.getLabel());
            TextView cn = convertView.findViewById(R.id.cn);
            cn.setText(cnn);
            // int flags = info.getApplicationInfo().flags;
            // cnn +=", system="+((flags&ApplicationInfo.FLAG_SYSTEM )!=0) // 系统应用
            // +",,update = "+((flags& ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) !=0);//经升级的系统应用
//            com.example.palette.MyPalette.findThemeColor(drawable, cn, cnn);
            convertView.setTag(info);
            convertView.setTag(R.id.name, position);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            String tag = v.getTag().toString();
            Toast.makeText(v.getContext(), tag, Toast.LENGTH_SHORT).show();
            toDetail((LauncherActivityInfo) v.getTag());
//            startActivity(new Intent(MainActivity.this, AppInfoActivity.class));
//            // Log.i("dd", "click "+tag +", post MyEvent");
//            // EventBus.getDefault().post(new TestEventBus.MyEvent(tag));
//            File file = new File("/storage/sdcard0/");
//            File sub[] = file.listFiles(new FilenameFilter() {
//                @Override
//                public boolean accept(File dir, String filename) {
//                    return filename.endsWith(".png");
//                }
//            });
//            int pos = (Integer) v.getTag(R.id.name);
//            int index = pos == 0 ? 0 : 1;
//            Intent i = new Intent(mContext, AppInfoActivity.class);
//            i.putExtra(AppInfoActivity.Key, sub[index].getAbsolutePath());
//            startActivity(i);
        }
    }

    private void toDetail(LauncherActivityInfo info) {
        MyPalette.sLauncherActivityInfo = info;
        Activity act = getActivity();
        if (act instanceof IActivity) {
            IActivity ia = (IActivity) act;
            AppInfoFragment fragment = new AppInfoFragment();
            ia.toFragment(fragment);
        } else {
            Toast.makeText(act, "can not to page. ", Toast.LENGTH_SHORT).show();
        }
    }
}
