package com.teli.audio.common;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class ExtractService extends IntentService
{
	static int countbroadcast=0;
	
	public ExtractService()
	{
		super(ExtractService.class.getName());
	}
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		try
		{
			//Log.d("Extract Service","Got Request");
			//short samples[]=intent.getExtras().getShortArray("PARAM_SAMPLES");
			int index=intent.getExtras().getInt("INDEX");
			short[] samples=TibRecorder.audiobuffer[index];
			
			if(RecordController.isDebugEnabled)
			{
				AudioRecordQueue.getDataQueue().addResultDataQueue(samples);
			}
		//	Log.d("Extract Service","b4 ExtractAudioFeatures");
			byte result[]=AudioJniHandler.ExtractAudioFeatures(samples);
            samples=null;
            if(result == null)
            {
                return;
            }
		//	Log.d("Extract Service","Aft ExtractAudioFeatures");
			AudioDataFrequencyQueue.getDataQueue().addResultDataQueue(result);
			result=null;
			++countbroadcast;
		//	Log.d("Count Extract Service","Added to queue::"+countbroadcast);

			sendBroadcast();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	private void sendBroadcast()
	{
		try
		{
			 Intent broadcastIntent = new Intent();
	    	 broadcastIntent.setAction(RecordController.AUDIO_EXTRACT_RECEIVER);
	         broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
	        
	         LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
	         
	     //    sendBroadcast(broadcastIntent);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
}


