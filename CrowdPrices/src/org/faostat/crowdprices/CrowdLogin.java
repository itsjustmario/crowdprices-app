package org.faostat.crowdprices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.devspark.appmsg.AppMsg;
import org.faostat.crowdprices.rc.R;

public class CrowdLogin extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		final Button login = (Button) findViewById(R.id.btnLogin);
		final Button cancel = (Button) findViewById(R.id.btnCancel);
		
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final TextView usr = (TextView) findViewById(R.id.etUsername);
				final TextView pwr = (TextView) findViewById(R.id.etPassword);

				if (usr.getText().toString().equalsIgnoreCase("fgrita") && pwr.getText().toString().equalsIgnoreCase("fgrita")) {
					login.setOnClickListener(null);
					AppMsg.makeText(CrowdLogin.this, R.string.iLogginIn ,AppMsg.STYLE_INFO).show();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							final Intent i = new Intent(CrowdLogin.this, CrowdMain.class);
							startActivity(i);
							finish();
						}
					}, 500);
				} else {
					AppMsg.makeText(CrowdLogin.this, R.string.eWrongUserPass ,AppMsg.STYLE_ALERT).show();
				}
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which){
						case DialogInterface.BUTTON_POSITIVE:
							finish();
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
					}
				};

				final AlertDialog.Builder builder = new AlertDialog.Builder(CrowdLogin.this);
				builder.setMessage(R.string.qAreYouSure).setPositiveButton(android.R.string.yes, dialogClickListener)
				.setNegativeButton(android.R.string.no, dialogClickListener).show();

			}
		});
		
		
	}
}

