package org.faostat.crowdprices;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.faostat.crowdprices.rc.R;
import org.faostat.crowdprices.ui.CrowdMultiMenus;
import org.faostat.crowdprices.util.SystemUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;

public class CrowdArchive extends SherlockActivity  {
	CrowdMultiMenus cmm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cmm = new CrowdMultiMenus(this, R.layout.activity_archive, true,0);

		ArrayAdapter<String> aa = new ArrayAdapter<String>(CrowdArchive.this, R.layout.view_archiveelement);
		final ListView lv = (ListView) findViewById(R.id.lvArchive);
		final String path = getResources().getString(R.string.sdpath);
		final ArrayList<String> flist = new ArrayList<String>();
		final ArrayList<String> countlist = SystemUtils.GetSurveys(path);
		final ArrayList<String> alist = new ArrayList<String>();// = SystemUtils.GetSurveys(path);
		try {
			for (int i = 0; i < countlist.size(); i++ ) {
				final JSONObject entries = SystemUtils.fromFiletoJSONObj(getApplicationContext(), countlist.get(i));
				Log.i("FENIX",entries.toString());
				final JSONObject jobj = entries.getJSONObject("surveydata").getJSONArray("data").getJSONObject(0);
				final SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
				final SimpleDateFormat fulldateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
				final Date date = fulldateFormat.parse(jobj.getString("fulldate"));
				
				flist.add("sur_"+jobj.getString("vendorname")+"_"+df.format(date)+".json");
				//String out = ja1.getString("vendorCode")+" - "+ja1.getString("date");
				String out = "<b>"+jobj.getString("vendorname")+"</b> - "+jobj.getString("date");
				if (!entries.getBoolean("sent")) out = "[U] "+out;
				alist.add(out);
			}
		}
		catch (final NotFoundException e) { e.printStackTrace(); }
		catch (final JSONException e) { e.printStackTrace(); }
		catch (final ParseException e) { e.printStackTrace(); }

		if (alist.size() > 0) {
			aa = new ArrayAdapter<String>(CrowdArchive.this, R.layout.view_archiveelement, alist)			
		            {
		                @Override
		                public View getView(int position, View convertView, ViewGroup parent) 
		                {
		                    View row;		                    

		                    if (null == convertView) {
		                    	row = getLayoutInflater().inflate(R.layout.view_archiveelement, null);
		                    } else {
		                    	row = convertView;
		                    }

		                    TextView tv = (TextView) row.findViewById(android.R.id.text1);
		                    tv.setText(Html.fromHtml(getItem(position)));
		                    //tv.setText(getItem(position));

		                    return row;
		                }

		            };		            
			lv.setAdapter(aa);
			lv.setOnItemClickListener(new ListView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Log.i("FENIX","FILE: "+flist.get(arg2));
					SystemUtils.fromFiletoJSONObj(getApplicationContext(), flist.get(arg2));
					final Intent mIntent = new Intent(CrowdArchive.this, CrowdAddData.class);
					mIntent.putExtra("FENIX", countlist.get(arg2));
					startActivity(mIntent);
				}

			});
		} else {
			final LinearLayout tar = (LinearLayout) findViewById(R.id.llArchive);
			tar.removeAllViews();
			tar.setBackgroundColor(getResources().getColor(R.color.gray));
			final TextView tv = new TextView(this);
			tv.setPadding(0, 50, 0, 0);
			tv.setText(R.string.eNoSurveyFound);
			tv.setGravity(Gravity.CENTER);
			tar.addView(tv);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}



}
