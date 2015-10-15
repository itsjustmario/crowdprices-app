package org.faostat.crowdprices;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.faostat.crowdprices.CrowdMaps.MapMakers;
import org.faostat.crowdprices.network.CrowdInAppUpdate;
import org.faostat.crowdprices.network.CrowdInAppUpdate.readServerVersion;
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
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.devspark.appmsg.AppMsg;

@SuppressLint("NewApi")
public class CrowdAddCity extends SherlockActivity {
	private CrowdMultiMenus cmm;
	private org.osmdroid.views.MapView myOpenMapView;
	private int size;
	private JSONArray entries;
	private MapMakers myItemizedIconOverlay;
	ArrayList<String> spinnerArray = new ArrayList<String>();
	private ViewGroup hiddenPanel;
	private ViewGroup root;
	private ViewGroup mainScreen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cmm = new CrowdMultiMenus(this, R.layout.activity_addcity, false,0);
		
		mainScreen = (ViewGroup)findViewById(R.id.svContainer);
		root = (ViewGroup)findViewById(R.id.rlRoot);
		hiddenPanel = (ViewGroup)getLayoutInflater().inflate(R.layout.map_fullscreen, root, false);
		hiddenPanel.setVisibility(View.INVISIBLE);
		root.addView(hiddenPanel);

		SystemUtils.lastLat = Double.valueOf(getResources().getString(R.string.STARTING_LAT));
		SystemUtils.lastLon = Double.valueOf(getResources().getString(R.string.STARTING_LON));
		
		try {
			final StringBuffer sb = SystemUtils.readFile(getApplicationContext(),"city.json");
			final String jsontext = new String(sb);
			entries = new JSONArray(jsontext);
			size = entries.length();
		}
		catch (final JSONException e) { e.printStackTrace(); }
		catch (final FileNotFoundException e) { e.printStackTrace(); }

		// Setup OpenStreet Maps
		
		myOpenMapView = (MapView)findViewById(R.id.osmMarket);
		myOpenMapView.setBuiltInZoomControls(true);
		myOpenMapView.setMultiTouchControls(true);

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
					final EditText etCit = (EditText) findViewById(R.id.txtCityName);
					final EditText etLat = (EditText) findViewById(R.id.etCLat);
					final EditText etLon = (EditText) findViewById(R.id.etCLon);
					final String j = etCit.getText().toString();
					final JSONObject job = new JSONObject();
					final Spinner spCityType = (Spinner) findViewById(R.id.spCityType);
					if (!etCit.getText().toString().matches("")||
							!etLat.getText().toString().matches("")||
							!etLon.getText().toString().matches("")
							){
							job.put("shown", true);
							job.put("code", size);
							job.put("name", j);		
							JSONObject geo = new JSONObject();
							geo.put("type","Point");
							JSONArray geoa = new JSONArray();
							geoa.put(Double.parseDouble(etLat.getText().toString()));
							geoa.put(Double.parseDouble(etLon.getText().toString()));
							geo.put("coordinates",geoa);						
							job.put("geo",geo);							
							JSONObject id = new JSONObject();
							id.put("$oid", java.util.UUID.randomUUID());						
							job.put("_id", id);
							job.put("type", spCityType.getSelectedItemPosition());							
							entries.put(job);
							isOK = true;
							//Log.i("FENIX", "FINAl: "+job.toString());
						} else { AppMsg.makeText(CrowdAddCity.this, R.string.eDataMissing ,AppMsg.STYLE_ALERT).show(); }
				} catch (final JSONException e) { e.printStackTrace(); }
				
				final Boolean b = SystemUtils.writeFile(getApplicationContext(), "city.json", entries.toString(), true);
				if (b&&isOK) AppMsg.makeText(CrowdAddCity.this, R.string.iCityAdded ,AppMsg.STYLE_INFO).show();
				//Toast.makeText(getApplicationContext(), "New City Added.", Toast.LENGTH_LONG).show();
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
