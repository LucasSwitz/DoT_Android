package com.iot.switzer.iotdormkitkat.presets;

import android.content.Context;
import android.util.Log;

import com.iot.switzer.iotdormkitkat.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Lucas Switzer on 7/3/2016.
 */
public class PresetManager extends ArrayList<Preset> {
    private static PresetManager instance;
    File presetsFile;
    private Context context;

    private PresetManager() {

    }

    public static PresetManager getInstance() {
        if (instance == null) {
            instance = new PresetManager();
        }
        return instance;
    }

    public void writePreset(String out) {
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(presetsFile, true);
            fos.write(out.getBytes());

            Log.d("ADDPRESET", "File successfully updated:" + presetsFile.getPath());
        } catch (IOException e) {
            Log.d("ADDPRESET", "An error occured when writing preset to file:" + presetsFile.getPath());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int find(Preset p) {
        if (this.size() > 0) {
            int i;

            for (i = 0; i < this.size(); i++) {
                if (get(i) == p) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void copyAndIgnore(int ignoredIndex) {
        File tempFile = new File(presetsFile.getParentFile().getPath(), "tmpPreset.txt");

        try {
            Log.d("PRESETMANAGER","Deleteing preset at index"+ String.valueOf(ignoredIndex));
            BufferedReader reader = new BufferedReader(new FileReader(presetsFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String readLine = "";
            int i = 0;

            if (ignoredIndex < this.size()) {
                while ((readLine = reader.readLine()) != null) {
                    if (i != ignoredIndex) {
                        writer.write(readLine);
                    }
                    i++;
                }

                writer.close();
                reader.close();

                tempFile.renameTo(presetsFile);
            }
        } catch (IOException e) {
            Log.d("PRESTMANAGER", "Failed to copy new file", e);
        }
    }

    public void deletePreset(Preset p) throws IOException {

        int deleteIndex = find(p);

        if (deleteIndex != -1) {
            copyAndIgnore(find(p));
        } else {
            Log.d("PRESETMANAGER", "Not deleting preset: preset not found");
        }

    }

    public void reload() {
        this.clear();
        this.loadFromFile();
    }

    public void setContext(Context c) {
        this.context = c;
        presetsFile = new File(c.getExternalFilesDir(null), Constants.PRESETS_FILE_NAME);
    }

    private int readFromPresetFile(char[] in) {
        File presetsFile = new File(context.getExternalFilesDir(null), Constants.PRESETS_FILE_NAME);
        int bytesRead = 0;

        try {
            BufferedReader bf = new BufferedReader(new FileReader(presetsFile));
            bytesRead = bf.read(in);
        } catch (IOException e) {
            Log.d("PRESETMANAGER", "Unable to read from preset file", e);
        }
        return bytesRead;
    }

    private void parsePresetsFromFile(char[] in, int size) {

        PresetParser parser = new PresetParser();
        ArrayList<Preset> presets = (ArrayList) parser.parseMany(in, size);

        for (Preset p : presets) {
            add(p);
        }
    }

    protected void loadFromFile() {

        char in[] = new char[4096];
        int bytesRead = 0;

        bytesRead = readFromPresetFile(in);
        parsePresetsFromFile(in, bytesRead);
    }
}
