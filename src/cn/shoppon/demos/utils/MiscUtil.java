package cn.shoppon.demos.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.widget.Toast;
import cn.shoppon.demos.bean.DetailProcess;
import cn.shoppon.demos.ui.TaskManager;
import cn.shoppon.demos.R;

public class MiscUtil {

	public static final int MENU_CANCEL = 0;
	public static final int MENU_SWITCH = 1;
	public static final int MENU_KILL = 2;
	public static final int MENU_DETAIL = 3;
	public static final int MENU_UNINSTALL = 4;

	public static PackageInfo getPackageInfo(PackageManager pm, String name) {
		PackageInfo ret = null;
		try {
			ret = pm.getPackageInfo(name, PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
		}
		return ret;
	}

	public static Dialog getTaskMenuDialog(final TaskManager ctx, final DetailProcess dp) {

		return new AlertDialog.Builder(ctx).setTitle(R.string.operation)
				.setItems(R.array.menu_task_operation, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case MENU_KILL: {
							ctx.am.restartPackage(dp.getPackageName());
							if (dp.getPackageName().equals(ctx.getPackageName()))
								return;
							ctx.refresh();
							return;
						}
						case MENU_SWITCH: {
							if (dp.getPackageName().equals(ctx.getPackageName()))
								return;
							Intent i = null;
							try {
								i = dp.getIntent();
							} catch (NameNotFoundException e) {
								e.printStackTrace();
							}
							if (i == null) {
								Toast.makeText(ctx, R.string.message_switch_fail, Toast.LENGTH_LONG).show();
								return;
							}
							try {
								ctx.startActivity(i);
							} catch (Exception ee) {
								Toast.makeText(ctx, ee.getMessage(), Toast.LENGTH_LONG).show();
							}
							return;
						}
						case MENU_UNINSTALL: {
							Uri uri = Uri.fromParts("package", dp.getPackageName(), null);
							Intent it = new Intent(Intent.ACTION_DELETE, uri);
							try {
								ctx.startActivity(it);
							} catch (Exception e) {
								Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();
							}
							return;
						}
						case MENU_DETAIL: {
							Intent detailsIntent = new Intent();
							detailsIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
							detailsIntent.putExtra("com.android.settings.ApplicationPkgName", dp.getPackageName());
							ctx.startActivity(detailsIntent);
							return;
						}
						}
					}
				}).create();
	}
}
