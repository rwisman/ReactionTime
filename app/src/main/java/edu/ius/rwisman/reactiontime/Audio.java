package edu.ius.rwisman.reactiontime;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.Nullable;

/*
 *     Copyright (C) 2017  Raymond Wisman
 * 			Indiana University SE
 * 			May 6, 2017
 *
 * 	Audio implements operations on static data for defining and recording an audio stream.
 *  Static data is created only when the app is initially executed and is not destroyed during
 *  the normally app life cycle.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details <http://www.gnu.org/licenses/>.

 */

public class Audio {
    private static AudioRecord mRecorder=null;
    private static int[] mSampleRates = new int[] { 44100, 22050, 11025, 8000 };
    private static int RECORDER_SAMPLERATE=0;


    public static void start() {
        try {
            if(mRecorder == null)
                mRecorder = findAudioRecord();
            mRecorder.startRecording();
        }
        catch(NullPointerException e) {}
    }

    public static void stop() {
        if(mRecorder != null)
            mRecorder.stop();
    }

    public static int read(byte [] data) {
        if(mRecorder != null)
            return mRecorder.read(data, 0, data.length);
        else
            return -1;
    }

    public static int bufferSize() {
        if(mRecorder == null)
            mRecorder = findAudioRecord();

        return AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }

    @Nullable
    private static AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            try {
                int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

                if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                    AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, bufferSize);

                    RECORDER_SAMPLERATE = rate;

                    if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                        return recorder;
                }
            } catch (Exception e) {}
        }
        return null;
    }
}
