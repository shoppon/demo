package cn.shoppon.demos.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import cn.shoppon.demos.R;
import cn.shoppon.demos.bean.DetailProcess;
import cn.shoppon.demos.bean.PackagesInfo;
import cn.shoppon.demos.bean.ProcessInfo;
import cn.shoppon.demos.model.TaskListAdapters.ProcessListAdapter;

public class TaskManager extends Activity {
	public static final boolean DEBUG = true;
	public static final String TAG = "TaskManager";
	private ProcessInfo pinfo = null;
	public ActivityManager am = null;
	private PackagesInfo packageinfo = null;
	// private PackageManager pm;
	protected static final String ACTION_LOADFINISH = "com.xmobileapp.taskmanager.ACTION_LOADFINISH";
	private ProcessListAdapter adapter;
	private BroadcastReceiver loadFinish = new LoadFinishReceiver();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.taskmanager);
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		// pm = this.getApplicationContext().getPackageManager();
		packageinfo = new PackagesInfo(this);

		IntentFilter filter = new IntentFilter(ACTION_LOADFINISH);
		this.registerReceiver(loadFinish, filter);

	}

	private ListView getListView() {
		return (ListView) this.findViewById(R.id.listbody);
	}

	public void refresh() {
		setProgressBarIndeterminateVisibility(true);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				pinfo = new ProcessInfo();
				getRunningProcess();

				Intent in = new Intent(ACTION_LOADFINISH);
				TaskManager.this.sendBroadcast(in);
			}

		});
		t.start();
	}

	public ProcessInfo getProcessInfo() {
		return pinfo;
	}

	public PackagesInfo getPackageInfo() {
		return packageinfo;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		super.onResume();
		packageinfo = new PackagesInfo(this);
		refresh();
	}

	public void getRunningProcess() {
		List<RunningAppProcessInfo> list2 = am.getRunningAppProcesses();
		ArrayList<DetailProcess> list = new ArrayList<DetailProcess>();
		for (RunningAppProcessInfo ti : list2) {
			if (ti.processName.equals("system") || ti.processName.equals("com.android.phone")) {
				continue;
			}
			DetailProcess dp = new DetailProcess(this, ti);
			dp.fetchApplicationInfo(packageinfo);
			dp.fetchPackageInfo();
			dp.fetchPsRow(pinfo);
			if (dp.isGoodProcess()) {
				list.add(dp);
			}
		}
		Collections.sort(list);
		adapter = new ProcessListAdapter(this, list);
	}

	private class LoadFinishReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(final Context ctx, Intent intent) {
			TaskManager.this.setProgressBarIndeterminateVisibility(false);
			TaskManager.this.getListView().setAdapter(adapter);
		}
	}
}
