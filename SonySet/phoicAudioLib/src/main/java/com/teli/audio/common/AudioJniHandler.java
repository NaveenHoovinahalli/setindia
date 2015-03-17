package com.teli.audio.common;

public class AudioJniHandler
{
	public static native byte[] ExtractAudioFeatures(short []data);
}
