package tk.dongyeblog.appexplorer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Runnable,
		OnItemClickListener {
	private static final int SEARCH_APP = 0;
	private static final int DELETE_APP = 1;
	private GridView gv;
	private ListView lv;
	private List<PackageInfo> packageInfos;
	private List<PackageInfo> userPackageInfos;
	private List<PackageInfo> showPackageInfos;
	private ProgressDialog pd;
	private ImageButton ibChangeView, ibChangeCategory;
	private Boolean isAllApp = true;
	private Boolean isListView = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SEARCH_APP:
				showPackageInfos = packageInfos;
				gv.setAdapter(new GridViewAdapter(MainActivity.this,
						showPackageInfos));
				lv.setAdapter(new ListViewAdapter(MainActivity.this,
						showPackageInfos));
				// pd.dismiss();
				setProgressBarIndeterminateVisibility(false);
				break;

			case DELETE_APP:

				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除title
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.show_app_grid);

		gv = (GridView) findViewById(R.id.gvApps);
		lv = (ListView) findViewById(R.id.lvApps);
		lv.setCacheColorHint(0);

		// pd = ProgressDialog.show(this, "请稍后...", "正在搜索你所安装的应用程序", true,
		// false);
		ibChangeView = (ImageButton) findViewById(R.id.ibChangeView);
		ibChangeCategory = (ImageButton) findViewById(R.id.ibChangeCategory);

		ibChangeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isListView) {
					lv.setVisibility(View.GONE);
					gv.setVisibility(View.VISIBLE);

					// AlphaAnimation 控制渐变透明的动画效果
					// ScaleAnimation 控制尺寸伸缩的动画效果
					// TranslateAnimation 控制画面平移的动画效果
					// RotateAnimation 控制画面角度变化的动画效果

					AnimationSet set = new AnimationSet(false);
					Animation animation = new RotateAnimation(60, 0);
					animation.setDuration(800);
					set.addAnimation(animation);
					animation = new AlphaAnimation(0, 1);
					animation.setDuration(800);
					set.addAnimation(animation);

					gv.startAnimation(set);

					isListView = false;
					ibChangeView.setImageResource(R.drawable.grids);
					Toast.makeText(MainActivity.this, "网格显示",
							Toast.LENGTH_SHORT).show();
				} else {
					gv.setVisibility(View.GONE);
					lv.setVisibility(View.VISIBLE);

					// 动画
					AnimationSet set = new AnimationSet(false);

					Animation animation = new TranslateAnimation(200, 1, 200, 1);
					animation.setDuration(800);
					set.addAnimation(animation);

					animation = new ScaleAnimation(200, 1, 200, 1);
					animation.setDuration(500);
					set.addAnimation(animation);

					lv.startAnimation(set);

					isListView = true;
					ibChangeView.setImageResource(R.drawable.list);
					Toast.makeText(MainActivity.this, "列表显示",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		ibChangeCategory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isAllApp) {
					ibChangeCategory.setImageResource(R.drawable.user);
					showPackageInfos = userPackageInfos;
					// gv.setAdapter(new
					// GridViewAdapter(MainActivity.this,userPackageInfos));
					isAllApp = false;
				} else {
					ibChangeCategory.setImageResource(R.drawable.all);
					showPackageInfos = packageInfos;
					// gv.setAdapter(new
					// GridViewAdapter(MainActivity.this,packageInfos));
					isAllApp = true;
				}
				gv.setAdapter(new GridViewAdapter(MainActivity.this,
						showPackageInfos));
				lv.setAdapter(new ListViewAdapter(MainActivity.this,
						showPackageInfos));

			}
		});

		setProgressBarIndeterminateVisibility(true);
		Thread thread = new Thread(this);
		thread.start();

		gv.setOnItemClickListener(this);
		lv.setOnItemClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class GridViewAdapter extends BaseAdapter {

		LayoutInflater inflater;
		private List<PackageInfo> pkInfos;

		public GridViewAdapter(Context context, List<PackageInfo> packageInfos) {
			inflater = LayoutInflater.from(context);
			this.pkInfos = packageInfos;
		}

		@Override
		public int getCount() {
			return pkInfos.size();
		}

		@Override
		public Object getItem(int arg0) {

			return pkInfos.get(arg0);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.gv_item, null);
			TextView tv = (TextView) view.findViewById(R.id.gvItemAppname);
			ImageView iv = (ImageView) view.findViewById(R.id.gvItemIcon);

			tv.setText(pkInfos.get(position).applicationInfo
					.loadLabel(getPackageManager()));
			iv.setImageDrawable(pkInfos.get(position).applicationInfo
					.loadIcon(getPackageManager()));
			return view;
		}

	}

	class ListViewAdapter extends BaseAdapter {

		LayoutInflater inflater;
		private List<PackageInfo> pkInfos;

		public ListViewAdapter(Context context, List<PackageInfo> packageInfos) {
			inflater = LayoutInflater.from(context);
			this.pkInfos = packageInfos;
		}

		@Override
		public int getCount() {
			return pkInfos.size();
		}

		@Override
		public Object getItem(int arg0) {

			return pkInfos.get(arg0);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.lv_item, null);
			ImageView iv = (ImageView) view.findViewById(R.id.lvItemIcon);
			TextView tvAppName = (TextView) view
					.findViewById(R.id.lvItemAppname);
			TextView tvPackagename = (TextView) view
					.findViewById(R.id.lvItemPackagename);

			iv.setImageDrawable(pkInfos.get(position).applicationInfo
					.loadIcon(getPackageManager()));
			tvAppName.setText(pkInfos.get(position).applicationInfo
					.loadLabel(getPackageManager()));
			tvPackagename.setText(pkInfos.get(position).packageName);
			return view;
		}

	}

	@Override
	public void run() {
		packageInfos = getPackageManager().getInstalledPackages(
				PackageManager.GET_UNINSTALLED_PACKAGES);

		// | PackageManager.GET_ACTIVITIES
		userPackageInfos = new ArrayList<PackageInfo>();
		for (int i = 0; i < packageInfos.size(); i++) {
			PackageInfo temp = packageInfos.get(i);
			ApplicationInfo appInfo = temp.applicationInfo;
			boolean flag = false;
			if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				flag = true;
			} else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				flag = true;
			}
			if (flag) {
				userPackageInfos.add(temp);
			}
		}

		mHandler.sendEmptyMessage(SEARCH_APP);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final PackageInfo tempPKInfo = showPackageInfos.get(position);

		// 创建ALertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("选项");
		builder.setItems(R.array.choose, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					String packageName = tempPKInfo.packageName;
					ActivityInfo info = getActivityByPkgName(packageName);
					if (info == null) {
						Toast.makeText(MainActivity.this, "没有activity",
								Toast.LENGTH_SHORT).show();
						return;
					}
					String activityName = info.name;
					Intent intent = new Intent();
					ComponentName component = new ComponentName(packageName,
							activityName);
					intent.setComponent(component);
					startActivity(intent);

					break;
				case 1:
					showAppDetail(tempPKInfo);
					break;
				case 2:
					Uri uri = Uri.parse("package:" + tempPKInfo.packageName);
					Intent deleteIntent = new Intent();
					deleteIntent.setAction(Intent.ACTION_DELETE);
					deleteIntent.setData(uri);
					startActivityForResult(deleteIntent, 0);
					break;
				}

			}

		});
		builder.setNegativeButton("取消", null);
		builder.create().show();

	}

	private void showAppDetail(PackageInfo tempPKInfo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("详细信息");
		StringBuffer message = new StringBuffer();
		message.append("程序名称："
				+ tempPKInfo.applicationInfo.loadLabel(getPackageManager()));
		message.append("\n包名：" + tempPKInfo.packageName);
		message.append("\n版本：" + tempPKInfo.versionCode);
		builder.setMessage(message);
		builder.setPositiveButton("确定", null);
		builder.create().show();
	}

	// 根据包名查询所有信息
	public ActivityInfo getActivityByPkgName(String packageName) {
		PackageManager pm = this.getPackageManager();
		ActivityInfo info = null;
		PackageInfo pkgInfo;
		try {
			pkgInfo = pm.getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
			info = pkgInfo.activities[0];
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return info;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 方法一,重新加载
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 方法二 onActivityResult
		packageInfos = getPackageManager().getInstalledPackages(
				PackageManager.GET_UNINSTALLED_PACKAGES
						| PackageManager.GET_ACTIVITIES);
		userPackageInfos = new ArrayList<PackageInfo>();
		for (int i = 0; i < packageInfos.size(); i++) {
			PackageInfo temp = packageInfos.get(i);
			ApplicationInfo appInfo = temp.applicationInfo;
			boolean flag = false;
			if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				flag = true;
			} else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				flag = true;
			}
			if (flag) {
				userPackageInfos.add(temp);
			}
		}

		if (isAllApp) {
			showPackageInfos = packageInfos;
		} else {
			showPackageInfos = userPackageInfos;
		}

		gv.setAdapter(new GridViewAdapter(MainActivity.this, showPackageInfos));
		lv.setAdapter(new ListViewAdapter(MainActivity.this, showPackageInfos));
	}

}