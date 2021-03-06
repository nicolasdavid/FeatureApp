package com.ecn.urbapp.syncToExt;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ecn.urbapp.R;
import com.ecn.urbapp.db.LocalDataSource;
import com.ecn.urbapp.db.MainActivity;
import com.ecn.urbapp.db.Project;
import com.google.gson.Gson;



public class BackTaskExportAllDB extends AsyncTask<Void, Integer, Integer> {

	//TODO Adddescription for javadoc
	private static final String TAG_ProjectS = "Projects";
	private static final String TAG_ID = "Projects_id";
	private static final String TAG_DESCRIPTION = "Projects_description";
	
	protected Activity mContext;
	private ProgressBar mProgressBar;
	
	

	//TODO Adddescription for javadoc
	public BackTaskExportAllDB(Activity context){
		super();
		this.mContext = context;
		
	}
	

	//TODO Adddescription for javadoc
	protected void onPreExecute(){
		this.mProgressBar = (ProgressBar) mContext.findViewById(R.id.pBAsyncDBToExt);
		this.mProgressBar.setVisibility(View.VISIBLE);
		super.onPreExecute();
		Toast.makeText(mContext,  "D�but de l'exportation", Toast.LENGTH_SHORT).show();
	}

	//TODO Adddescription for javadoc
	protected Integer doInBackground(Void... params) { 
		LocalDataSource tampon = ((MainActivity)this.mContext).getDatasource();
		List<Project> allProjects = new ArrayList<Project>(tampon.getAllProjects());
		//String name = MySQLiteHelper.getTableProject();
		Gson gson = new Gson();
		String dataJson = gson.toJson(allProjects);
		//String jSonComplete = "{\""+name+"\":"+dataJson+"}";
		String jSonComplete = dataJson;
		postData(jSonComplete);
				return null;
				
    }
 

	//TODO Adddescription for javadoc
    public void postData(String param) {
	    HttpClient httpclient = new DefaultHttpClient();
	    // specify the URL you want to post to
	    HttpPost httppost = new HttpPost("http://192.168.34.1/SQLite/");
	    try {
		    // create a list to store HTTP variables and their values
		    List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		    // add an HTTP variable and value pair
		    nameValuePairs.add(new BasicNameValuePair("myHttpData", param));
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    // send the variable and value, in other words post, to the URL
		    httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } ;
    }

	//TODO Adddescription for javadoc
    //mise en place d'une progress bar : cf tuto : http://www.tutos-android.com/asynctask-android-traitement-asynchrone-background
    @Override
    protected void onProgressUpdate(Integer... values){
		this.mProgressBar.setProgress(values[0]);
		super.onProgressUpdate(values);
    }

	//TODO Adddescription for javadoc
    @Override
    protected void onPostExecute(Integer result) {
    	//this.mProgressBar.setVisibility(View.GONE);
    	
        Toast.makeText(mContext, "Le traitement asynchrone est termin�", Toast.LENGTH_SHORT).show();
        super.onPostExecute(result);
    }
    

}
