package com.iot.switzer.iotdormkitkat.presets;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.iot.switzer.iotdormkitkat.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Lucas Switzer on 7/3/2016.
 */
public class PresetManager extends ArrayList<Preset>{
    private static PresetManager instance;
    private Context context;
    File presetsFile;

    public void writePreset(String out)
    {
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(presetsFile,true);
            fos.write(out.getBytes());

            Log.d("ADDPRESET","File successfully updated:"+presetsFile.getPath());
        }catch (IOException e)
        {
            Log.d("ADDPRESET","An error occured when writing preset to file:"+presetsFile.getPath());
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deletePreset(Preset p) throws IOException {

        if (this.size() > 0) {

            File tempFile = new File(presetsFile.getParentFile().getPath(),"tmpPreset.txt");

            BufferedReader reader = new BufferedReader(new FileReader(presetsFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            int deleteIndex;

            for (deleteIndex = 0; deleteIndex < this.size(); deleteIndex++) {
                if (get(deleteIndex) == p) {
                    break;
                }

            }
            Log.d("PRESETSMANAGER","Delete Index:"+String.valueOf(deleteIndex));

            String readLine = "";
            int i = 0;
            if (deleteIndex < this.size()) {
                while ((readLine = reader.readLine()) != null) {
                    if (i != deleteIndex) {
                        writer.write(readLine);
                    }
                    i++;
                }

                writer.close();
                reader.close();

                tempFile.renameTo(presetsFile);
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
        presetsFile = new File(c.getExternalFilesDir(null), Constants.PRESETS_FILE_NAME);
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
