package com.iot.switzer.iotdormkitkat.presets;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.iot.switzer.iotdormkitkat.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Lucas Switzer on 7/3/2016.
 */
public class PresetManager extends ArrayList<Preset>{
    private static PresetManager instance;
    private Context context;

    public void writePreset(String out)
    {
        File outFile = new File(context.getExternalFilesDir(null), Constants.PRESETS_FILE_NAME);
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(outFile,true);
            Log.d("ADDPRESET","Out: "+out);
            fos.write(out.getBytes());

            Log.d("ADDPRESET","File successfully updated:"+outFile.getPath());
        }catch (IOException e)
        {
            Log.d("ADDPRESET","An error occured when writing preset to file:"+outFile.getPath());
        }
        finally {
            try {
                if (fos != null) {
                    Log.d("ADDPRESET","Closing File Buffer");
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reload()
    {
        this.clear();
        this.loadFromFile();
    }

    public void setContext(Context c)
    {
        this.context = c;
    }

    protected void loadFromFile()
    {
        File presetsFile = new File(context.getExternalFilesDir(null),Constants.PRESETS_FILE_NAME);
        char in[] = new char[4096];
        String s = "";
        int bytesRead;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(presetsFile));
            bytesRead = bf.read(in);

            int index = 0;
            while(index < bytesRead)
            {
                if(in[index] == Preset.PRESET_DELIM)
                {
                    add(new Preset(s));
                    s="";
                }
                else
                {
                    s+=in[index];
                }
                index++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PresetManager getInstance()
    {
        if(instance == null)
        {
            instance = new PresetManager();
        }
        return instance;
    }
    
    private PresetManager()
    {
        
    }
}
