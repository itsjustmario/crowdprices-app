package org.faostat.crowdprices;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.faostat.crowdprices.CrowdMaps.MapMakers;
import org.faostat.crowdprices.external.SwipeDismissTouchListener;
import org.faostat.crowdprices.rc.R;
import org.faostat.crowdprices.ui.CrowdMultiMenus;
import org.faostat.crowdprices.util.SystemUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.devspark.appmsg.AppMsg;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class CrowdAddData extends SherlockActivity {
	//private SystemUiHider mSystemUiHider;

	public RelativeLayout v0;
	public RelativeLayout v1;
	public static RelativeLayout v2;
	private LinearLayout ll;
	//private ListView ll; 
	private List<EditText> listPrices;
	private List<Spinner> listCommodities;
	private List<Spinner> listVarieties;
	private List<TextView> listCurrency;
	private List<TextView> listMeasurement;
	private List<EditText> listRealDivisor;
	private ArrayList<Integer> listMarket;
	private ArrayList<Integer> listCity;
	private ArrayList<Integer> listVendor;
	//private ArrayList<String> listGPSLat;
	//private ArrayList<String> listGPSLon;
	private ArrayList<Double> listGPSLat;
	private ArrayList<Double> listGPSLon;
	private int lastID;
	private String citycode;
	//private String marketcode;
	private String s = "";
	private boolean isdone = false;
	private boolean hasSent = false;
	private boolean varOpt = true;
	private boolean oldWeb = false;
	private boolean fromTemplate = false;
	private int curStd = 0;
	private String currentFile = "";
	private String hasWritten = "";
	//private JSONArray ja;
	public static CrowdViewPager myPager;
	private JSONArray jaSpinner;
	private JSONArray jaSpinnerComm;
	private JSONArray jaSurvey;
	private JSONArray jaSpinnerMunit;
	private JSONArray jaSpinnerVarr;
	private JSONArray jaSpinnerMarket;
	private JSONObject entries;
	private int currMarket = 0;
	private final ArrayList<String> spinnerArrayComm = new ArrayList<String>();
	private ArrayList<String> spinnerArrayVarr = new ArrayList<String>();
	private final ArrayList<String> spinnerArrayMUnit = new ArrayList<String>();
	
	CrowdMultiMenus cmm;

	@SuppressLint("NewApi")
	private void addElementSurvey(JSONObject jobj) {
		// TODO Auto-generated method stub
		final Context ctx = this;
		final RelativeLayout container = new RelativeLayout(this);
		final TextView twCommodities = new TextView(this);
		final Spinner spCommodities = new Spinner(this);
		final Spinner spVarieties = new Spinner(this);
		final EditText etQuantity = new EditText(this);
		final EditText etPrice = new EditText(this);
		//final Spinner spCurrency = new Spinner(this);
		final RelativeLayout QuaMeaContainer = new RelativeLayout(this);
		final RelativeLayout PriCuContainer = new RelativeLayout(this);
		//final Spinner spMeasure = new Spinner(this);
		final TextView tvMeasure = new TextView(this);
		final TextView tvCurrency = new TextView(this);
		final RelativeLayout.LayoutParams lplbl = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		lplbl.setMargins(5, 0, 5, 0);
		ll.getParent();
		//int pixels = (parent.getWidth()/2);
		final Display display = getWindowManager().getDefaultDisplay();
		final int pixels = display.getWidth()/2;

		// Commodity Text
		twCommodities.setText(R.string.tCommodity);
		twCommodities.setLayoutParams(lplbl);
		twCommodities.setId((listCommodities.size()*100)+1);

		// Commodity List
		//String[] commoArray = getResources().getStringArray(R.array.commodities);
		//ArrayList spinnerArray = new ArrayList<String>(Arrays.asList(commoArray));
		final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, spinnerArrayComm);
		spCommodities.setAdapter(spinnerArrayAdapter);
		spCommodities.setId((listCommodities.size()*100)+2);
		final OnItemSelectedListener isl = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				//etQuantity.setText(Integer.toString(listDivisor[position]));
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

		/*
		// Quantity
		etQuantity.setHint(R.string.tQuantity);
		etQuantity.setId((listCommodities.size()*100)+3);
		etQuantity.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		etQuantity.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)});
		listRealDivisor.add(etQuantity);
		lparams = new RelativeLayout.LayoutParams(pixels/2+50,LayoutParams.WRAP_CONTENT);
		lparams.addRule(RelativeLayout.BELOW, twCommodities.getId());
		lparams.addRule(RelativeLayout.RIGHT_OF, spCommodities.getId());
		etQuantity.setLayoutParams(lparams);

		// Measurement Unit List
		tvMeasure.setText(spinnerArrayMUnit.get(0));
		tvMeasure.setId((listCommodities.size()*100)+4);	
		tvMeasure.setPadding(0, 17, 0, 0);
		lparams = new RelativeLayout.LayoutParams(pixels/2-50,LayoutParams.WRAP_CONTENT);
		lparams.addRule(RelativeLayout.BELOW, twCommodities.getId());
		lparams.addRule(RelativeLayout.RIGHT_OF, etQuantity.getId());		
		tvMeasure.setGravity(Gravity.CENTER);
		tvMeasure.setLayoutParams(lparams);			
		listMeasurement.add(tvMeasure);
		*/
		
		/*
		final ArrayAdapter<String> spinner2ArrayAdapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, spinnerArrayMUnit);
		spMeasure.setAdapter(spinner2ArrayAdapter);
		spMeasure.setId((listCommodities.size()*100)+4);
		lparams = new RelativeLayout.LayoutParams(pixels/2-50,LayoutParams.WRAP_CONTENT);
		lparams.addRule(RelativeLayout.BELOW, twCommodities.getId());
		lparams.addRule(RelativeLayout.RIGHT_OF, etQuantity.getId());
		spMeasure.setLayoutParams(lparams);
		spMeasure.setSelection(curStd);		
		listMeasurement.add(spMeasure);
		*/
		
		// New Price and Currency Unit
		lparams = new RelativeLayout.LayoutParams(pixels*2,LayoutParams.WRAP_CONTENT);
		lparams.addRule(RelativeLayout.BELOW, spCommodities.getId());			
		
		PriCuContainer.setLayoutParams(lparams);
		PriCuContainer.setId((listCommodities.size()*100)+3);
		etPrice.setHint(R.string.tPrice);
		etPrice.setId((listCommodities.size()*100)+4);
		etPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
		etPrice.setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));
		etPrice.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)});
		etPrice.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		listPrices.add(etPrice);
		lparams = new RelativeLayout.LayoutParams(pixels/2+50,LayoutParams.WRAP_CONTENT);
		lparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);	
		etPrice.setLayoutParams(lparams);				
		PriCuContainer.addView(etPrice);
		// remove this and take it from settings
		final String[] currArray = getResources().getStringArray(R.array.currency);
		final ArrayList spinner3Array = new ArrayList<String>(Arrays.asList(currArray));
		tvCurrency.setText(spinner3Array.get(0).toString());		
		tvCurrency.setId((listCommodities.size()*100)+5);	
		lparams = new RelativeLayout.LayoutParams(pixels/2-50,LayoutParams.WRAP_CONTENT);
		lparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		tvCurrency.setGravity(Gravity.CENTER);
		tvCurrency.setTextSize(12);
		//tvCurrency.setSingleLine();
		tvCurrency.setLayoutParams(lparams);			
		listCurrency.add(tvCurrency);
		PriCuContainer.addView(tvCurrency);
		if (android.os.Build.VERSION.SDK_INT < 11) {
			PriCuContainer.setBackgroundResource(android.R.drawable.editbox_background);
			etPrice.setPadding(0, 10, 30, 6); 	
			tvCurrency.setPadding(0, 5, 0, 0);
		} else {
			PriCuContainer.setBackgroundResource(R.drawable.apptheme_textfield_default_holo_light);
			etPrice.setPadding(0, -4, 30, 3); 	
			tvCurrency.setPadding(0, 2, 0, 0);
		}
		

		/*
		// Price Value
		etPrice.setHint(R.string.tPrice);
		etPrice.setId((listCommodities.size()*100)+5);
		etPrice.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		etPrice.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)});
		listPrices.add(etPrice);
		lparams = new RelativeLayout.LayoutParams(pixels,LayoutParams.WRAP_CONTENT);
		lparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		lparams.addRule(RelativeLayout.BELOW, spCommodities.getId());
		etPrice.setLayoutParams(lparams);

		// Currency Unit
		final String[] currArray = getResources().getStringArray(R.array.currency);
		final ArrayList spinner3Array = new ArrayList<String>(Arrays.asList(currArray));
		final ArrayAdapter<String> spinner3ArrayAdapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, spinner3Array);
		spCurrency.setAdapter(spinner3ArrayAdapter);
		spCurrency.setId((listCommodities.size()*100)+6);
		lparams = new RelativeLayout.LayoutParams(pixels,LayoutParams.WRAP_CONTENT);
		//lparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		lparams.addRule(RelativeLayout.BELOW, spCommodities.getId());
		lparams.addRule(RelativeLayout.RIGHT_OF, etPrice.getId());
		spCurrency.setLayoutParams(lparams);
		listCurrency.add(spCurrency);
		*/
		
		// Varieties
		if (varOpt) {
			final ArrayAdapter<String> spinnerVarArrayAdapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, spinnerArrayVarr);
			spVarieties.setAdapter(spinnerVarArrayAdapter);
			spVarieties.setId((listCommodities.size()*100)+6);
			lparams = new RelativeLayout.LayoutParams(pixels,LayoutParams.WRAP_CONTENT);
			lparams.addRule(RelativeLayout.BELOW, twCommodities.getId());
			lparams.addRule(RelativeLayout.RIGHT_OF, spCommodities.getId());
			spVarieties.setLayoutParams(lparams);			
			listVarieties.add(spVarieties);			
		}
		
		// New Quantity with Measurement Inside
		lparams = new RelativeLayout.LayoutParams(pixels*2,LayoutParams.WRAP_CONTENT);
		lparams.addRule(RelativeLayout.BELOW, PriCuContainer.getId());			
		
		QuaMeaContainer.setLayoutParams(lparams);
		QuaMeaContainer.setId((listCommodities.size()*100)+7);
		etQuantity.setHint(R.string.tQuantity);
		etQuantity.setId((listCommodities.size()*100)+8);
		etQuantity.setBackgroundColor(getResources().getColor(android.R.color.transparent));	//
		etQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
		etQuantity.setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));
		etQuantity.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)});
		listRealDivisor.add(etQuantity);
		lparams = new RelativeLayout.LayoutParams(pixels/2+50,LayoutParams.WRAP_CONTENT);
		lparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);	
		etQuantity.setLayoutParams(lparams);		
		QuaMeaContainer.addView(etQuantity);
		tvMeasure.setText(spinnerArrayMUnit.get(0));
		tvMeasure.setId((listCommodities.size()*100)+9);	
		lparams = new RelativeLayout.LayoutParams(pixels/2-50,LayoutParams.WRAP_CONTENT);
		lparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		tvMeasure.setGravity(Gravity.CENTER);
		tvMeasure.setLayoutParams(lparams);			
		listMeasurement.add(tvMeasure);
		QuaMeaContainer.addView(tvMeasure);
		if (android.os.Build.VERSION.SDK_INT < 11) {
			QuaMeaContainer.setBackgroundResource(android.R.drawable.editbox_background);
			etQuantity.setPadding(0, 10, 30, 6); 	
			tvMeasure.setPadding(0, 12, 0, 0);
		} else {
			QuaMeaContainer.setBackgroundResource(R.drawable.apptheme_textfield_default_holo_light);
			etQuantity.setPadding(0, -4, 30, 3); 	
			tvMeasure.setPadding(0, 9, 0, 0);
		}

		
		// Delete button
		final Button btnDel = new Button(this);
		btnDel.setText("Delete");
		btnDel.setId((listCommodities.size()*100)+99);
		lparams = new RelativeLayout.LayoutParams(pixels,LayoutParams.FILL_PARENT);
		lparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		btnDel.setLayoutParams(lparams);


		// Add them all...
		//container.addView(twCommodities);
		container.addView(spCommodities);
		//		container.addView(etQuantity);		
		//		container.addView(tvMeasure);
		//container.addView(spMeasure);
		//		container.addView(etPrice);
		//		container.addView(spCurrency);
		container.addView(PriCuContainer);
		if (varOpt) container.addView(spVarieties);
		container.addView(QuaMeaContainer);
		//container.addView(btnDel);

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

		
		//twCommodities.setOnTouchListener(dismiss);
		//spCommodities.setOnItemLongClickListener(null);
		etQuantity.setOnTouchListener(dismiss);
		etQuantity.setLongClickable(false);
		//spMeasure.setOnTouchListener(dismiss);		
		etPrice.setOnTouchListener(dismiss);
		etPrice.setLongClickable(false);
		//spCurrency.setOnTouchListener(dismiss);
		
		
		// preload element
		if (jobj.length() > 0) {	
			
			try {
				JSONArray jar = jobj.getJSONArray("data"); 
				jobj = jar.getJSONObject(0);
				spCommodities.setOnItemSelectedListener(null);
				spCommodities.setSelection(fromNametoPosition(jobj.getString("commoditycode"),spCommodities));
				spVarieties.setSelection(fromNametoPosition(jobj.getString("varietycode"),spVarieties));
				if (!fromTemplate) etPrice.setText(jobj.getString("price"));
				etQuantity.setText(jobj.getString("quantity"));
				// TODO: Set measurement unit and currenty 'FO REAL!
				//tvMeasure.setText(jobj.getString("munitcode"));
				//spMeasure.setSelection(fromNametoPosition(jobj.getString("munitCode"),spMeasure));
				//spCurrency.setSelection(fromNametoPosition(jobj.getString("currencyCode"),spCurrency));				
				
			} catch (final JSONException e) { e.printStackTrace(); }
		}
		
		
		if (listCommodities.size() > 1 && varOpt && !getIntent().hasExtra("FENIX")) {
			Spinner sp = (Spinner) findViewById(lastID);			
			int kkk = sp.getSelectedItemPosition();			
			spCommodities.setSelection(kkk);
		}
		
		
		lastID = spCommodities.getId();
		ll.addView(container);
		// .. e bona.
	}

	private void changeMessage(Context c, boolean b, final int area) {
		// area = 0 > saved on device
		// area = 1 > tranmission
		// area = 2 > save as template
		isdone = b;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				// TODO Auto-generated method stub
				final TextView subStatus = (TextView) v0.findViewById(R.id.StatusTransmission);
				final TextView devStatus = (TextView) v0.findViewById(R.id.StatusDevice);

				if (isdone) {
					switch(area) {
						case 0:
							devStatus.setText("SAVED");
							AppMsg.makeText(CrowdAddData.this, R.string.okSaved ,AppMsg.STYLE_INFO).show();
						break;
						case 1:
							subStatus.setText("SUBMITTED");
							AppMsg.makeText(CrowdAddData.this, R.string.okSubmitted ,AppMsg.STYLE_INFO).show();
						break;
						case 2:
							devStatus.setText("SAVED");
							AppMsg.makeText(CrowdAddData.this, R.string.okTemplate ,AppMsg.STYLE_INFO).show();
						break;
					}
				if (area != 2) {
						final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which){
								case DialogInterface.BUTTON_POSITIVE:
									//Yes button clicked
									clearSurvey();
									break;

								case DialogInterface.BUTTON_NEGATIVE:
									//No button clicked
									break;
								}
							}
						};

						final AlertDialog.Builder builder = new AlertDialog.Builder(CrowdAddData.this);
						builder.setMessage(R.string.qNewSurvey).setPositiveButton(android.R.string.yes, dialogClickListener)
						.setNegativeButton(android.R.string.no, dialogClickListener).show();
				}
					
				} else {
					switch(area) {
					case 0:
						devStatus.setTextColor(getResources().getColor(R.color.red));
						devStatus.setText("ERROR");
						AppMsg.makeText(CrowdAddData.this, R.string.errSaved ,AppMsg.STYLE_ALERT).show();
					break;
					case 1:
						subStatus.setTextColor(getResources().getColor(R.color.red));
						subStatus.setText("ERROR");
						AppMsg.makeText(CrowdAddData.this, R.string.errSubmitted ,AppMsg.STYLE_ALERT).show();
					break;
					case 2:
						devStatus.setTextColor(getResources().getColor(R.color.red));
						devStatus.setText("ERROR");
						AppMsg.makeText(CrowdAddData.this, R.string.errTemplate ,AppMsg.STYLE_ALERT).show();
					break;
					}
				}

			}
		});
	}

	private void clearSurvey() {
		myPager.setCurrentItem(myPager.getCurrentItem()-1);
		listPrices = new ArrayList<EditText>();
		listCommodities = new ArrayList<Spinner>();
		listVarieties = new ArrayList<Spinner>();
		listCurrency = new ArrayList<TextView>();
		listMeasurement = new ArrayList<TextView>();
		listRealDivisor	= new ArrayList<EditText>();		
		ll.removeAllViews();
	}

	private int fromNametoPosition(String name, Spinner spin) {
		final ArrayAdapter<String> aa = (ArrayAdapter<String>) spin.getAdapter();
		final int sp = aa.getPosition(name);
		return sp;
	}

	private int fromCodetoPosition(String code, String JSON) {
		int sp = 0;
		StringBuffer sb;
		JSONObject jObj;
		JSONArray jArr;
		
		try {
			sb = SystemUtils.readFile(getApplicationContext(),JSON);
			String jsontext = new String(sb);
			jArr = new JSONArray(jsontext);
			Log.i("FENIX",jsontext);
			for (int i=0; i< jArr.length(); i++) {				
				Log.i("FENIX", "check if "+jArr.getJSONObject(i).getString("code")+ " = "+code);
				if (jArr.getJSONObject(i).getString("code").equals(code)) sp = i;
			}
		}		
		catch (FileNotFoundException e) { e.printStackTrace(); } 
		catch (JSONException e) { e.printStackTrace(); }
		
		Log.i("FENIX", "spin is :"+sp);
		return sp;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cmm = new CrowdMultiMenus(this, R.layout.activity_adddata, true,0);
		
		SharedPreferences settings = getSharedPreferences("FENIX", 0);
		varOpt = settings.getBoolean("commVariety", true);
		curStd = settings.getInt("defMeasure", 0);
		oldWeb = settings.getBoolean("oldWeb", false);

		Log.i("FENIX", "old is " + oldWeb);
		
		listPrices = new ArrayList<EditText>();
		listCommodities = new ArrayList<Spinner>();
		listVarieties = new ArrayList<Spinner>();
		listCurrency = new ArrayList<TextView>();
		listMeasurement = new ArrayList<TextView>();
		listRealDivisor	= new ArrayList<EditText>();

		//listDivisor = new ArrayList<Integer>(R.array.commoditiesDivisor);

	
		final CrowdPager adapter = new CrowdPager();
		myPager = (CrowdViewPager) findViewById (R.id.pager);
		v0 =  (RelativeLayout)  getLayoutInflater().inflate (R.layout.view_statustab, null);
		adapter.addView (v0, 0);
		v1 =  (RelativeLayout)  getLayoutInflater().inflate (R.layout.view_surveytab, null);
		adapter.addView (v1, 0);
		v2 =  (RelativeLayout)  getLayoutInflater().inflate (R.layout.view_locationtab, null);
		adapter.addView (v2, 0);
		myPager.setAdapter (adapter);
		myPager.setCurrentItem(0);
		final TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		indicator.setBackgroundColor(getResources().getColor(R.color.white));
		indicator.setViewPager(myPager);
		
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
		final ArrayList<String> spinnerArrayCity = new ArrayList<String>();
		final ArrayList<String> spinnerArrayMark = new ArrayList<String>();
		final ArrayList<String> spinnerArrayVend = new ArrayList<String>();
		listGPSLat = new ArrayList<Double>();
		listGPSLon = new ArrayList<Double>();
		listMarket = new ArrayList<Integer>();
		listCity = new ArrayList<Integer>();
		listVendor = new ArrayList<Integer>();


		if(getIntent().hasExtra("FENIX") && !fromTemplate) {
			save.setText(R.string.btnUpdt);
			clear.setText(R.string.btnDeleteSurvey);
			//submit.setText(R.string.btnSubmitAndDelete);
		}

		// Fill Spinners
		try {
			StringBuffer sb = SystemUtils.readFile(getApplicationContext(),"city.json");
			String jsontext = new String(sb);
			jaSpinner = new JSONArray(jsontext);
			for (int j = 0; j < jaSpinner.length(); j++) if(jaSpinner.getJSONObject(j).getBoolean("shown")) {
				listCity.add(jaSpinner.getJSONObject(j).getInt("code"));
				spinnerArrayCity.add(jaSpinner.getJSONObject(j).getString("name"));
			}
				

			sb = SystemUtils.readFile(getApplicationContext(),"market.json");
			jsontext = new String(sb);
			jaSpinner = new JSONArray(jsontext);
			for (int j = 0; j < jaSpinner.length(); j++) if(jaSpinner.getJSONObject(j).getBoolean("shown")) {
				listMarket.add(jaSpinner.getJSONObject(j).getInt("code"));
				spinnerArrayMark.add(jaSpinner.getJSONObject(j).getString("name"));
			}

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
				listVendor.add(jaSpinner.getJSONObject(j).getInt("code"));
				//listGPSLon.add(jaSpinner.getJSONObject(j).getString("vLon"));
				//listGPSLat.add(jaSpinner.getJSONObject(j).getString("vLat"));
				spinnerArrayVend.add(jaSpinner.getJSONObject(j).getString("name"));
			}

		}
		catch (final JSONException e) { e.printStackTrace(); }
		catch (final FileNotFoundException e) { e.printStackTrace(); }

		final ArrayAdapter<String> spinnerCityArrayAdapter = new ArrayAdapter<String>(CrowdAddData.this, R.layout.sherlock_spinner_item, spinnerArrayCity);
		spCity.setAdapter(spinnerCityArrayAdapter);
		final ArrayAdapter<String> spinnerMarketArrayAdapter = new ArrayAdapter<String>(CrowdAddData.this, R.layout.sherlock_spinner_item, spinnerArrayMark);
		spMarket.setAdapter(spinnerMarketArrayAdapter);
		final ArrayAdapter<String> spinnerVendorArrayAdapter = new ArrayAdapter<String>(CrowdAddData.this, R.layout.sherlock_spinner_item, spinnerArrayVend);
		spVendor.setAdapter(spinnerVendorArrayAdapter);


		spCity.setOnItemSelectedListener(new OnItemSelectedListener() {
			/*

	    	@Override
	        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	        	final Spinner spMarket = (Spinner) v2.findViewById(R.id.spinnerMarket);
	        	//spMarket.setSelection(position);
	        	InputStream isMarkets = getApplication().getResources().openRawResource(R.raw.markets);
	        	String[] marketArray = SystemUtils.fromArrayStringtoStringArray(SystemUtils.fromJSONtoArrayString(isMarkets, "Markets", "mName"));
	        	//marketArray = getResources().getStringArray(R.array.markets);
	    	    String singleMArray = getResources().getString(R.string.sMarketOf)+" "+marketArray[position];
	    	    ArrayList spinnerArray = new ArrayList<String>(Arrays.asList(singleMArray));
	            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CrowdAddData.this, R.layout.sherlock_spinner_item, spinnerArray);
	            spMarket.setAdapter(spinnerArrayAdapter);

	        }

			 */

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
							//Log.i("FENIX","found: "+mark+ " = "+city+" ["+ja1.getJSONObject(j).getString("name")+"="+ja2.getJSONObject(position).getString("name")+"]");
							marketArray[j] = ja1.getJSONObject(j).getString("name");
							currMarket = j;
							numb++;
						}
					}
					//spinnerArray.add(ja.getJSONObject(j).getString("mName"));
				}
				catch (final JSONException e) { e.printStackTrace(); }
				catch (final FileNotFoundException e) { e.printStackTrace(); }
				//InputStream isMarkets = getApplication().getResources().openRawResource(R.raw.markets);
				//String[] marketArray = SystemUtils.fromArrayStringtoStringArray(SystemUtils.fromJSONtoArrayString(isMarkets, "Markets", "mName"));
				//marketArray = getResources().getStringArray(R.array.markets);
				final String[] singleMArray = new String[numb];
				int pos = 0;
				for (int k=0; k < ja1.length(); k++) if (marketArray[k] != null) {
					//singleMArray[pos] = getResources().getString(R.string.sMarketOf)+" "+marketArray[k];
					singleMArray[pos] = marketArray[k];
					pos++;
					//Log.i("DAJE", "z"+singleMArray[pos]+" ++ "+k);
				}
				final ArrayList<String> spinnerArray = new ArrayList<String>(Arrays.asList(singleMArray));
				final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CrowdAddData.this, R.layout.sherlock_spinner_item, spinnerArray);
				spMarket.setAdapter(spinnerArrayAdapter);
				//currMarket = position;
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
				//Log.i("MARKET", "SELECTED "+position);
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
							//Log.i("FENIX","vendor: "+mark+ " = "+vendor+" ["+mark+"="+ja1.getJSONObject(j).getString("name")+"]");
							vendorArray[j] = ja1.getJSONObject(j).getString("name");
							numb++;
						}
					}
				}
				catch (final JSONException e) { e.printStackTrace(); }
				catch (final FileNotFoundException e) { e.printStackTrace(); }

				final String[] singleMArray = new String[numb];
				int pos = 0;
				//Log.i("MARKET", "n"+numb);
				for (int k=0; k < ja1.length(); k++) if (vendorArray[k] != null) {
					singleMArray[pos] = vendorArray[k];
					pos++;
				}
				final ArrayList<String> spinnerArray = new ArrayList<String>(Arrays.asList(singleMArray));
				final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CrowdAddData.this, R.layout.sherlock_spinner_item, spinnerArray);
				spVendor.setAdapter(spinnerArrayAdapter);

			}


			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// ?
			}

		});

		add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				//addElementSurvey(null);
				addElementSurvey(new JSONObject());
				//addElementList();
				/*
				SurveyElement se = new SurveyElement(getApplicationContext());
				ll.addView(se);
				*/
			}
		});
		
		

		next1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myPager.setCurrentItem(myPager.getCurrentItem()+1);
			}
		});
		next2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myPager.setCurrentItem(myPager.getCurrentItem()+1);
			}
		});

		back2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myPager.setCurrentItem(myPager.getCurrentItem()-1);
			}
		});
		back3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myPager.setCurrentItem(myPager.getCurrentItem()-1);
			}
		});

		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					saveSurvey();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		template.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveAsTemplate();
			}
		});

		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//submitSurvey();
				if (oldWeb) { 
					submitSurveyOLD(); 
				} else { 
					submitPGSQL(); 
				} 
				//submitPGSQL();
			}
		});

		clear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearSurvey();
			}
		});

		ll = (LinearLayout) v1.findViewById(R.id.llSurvey);
		//ll = (ListView) v1.findViewById(R.id.llSurvey);

		// Read Extras
		if (getIntent().hasExtra("FENIX")) {
				final String value = getIntent().getExtras().getString("FENIX");
				StringBuffer sb;
				try {
					sb = SystemUtils.readFile(getApplicationContext(),value);
					
					currentFile = value;
					final String jsontext = new String(sb);
					entries = new JSONObject(jsontext);
					final JSONObject jobj = entries.getJSONObject("surveydata");
					if (entries.get("surveydata") instanceof JSONArray) {
						jaSurvey = entries.getJSONArray("surveydata");
					} else {
						
						jaSurvey = new JSONArray("["+jobj.toString()+"]");
					}
					//final JSONObject josta = entries.getJSONObject("Status");				
					
					fromTemplate = entries.getBoolean("fromTemplate");
					if (fromTemplate) {
						save.setText(R.string.btnSave);
						clear.setText(R.string.btnClearSurvey);
						template.setVisibility(View.GONE);
					}
					for(int i=0; i< jaSurvey.length(); i++) addElementSurvey(jaSurvey.getJSONObject(i));
					//final JSONObject joob = entries.getJSONObject("Location");					
					JSONObject jobjdata = jobj.getJSONArray("data").getJSONObject(0);
					Log.i("FENIX",jobjdata.toString());
					//spCity.setSelection(fromNametoPosition(jobjdata.getString("citycode"),spCity));
					spCity.setSelection(fromCodetoPosition(jobjdata.getString("citycode"),"city.json"));
					spMarket.setSelection(fromCodetoPosition(jobjdata.getString("marketcode"),"market.json"));
					spVendor.setSelection(fromCodetoPosition(jobjdata.getString("vendorcode"),"vendor.json"));
					spKind.setSelection(jobjdata.getInt("saletypecode"));
					etNotes.setText(jobjdata.getString("note"));

				} catch (final FileNotFoundException e) { e.printStackTrace(); }
				catch (final JSONException e) { e.printStackTrace(); }
		} else {
			Log.i("FENIX", "NULL");
		}

	}
	
	private void removeElementSurvey() {
		if (!listPrices.isEmpty()) listPrices.remove(listPrices.size()-1);
		if (!listCommodities.isEmpty()) listCommodities.remove(listCommodities.size()-1);
		if (!listCurrency.isEmpty()) listCurrency.remove(listCurrency.size()-1);
		if (!listMeasurement.isEmpty()) listMeasurement.remove(listMeasurement.size()-1);
		if (!listRealDivisor.isEmpty()) listRealDivisor.remove(listRealDivisor.size()-1);
	}
	
	private void saveAsTemplate() {
		// TODO Auto-generated method stub
		final Spinner nationSpinner = (Spinner) v2.findViewById(R.id.spinnerNation);
		final Spinner citySpinner = (Spinner) v2.findViewById(R.id.spinnerCity);
		final Spinner marketSpinner = (Spinner) v2.findViewById(R.id.spinnerMarket);
		final Spinner vendorSpinner = (Spinner) v2.findViewById(R.id.spinnerVendor);
		final Spinner kindSpinner = (Spinner) v2.findViewById(R.id.spinnerKind);
		final EditText notes = (EditText) v2.findViewById(R.id.etNote);
		boolean isEmpty = false;

		citycode = citySpinner.getSelectedItem().toString();
		
		final int vendcode = citySpinner.getSelectedItemPosition();
		Double geolat = listGPSLat.get(vendcode); 
		Double geolon = listGPSLon.get(vendcode);

		final String vendorCode = vendorSpinner.getSelectedItem().toString();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		final SimpleDateFormat df = new SimpleDateFormat("SD");
		final Date date = new Date();

		final JSONObject jStatus = new JSONObject();
		final JSONObject jSurvey = new JSONObject();
		final JSONObject jLocation = new JSONObject();

		if(listPrices.size() < 1) {
			AppMsg.makeText(this, R.string.eNoSurvey ,AppMsg.STYLE_ALERT).show();
		} else {

			for (int i = 0; i < listPrices.size(); i++) {
				// make daddy proud
				final EditText et = listPrices.get(i);
				final EditText dt = listRealDivisor.get(i);
				Spinner sp = listCommodities.get(i);
				Spinner spKind = (Spinner) v2.findViewById(R.id.spinnerKind);
				Spinner spMarket = (Spinner) v2.findViewById(R.id.spinnerMarket);
				Spinner spVendor = (Spinner) v2.findViewById(R.id.spinnerVendor);	
				final String com = sp.getSelectedItem().toString();	
				final int comcode = sp.getSelectedItemPosition();
				TextView tv = listCurrency.get(i);
				final String curr = tv.getText().toString();
				tv = listMeasurement.get(i);
				final String meas = tv.getText().toString();
				final String var;
				final int varcode;
				if (varOpt) {
					sp = listVarieties.get(i);
					var = sp.getSelectedItem().toString();
					varcode = sp.getSelectedItemPosition();
				} else {
					var = com;
					varcode = comcode;
				}

				final SimpleDateFormat fulldateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");				
				final String ss = et.getText().toString();
				final String kk = dt.getText().toString();
				if ( ss == null || ss.length() == 0 || kk == null || kk.length() == 0 ) {
					AppMsg.makeText(this, R.string.eNoPrice ,AppMsg.STYLE_ALERT).show();
				} else {
					int p = Integer.parseInt(et.getText().toString());
					final int d = Integer.parseInt(dt.getText().toString());	//listDivisor[i];
					p = p / d;
					final JSONArray jarr = new JSONArray();
					final JSONObject json = new JSONObject();						
					final JSONObject jcont = new JSONObject();
					
					try {
						json.put("commoditycode", ""+comcode);
						json.put("varietycode", ""+varcode);
						json.put("citycode", ""+vendcode);
						json.put("price", ""+p);
						json.put("lat",""+geolat);
						json.put("lon",""+geolon);
						json.put("date", dateFormat.format(date));
						json.put("fulldate", fulldateFormat.format(date));
						json.put("quantity", ""+d);		
						json.put("munitcode", ""+curStd);
						json.put("marketcode", ""+listMarket.get(spMarket.getSelectedItemPosition()));
						json.put("vendorcode", ""+listVendor.get(spVendor.getSelectedItemPosition()));
						json.put("vendorname", spVendor.getSelectedItem().toString());
						json.put("saletypecode", ""+spKind.getSelectedItemPosition());
						json.put("note", notes.getText().toString());
						json.put("currencycode", ""+0);
						json.put("userid", "Test");
						jcont.put("table", "data");
						jarr.put(json);
						jcont.put("data", jarr);
						//json.put("untouchedprice", kk);
						
						jSurvey.put("sent", hasSent);
						jSurvey.put("fromTemplate", true);
						jSurvey.put("surveydata",jcont);

					} catch (JSONException e) { e.printStackTrace(); }
				}
			}
			if (!isEmpty) {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
	
				alert.setTitle("Save as Template");
				alert.setMessage("Name of Template");
	
				// Set an EditText view to get user input 
				final EditText input = new EditText(this);
				alert.setView(input);
	
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString();						
						String fname = "tem_"+value+"_"+df.format(date)+".json";
						final Boolean b = SystemUtils.writeFile(getApplicationContext(), fname, jSurvey.toString(), true);
						if (b) changeMessage(getApplicationContext(),true,2);
						if (b) hasWritten = fname;
						if (hasSent) SystemUtils.deleteDir(new File(Environment.getExternalStorageDirectory()+"/crowdprices/"+currentFile));
					}
				  
				  }
				);
	
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  @Override
				public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
				  }
				});
	
				alert.show();			
			}
		}
		
	}

	private void saveSurvey() throws JSONException {
		// TODO Auto-generated method stub
		final Spinner nationSpinner = (Spinner) v2.findViewById(R.id.spinnerNation);
		final Spinner citySpinner = (Spinner) v2.findViewById(R.id.spinnerCity);
		final Spinner marketSpinner = (Spinner) v2.findViewById(R.id.spinnerMarket);
		final Spinner vendorSpinner = (Spinner) v2.findViewById(R.id.spinnerVendor);
		final Spinner kindSpinner = (Spinner) v2.findViewById(R.id.spinnerKind);
		final EditText notes = (EditText) v2.findViewById(R.id.etNote);
		boolean isEmpty = false;
		


		citycode = citySpinner.getSelectedItem().toString();
		final String vendorCode = vendorSpinner.getSelectedItem().toString();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		final SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
		final Date date = new Date();
		final int vendcode = citySpinner.getSelectedItemPosition();
		Double geolat = listGPSLat.get(vendcode); 
		Double geolon = listGPSLon.get(vendcode);

		final JSONObject jStatus = new JSONObject();
		final JSONObject jSurvey = new JSONObject();
		final JSONObject jLocation = new JSONObject();

		if(listPrices.size() < 1) {
			AppMsg.makeText(this, R.string.eNoSurvey ,AppMsg.STYLE_ALERT).show();
		} else {

			for (int i = 0; i < listPrices.size(); i++) {
				// make daddy proud
				final EditText et = listPrices.get(i);
				final EditText dt = listRealDivisor.get(i);
				Spinner sp = listCommodities.get(i);
				Spinner spKind = (Spinner) v2.findViewById(R.id.spinnerKind);
				Spinner spMarket = (Spinner) v2.findViewById(R.id.spinnerMarket);
				Spinner spVendor = (Spinner) v2.findViewById(R.id.spinnerVendor);	
				final String com = sp.getSelectedItem().toString();	
				final int comcode = sp.getSelectedItemPosition();
				TextView tv = listCurrency.get(i);
				final String curr = tv.getText().toString();
				tv = listMeasurement.get(i);
				final String meas = tv.getText().toString();
				final String var;
				final int varcode;
				if (varOpt) {
					sp = listVarieties.get(i);
					var = sp.getSelectedItem().toString();
					varcode = sp.getSelectedItemPosition();
				} else {
					var = com;
					varcode = comcode;
				}

				final SimpleDateFormat fulldateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");				
				final String ss = et.getText().toString();
				final String kk = dt.getText().toString();
				if ( ss == null || ss.length() == 0 || kk == null || kk.length() == 0 ) {
					AppMsg.makeText(this, R.string.eNoPrice ,AppMsg.STYLE_ALERT).show();
				} else {
					int p = Integer.parseInt(et.getText().toString());
					final int d = Integer.parseInt(dt.getText().toString());	//listDivisor[i];
					p = p / d;
					final JSONArray jarr = new JSONArray();
					final JSONObject json = new JSONObject();						
					final JSONObject jcont = new JSONObject();
					
					try {
						json.put("commoditycode", ""+comcode);
						json.put("varietycode", ""+varcode);
						json.put("citycode",""+listCity.get(citySpinner.getSelectedItemPosition()));
						//json.put("citycode", ""+citySpinner.getSelectedItem().toString());
						json.put("price", ""+p);
						json.put("lat",""+geolat);
						json.put("lon",""+geolon);
						json.put("date", dateFormat.format(date));
						json.put("fulldate", fulldateFormat.format(date));
						json.put("quantity", ""+d);		
						json.put("munitcode", ""+curStd);
						json.put("marketcode", ""+listMarket.get(spMarket.getSelectedItemPosition()));
						json.put("vendorcode", ""+listVendor.get(spVendor.getSelectedItemPosition()));
						json.put("vendorname", spVendor.getSelectedItem().toString());
						json.put("saletypecode", ""+spKind.getSelectedItemPosition());
						json.put("note", notes.getText().toString());
						json.put("currencycode", ""+0);
						json.put("userid", "Test");
						jcont.put("table", "data");
						jarr.put(json);
						jcont.put("data", jarr);
						//json.put("untouchedprice", kk);

						jSurvey.put("sent", hasSent);
						jSurvey.put("fromTemplate",false);
						jSurvey.put("surveydata",jcont);

					} catch (JSONException e) { e.printStackTrace(); }
				}
			}
			if (!isEmpty) {
				String fname = "sur_"+vendorCode+"_"+df.format(date)+".json";
				final Boolean b = SystemUtils.writeFile(getApplicationContext(), fname, jSurvey.toString(), true);
				if (b) changeMessage(getApplicationContext(),true,0);
				if (b) hasWritten = fname;
				if (hasSent) SystemUtils.deleteDir(new File(Environment.getExternalStorageDirectory()+"/crowdprices/"+currentFile));
			}

		}

	}

	private void submitSurvey() {
		// TODO Auto-generated method stub
		final Spinner citySpinner = (Spinner) v2.findViewById(R.id.spinnerCity);
		citycode = citySpinner.getSelectedItem().toString();
		final int vendcode = citySpinner.getSelectedItemPosition();
		Double geolat = listGPSLat.get(vendcode); 
		Double geolon = listGPSLon.get(vendcode);

		if(listPrices.size() < 1) {
			AppMsg.makeText(this, R.string.eNoSurvey ,AppMsg.STYLE_ALERT).show();
		} else {
			for (int i = 0; i < listPrices.size(); i++) {
				// make daddy proud
				final EditText et = listPrices.get(i);
				final EditText dt = listRealDivisor.get(i);
				Spinner sp = listCommodities.get(i);
				final String com = sp.getSelectedItem().toString();	
				final int comcode = sp.getSelectedItemPosition();
				TextView tv = listCurrency.get(i);
				final String curr = tv.getText().toString();
				tv = listMeasurement.get(i);
				final String meas = tv.getText().toString();
				final String var;
				final int varcode;
				if (varOpt) {
					sp = listVarieties.get(i);
					var = sp.getSelectedItem().toString();
					varcode = sp.getSelectedItemPosition();
				} else {
					var = com;
					varcode = comcode;
				}

				final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy,MM,dd");
				final SimpleDateFormat fulldateFormat = new SimpleDateFormat("yyyy,MM,dd,kk,mm,ss");
				final SimpleDateFormat timezoneFormat = new SimpleDateFormat("z");
				final Date date = new Date();
				//Toast.makeText(this,"et:"+et.getText().toString() ,Toast.LENGTH_SHORT).show();
				//Toast.makeText(this,"dt:"+dt.getText().toString() ,Toast.LENGTH_SHORT).show();
				final String ss = et.getText().toString();
				final String kk = dt.getText().toString();
				if ( ss == null || ss.length() == 0 || kk == null || kk.length() == 0 ) {
					AppMsg.makeText(this, R.string.eNoPrice ,AppMsg.STYLE_ALERT).show();
				} else {
					int p = Integer.parseInt(et.getText().toString());
					final int d = Integer.parseInt(dt.getText().toString());	//listDivisor[i];
					p = p / d;
					s = citycode+","+citycode+","+com+","+var+","+dateFormat.format(date)+","+curr+","+meas+","+Integer.toString(p);
					s = s.replace(" ","%20");
					
					final JSONObject json = new JSONObject();
					try {
						//TODO: json.put("user", "");
						json.put("commoditycode", comcode);
						json.put("commodityname", com);
						json.put("varietycode", varcode);
						json.put("varietyname", var);						
						json.put("citycode", vendcode);
						json.put("price", p);
						JSONObject geo = new JSONObject();
						geo.put("type","Point");
						JSONArray geoa = new JSONArray();
						geoa.put(geolat);
						geoa.put(geolon);
						geo.put("coordinates",geoa);						
						json.put("geo",geo);
						json.put("date", dateFormat.format(date));
						json.put("fulldate", fulldateFormat.format(date));
						json.put("timezone", timezoneFormat.format(date));
						json.put("quantity", d);
						json.put("munitsymbol", meas);
						
						json.put("untouchedprice", 324);
						json.put("currencycode", 1);
						json.put("currencysymbol", "KSh");
						json.put("munitcode", 1);
						json.put("nationcode", 1);						
						json.put("marketcode", 1);
						json.put("marketname", "Market of Nairobi");
						json.put("vendorcode", 1);
						json.put("vendorname", "Vendor of Nairobi");
						json.put("notes", "");
						json.put("kind", 0);
					} catch (JSONException e) { e.printStackTrace(); }
					//Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
					/*	citycode,marketcode,commoditycode,varietycode,date,currencycode,measurementunitcode,price
					 *  Nairobi,Nairobi,Dry Maize,Dry Maize,2013-10-10,Kenyan Shilling,kg,99
					 */
					final Thread thread = new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								if (getIntent().hasExtra("FENIX")) { 
									hasSent = true; 
									saveSurvey();
								}
								//String h = "aaaafhhhh";
								//String h = "{\"payload\":" + json.toString() + "}";
								String h = json.toString();
								Log.i("FENIX", "PEOPLE! H IS :"+h);
								
								//String h = "{\"commoditycode\": 1,\"commodityname\": \"Avocado\",\"varietycode\": 1,\"varietyname\": \"Variety of Avocado\",\"quantity\": 90,\"price\": 3.6,\"currencycode\": 1,\"currencysymbol\": \"KSh\",\"untouchedprice\": 324,\"munitcode\": 1,\"munitsymbol\": \"Kg\",\"nationcode\": 1,\"citycode\": 1,\"marketcode\": 1,\"vendorcode\": 1,\"vendorname\": \"Vendor of Nairobi\",\"geo\": {    \"type\": \"Point\",    \"coordinates\": [38.36402,-3.40193    ]},\"date\": \"2014,8,12\",\"notes\": \"\",\"kind\": 0}";//+"}";
								//uploadMongo(getApplicationContext(), s, h);
								//uploadData(getApplicationContext(),s);
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					});

					thread.start();
					SharedPreferences settings = getSharedPreferences("FENIX", 0);
					boolean del = settings.getBoolean("delSurvey", false);
					if (del && hasWritten.length() > 0) {
						Log.i("FENIX", Environment.getExternalStorageDirectory()+"/crowdprices/"+hasWritten);
						SystemUtils.deleteDir(new File(Environment.getExternalStorageDirectory()+"/crowdprices/"+hasWritten));
						AppMsg.makeText(this, "FILE REMOVED ON SUBMIT" ,AppMsg.STYLE_CONFIRM).show();
					}
				}
			}
		}
		//String s = ""+listElement.size();

	}
	
	private void submitPGSQL() {
		// TODO Auto-generated method stub
		final Spinner citySpinner = (Spinner) v2.findViewById(R.id.spinnerCity);
		citycode = citySpinner.getSelectedItem().toString();
		final int vendcode = citySpinner.getSelectedItemPosition();
		Double geolat = listGPSLat.get(vendcode); 
		Double geolon = listGPSLon.get(vendcode);

		if(listPrices.size() < 1) {
			AppMsg.makeText(this, R.string.eNoSurvey ,AppMsg.STYLE_ALERT).show();
		} else {
			for (int i = 0; i < listPrices.size(); i++) {
				// make daddy proud
				final EditText et = listPrices.get(i);
				final EditText dt = listRealDivisor.get(i);
				Spinner sp = listCommodities.get(i);
				Spinner spKind = (Spinner) v2.findViewById(R.id.spinnerKind);
				Spinner spMarket = (Spinner) v2.findViewById(R.id.spinnerMarket);
				Spinner spVendor = (Spinner) v2.findViewById(R.id.spinnerVendor);	
				EditText notes = (EditText) v2.findViewById(R.id.etNote);
				final String com = sp.getSelectedItem().toString();	
				final int comcode = sp.getSelectedItemPosition();
				TextView tv = listCurrency.get(i);
				final String curr = tv.getText().toString();
				tv = listMeasurement.get(i);
				final String meas = tv.getText().toString();
				final String var;
				final int varcode;
				if (varOpt) {
					sp = listVarieties.get(i);
					var = sp.getSelectedItem().toString();
					varcode = sp.getSelectedItemPosition();
				} else {
					var = com;
					varcode = comcode;
				}

				final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				final SimpleDateFormat fulldateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");				
				final Date date = new Date();
				//Toast.makeText(this,"et:"+et.getText().toString() ,Toast.LENGTH_SHORT).show();
				//Toast.makeText(this,"dt:"+dt.getText().toString() ,Toast.LENGTH_SHORT).show();
				final String ss = et.getText().toString();
				final String kk = dt.getText().toString();
				if ( ss == null || ss.length() == 0 || kk == null || kk.length() == 0 ) {
					AppMsg.makeText(this, R.string.eNoPrice ,AppMsg.STYLE_ALERT).show();
				} else {
					int p = Integer.parseInt(et.getText().toString());
					final int d = Integer.parseInt(dt.getText().toString());	//listDivisor[i];
					p = p / d;
					final JSONArray jarr = new JSONArray();
					final JSONObject json = new JSONObject();						
					final JSONObject jcont = new JSONObject();
					
					try {
						json.put("commoditycode", ""+comcode);
						json.put("varietycode", ""+varcode);
						json.put("citycode", ""+vendcode);
						json.put("price", ""+p);
						json.put("lat",""+geolat);
						json.put("lon",""+geolon);
						json.put("date", dateFormat.format(date));
						json.put("fulldate", fulldateFormat.format(date));
						json.put("quantity", ""+d);		
						json.put("munitcode", ""+curStd);
						json.put("marketcode", ""+listMarket.get(spMarket.getSelectedItemPosition()));
						json.put("vendorcode", ""+listVendor.get(spVendor.getSelectedItemPosition()));
						json.put("vendorname", spVendor.getSelectedItem().toString());
						json.put("saletypecode", ""+spKind.getSelectedItemPosition());
						json.put("note", notes.getText().toString());
						json.put("currencycode", ""+0);
						json.put("userid", "Test");
						//jcont.put("table", "data");
						jarr.put(json);
						jcont.put("datas", jarr);
						//json.put("untouchedprice", kk);

					} catch (JSONException e) { e.printStackTrace(); }

					final Thread thread = new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								if (getIntent().hasExtra("FENIX")) { 
									hasSent = true; 
									saveSurvey();
								}
								String h = jcont.toString();
								//String h = "{\"payload\":[" + jcont.toString() + "]}";
								Log.i("FENIX", h);
								uploadPGSQL(getApplicationContext(), s, h);
								//uploadData(getApplicationContext(),s);
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					});

					thread.start();
					SharedPreferences settings = getSharedPreferences("FENIX", 0);
					boolean del = settings.getBoolean("delSurvey", false);
					if (del && hasWritten.length() > 0) {
						Log.i("FENIX", Environment.getExternalStorageDirectory()+"/crowdprices/"+hasWritten);
						SystemUtils.deleteDir(new File(Environment.getExternalStorageDirectory()+"/crowdprices/"+hasWritten));
						AppMsg.makeText(this, "FILE REMOVED ON SUBMIT" ,AppMsg.STYLE_CONFIRM).show();
					}
				}
			}
		}
		//String s = ""+listElement.size();

	}
	
	private void uploadMongo(Context c, String sUrl, String json) {
		final String address = getResources().getString(R.string.MONGO_URL)+"/insert";
		Log.i("FENIX", address);
		SharedPreferences settings = getSharedPreferences("FENIX", 0);
		boolean send = settings.getBoolean("sendServer", true);
		if (!send) {
			AppMsg.makeText(this, R.string.iSendToServerOFF ,AppMsg.STYLE_ALERT).show();
		} else {
			if (!SystemUtils.haveNetworkConnection(getApplicationContext())) {
				AppMsg.makeText(this, R.string.eNoConn ,AppMsg.STYLE_ALERT).show();
			} else {
				try {
					final HttpClient client = new DefaultHttpClient();
					final HttpPut put = new HttpPut(address);					

					StringEntity se = new StringEntity(json);  
					se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
					put.setHeader("Accept", "application/json");
					put.setHeader("Content-type", "application/json");
					put.setEntity(se);
					HttpResponse responseEntity = client.execute(put);
					 
			        //nameValuePairs.add(new BasicNameValuePair("payload", obj.toString()));
			        //put.setEntity(new StringEntity(nameValuePairs.toString(), "UTF-8"));
					//put.setEntity(obj);
			        //put.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        //put.setHeader("Content-type", "application/json; charset=UTF-8");
			        //put.setHeader("Accept", "application/json");
			        //HttpResponse responseEntity = client.execute(put);
					
					if( responseEntity != null ) {
						changeMessage(c,true,1);
					} else {
						changeMessage(c,false,1);
					}
				} catch (final Exception e) {
					String error = "";
					Log.e("ERRORE", "Error Message: " + e);
					if (e.getMessage() == null) {
						error = "Error: exception thrown null.";
					} else {
						error = e.getMessage();
					}
					Log.e("RESPONSE", error);
					//AppMsg.makeText(this, error ,AppMsg.STYLE_ALERT).show();
					android.widget.Toast.makeText(this,error,android.widget.Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void uploadPGSQL(Context c, String sUrl, String json) {
		final String address = getResources().getString(R.string.POSTGRESQL_URL)+"/auto.data";
		Log.i("FENIX", "URL>"+address);
		SharedPreferences settings = getSharedPreferences("FENIX", 0);
		boolean send = settings.getBoolean("sendServer", true);
		if (!send) {
			AppMsg.makeText(this, R.string.iSendToServerOFF ,AppMsg.STYLE_ALERT).show();
		} else {
			if (!SystemUtils.haveNetworkConnection(getApplicationContext())) {
				AppMsg.makeText(this, R.string.eNoConn ,AppMsg.STYLE_ALERT).show();
			} else {
				try {
					final HttpClient client = new DefaultHttpClient();
					final HttpPost post = new HttpPost(address);					
					
					StringEntity se = new StringEntity(json);  
					se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
					post.setHeader("Accept", "application/json");
					post.setHeader("Content-type", "application/json");
					post.setEntity(se);
					HttpResponse responseEntity = client.execute(post);
															
					if( responseEntity != null ) {
						String result = EntityUtils.toString(responseEntity.getEntity());
						Log.i("FENIX",result);
						changeMessage(c,true,1);
					} else {
						changeMessage(c,false,1);
					}
				} catch (final Exception e) {
					String error = "";
					Log.e("ERRORE", "Error Message: " + e);
					if (e.getMessage() == null) {
						error = "Error: exception thrown null.";
					} else {
						error = e.getMessage();
					}
					Log.e("RESPONSE", error);
					//AppMsg.makeText(this, error ,AppMsg.STYLE_ALERT).show();
					android.widget.Toast.makeText(this,error,android.widget.Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	
	private static String convertStreamToString(InputStream is) {

	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append((line + "\n"));
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}

	private void submitSurveyOLD() {
		// TODO Auto-generated method stub
		final Spinner citySpinner = (Spinner) v2.findViewById(R.id.spinnerCity);
		citycode = citySpinner.getSelectedItem().toString();

		if(listPrices.size() < 1) {
			AppMsg.makeText(this, R.string.eNoSurvey ,AppMsg.STYLE_ALERT).show();
		} else {
			for (int i = 0; i < listPrices.size(); i++) {
				// make daddy proud
				final EditText et = listPrices.get(i);
				final EditText dt = listRealDivisor.get(i);
				Spinner sp = listCommodities.get(i);
				final String com = sp.getSelectedItem().toString();							
				TextView tv = listCurrency.get(i);
				final String curr = tv.getText().toString();
				tv = listMeasurement.get(i);
				final String meas = tv.getText().toString();
				final String var;
				if (varOpt) {
					sp = listVarieties.get(i);
					var = sp.getSelectedItem().toString();
				} else {
					var = com;
				}

				final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				final Date date = new Date();
				//Toast.makeText(this,"et:"+et.getText().toString() ,Toast.LENGTH_SHORT).show();
				//Toast.makeText(this,"dt:"+dt.getText().toString() ,Toast.LENGTH_SHORT).show();
				final String ss = et.getText().toString();
				final String kk = dt.getText().toString();
				if ( ss == null || ss.length() == 0 || kk == null || kk.length() == 0 ) {
					AppMsg.makeText(this, R.string.eNoPrice ,AppMsg.STYLE_ALERT).show();
				} else {
					int p = Integer.parseInt(et.getText().toString());
					final int d = Integer.parseInt(dt.getText().toString());	//listDivisor[i];
					p = p / d;
					s = citycode+","+citycode+","+com+","+var+","+dateFormat.format(date)+","+curr+","+meas+","+Integer.toString(p);
					s = s.replace(" ","%20");
					//Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
					/*	citycode,marketcode,commoditycode,varietycode,date,currencycode,measurementunitcode,price
					 *  Nairobi,Nairobi,Dry Maize,Dry Maize,2013-10-10,Kenyan Shilling,kg,99
					 */
					final Thread thread = new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								if (getIntent().hasExtra("FENIX")) { 
									hasSent = true; 
									saveSurvey();
								}
								uploadData(getApplicationContext(),s);
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					});

					thread.start();
					SharedPreferences settings = getSharedPreferences("FENIX", 0);
					boolean del = settings.getBoolean("delSurvey", false);
					if (del && hasWritten.length() > 0) {
						Log.i("FENIX", Environment.getExternalStorageDirectory()+"/crowdprices/"+hasWritten);
						SystemUtils.deleteDir(new File(Environment.getExternalStorageDirectory()+"/crowdprices/"+hasWritten));
						AppMsg.makeText(this, "FILE REMOVED ON SUBMIT" ,AppMsg.STYLE_CONFIRM).show();
					}
				}
			}
		}
		//String s = ""+listElement.size();

	}

	
	private void uploadData(Context c, String s) {
		final String address =  getResources().getString(R.string.UPLOAD_URL) + s;
		Log.i("FENIX", address);
		// Check if SendToServer is true
		SharedPreferences settings = getSharedPreferences("FENIX", 0);
		boolean send = settings.getBoolean("sendServer", true);
		if (!send) {
			AppMsg.makeText(this, R.string.iSendToServerOFF ,AppMsg.STYLE_ALERT).show();
		} else {
			// Check internet Connection
			if (!SystemUtils.haveNetworkConnection(getApplicationContext())) {
				AppMsg.makeText(this, R.string.eNoConn ,AppMsg.STYLE_ALERT).show();
			} else {

				try {
					
					final HttpClient client = new DefaultHttpClient();
					final HttpPut put= new HttpPut(address);
					final HttpResponse response = client.execute(put);
					final HttpEntity responseEntity = response.getEntity();
					if( responseEntity != null ) {
						changeMessage(c,true,1);
					} else {
						changeMessage(c,false,1);
					}
				} catch (final Exception e) {
					String error = "";
					Log.e("ERRORE", "Error Message: " + e);
					if (e.getMessage() == null) {
						error = "Error: exception thrown null.";
					} else {
						error = e.getMessage();
					}
					Log.e("RESPONSE", error);
					AppMsg.makeText(this, error ,AppMsg.STYLE_ALERT).show();
					//Toast.makeText(this,error,Toast.LENGTH_LONG).show();
				}
			}
			
		}

	}


}
