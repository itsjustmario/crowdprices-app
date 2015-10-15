package org.faostat.crowdprices;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.faostat.crowdprices.rc.R;
import org.faostat.crowdprices.util.SystemUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockActivity;



public class CrowdMaps extends SherlockActivity {
	//com.esri.android.map.MapView mMapView;

	public class MapMakers {
		protected ItemizedIconOverlay<OverlayItem> mOverlay;
		protected Context mContext;
		protected Drawable mMarker;
		protected ArrayList<OverlayItem> mItems;
		protected Boolean mTap;

		public MapMakers(ArrayList<OverlayItem> items, Drawable marker, Context context, Boolean taphelper) {
			mTap = taphelper;
			mContext = context;
			mItems = items;
			final ResourceProxy resourceProxy = new DefaultResourceProxyImpl(mContext);
			mMarker = marker;

			mOverlay = new ItemizedIconOverlay<OverlayItem>(
					items, mMarker,
					new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
						@Override public boolean onItemLongPress(final int index, final OverlayItem item) {
							return true;
						}

						@Override public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
							return onSingleTapUpHelper(index, item);
						}
					}, resourceProxy);

		}

		public void addItem(OverlayItem item){
			mOverlay.addItem(item);
		}

		public ItemizedIconOverlay<OverlayItem> getOverlay(){
			return mOverlay;
		}
		
		public boolean onSingleTapUpHelper(final int i, final OverlayItem item) {
			final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which){
					case DialogInterface.BUTTON_POSITIVE:
						isPointSet = true;
						lastPoint = new GeoPoint(item.getPoint().getLatitude(), item.getPoint().getLongitude());
						myMapController.setCenter(lastPoint);
						CrowdAddData.myPager.setCurrentItem(0);
						final Spinner spCity = (Spinner) CrowdAddData.v2.findViewById(R.id.spinnerCity);
						spCity.setSelection(i);
						final Spinner spMarket = (Spinner) CrowdAddData.v2.findViewById(R.id.spinnerMarket);
						spMarket.setSelection(0);
						finish();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						break;
					}
				}
			};
			if (mTap) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setMessage(getResources().getString(R.string.qstMap1)+" "+item.getTitle()+" "+getResources().getString(R.string.qstMap2))
				.setPositiveButton(android.R.string.yes, dialogClickListener)
				.setNegativeButton(android.R.string.no, dialogClickListener).show();
			}
			return true;
		}
	}
	private org.osmdroid.views.MapView myOpenMapView;
	private MapController myMapController;
	private Boolean isPointSet = false;
	private GeoPoint lastPoint;

	private final ArrayList<OverlayItem> itemsVendor = new ArrayList<OverlayItem>();
	private final ArrayList<OverlayItem> itemsCity = new ArrayList<OverlayItem>();
	//private GraphicsLayer gLayer = new GraphicsLayer();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		OverlayItem item;
		SystemUtils.lastLat = Double.valueOf(getResources().getString(R.string.STARTING_LAT));
		SystemUtils.lastLon = Double.valueOf(getResources().getString(R.string.STARTING_LON));

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		/*
        // Setup ERSI Maps
        mMapView = (com.esri.android.map.MapView)findViewById(R.id.map);
		mMapView.addLayer(new ArcGISTiledMapServiceLayer("" +getResources().getString(R.string.ESRI_WORLD_STREET_MAP)));
		PictureMarkerSymbol pms = new PictureMarkerSymbol(getResources().getDrawable(R.drawable.location_small));
		SimpleRenderer simRenderer = new SimpleRenderer(pms);
		gLayer.setRenderer(simRenderer);
		 */
		// Setup OpenStreet Maps
		myOpenMapView = (MapView)findViewById(R.id.openmapview);
		myOpenMapView.setBuiltInZoomControls(true);
		myOpenMapView.setMultiTouchControls(true);
		/*
        // Add Switcher
        tileSwitch = (Button) findViewById(R.id.btnChangeTile);
        tileSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isESRIMaps) {
					isESRIMaps = false;
					mMapView.setVisibility(View.GONE);
					myOpenMapView.setVisibility(View.VISIBLE);
				} else {
					isESRIMaps = true;
					mMapView.setVisibility(View.VISIBLE);
					myOpenMapView.setVisibility(View.GONE);
				}
			}
		});
		 */
		GeoPoint startPoint = new GeoPoint(-1.2962, 36.8314);
		lastPoint = new GeoPoint(SystemUtils.lastLat, SystemUtils.lastLon);

		//myOpenMapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
		if (SystemUtils.gps_enabled) {
			if (isPointSet) {
				myMapController.setCenter(lastPoint);
			} else {
				SystemUtils.checkUseOfLocation(getApplicationContext(), this, myOpenMapView);
				//AppMsg.makeText(this, "Pointing to Last Know Position..." ,AppMsg.STYLE_CONFIRM).show();
				startPoint = new GeoPoint(SystemUtils.lastLat, SystemUtils.lastLon);
				isPointSet = true;
			}
		} else {
			//AppMsg.makeText(this, "No GPS Avalable. Pointing to Nations' Capital." ,AppMsg.STYLE_ALERT).show();
		}
		final Drawable markoVendor = getResources().getDrawable(R.drawable.location_small_lv1);
		markoVendor.setBounds(0, 0, 9, 9);
		String[] vendorName = new String[0];
		Double[] vendorLon = new Double[0];
		Double[] vendorLat = new Double[0];

		try {
			StringBuffer sb = SystemUtils.readFile(getApplicationContext(),"vendor.json");
			String jsontext = new String(sb);
			JSONArray ja = new JSONArray(jsontext);
			// Maker(s)
			vendorName = new String[ja.length()];
			vendorLon = new Double[ja.length()];
			vendorLat = new Double[ja.length()];
			for (int j = 0; j < ja.length(); j++) if(ja.getJSONObject(j).getBoolean("shown")) {
				vendorLat[j] = ja.getJSONObject(j).getJSONObject("geo").getJSONArray("coordinates").getDouble(0);
				vendorLon[j] = ja.getJSONObject(j).getJSONObject("geo").getJSONArray("coordinates").getDouble(1);			
				vendorName[j] = ja.getJSONObject(j).getString("name");			
			}
		} catch (JSONException | FileNotFoundException e) { e.printStackTrace(); }

		final Drawable markoCity = getResources().getDrawable(R.drawable.location_small_lv3);
		markoCity.setBounds(0, 0, 9, 9);
		String[] cityName = new String[0];
		Double[] cityLon = new Double[0];
		Double[] cityLat = new Double[0];
		
		
		try {
			StringBuffer sb = SystemUtils.readFile(getApplicationContext(),"city.json");
			String jsontext = new String(sb);
			JSONArray ja = new JSONArray(jsontext);
			// Maker(s)
			cityName = new String[ja.length()];
			cityLon = new Double[ja.length()];
			cityLat = new Double[ja.length()];
			for (int j = 0; j < ja.length(); j++) if(ja.getJSONObject(j).getBoolean("shown")) {
				cityLat[j] = ja.getJSONObject(j).getJSONObject("geo").getJSONArray("coordinates").getDouble(1);
				cityLon[j] = ja.getJSONObject(j).getJSONObject("geo").getJSONArray("coordinates").getDouble(0);			
				cityName[j] = ja.getJSONObject(j).getString("name");			
			}
		} catch (JSONException | FileNotFoundException e) { e.printStackTrace(); }

		
		/*		
		
		//InputStream isCity = getApplication().getResources().openRawResource(R.raw.cities);
		try {
			//StringBuffer sb = SystemUtils.readFile(getApplicationContext(),"cities.json");
			final StringBuffer sb = SystemUtils.readFile(getApplicationContext(),"vendor.json");
			final String jsontext = new String(sb);
			JSONArray ja;
			JSONObject entries;
			entries = new JSONObject(jsontext);
			//ja = entries.getJSONArray("Cities");
			ja = entries.getJSONArray("Vendors");
			aCit = new String[ja.length()];
			aLon = new String[ja.length()];
			aLat = new String[ja.length()];
			for (int j = 0; j < ja.length(); j++) {
				//aCit[j] = ja.getJSONObject(j).getString("cName");
				//aLon[j] = ja.getJSONObject(j).getString("cLon");
				//aLat[j] = ja.getJSONObject(j).getString("cLat");
				aCit[j] = ja.getJSONObject(j).getString("vName");
				aLon[j] = ja.getJSONObject(j).getString("vLon");
				aLat[j] = ja.getJSONObject(j).getString("vLat");
			}
		}
		catch (final JSONException e) { e.printStackTrace(); }
		catch (final FileNotFoundException e) { e.printStackTrace(); }

*/

		//Gson gson = new Gson();
		//String[] aLat = getResources().getStringArray(R.array.citiesLat);
		//String[] aLon = getResources().getStringArray(R.array.citiesLon);
		//String[] aCit = getResources().getStringArray(R.array.cities);


		/* REAL ONE
        String[] aLat = SystemUtils.fromArrayStringtoStringArray(SystemUtils.fromJSONtoArrayString(isCity, "Cities", "cLat"));
        String[] aLon = SystemUtils.fromArrayStringtoStringArray(SystemUtils.fromJSONtoArrayString(isCity, "Cities", "cLon"));
        String[] aCit = SystemUtils.fromArrayStringtoStringArray(SystemUtils.fromJSONtoArrayString(isCity, "Cities", "cName"));
		 */		
		
		for (int i=0; i < vendorLat.length ; i++) {
			item = new OverlayItem(vendorName[i], vendorName[i], new GeoPoint(vendorLat[i], vendorLon[i]));
			itemsVendor.add(item);
			//Point pnt = new Point(-1.2056463334580729E7, 4031300.7555036694);
			/*
        	double l1[] = {Double.parseDouble(aLat[i])};
        	double l2[] = {Double.parseDouble(aLon[i])};
        	double[][] ll = {l1,l2};
        	Point[] pnt = Latlon.createPoints(ll);
    		//Point pnt = new Point(Double.parseDouble(aLon[i]), Double.parseDouble(aLat[i]));
    		Log.i("TAG", ""+pnt[0]);
    		graphic[i] = new Graphic(pnt[0], pms);
			 */
		}
		for (int i=0; i < cityLat.length ; i++) {
			item = new OverlayItem(cityName[i], cityName[i], new GeoPoint(cityLat[i], cityLon[i]));
			itemsCity.add(item);
		}
		/*
        SimpleMarkerSymbol sms = new SimpleMarkerSymbol(Color.RED, 5, STYLE.CIRCLE);
        SimpleMarkerSymbol sms2 = new SimpleMarkerSymbol(Color.BLUE, 5, STYLE.CIRCLE);
        // create a point geometry that defines the graphic
        Point pnt = new Point(36.8314, -1.2962);
        Point pnt2 = new Point(34.83014, 0.36186);
     	// create the graphic using the symbol and point geometry
     	Graphic graphic = new Graphic(pnt, sms);
     	gLayer.addGraphic(graphic);
     	//graphic = new Graphic(pnt2, sms2);
     	// add the graphic to a graphics layer
     	//gLayer.addGraphic(graphic);
        mMapView.addLayer(gLayer);
        mMapView.enableWrapAround(true);
        mMapView.centerAt(36.8314, -1.2962, true);
        mMapView.setResolution(6500);
        mMapView.setScale(577790.554289);
		 */
		myMapController = (MapController) myOpenMapView.getController();
		myMapController.setZoom(9);
		myMapController.setCenter(startPoint);
		//myScaleBarOverlay = new ScaleBarOverlay(this);
		//myOpenMapView.getOverlays().add(myScaleBarOverlay);
		//myOpenMapView.getOverlays().add(0, items);

		MapMakers myItemizedIconOverlay = new MapMakers(itemsVendor, markoVendor, this, true);
		myOpenMapView.getOverlays().add(myItemizedIconOverlay.getOverlay());
		
		myItemizedIconOverlay = new MapMakers(itemsCity, markoCity, this, true);
		myOpenMapView.getOverlays().add(myItemizedIconOverlay.getOverlay());

	}

	@Override
	protected void onPause() {
		super.onPause();
		//mMapView.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//mMapView.unpause();
	}


}