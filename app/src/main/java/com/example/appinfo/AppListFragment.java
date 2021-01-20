package com.example.appinfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TopUtilKt;
import com.example.kotlindemo.IActivity;
import com.example.kotlindemo.R;

import java.util.ArrayList;
import java.util.List;

// https://github.com/orient33/getAppInfo

@SuppressLint("NewApi")
public class AppListFragment extends Fragment {
    View root;
    RecyclerView mRecyclerView;
    Spinner mSpinner;

    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
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
        mRecyclerView = findViewById(R.id.recyclerView);
        LauncherApps la = mContext.getSystemService(LauncherApps.class);
        List<LauncherActivityInfo> lai = la
                .getActivityList(null, android.os.Process.myUserHandle());
        final AppAdapter aa = new AppAdapter(mContext, lai);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mRecyclerView.setAdapter(aa);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aa.setFilter(position);
                TopUtilKt.toast(view.getContext(), "onItemSelected=" + position, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TopUtilKt.toast(parent.getContext(), "onNothingSelected", 0);
            }
        });
        return root;
    }

    class AppAdapter extends RecyclerView.Adapter<VH> implements View.OnClickListener {

        final Context context;
        final List<LauncherActivityInfo> data = new ArrayList<>();
        final List<LauncherActivityInfo> all;
        final int dm;

        AppAdapter(Context ctx, List<LauncherActivityInfo> d) {
            context = ctx;
            dm = ctx.getResources().getDisplayMetrics().densityDpi;
            all = d;
            setFilter(0);
        }

        // 0用户, 1系统, 2所有, 3系统更新
        void setFilter(int flag) {
            data.clear();
            if (flag == 2) {
                data.addAll(all);
            } else {
                for (LauncherActivityInfo item : all) {
                    if (flag == 1) {
                        if ((item.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                            data.add(item);
                        }
                    } else if (flag == 0) {
                        if ((item.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                            data.add(item);
                        }
                    } else if (flag == 3) {
                        if ((item.getApplicationInfo().flags
                                & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                            data.add(item);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appinfo, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            final LauncherActivityInfo info = data.get(position);
            Drawable drawable = info.getIcon(dm);
            String cnn = info.getComponentName().toShortString();
            holder.icon.setImageDrawable(drawable);
            holder.name.setText(++position + ")  " + info.getLabel());
            holder.cn.setText(cnn);
            holder.itemView.setTag(info);
            holder.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String tag = v.getTag().toString();
            Toast.makeText(v.getContext(), tag, Toast.LENGTH_SHORT).show();
            toDetail((LauncherActivityInfo) v.getTag());
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
