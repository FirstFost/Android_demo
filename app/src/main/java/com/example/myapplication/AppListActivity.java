package com.example.myapplication;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    private RecyclerView rvInstalledApps;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        rvInstalledApps = findViewById(R.id.rvInstalledApps);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        List<InstalledApp> installedApps = getInstalledApps();
        rvInstalledApps.setLayoutManager(new LinearLayoutManager(this));
        rvInstalledApps.setAdapter(new InstalledAppAdapter(installedApps));
    }

    private List<InstalledApp> getInstalledApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<InstalledApp> apps = new ArrayList<>();

        for (ApplicationInfo pkg : packages) {
            // 过滤系统应用，只显示用户已安装的
            if ((pkg.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = pm.getApplicationLabel(pkg).toString();
                Drawable icon = pm.getApplicationIcon(pkg);
                apps.add(new InstalledApp(appName, icon, pkg.packageName));
            }
        }

        // 按名称排序
        Collections.sort(apps, (a, b) -> a.name.compareToIgnoreCase(b.name));
        return apps;
    }

    static class InstalledApp {
        String name;
        Drawable icon;
        String packageName;

        InstalledApp(String name, Drawable icon, String packageName) {
            this.name = name;
            this.icon = icon;
            this.packageName = packageName;
        }
    }

    class InstalledAppAdapter extends RecyclerView.Adapter<InstalledAppAdapter.ViewHolder> {

        private List<InstalledApp> apps;

        InstalledAppAdapter(List<InstalledApp> apps) {
            this.apps = apps;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_installed_app, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            InstalledApp app = apps.get(position);
            holder.ivIcon.setImageDrawable(app.icon);
            holder.tvName.setText(app.name);
            holder.itemView.setOnClickListener(v -> {
                PackageManager pm = getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage(app.packageName);
                if (launchIntent != null) {
                    v.getContext().startActivity(launchIntent);
                } else {
                    Toast.makeText(AppListActivity.this, "无法启动 " + app.name, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return apps.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvName;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.ivIcon);
                tvName = itemView.findViewById(R.id.tvName);
            }
        }
    }
}