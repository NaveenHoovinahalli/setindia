package com.teli.audio.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.R.bool;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
public final  class RecordController 
{
	public static String AUDIO_RESULT_DATA="AudioResultData";
	public static String AUDIO_RESP_DATA="AudioRespData";
	public static String AUDIO_RESP_CODE="AudioRespCode";
	public static String AUDIO_URL="AUDIO_SERVER_URL";
	public static final String AUDIO_RESP_RECEIVER = "com.teli.audio.sdk.intent.action.AUDIO_RESP_RECEIVER";
	public static final String AUDIO_EXTRACT_RECEIVER = "com.teli.audio.sdk.intent.action.AUDIO_EXTRACT_RECEIVER";
	public static final int QUERY_DURATION = 4000;
	private static final int DELAY_DURATION=0;
	public static final int QUEUE_SIZE=4;
	public static final int MAX_QUEUE_SIZE=10;
	public static final boolean isDebugEnabled=false;
	
	//private static final String serverURL="http://122.181.186.237:5005/";
    //public static String serverURL="http://phonic.pointart.mobi/audiomanager/processdata/";
	//private static final String serverURL="http://122.181.186.237:5005";
	
	private static final String serverURL="http://122.248.247.3:5005";
	private Handler mObserver;
	private TibRecorder mRecorder = null;
	private boolean mRecording = false;
	private AudioExtractReceiver audioExtractReceiver;
	private boolean isAudioDataProcessing=false;
	private Context mContext;
	private ControllerEventCallback mResponseCallback;
	private RequestQueue mRequestQueue;
	private Request<String> request;

	private int detectedId=-1;
	private int TIME_OUT=8000;
	private static String TIME_OUT_ERROR="{\"id\":\"-1\",\"message\":\"NetworkTimeOut\"}";
	private static String CONNECTION_ERROR="{\"id\":\"-1\",\"message\":\"ConnectionError\"}";
	private static String NETWORK_ERROR="{\"id\":\"-1\",\"message\":\"NetworkError\"}";
	private static int CONNECTION_ERROR_CODE=503;
	private static int TIME_OUT_CODE=522;
    private static long reqtime=0;
    private static long resptime=0;
    
	static
	{
		System.loadLibrary( "tib-afp" );
	}
	public RecordController(Context context) 
	{
		mContext=context;
		detectedId=-1;
		   if (android.os.Build.VERSION.SDK_INT > 9) {
			      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			      StrictMode.setThreadPolicy(policy);
			    }
		init();
	}
	
	public long getTimeDelay()
	{
		return (resptime-reqtime);
	}
	public void setResponseCallback(ControllerEventCallback callback)
	{
		mResponseCallback=callback;
	}
	private void init()
	{
		mObserver=new RecordControlerCallback();
		registerAudioExtractReciver();
		if(MyVolley.getRequestQueue()==null)
			MyVolley.init(mContext);
		mRequestQueue=MyVolley.getRequestQueue();
	}
	
	public void startRecognition()
	{
		if(isRecording())
			return;
        int durationInMs=QUERY_DURATION;
        int delayInMs=DELAY_DURATION;
		int recordingDuration = TibRecorder.RECORDING_DURATION_UNLIMITED;
		if(AudioDataFrequencyQueue.getDataQueue()!=null)
			AudioDataFrequencyQueue.getDataQueue().clearAudioQueue();
		if(AudioRecordQueue.getDataQueue()!=null)
			AudioRecordQueue.getDataQueue().clearAudioQueue();
		mRecording = true;
		mRecorder = new TibRecorder(durationInMs,recordingDuration,delayInMs);
		mRecorder.setObserverCallback(mObserver);
		mRecorder.start();
	}
	
	public void stopRecognition()
	{
		if(!isRecording()) 
			return;
		mRecording = false;
		if(mRecorder != null) 
			mRecorder.cancel();
		mRecorder=null;
		if(request!=null)
		{
			request.cancel();
		}
		request=null;
	}
	
	public void destroy()
	{
		stopRecognition();
		detectedId=-1;
		unregisterAudioExtractReciver();
		if(mRequestQueue!=null)
			mRequestQueue.cancelAll(true);
		mRequestQueue=null;
		mRecorder = null;
		mObserver=null;
		mResponseCallback=null;
		if(AudioDataFrequencyQueue.getDataQueue()!=null)
			AudioDataFrequencyQueue.getDataQueue().destroyAudioQueue();
		if(AudioRecordQueue.getDataQueue()!=null)
			AudioRecordQueue.getDataQueue().destroyAudioQueue();
		
		mContext=null;
	}
	public boolean isRecording() 
	{
		return mRecording;
	}
	private class RecordControlerCallback extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			int arg1=msg.arg1;
        	
            switch( msg.what )
            {
	            case TibRecorder.MSG_ON_RECORDER_INIT:
	            	if(mResponseCallback!=null)
	            	{
	            		mResponseCallback.audioRecorderInited(arg1);
	            	}
	                break;
	
	            case TibRecorder.MSG_ON_RECORDER_INIT_ERROR:
	            	if(mResponseCallback!=null)
	            	{
	            		mResponseCallback.audioRecorderInitError();
	            	}
	                break;
	
				case TibRecorder.MSG_ON_RECORDED:
					if(!isRecording()) 
						return; 
					
					startExtractAudioService(arg1);
					break;
	
	            case TibRecorder.MSG_ON_RECORDER_FINISHED:
	                break;
            }
		}
	}
	
	int countnumrequest=0;
	int countnumresponse=0;
	private void processRequest()
	{
		
		
		if(!isAudioDataProcessing)
		{
			 reqtime = java.util.Calendar.getInstance().getTimeInMillis();
			isAudioDataProcessing=true;
			ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
			try
			{
				outputStream.write(AudioDataFrequencyQueue.getDataQueue().getAudioSampleQueueData());

                if(RecordController.isDebugEnabled)
				{
					outputStream.write(AudioRecordQueue.getDataQueue().getAudioRecordQueueData());
					
				}
                byte []postData=outputStream.toByteArray();
    			
				outputStream.flush();
				outputStream.close();
				outputStream=null;
				sendRequest(postData, postData.length);

			}
			catch (IOException e) 
			{
				isAudioDataProcessing=false;
				e.printStackTrace();
			}catch(NullPointerException npe){
				isAudioDataProcessing=false;
                npe.printStackTrace();
            }
			//isAudioDataProcessing=false;
		}
	}
	
	private void startExtractAudioService(int index)//Bundle data)
	{
		try
		{
			Intent msgIntent = new Intent(mContext, ExtractService.class);
			msgIntent.putExtra("INDEX",index);
			mContext.startService(msgIntent);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	private void sendRequest(final byte []postData,final int contentLength)
	{
		request=new Request<String>(Method.POST,serverURL,createMyReqErrorListener())
		{
			@Override
			public Priority getPriority()
			{
				return Priority.HIGH;
			}
    		@Override
    		public byte[] getBody() 
    		{
    			return postData;
    		}
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError
			{
				Map<String, String> headers = super.getHeaders();

		        if (headers == null|| headers.equals(Collections.emptyMap())) 
		        {
		            headers = new HashMap<String, String>();
		        }
                headers.put("app", "9xm");
		        headers.put("Accept", "text/html,application/xml," +"application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		        headers.put("Content-Type", "application/x-www-form-urlencoded");
		        headers.put("Content-Length", contentLength+"");
		        headers.put("accept", "application/json");
		        headers.put("Accept-Encoding", "");
                headers.put("lc","");
                headers.put("lt","");
				return headers;
			}
			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response)
			{
                Log.d("Phonic Lib" , "Response: " + response);

                String parsed;
		        try
		        {
		            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		        }
		        catch (UnsupportedEncodingException e)
		        {
		            parsed = new String(response.data);
		        }
                Log.d("RecordController" , "Parsed: " + parsed);
		        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
			}
			@Override
			protected void deliverResponse(String response)
			{
				processResponse(response, 200);
				//++countnumresponse;
				//Log.d("Count Record Controller",countnumresponse +" Got response");
			}
		};
		request.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		request.setShouldCache(false);
	    mRequestQueue.add(request);
	}
	

    public Response.ErrorListener createMyReqErrorListener() 
    {
    	return new Response.ErrorListener() 
    	{
            @Override
            public void onErrorResponse(VolleyError volleyError) 
            {
            	int statuscode=0;
				String resp="";
				volleyError.printStackTrace();
            	if(volleyError.networkResponse!=null)
            	{
            		statuscode=volleyError.networkResponse.statusCode;
            		try
            		{
            			resp=new String(volleyError.networkResponse.data);
            		}
            		catch (Exception e){}
            	}
            	if(volleyError instanceof TimeoutError)
            	{
            		resp=TIME_OUT_ERROR;
            		statuscode=TIME_OUT_CODE;
            	}
            	else if(volleyError instanceof NoConnectionError)
            	{
            		resp=CONNECTION_ERROR;
            		statuscode=CONNECTION_ERROR_CODE;
            	}
            	else if(volleyError instanceof NetworkError)
            	{
            		resp=NETWORK_ERROR;
            	}
            	processResponse(resp, statuscode);
            }
        };
    }

	private void processResponse(String response,int statusCode )
	{
		
		try
		{
				Log.d("RECORDCONTROLLER" , "JSONOBJECT RECEIVED: " + statusCode);
				resptime = java.util.Calendar.getInstance().getTimeInMillis();
				boolean clearqueue=mResponseCallback.audioDetectedResponse(response,statusCode);
				if (clearqueue )
				{
					AudioDataFrequencyQueue.getDataQueue().clearAudioQueue();
					AudioRecordQueue.getDataQueue().clearAudioQueue();
					Log.d("Record Queue","Cleared record Queue");
				}
				response=null;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		isAudioDataProcessing=false;
		
	}
	private void registerAudioExtractReciver()
	{
		try
		{
			if(audioExtractReceiver==null)
			{
				audioExtractReceiver = new AudioExtractReceiver();
				IntentFilter filter = new IntentFilter(AUDIO_EXTRACT_RECEIVER);
		        filter.addCategory(Intent.CATEGORY_DEFAULT);
		        audioExtractReceiver = new AudioExtractReceiver();
		       // mContext.registerReceiver(audioExtractReceiver, filter);
				 LocalBroadcastManager.getInstance(mContext).registerReceiver(audioExtractReceiver,filter);
					
			}
		}
		catch (Exception e) {}
	}
	private void unregisterAudioExtractReciver()
	{
		try
		{
			if(audioExtractReceiver!=null)
			{
				LocalBroadcastManager.getInstance(mContext).unregisterReceiver(audioExtractReceiver);
				//mContext.unregisterReceiver(audioExtractReceiver);
			}
			audioExtractReceiver=null;
		}
		catch (Exception e){}
	}
	private class AudioExtractReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
            processRequest();
		}
	}
	
	
	public static abstract class ControllerEventCallback
	{
		protected void audioRecorderInited( int sampleRate){}
		protected void audioRecorderInitError(){}
        protected void doSendRequest(){};
		protected boolean audioDetectedResponse(String response,int statuscode){
			return false;}
	}
}
