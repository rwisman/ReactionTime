/*
 *     Copyright (C) 2017  Raymond Wisman
 * 			Indiana University SE
 * 			April 7, 2017
 *
 * 	AudioSineWave produces a sine wave on audio.

    Credits: http://stackoverflow.com/questions/20889627/playing-repeated-audiotrack-in-android-activity

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details <http://www.gnu.org/licenses/>.

 */

package edu.ius.rwisman.reactiontime;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

class AudioSineWave {										// Some phones require more than a single wave in buffer (e.g. Nexus 5)
    static AudioTrack audioTrack=null;
    static AudioManager audioManager=null;
    static byte [] sineWave=null;
    static boolean first = true;

    private static byte [] sineWave(double Hz, double seconds, int sampleRate) {
        double dnumSamples = seconds * sampleRate;
        dnumSamples = Math.ceil(dnumSamples);
        int numSamples = (int) dnumSamples;
        double sample[] = new double[numSamples];
        byte [] sw = new byte[2 * numSamples];

        for (int i = 0; i < numSamples; ++i) {      // Fill the sample array
            sample[i] = Math.sin(Hz * 2 * Math.PI * i / (sampleRate));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalized.
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        int i = 0 ;

        for (i = 0; i < numSamples; ++i) {                               // Max amplitude for most of the samples
            double dVal = sample[i];
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            sw[idx++] = (byte) (val & 0x00ff);
            sw[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        return sw;
    }

    /*
        Static class data is not reclaimed or altered when Activity onDestroy method is called. Each static class variable is normally created only once and
        maintains its binding while the Activity object is destroyed and recreated (e.g. after screen rotation).
     */

    public static void playTone(final Context context, double Hz, double seconds, double VOLUME, int sampleRate) {
        if(sineWave == null)
            sineWave = sineWave(Hz, seconds, sampleRate);

        if(audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (int) (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * VOLUME),                            // Set to fractional max volume
                AudioManager.MODE_NORMAL);

        try {
            if(audioTrack == null) {
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT),
                        AudioTrack.MODE_STATIC);
                audioTrack.write(sineWave, 0, sineWave.length);         // Load the track
                first = true;
            }

            audioTrack.stop();

//            if( first )                                                 // Workaround for some devices (e.g. Blu) that echo sound first time played
//                first = false;
//            else
                audioTrack.reloadStaticData();

            audioTrack.play();                                          // Play the track
        }
        catch (Exception e){  }
    }

    public static void close() {
       if(audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
            audioManager = null;
            sineWave = null;
            first = true;
        }
    }
}

