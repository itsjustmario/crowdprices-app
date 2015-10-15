package org.faostat.crowdprices;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import com.actionbarsherlock.app.SherlockActivity;
import com.devspark.appmsg.AppMsg;
import org.faostat.crowdprices.rc.R;

public class CrowdSettings extends SherlockActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_settings);
				
		View.OnClickListener ctwBehivoir = new View.OnClickListener() {
	        @Override
			public void onClick(View v) {
	            ((CheckedTextView) v).toggle();
	        }
	    };
	    
	    final Spinner spGaul = (Spinner) findViewById(R.id.spGaulChoice);
	    final Spinner spLang = (Spinner) findViewById(R.id.spLanguages);	    
	    final CheckedTextView cbOldWeb = (CheckedTextView) findViewById(R.id.cbUseOldWebsite);
	    cbOldWeb.setOnClickListener(ctwBehivoir);
	    final CheckedTextView cbDebug = (CheckedTextView) findViewById(R.id.cbDebug);
		cbDebug.setOnClickListener(ctwBehivoir);
		final CheckedTextView cbCrypto = (CheckedTextView) findViewById(R.id.cbCrypto);
		cbCrypto.setOnClickListener(ctwBehivoir);
		final CheckedTextView cbDelSurvey = (CheckedTextView) findViewById(R.id.cbDelSurvey);
		cbDelSurvey.setOnClickListener(ctwBehivoir);
		final CheckedTextView cbInAppUpd = (CheckedTextView) findViewById(R.id.cbInAppUpd);
		cbInAppUpd.setOnClickListener(ctwBehivoir);
		final CheckedTextView cbVariety = (CheckedTextView) findViewById(R.id.cbVariety);
		cbVariety.setOnClickListener(ctwBehivoir);
		final CheckedTextView cbSendToServer = (CheckedTextView) findViewById(R.id.cbSendToServer);
		cbSendToServer.setOnClickListener(ctwBehivoir);
		final CheckedTextView cbUpdatesFromServer = (CheckedTextView) findViewById(R.id.cbUpdatesFromServer);
		cbUpdatesFromServer.setOnClickListener(ctwBehivoir);
		
		
		// Read
		SharedPreferences settings = getSharedPreferences("FENIX", 0);

		cbSendToServer.setChecked(settings.getBoolean("sendServer", true));
		cbVariety.setChecked(settings.getBoolean("commVariety", true));
		cbOldWeb.setChecked(settings.getBoolean("oldWeb", false));
		cbDebug.setChecked(settings.getBoolean("debugMode", true));
		cbCrypto.setChecked(settings.getBoolean("cryptoSD", false));
		cbDelSurvey.setChecked(settings.getBoolean("delSurvey", false));
		cbInAppUpd.setChecked(settings.getBoolean("inAppUpd", true));
		cbUpdatesFromServer.setChecked(settings.getBoolean("updFromSrv", true));
	    spGaul.setSelection(settings.getInt("gaulLevel", 0));
	    spLang.setSelection(settings.getInt("language",0));
	    

		
	    final Button btnSetDefault = (Button) findViewById(R.id.btnDefaultTemplate);
	    btnSetDefault.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				final Intent i = new Intent(CrowdSettings.this, CrowdTemplates.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});
	    
	    Button back = (Button) findViewById(R.id.btnBack);
	    back.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Button save = (Button) findViewById(R.id.btnSaveSingle);		
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    SharedPreferences settings = getSharedPreferences("FENIX", 0);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putBoolean("sendServer", cbSendToServer.isChecked());
			    editor.putBoolean("commVariety", cbVariety.isChecked());
			    editor.putBoolean("debugMode", cbDebug.isChecked());
			    editor.putBoolean("cryptoSD", cbCrypto.isChecked());
			    editor.putBoolean("delSurvey", cbDelSurvey.isChecked());
			    editor.putBoolean("inAppUpd", cbInAppUpd.isChecked());
			    editor.putBoolean("oldWeb", cbOldWeb.isChecked());
			    editor.putInt("gaulLevel", spGaul.getSelectedItemPosition());
			    editor.putInt("language", spLang.getSelectedItemPosition());			    
			    
			    boolean b = editor.commit();			
			    if (b) AppMsg.makeText(CrowdSettings.this, R.string.iDataUpdated ,AppMsg.STYLE_INFO).show();
			}
		});
	}

}
