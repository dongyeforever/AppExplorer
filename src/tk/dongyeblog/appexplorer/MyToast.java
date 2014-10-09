package tk.dongyeblog.appexplorer;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MyToast {
	public static void myToastShow(Context context,int imageResId,String content,int duration){
		Toast toast = new Toast(context);
		toast.setDuration(duration);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 25);
		
		LinearLayout toastLayout = new LinearLayout(context);
		toastLayout.setOrientation(LinearLayout.HORIZONTAL);
		toastLayout.setGravity(Gravity.CENTER_VERTICAL);
		
		ImageView img = new ImageView(context);
		img.setImageResource(imageResId);
		toastLayout.addView(img);
		
		TextView tv = new TextView(context);
		tv.setText(content);
		tv.setBackgroundColor(Color.BLACK);
		
		toastLayout.addView(tv);
		
		toast.setView(toastLayout);
		toast.show();
	}
}