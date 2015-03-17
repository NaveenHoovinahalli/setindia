 package com.teli.audio.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public final class AudioDataFrequencyQueue 
{
	public static AudioDataFrequencyQueue audioDataQueue=null;
	private Queue<ByteBuffer> audioQueue;
	public static AudioDataFrequencyQueue getDataQueue()
	{
		if(audioDataQueue==null)
		{
			audioDataQueue=new AudioDataFrequencyQueue();
		}
		return audioDataQueue;
	}
	private AudioDataFrequencyQueue()
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
		audioDataQueue=null;
	}
	
	public  void addResultDataQueue(byte[] samples)
	{
		if(audioQueue.size()==RecordController.QUEUE_SIZE)
			audioQueue.poll();
		
		ByteBuffer data=ByteBuffer.allocate(samples.length);
		data.order(ByteOrder.LITTLE_ENDIAN);
		data.put(samples);
		audioQueue.add(data);
		data.clear();
		data=null;
	}
	public byte[]  getAudioSampleQueueData()
	{
		if(audioQueue.size()==0)
			return null;
		ByteArrayOutputStream resultBuffer=new ByteArrayOutputStream();
		byte slot=(byte) ((byte)(RecordController.QUERY_DURATION/1000) <<4|(byte)(audioQueue.size()));
		resultBuffer.write(slot);
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
