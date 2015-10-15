package org.faostat.crowdprices;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.faostat.crowdprices.external.SwipeDismissTouchListener;
import org.faostat.crowdprices.rc.R;
import org.faostat.crowdprices.ui.CrowdMultiMenus;
import org.faostat.crowdprices.util.SystemUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class CrowdAdd  extends SherlockActivity {
	
	// Add City
	// Add Market
	// Add Vendor
	// Add Data

	CrowdMultiMenus cmm;
	public CrowdViewPager pager;
	public RelativeLayout v0;
	public RelativeLayout v1;
	public RelativeLayout v2;
	private LinearLayout ll;
	Boolean hasPager = false;
	private int currMarket = 0;
	
	private JSONArray jaSpinner;
	private JSONArray jaSpinnerComm;
	private JSONArray jaSurvey;
	private JSONArray jaSpinnerMunit;
	private JSONArray jaSpinnerVarr;
	private JSONArray jaSpinnerMarket;
	
	private List<EditText> listPrices;
	private List<Spinner> listCommodities;
	private List<Spinner> listVarieties;
	private List<TextView> listCurrency;
	private List<TextView> listMeasurement;
	private List<EditText> listRealDivisor;
	
	private ArrayList<String> spinnerArrayComm = new ArrayList<String>();
	private ArrayList<String> spinnerArrayVarr = new ArrayList<String>();
	private ArrayList<String> spinnerArrayMUnit = new ArrayList<String>();


	private boolean varOpt = true;
	private boolean fromTemplate = false;
	private int curStd = 0;
	
	@Override
	public void onPause() {
	    super.onPause();
	    this.finish();
	}
	
	private int fromNametoPosition(String name, Spinner spin) {
		final ArrayAdapter<String> aa = (ArrayAdapter<String>) spin.getAdapter();
		final int sp = aa.getPosition(name);
		return sp;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		final CrowdPager adapter = new CrowdPager();
		TitlePageIndicator indicator = new TitlePageIndicator(this);
		
		SharedPreferences settings = getSharedPreferences("FENIX", 0);
		varOpt = settings.getBoolean("commVariety", true);
		curStd = settings.getInt("defMeasure", 0);

		listPrices = new ArrayList<EditText>();
		listCommodities = new ArrayList<Spinner>();
		listVarieties = new ArrayList<Spinner>();
		listCurrency = new ArrayList<TextView>();
		listMeasurement = new ArrayList<TextView>();
		listRealDivisor	= new ArrayList<EditText>();


		// Read UI Extras
		String mode = getIntent().getExtras().getString("MODE");
		int res = 0;
		switch (mode) {
			case "data" : 
				hasPager = true;
				res = R.layout.activity_adddata;
				v0 =  (RelativeLayout)  getLayoutInflater().inflate (R.layout.view_statustab, null);
				adapter.addView (v0, 0);
				v1 =  (RelativeLayout)  getLayoutInflater().inflate (R.layout.view_surveytab, null);
				adapter.addView (v1, 0);
				v2 =  (RelativeLayout)  getLayoutInflater().inflate (R.layout.view_locationtab, null);
				adapter.addView (v2, 0);	
				
			break;
			
			case "city" : res = R.layout.activity_addcity;
			break;
			
			case "market" : res = R.layout.activity_addmarket;
			break;
			
			case "vendor" : 
				res = R.layout.activity_addvendor;
			break;
			
		}
		
		// Setup UI
		
		cmm = new CrowdMultiMenus(this, res, true,0);
		
		if (hasPager) {
			pager = (CrowdViewPager) findViewById (R.id.pager);
			pager.setAdapter (adapter);
			pager.setCurrentItem(0);
			indicator = (TitlePageIndicator) findViewById(R.id.indicator);
			indicator.setBackgroundColor(getResources().getColor(R.color.white));
			indicator.setViewPager(pager);
			
			final Button next1 = (Button) v2.findViewById(R.id.btnNext1);
			final Button next2 = (Button) v1.findViewById(R.id.btnNext2);
			final Button back2 = (Button) v1.findViewById(R.id.btnBack2);
			final Button back3 = (Button) v0.findViewById(R.id.btnBack3);
			final Button submit = (Button) v0.findViewById(R.id.btnSubmit);
			final Button clear = (Button) v0.findViewById(R.id.btnClear);
			final Button add = (Button) v1.findViewById(R.id.btnAddCommodity);
			final Button save = (Button) v0.findViewById(R.id.btnSave);
			final Button template = (Button) v0.findViewById(R.id.btnTemplates);
			final Spinner spCity = (Spinner) v2.findViewById(R.id.spinnerCity);
			final Spinner spMarket = (Spinner) v2.findViewById(R.id.spinnerMarket);
			final Spinner spVendor = (Spinner) v2.findViewById(R.id.spinnerVendor);		
			final Spinner spKind = (Spinner) v2.findViewById(R.id.spinnerKind);
			final EditText etNotes = (EditText) v2.findViewById(R.id.etNote);
			

			next1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) { pager.setCurrentItem(pager.getCurrentItem()+1); }
			});
			next2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) { pager.setCurrentItem(pager.getCurrentItem()+1); }
			});
			back2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) { pager.setCurrentItem(pager.getCurrentItem()-1); }
			});
			back3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) { pager.setCurrentItem(pager.getCurrentItem()-1); }
			});

			// Fill Spinners
			final ArrayList<String> spinnerArrayCity = new ArrayList<String>();
			final ArrayList<String> spinnerArrayMark = new ArrayList<String>();
			final ArrayList<String> spinnerArrayVend = new ArrayList<String>();
			final ArrayList<String> spinnerArrayComm = new ArrayList<String>();
			final ArrayList<String> spinnerArrayVarr = new ArrayList<String>();
			final ArrayList<String> spinnerArrayMUnit = new ArrayList<String>();
			final ArrayList<Double> listGPSLat = new ArrayList<Double>();
			final ArrayList<Double> listGPSLon = new ArrayList<Double>();
			int curStd = 0;			
			
			try {
				StringBuffer sb = SystemUtils.readFile(getApplicationContext(),"city.json");
				String jsontext = new String(sb);
				jaSpinner = new JSONArray(jsontext);
				for (int j = 0; j < jaSpinner.length(); j++) if(jaSpinner.getJSONObject(j).getBoolean("shown"))
					spinnerArrayCity.add(jaSpinner.getJSONObject(j).getString("name"));

				sb = SystemUtils.readFile(getApplicationContext(),"market.json");
				jsontext = new String(sb);
				jaSpinner = new JSONArray(jsontext);
				for (int j = 0; j < jaSpinner.length(); j++) if(jaSpinner.getJSONObject(j).getBoolean("shown"))
					spinnerArrayMark.add(jaSpinner.getJSONObject(j).getString("name"));

				sb = SystemUtils.readFile(getApplicationContext(),"munit.json");
				jsontext = new String(sb);
				jaSpinnerMunit = new JSONArray(jsontext);
				for (int j = 0; j < jaSpinnerMunit.length(); j++) if(jaSpinnerMunit.getJSONObject(j).getBoolean("shown"))
				if (j == curStd) 
					spinnerArrayMUnit.add(jaSpinnerMunit.getJSONObject(j).getString("name"));

				sb = SystemUtils.readFile(getApplicationContext(),"commodity.json");
				jsontext = new String(sb);
				jaSpinnerComm = new JSONArray(jsontext);
				for (int j = 0; j < jaSpinnerComm.length(); j++) if(jaSpinnerComm.getJSONObject(j).getBoolean("shown"))
					spinnerArrayComm.add(jaSpinnerComm.getJSONObject(j).getString("name"));

				sb = SystemUtils.readFile(getApplicationContext(),"variety.json");
				jsontext = new String(sb);
				jaSpinnerVarr = new JSONArray(jsontext);
				for (int j = 0; j < jaSpinnerVarr.length(); j++) if(jaSpinnerVarr.getJSONObject(j).getBoolean("shown"))
					spinnerArrayVarr.add(jaSpinnerVarr.getJSONObject(j).getString("name"));
				
				
				sb = SystemUtils.readFile(getApplicationContext(),"vendor.json");
				jsontext = new String(sb);
				jaSpinner = new JSONArray(jsontext);
				for (int j = 0; j < jaSpinner.length(); j++) if(jaSpinner.getJSONObject(j).getBoolean("shown")) {
					listGPSLat.add(jaSpinner.getJSONObject(j).getJSONObject("geo").getJSONArray("coordinates").getDouble(0));
					listGPSLon.add(jaSpinner.getJSONObject(j).getJSONObject("geo").getJSONArray("coordinates").getDouble(1));
					spinnerArrayVend.add(jaSpinner.getJSONObject(j).getString("name"));
				}

			}
			catch (final JSONException e) { e.printStackTrace(); }
			catch (final FileNotFoundException e) { e.printStackTrace(); }
			
			final ArrayAdapter<String> spinnerCityArrayAdapter = new ArrayAdapter<String>(CrowdAdd.this, R.layout.sherlock_spinner_item, spinnerArrayCity);
			spCity.setAdapter(spinnerCityArrayAdapter);
			final ArrayAdapter<String> spinnerMarketArrayAdapter = new ArrayAdapter<String>(CrowdAdd.this, R.layout.sherlock_spinner_item, spinnerArrayMark);
			spMarket.setAdapter(spinnerMarketArrayAdapter);
			final ArrayAdapter<String> spinnerVendorArrayAdapter = new ArrayAdapter<String>(CrowdAdd.this, R.layout.sherlock_spinner_item, spinnerArrayVend);
			spVendor.setAdapter(spinnerVendorArrayAdapter);
			

			spCity.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					final Spinner spMarket = (Spinner) v2.findViewById(R.id.spinnerMarket);
					int numb = 0;
					String[] marketArray = new String[50];
					JSONArray ja1 = new JSONArray();
					JSONArray ja2 = new JSONArray();
					try {
						final StringBuffer sb1 = SystemUtils.readFile(getApplicationContext(),"market.json");
						final String jsontext1 = new String(sb1);
						
						final StringBuffer sb2 = SystemUtils.readFile(getApplicationContext(),"city.json");
						final String jsontext2 = new String(sb2);
						
						ja1 = new JSONArray(jsontext1);
						ja2 = new JSONArray(jsontext2);
						
						marketArray = new String[ja1.length()];
						
						for (int j = 0; j < ja1.length(); j++) {
							int mark = ja1.getJSONObject(j).getInt("citycode");
							int city = ja2.getJSONObject(position).getInt("code");
							
							if (mark == city) {
								marketArray[j] = ja1.getJSONObject(j).getString("name");
								currMarket = j;
								numb++;
							}
						}
					}
					catch (final JSONException e) { e.printStackTrace(); }
					catch (final FileNotFoundException e) { e.printStackTrace(); }
					final String[] singleMArray = new String[numb];
					int pos = 0;
					for (int k=0; k < ja1.length(); k++) if (marketArray[k] != null) {
						singleMArray[pos] = marketArray[k];
						pos++;
					}
					final ArrayList<String> spinnerArray = new ArrayList<String>(Arrays.asList(singleMArray));
					final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CrowdAdd.this, R.layout.sherlock_spinner_item, spinnerArray);
					spMarket.setAdapter(spinnerArrayAdapter);
					spMarket.setSelection(0);
				}


				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					// ?
				}

			});
			spMarket.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					final Spinner spVendor = (Spinner) v2.findViewById(R.id.spinnerVendor);
					int numb = 0;
					int vendor = 0;
					JSONArray ja1 = new JSONArray();
					JSONArray ja2 = new JSONArray();
					String[] vendorArray = new String[50];
					try {
						final StringBuffer sb1 = SystemUtils.readFile(getApplicationContext(),"vendor.json");
						final String jsontext1 = new String(sb1);
						final StringBuffer sb2 = SystemUtils.readFile(getApplicationContext(),"market.json");
						final String jsontext2 = new String(sb2);
						ja1 = new JSONArray(jsontext1);
						ja2 = new JSONArray(jsontext2);
						vendorArray = new String[ja1.length()];
						
						for (int j = 0; j < ja1.length(); j++) {
							vendor = ja1.getJSONObject(j).getInt("marketcode");
							int mark = ja2.getJSONObject(currMarket).getInt("code");							
							if (vendor == mark) {
								vendorArray[j] = ja1.getJSONObject(j).getString("name");
								numb++;
							}
						}
					}
					catch (final JSONException e) { e.printStackTrace(); }
					catch (final FileNotFoundException e) { e.printStackTrace(); }

					final String[] singleMArray = new String[numb];
					int pos = 0;
					for (int k=0; k < ja1.length(); k++) if (vendorArray[k] != null) {
						singleMArray[pos] = vendorArray[k];
						pos++;
					}
					final ArrayList<String> spinnerArray = new ArrayList<String>(Arrays.asList(singleMArray));
					final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CrowdAdd.this, R.layout.sherlock_spinner_item, spinnerArray);
					spVendor.setAdapter(spinnerArrayAdapter);

				}


				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					// ?
				}

			});
			
			add.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) { addElementSurvey(new JSONObject()); }
			});
			template.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) { /* TODO  saveAsTemplate(); */  }
			});
			submit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) { /* TODO  submitPGSQL(); */  }
			});
			clear.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) { clearSurvey();  }
			});			
				
			ll = (LinearLayout) v1.findViewById(R.id.llSurvey);
			
			// Read other Extras
			if (getIntent().hasExtra("FENIX")) {
				
				final String value = getIntent().getExtras().getString("FENIX");
				String currentFile = "";
				String hasWritten = "";
				JSONObject entries;
				JSONArray jaSurvey;

				StringBuffer sb;
				try {
					sb = SystemUtils.readFile(getApplicationContext(),value);
					currentFile = value;
					final String jsontext = new String(sb);
					entries = new JSONObject(jsontext);
					if (entries.get("SurveyData") instanceof JSONArray) {
						jaSurvey = entries.getJSONArray("SurveyData");
					} else {
						final JSONObject jobj = entries.getJSONObject("SurveyData");
						jaSurvey = new JSONArray("["+jobj.toString()+"]");
					}
					final JSONObject josta = entries.getJSONObject("Status");				
					fromTemplate = josta.getBoolean("fromTemplate");
					if (fromTemplate) {
						save.setText(R.string.btnSave);
						clear.setText(R.string.btnClearSurvey);
						template.setVisibility(View.GONE);
					}
					for(int i=0; i< jaSurvey.length(); i++) addElementSurvey(jaSurvey.getJSONObject(i));
					final JSONObject joob = entries.getJSONObject("Location");				
					spCity.setSelection(fromNametoPosition(joob.getString("cityCode"),spCity));
					spMarket.setSelection(fromNametoPosition(joob.getString("marketCode"),spMarket));
					spVendor.setSelection(fromNametoPosition(joob.getString("vendorCode"),spVendor));
					spKind.setSelection(joob.getInt("kind"));
					etNotes.setText(joob.getString("notes"));

				} catch (final FileNotFoundException e) { e.printStackTrace(); }
				catch (final JSONException e) { e.printStackTrace(); }

			} else {
				Log.i("FENIX", "NULL");
			}
			
		}
		
		
		
	}

	private void clearSurvey() {
		pager.setCurrentItem(pager.getCurrentItem()-1);
		listPrices = new ArrayList<EditText>();
		listCommodities = new ArrayList<Spinner>();
		listVarieties = new ArrayList<Spinner>();
		listCurrency = new ArrayList<TextView>();
		listMeasurement = new ArrayList<TextView>();
		listRealDivisor	= new ArrayList<EditText>();		
		ll.removeAllViews();
	}

	private void removeElementSurvey() {
		if (!listPrices.isEmpty()) listPrices.remove(listPrices.size()-1);
		if (!listCommodities.isEmpty()) listCommodities.remove(listCommodities.size()-1);
		if (!listCurrency.isEmpty()) listCurrency.remove(listCurrency.size()-1);
		if (!listMeasurement.isEmpty()) listMeasurement.remove(listMeasurement.size()-1);
		if (!listRealDivisor.isEmpty()) listRealDivisor.remove(listRealDivisor.size()-1);
	}
	
	private void addElementSurvey(JSONObject jobj) {
		// Init Variables
		final Context ctx = this;
		final RelativeLayout container = new RelativeLayout(this);
		final TextView twCommodities = new TextView(this);
		final Spinner spCommodities = new Spinner(this);
		final Spinner spVarieties = new Spinner(this);
		final EditText etQuantity = new EditText(this);
		
		final RelativeLayout.LayoutParams lplbl = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		lplbl.setMargins(5, 0, 5, 0);
		ll.getParent();
		final Display display = getWindowManager().getDefaultDisplay();
		final int pixels = display.getWidth()/2;
		
		// Set UI
		// Commodity List
		final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, spinnerArrayComm);
		spCommodities.setAdapter(spinnerArrayAdapter);
		spCommodities.setId((listCommodities.size()*100)+2);
		final OnItemSelectedListener isl = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				try {
					etQuantity.setText(jaSpinnerComm.getJSONObject(position).getString("divisor"));
					if (varOpt) {
						spinnerArrayVarr = new ArrayList<String>();
						for (int j = 0; j < jaSpinnerVarr.length(); j++) if(jaSpinnerVarr.getJSONObject(j).getInt("commoditycode") == position) 
							spinnerArrayVarr.add(jaSpinnerVarr.getJSONObject(j).getString("name"));						
						final ArrayAdapter<String> spinnerVarArrayAdapter = new ArrayAdapter<String>(ctx, R.layout.sherlock_spinner_item, spinnerArrayVarr);
						spVarieties.setAdapter(spinnerVarArrayAdapter);
					}					
				} catch (final JSONException e) { e.printStackTrace(); }
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				etQuantity.setText("");
			}
		};
				
		spCommodities.setOnItemSelectedListener(isl);
		int cpix = pixels*2;
		if (varOpt) cpix = pixels;
		RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(cpix,LayoutParams.WRAP_CONTENT);
		lparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		lparams.addRule(RelativeLayout.BELOW, twCommodities.getId());
		spCommodities.setLayoutParams(lparams);
		listCommodities.add(spCommodities);
			
		// Add
			
			final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 0, 0, 20);
			container.setLayoutParams(lp);

			//Swipe to Dismiss
			SwipeDismissTouchListener dismiss;
			dismiss = new SwipeDismissTouchListener(container,null, new SwipeDismissTouchListener.OnDismissCallback() {
				
				@Override
				public void onDismiss(View view, Object token) {	
					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
					ll.removeView(container);
					ll.invalidate();
					removeElementSurvey();
				}
				
				@Override
				public void onPreDismiss(View view, Object token) {
					// TODO Auto-generated method stub					
				}
			});
			twCommodities.setOnTouchListener(dismiss);
			
			
			ll.addView(container);
			// .. e bona.
	}
	

}
