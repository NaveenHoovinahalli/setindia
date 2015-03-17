package com.teli.audio.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class AudioRecordQueue
{

	public static AudioRecordQueue audioRecordQueue=null;
	private Queue<ByteBuffer> audioQueue;
	public static AudioRecordQueue getDataQueue()
	{
		if(audioRecordQueue==null)
		{
			audioRecordQueue=new AudioRecordQueue();
		}
		return audioRecordQueue;
	}
	private AudioRecordQueue()
	{
		audioQueue=new LinkedList<ByteBuffer>();
	}
	
	public void clearAudioQueue()
	{
		if(audioQueue!=null)
		{
			audioQueue.clear();
		}
	}
	
	public void destroyAudioQueue()
	{
		if(audioQueue!=null)
		{
			audioQueue.clear();
		}
		audioQueue=null;
		audioRecordQueue=null;
	}
	
	public  void addResultDataQueue(short[] samples)
	{
		if(audioQueue.size()==RecordController.QUEUE_SIZE)
			audioQueue.poll();
		int i=0;
		int N=samples.length;
		ByteBuffer data = ByteBuffer.allocate(2*N);
		data.order(ByteOrder.LITTLE_ENDIAN);
		while (N > i) 
		{
			data.putShort(samples[i]);
		    i++;
		}
		audioQueue.add(data);
		data.clear();
		data=null;
	}
	public byte[]  getAudioRecordQueueData()
	{
		if(audioQueue.size()==0)
			return null;
		ByteArrayOutputStream resultBuffer=new ByteArrayOutputStream();
		resultBuffer.write(audioQueue.size());
		Iterator<ByteBuffer> iterator=audioQueue.iterator();
		
		while(iterator.hasNext())
		{
			ByteBuffer buffer=iterator.next();
			byte result[]=buffer.array();
			buffer.clear();
			buffer=null;
			try 
			{
				resultBuffer.write(result);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			result=null;
		}
		return resultBuffer.toByteArray();
	}


}
