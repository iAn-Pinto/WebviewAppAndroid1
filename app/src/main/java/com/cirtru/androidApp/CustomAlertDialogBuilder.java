package com.cirtru.androidApp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;

public class CustomAlertDialogBuilder extends AlertDialog.Builder {
	public CustomAlertDialogBuilder(Context context) {
		super(new ContextThemeWrapper(context, R.style.CustomCirtruDialog));
	}
}
