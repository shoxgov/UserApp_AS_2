package com.qingwing.safebox.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qingwing.safebox.R;

public class ToastTool {
	public static void showLongToast(Context cont, String msg) {
		try {
			Toast.makeText(cont, msg, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showShortToast(Context cont, String msg) {
		try {
			Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showLongBigToast(Context cont, String msg) {
		try {
			CustomToast toast = new CustomToast(cont);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 30);
			toast.setText(msg);
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showShortBigToast(Context cont, String msg) {
		try {
			CustomToast toast = new CustomToast(cont);
			toast.setGravity(Gravity.CENTER, 0, 30);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setText(msg);
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showShortBigToast(Context cont, String msg, int duration) {
		try {
			CustomToast toast = new CustomToast(cont);
			toast.setGravity(Gravity.CENTER, 0, 30);
			toast.setDuration(duration);
			toast.setText(msg);
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showShortBigToast(Context cont, int resId) {
		try {
			CustomToast toast = new CustomToast(cont);
			toast.setGravity(Gravity.CENTER, 0, 30);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setText(cont.getString(resId));
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showLongBigToast(Context cont, int resId) {
		try {
			CustomToast toast = new CustomToast(cont);
			toast.setGravity(Gravity.CENTER, 0, 30);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setText(cont.getText(resId));
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class CustomToast extends Toast {
		private TextView message;

		public CustomToast(Context context) {
			super(context);
			View toastRoot = LayoutInflater.from(context).inflate(
					R.layout.customtoast, null);
			message = (TextView) toastRoot.findViewById(R.id.message);
			setView(toastRoot);
		}

		@Override
		public void setText(CharSequence s) {
			message.setText(s);
		}

		@Override
		public void setDuration(int duration) {
			super.setDuration(duration);
		}

		@Override
		public void setView(View view) {
			super.setView(view);
		}
	}

}
