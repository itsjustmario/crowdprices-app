package org.faostat.crowdprices;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.faostat.crowdprices.CrowdMaps.MapMakers;
import org.faostat.crowdprices.rc.R;
import org.faostat.crowdprices.ui.CrowdMultiMenus;
import org.faostat.crowdprices.util.SystemUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.devspark.appmsg.AppMsg;

@SuppressLint("NewApi")
public class CrowdAddVendor extends SherlockActivity {

	private CrowdMultiMenus cmm;
	private org.osmdroid.views.MapView myOpenMapView;
	//private GraphicsLayer gLayer = new GraphicsLayer();
	private JSONArray ja;
	private JSONArray entries;
	private int size;
	ArrayList<String> arrayCityName = new ArrayList<String>();
	ArrayList<String> arrayCityCode = new ArrayList<String>();
	ArrayList<String> arrayMarkName = new ArrayList<String>();
	ArrayList<String> arrayMarkCode = new ArrayList<String>();
	private ViewGroup hiddenPanel;
	private ViewGroup root;
	private ViewGroup mainScreen;
	private MapMakers myItemizedIconOverlay;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cmm = new CrowdMultiMenus(this, R.layout.activity_addvendor, false,0);
		SystemUtils.lastLat = Double.valueOf(getResources().getString(R.string.STARTING_LAT));
		SystemUtils.lastLon = Double.valueOf(getResources().getString(R.string.STARTING_LON));

		mainScreen = (ViewGroup)findViewById(R.id.svContainer);
		root = (ViewGroup)findViewById(R.id.rlRoot);
		hiddenPanel = (ViewGroup)getLayoutInflater().inflate(R.layout.map_fullscreen, root, false);
		hiddenPanel.setVisibility(View.INVISIBLE);
		root.addView(hiddenPanel);


		try {
			StringBuffer sb = SystemUtils.readFile(getApplicationContext(),"city.json");
			String jsontext = new String(sb);
			ja = new JSONArray(jsontext);
			for (int j = 0; j < ja.length(); j++) {
				arrayCityName.add(ja.getJSONObject(j).getString("name"));
				arrayCityCode.add(ja.getJSONObject(j).getString("code"));
			}

			sb = SystemUtils.readFile(getApplicationContext(),"market.json");
			jsontext = new String(sb);
			ja = new JSONArray(jsontext);
			for (int j = 0; j < ja.length(); j++) {
				arrayMarkName.add(ja.getJSONObject(j).getString("name"));
				arrayMarkCode.add(ja.getJSONObject(j).getString("code"));
			}
			sb = SystemUtils.readFile(getApplicationContext(),"vendor.json");
			jsontext = new String(sb);
			entries = new JSONArray(jsontext);
			size = entries.length();
			

		}
		catch (final JSONException e) { e.printStackTrace(); }
		catch (final FileNotFoundException e) { e.printStackTrace(); }
		// Setup OpenStreet Maps
		myOpenMapView = (MapView)findViewById(R.id.osmMarket);
		myOpenMapView.setBuiltInZoomControls(true);
		myOpenMapView.setMultiTouchControls(true);

		final Spinner spCities = (Spinner) findViewById(R.id.spinnerCity);
		final ArrayAdapter<String> spinnerCityArrayAdapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, arrayCityName);
		spCities.setAdapter(spinnerCityArrayAdapter);
		final Spinner spMarket = (Spinner) findViewById(R.id.spinnerMarket);
		final ArrayAdapter<String> spinnerMarketArrayAdapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, arrayMarkName);
		spMarket.setAdapter(spinnerMarketArrayAdapter);



		final Button saveit = (Button) findViewById(R.id.btnSave);
		final ImageButton mapMe = (ImageButton) findViewById(R.id.btnMap);
		final Button mapBack = (Button) findViewById(R.id.btnBackMap);
		final TextView lat = (TextView) findViewById(R.id.txtLat);
		final TextView lon = (TextView) findViewById(R.id.txtLon);
		
		View.OnClickListener toggleMap = new View.OnClickListener() {			
			@Override
			public void onClick(View v) {		
					if (hiddenPanel.getVisibility() == View.INVISIBLE) {
						hiddenPanel.setVisibility(View.VISIBLE);
					} else {
						hiddenPanel.setVisibility(View.INVISIBLE);
					}	      
				}
			};
		
		mapMe.setOnClickListener(toggleMap);
		mapBack.setOnClickListener(toggleMap);

		saveit.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Boolean isOK = false;
				try {
					final EditText eName = (EditText) findViewById(R.id.txtVendorName);
					final EditText eLat = (EditText) findViewById(R.id.etCLat);
					final EditText eLon = (EditText) findViewById(R.id.etCLon);
					final String vName = eName.getText().toString();
					final String vLat = eLat.getText().toString();
					final String vLon = eLon.getText().toString();
					new JSONObject();
					final JSONObject job = new JSONObject();
					final Spinner spCities = (Spinner) findViewById(R.id.spinnerCity);
					final Spinner mkCities = (Spinner) findViewById(R.id.spinnerMarket);
					final Spinner spSize = (Spinner) findViewById(R.id.spinnerSize);

					if (!vName.matches("")||!vLat.matches("")||!vLon.matches("")) {
						
						JSONObject geo = new JSONObject();
						geo.put("type","Point");
						JSONArray geoa = new JSONArray();
						geoa.put(Double.parseDouble(vLat));
						geoa.put(Double.parseDouble(vLon));
						geo.put("coordinates",geoa);						
						job.put("geo",geo);							
						JSONObject id = new JSONObject();
						id.put("$oid", java.util.UUID.randomUUID());						
						job.put("_id", id);
						job.put("name", vName);
						job.put("shown", true);
						job.put("type", spSize.getSelectedItemPosition());
								
						job.put("code", size);
						job.put("marketcode", arrayMarkCode.get(mkCities.getSelectedItemPosition()));
						job.put("citycode", arrayCityCode.get(spCities.getSelectedItemPosition()));
						entries.put(job);
						isOK = true;
					} else { AppMsg.makeText(CrowdAddVendor.this, R.string.eDataMissing ,AppMsg.STYLE_ALERT).show(); }
				} catch (final JSONException e) { e.printStackTrace(); }
				final Boolean b = SystemUtils.writeFile(getApplicationContext(), "vendor.json", entries.toString(), true);
				if (b&&isOK) AppMsg.makeText(CrowdAddVendor.this, R.string.iVendorAdded ,AppMsg.STYLE_INFO).show();
				//Toast.makeText(getApplicationContext(), "New Vendor Added.", Toast.LENGTH_LONG).show();

			}
		});

		// Maps
		Boolean isPointSet = false;
		GeoPoint lastPoint;
		
		GeoPoint startPoint = new GeoPoint(-1.2962, 36.8314);		
		MapController myMapController = (MapController) myOpenMapView.getController();
		
		final MapEventsReceiver mReceive = new MapEventsReceiver() {

			@Override
			public boolean longPressHelper(IGeoPoint arg0) {
				return false;
			}

			@Override
			public boolean singleTapUpHelper(IGeoPoint arg0) {
				final EditText eLat = (EditText) findViewById(R.id.etCLat);
				final EditText eLon = (EditText) findViewById(R.id.etCLon);
				String txtLat = String.valueOf(arg0.getLatitude());
				String txtLon = String.valueOf(arg0.getLongitude());
				GeoPoint curPoint = new GeoPoint(arg0.getLatitude(), arg0.getLongitude());		

				final Drawable markoVendor = getResources().getDrawable(R.drawable.location_small_lv1);
				markoVendor.setBounds(0, 0, 9, 9);
				final ArrayList<OverlayItem> itemsVendor = new ArrayList<OverlayItem>();
				
				OverlayItem item = new OverlayItem("Point", "Point", curPoint);				
			
				if (myOpenMapView.getOverlays().size() > 1) myOpenMapView.getOverlays().remove(1);
				itemsVendor.clear();
				itemsVendor.add(item);
				
				CrowdMaps origin = new CrowdMaps();
				myItemizedIconOverlay = origin.new MapMakers(itemsVendor, markoVendor, getApplicationContext(), false);
				
				myOpenMapView.getOverlays().add(myItemizedIconOverlay.getOverlay());				
				myOpenMapView.invalidate();

				
				if ((txtLat != null && txtLat.length() > 9) || (txtLon != null && txtLon.length() > 9)) {
					String sLat = txtLat.substring(0,9);
					String sLon = txtLon.substring(0,9);
					
					eLat.setText(sLat);
					lat.setText("LAT: "+sLat);					
					eLon.setText(sLon);
					lon.setText("LON: "+sLon);
				} else {
					eLat.setText(txtLat);
					lat.setText("LAT: "+txtLat);
					eLon.setText(txtLon);					
					lon.setText("LON: "+txtLon);					
				}
				
				return false;
			}

		};
		
		
		if (SystemUtils.gps_enabled) {			
			if (isPointSet) {
				lastPoint = new GeoPoint(SystemUtils.lastLat, SystemUtils.lastLon);
				myMapController.setCenter(lastPoint);
			} else {
			
				SystemUtils.checkUseOfLocation(getApplicationContext(), this, myOpenMapView);
				startPoint = new GeoPoint(SystemUtils.lastLat, SystemUtils.lastLon);
				isPointSet = true;
			}
		} 
		

		myMapController.setZoom(9);
		myMapController.setCenter(startPoint);		
		
		final MapEventsOverlay OverlayEventos = new MapEventsOverlay(getBaseContext(), mReceive);		
		myOpenMapView.getOverlays().add(OverlayEventos);
		myOpenMapView.invalidate();

	}

}
