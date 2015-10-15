package org.faostat.crowdprices;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.faostat.crowdprices.network.CrowdInAppUpdate;
import org.faostat.crowdprices.network.CrowdServerReader;
import org.faostat.crowdprices.network.CrowdServerReader.getJSONfromURL;
import org.faostat.crowdprices.rc.R;
import org.faostat.crowdprices.ui.CrowdMultiMenus;
import org.faostat.crowdprices.util.SystemUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockActivity;
import com.devspark.appmsg.AppMsg;

public class CrowdMain extends SherlockActivity  {
	CrowdMultiMenus cmm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cmm = new CrowdMultiMenus(this, R.layout.activity_mainscreen, false,0);

		// Check the device connection and position
		if (!SystemUtils.haveNetworkConnection(getApplicationContext())) AppMsg.makeText(this, R.string.eNoConn ,AppMsg.STYLE_ALERT).show();
		// If connection is available...
		SystemUtils.lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		SystemUtils.gps_enabled = SystemUtils.lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		SystemUtils.rit = SystemUtils.checkUseOfLocation(getApplicationContext(), this, null);
		// ...update the .json files for settings from server...
		/*
		if (cmm.updFromServer) {
			Thread t = new Thread("parseURL") {
		        @Override
		        public void run() {
					final JSONArray aVendor = CrowdServerReader.parseJSONfromURL(CrowdMain.this,getResources().getString(R.string.MONGO_URL)+"/find/vendor");
					final JSONArray aMarket = CrowdServerReader.parseJSONfromURL(CrowdMain.this,getResources().getString(R.string.MONGO_URL)+"/find/market");
					final JSONArray aCities = CrowdServerReader.parseJSONfromURL(CrowdMain.this,getResources().getString(R.string.MONGO_URL)+"/find/city");
					final JSONArray aCommod = CrowdServerReader.parseJSONfromURL(CrowdMain.this,getResources().getString(R.string.MONGO_URL)+"/find/commodity");
					final JSONArray aMunits = CrowdServerReader.parseJSONfromURL(CrowdMain.this,getResources().getString(R.string.MONGO_URL)+"/find/munit");
					final JSONArray aVariet = CrowdServerReader.parseJSONfromURL(CrowdMain.this,getResources().getString(R.string.MONGO_URL)+"/find/variety");
					SystemUtils.writeFile(CrowdMain.this, "vendor.json", aVendor.toString(), true);
					SystemUtils.writeFile(CrowdMain.this, "market.json", aMarket.toString(), true);
					SystemUtils.writeFile(CrowdMain.this, "city.json", aCities.toString(), true);
					SystemUtils.writeFile(CrowdMain.this, "commodity.json", aCommod.toString(), true);
					SystemUtils.writeFile(CrowdMain.this, "munits.json", aMunits.toString(), true);
					SystemUtils.writeFile(CrowdMain.this, "variety.json", aVariet.toString(), true);
		        }
		    };
		    t.start();
		} else {
			// ... or create it locally ...
			final Boolean z = SystemUtils.ifFileExist(getApplicationContext(),"vendor.json");
			final Boolean x = SystemUtils.ifFileExist(getApplicationContext(),"market.json");
			final Boolean k = SystemUtils.ifFileExist(getApplicationContext(),"city.json");
			final Boolean h = SystemUtils.ifFileExist(getApplicationContext(),"commodity.json");
			final Boolean y = SystemUtils.ifFileExist(getApplicationContext(),"munits.json");
			final Boolean j = SystemUtils.ifFileExist(getApplicationContext(),"variety.json");
			try {
				if (!z||!x||!k||!h||!y||!j) {
					SystemUtils.ifDirisEmpty(getApplicationContext());
					if(!z) SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.old_vendors, "vendor.json");
					if(!x) SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.old_markets, "market.json");
					if(!k) SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.old_cities, "city.json");
					if(!h) SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.old_commodities, "commodity.json");
					if(!y) SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.old_munits, "munit.json");
					if(!j) SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.old_varieties, "variety.json");
				}
			} catch (final IOException e) { e.printStackTrace(); }
		}
		*/
			// 	... but if is in debug mode, delete and generate anyway!
			if (cmm.debugEnabled) {
				final File path = new File(getResources().getString(R.string.sdpath));
				SystemUtils.deleteDir(path);
				path.mkdirs();
				
				if (cmm.updFromServer) {
					this.runOnUiThread(new Runnable() {
				        @Override
				        public void run() {
				        	try {
				        		AppMsg.makeText(CrowdMain.this, R.string.iUpdating ,AppMsg.STYLE_INFO).show();
				        		//android.widget.Toast.makeText(getApplicationContext(), "Downloading files...", android.widget.Toast.LENGTH_LONG).show();
				        		
				        		CrowdServerReader origin = new CrowdServerReader();
				        		getJSONfromURL getJSON = origin.new getJSONfromURL();
				        		getJSON.execute(getResources().getString(R.string.RAW_URL)+"/vendor.json");				        	
								final JSONArray aVendor = getJSON.get();
								SystemUtils.writeFile(CrowdMain.this, "vendor.json", aVendor.toString(), true);
								getJSON.cancel(true);
								
								getJSON = origin.new getJSONfromURL();
								getJSON.execute(getResources().getString(R.string.RAW_URL)+"/market.json");
								final JSONArray aMarket = getJSON.get();
								SystemUtils.writeFile(CrowdMain.this, "market.json", aMarket.toString(), true);
								getJSON.cancel(true);
								
								getJSON = origin.new getJSONfromURL();
								getJSON.execute(getResources().getString(R.string.RAW_URL)+"/city.json");
								final JSONArray aCities = getJSON.get();
								SystemUtils.writeFile(CrowdMain.this, "city.json", aCities.toString(), true);
								getJSON.cancel(true);
								
								getJSON = origin.new getJSONfromURL();
								getJSON.execute(getResources().getString(R.string.RAW_URL)+"/commodity.json");
								final JSONArray aCommod = getJSON.get();
								SystemUtils.writeFile(CrowdMain.this, "commodity.json", aCommod.toString(), true);
								getJSON.cancel(true);
								
								getJSON = origin.new getJSONfromURL();
								getJSON.execute(getResources().getString(R.string.RAW_URL)+"/munit.json");
								final JSONArray aMunits = getJSON.get();
								SystemUtils.writeFile(CrowdMain.this, "munit.json", aMunits.toString(), true);
								getJSON.cancel(true);
								
								getJSON = origin.new getJSONfromURL();
								getJSON.execute(getResources().getString(R.string.RAW_URL)+"/variety.json");
								final JSONArray aVariet = getJSON.get();
								SystemUtils.writeFile(CrowdMain.this, "variety.json", aVariet.toString(), true);
								getJSON.cancel(true);
							} 
				        	catch (InterruptedException e) { e.printStackTrace();} 
				        	catch (ExecutionException e) { e.printStackTrace(); }
				        }
					});
					
				} else {
					try {
						SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.vendor, "vendor.json");
						SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.market, "market.json");
						SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.city, "city.json");
						SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.commodity, "commodity.json");
						SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.munit, "munit.json");
						SystemUtils.CopyRAWtoSDCard(CrowdMain.this,R.raw.variety, "variety.json");
					} catch (IOException e) { e.printStackTrace(); }					
				}				
			}		

		// Check for updates		
		if (cmm.aUpdtEnabled) CrowdInAppUpdate.checkUpdates(this);  
		
		
		// Setup Buttons
		final Button fullscreen_surveymanagement = (Button) findViewById(R.id.fullscreen_management);
		fullscreen_surveymanagement.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent i = new Intent(CrowdMain.this, CrowdSurveyManagement.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});

		final Button fullscreen_templates = (Button) findViewById(R.id.fullscreen_templates);
		fullscreen_templates.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent i = new Intent(CrowdMain.this, CrowdTemplateList.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});

		final Button fullscreen_adddata = (Button) findViewById(R.id.fullscreen_adddata);
		fullscreen_adddata.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent i = new Intent(CrowdMain.this, CrowdAddData.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});
		
		final Button fullscreen_archive = (Button) findViewById(R.id.fullscreen_archive);
		fullscreen_archive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent i = new Intent(CrowdMain.this, CrowdArchive.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});
	}

}
