package org.faostat.crowdprices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import org.faostat.crowdprices.rc.R;

public class CrowdSplash extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 2000;
	//private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		//final View contentView = findViewById(R.id.fullscreen_adddata);
		/*
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, SystemUiHider.FLAG_HIDE_NAVIGATION);
		mSystemUiHider.setup();
		mSystemUiHider.hide();
		 */
		new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				// Intent i = new Intent(CrowdSplash.this, CrowdMain.class);
				final Intent i = new Intent(CrowdSplash.this, CrowdLogin.class);
				startActivity(i);

				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}