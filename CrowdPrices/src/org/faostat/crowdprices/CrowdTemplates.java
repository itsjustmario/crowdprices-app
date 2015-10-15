package org.faostat.crowdprices;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.faostat.crowdprices.rc.R;
import org.faostat.crowdprices.ui.MultiSpinner;
import org.faostat.crowdprices.util.SystemUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.devspark.appmsg.AppMsg;

public class CrowdTemplates extends SherlockActivity  {
	private SharedPreferences settings;
	private final ArrayList<String> defaultCommodities = new ArrayList<String>();
	private final ArrayList<String> commodities = new ArrayList<String>();
	private final ArrayList<String> defaultCities = new ArrayList<String>();
	private final ArrayList<String> cities = new ArrayList<String>();
	private final ArrayList<String> defaultMarkets = new ArrayList<String>();
	private final ArrayList<String> markets = new ArrayList<String>();
	private final ArrayList<String> defaultVendors = new ArrayList<String>();
	private final ArrayList<String> vendors = new ArrayList<String>();
	private final ArrayList<String> defaultMeasurments = new ArrayList<String>();
	private final ArrayList<String> measurments = new ArrayList<String>();
	
	void generateArray(String filename, ArrayList<String> defaultList, ArrayList<String> allList) {
		StringBuffer sb;
		try {
			sb = SystemUtils.readFile(getApplicationContext(),filename);
			final String jsontext = new String(sb);
			//final JSONObject entries = new JSONObject(jsontext);
			final JSONArray ja = new JSONArray(jsontext);
			
			for (int j = 0; j < ja.length(); j++) {
				allList.add(ja.getJSONObject(j).getString("name"));
				if (ja.getJSONObject(j).getBoolean("shown")) defaultList.add(ja.getJSONObject(j).getString("name"));
			}
			
		} catch (final FileNotFoundException e) { e.printStackTrace(); }
		catch (final JSONException e) { e.printStackTrace(); }
	}

	OnClickListener generateClicks(final String[] filename, final MultiSpinner[] ms, final String[] jasObj, final String[] prefix, final ArrayList<?>[] defaultList, final ArrayList<?>[] allList) {
		final OnClickListener ocl = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean updt = false;
				for (int kk=0; kk < filename.length; kk++) {
					final ArrayList<String> a = ms[kk].getSelected();
					int j = a.size()-1;
					int i=allList[kk].size()-1;
					final JSONArray ja;
					final JSONObject entries;
					StringBuffer sb;
					
					try {
						sb = SystemUtils.readFile(getApplicationContext(),filename[kk]);
						final String jsontext = new String(sb);
						//entries = new JSONObject(jsontext);
						//android.util.Log.i("FENIX",jsontext);
						ja = new JSONArray(jsontext);						
						while(i>-1) {							
							ja.getJSONObject(i).put("shown", false);
							if (j>-1) if (allList[kk].get(i).equals(a.get(j)))  {								
								ja.getJSONObject(i).put("shown", true);
								j--;								
							}							
							i--;			
						}
						//final JSONObject jObj = new JSONObject();
						//jObj.put(jasObj[kk], ja);
						android.util.Log.i("FENIX",ja.toString());
						updt = SystemUtils.writeFile(getApplicationContext(), filename[kk], ja.toString(), true);						
					} catch (final FileNotFoundException e) {  e.printStackTrace(); }
					catch (final JSONException e) { e.printStackTrace(); }
				}
				if (updt) AppMsg.makeText(CrowdTemplates.this, R.string.iDataUpdated ,AppMsg.STYLE_INFO).show();
			}
		};
		return ocl;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_templates);
		settings = getSharedPreferences("FENIX", 0);
		
//		final String[] a = {"commodities.json","cities.json","markets.json","vendors.json"/*, "munits.json"*/};
		final String[] a = {"commodity.json","city.json","market.json","vendor.json"/*, "munit.json"*/};
//		final String[] c = {"Commodities","Cities","Markets","Vendors"/*,"Measurements"*/};
		final String[] c = {"","","",""/*,""*/};
//		final String[] d = {"com","c","m","v",/*"munit"*/};		
		final String[] d = {"","","","",/*""*/};
		final ArrayList<?>[] e = {defaultCommodities,defaultCities,defaultMarkets,defaultVendors/*,defaultMeasurments*/};
		final ArrayList<?>[] f = {commodities,cities,markets,vendors/*,measurments*/};
/*
		generateArray("commodities.json","Commodities","com",defaultCommodities,commodities);
		generateArray("cities.json","Cities","c",defaultCities,cities);
		generateArray("markets.json","Markets","m",defaultMarkets,markets);
		generateArray("vendors.json","Vendors","v",defaultVendors,vendors);
		generateArray("munits.json","Measurements","munit",defaultMeasurments,measurments);
*/		// (filename, jObj, prefix, defaultList, allList);
		
		generateArray("commodity.json",defaultCommodities,commodities);
		generateArray("city.json",defaultCities,cities);
		generateArray("market.json",defaultMarkets,markets);
		generateArray("vendor.json",defaultVendors,vendors);
		generateArray("munit.json",defaultMeasurments,measurments);
		
		
		final Spinner spMeasurement = (Spinner) findViewById(R.id.spMeasurement);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, measurments);
		spMeasurement.setAdapter(arrayAdapter);
		spMeasurement.setSelection(settings.getInt("defMeasure", 0));
		final OnItemSelectedListener isl = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {				
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putInt("defMeasure", spMeasurement.getSelectedItemPosition());
			    editor.commit();					    
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		};

		spMeasurement.setOnItemSelectedListener(isl);

		final MultiSpinner msCommodities = (MultiSpinner) findViewById(R.id.msCommodities);
		msCommodities.setItems(commodities, getString(R.string.tCommodity), defaultCommodities);

		final MultiSpinner msCities = (MultiSpinner) findViewById(R.id.msCities);
		msCities.setItems(cities, getString(R.string.tCity), defaultCities);

		final MultiSpinner msMarkets = (MultiSpinner) findViewById(R.id.msMarkets);
		msMarkets.setItems(markets, getString(R.string.tMarket), defaultMarkets);

		final MultiSpinner msVendors = (MultiSpinner) findViewById(R.id.msVendors);
		msVendors.setItems(vendors, getString(R.string.tVendor), defaultVendors);

		final MultiSpinner[] b = {msCommodities,msCities,msMarkets,msVendors};

		final Button bnt = (Button) findViewById(R.id.btnUpdateTemplate);
		bnt.setOnClickListener(generateClicks(a, b, c, d, e, f));

	}




}
