package com.teli.audio.common;
import java.util.Calendar;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public final class TibRecorder extends Thread 
{
	public static final int MSG_ON_RECORDER_INIT = 1;
	public static final int MSG_ON_RECORDER_INIT_ERROR = 2;
	public static final int MSG_ON_RECORDER_UPDATE = 3;
	public static final int MSG_ON_RECORDED = 4;
	public static final int MSG_ON_RECORDER_FINISHED = 5;
	
	public static final String PARAM_SAMPLE_RATE = "SAMPLE_RATE";
	public static final String PARAM_CHANNELS = "CHANNELS";
	public static final String PARAM_SAMPLES = "SAMPLES";
	public static final String PARAM_SAMPLES_COUNT = "SAMPLES_COUNT";
	
	private static final int SAMPLE_RATE=11025;
	private static final int MIN_BUFFER_SIZE = 44100;
	private static final int CHANNEL_CONFIGURATION = AudioFormat.CHANNEL_IN_MONO;
	private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	public static final int RECORDING_DURATION_UNLIMITED = -1;
	private static final int OFFSET_MS = 300;
	
	private boolean canceled = false;
	private int bufferSize;
	private Handler observer = null;
    private final int queryDuration;
	private final int maxDuration;
    private int delayDuration;
    private int requiredSamples;
	private int tries;
	
	public static short[][] audiobuffer;
	int currentdata_index=0;
	int maxbuffersize=4;
	int countrecorded=0;
	
	public TibRecorder( int queryDuration,int maxDuration, int delayDuration )
    {
        this.queryDuration = 4000;//queryDuration;
        if( maxDuration == RECORDING_DURATION_UNLIMITED )
			this.maxDuration = Integer.MAX_VALUE;
		else
			this.maxDuration = maxDuration;
		
        this.delayDuration = delayDuration;
    }
	
	public void setObserverCallback(Handler callback)
	{
		this.observer=callback;
	}
	public AudioRecord init()
	{
		
		bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIGURATION, AUDIO_ENCODING );
        if( bufferSize < 0 )
        {
            return null;
        }
        bufferSize = Math.max( bufferSize, MIN_BUFFER_SIZE );
        requiredSamples = (int)(SAMPLE_RATE * ((float)queryDuration / 1000f));
		tries = (int)(((float)maxDuration / (float)queryDuration) + .5f);
        AudioRecord recorder = new AudioRecord( MediaRecorder.AudioSource.MIC,
        										SAMPLE_RATE,CHANNEL_CONFIGURATION,AUDIO_ENCODING,bufferSize * 2 );
        int recording_state=recorder.getState();
        if(recording_state != AudioRecord.STATE_INITIALIZED)
        {
        	//Log.d("TiB Recorder","Audio recorder initilization failed");
        	sendMessage(MSG_ON_RECORDER_INIT_ERROR, 0);
        	return null;
        
        }
        sendRecordInitMessage(recorder.getSampleRate());
        return recorder;
	}
	
	@Override
	public void run()
	{
		super.run();
		final AudioRecord recorder;
		if( (recorder = init()) == null )
        {
			sendMessage( MSG_ON_RECORDER_INIT_ERROR, -1 );
			return;
        }
		recorder.startRecording();
		requiredSamples=((SAMPLE_RATE*(queryDuration/1000))/256)*256;
        int x = 0;
		boolean flag=false;
		
		audiobuffer=new short[maxbuffersize][requiredSamples];
		int samplesRead=0;
        recording: do
        {
           // milis1 = java.util.Calendar.getInstance().getTimeInMillis();
           
            samplesRead = recorder.read( audiobuffer[currentdata_index],samplesRead,requiredSamples );            
            if(samplesRead < requiredSamples)
            {
            	continue;
            }
            samplesRead=0;
			
            if(x < tries)
				sendMessage( MSG_ON_RECORDED, currentdata_index );
			else
				sendMessage( MSG_ON_RECORDER_FINISHED, currentdata_index );
			
			++currentdata_index;
			if(currentdata_index == maxbuffersize)
				currentdata_index=0;
			++countrecorded;
		//	milis2 = java.util.Calendar.getInstance().getTimeInMillis();
			//Log.d("Count Tib Recorder",countrecorded +" time::"+(milis2-milis1));

			
        } 
        while( !canceled && x < tries ); 
        recorder.stop();
        recorder.release();
        return;
	}
	
	public void cancel()
	{
		canceled=true;
	}
	
	private void sendRecordInitMessage(int sampleRate)
	{
	   sendMessage( MSG_ON_RECORDER_INIT,sampleRate  );
	}
	
	private void sendMessage( int what, int index )
    {
		Log.d("Tib Recorder","Sending record complete request");
        Message message = Message.obtain( observer, what );
        message.arg1=index;
        observer.sendMessage( message );
    }
	
}
