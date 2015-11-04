package com.example.angelas.myapplication;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.upm.miw.ficheros.R;

public class DeleteFilesActivity extends AppCompatActivity {

    private String fileName;
    private Boolean sdcard;
    private TextView textView;
    private ListView listView;
    private ArrayList<String> f;
    private ArrayAdapter adaptador;
    List<String> ff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_files);
        textView = (TextView) findViewById(R.id.titleSave);

        Bundle bundle = this.getIntent().getExtras();
        String elementoElegido = bundle.getString("OPCION_ELEGIDA");

        setupActionBar();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sdcard = sharedPref.getBoolean("pref_sync",false);

        if(sdcard){
            textView.setText("Ficheros SDCard");
        }
        else{
            textView.setText("Ficheros internos");
        }

        listView = (ListView) findViewById(R.id.listFiles);
        f = getFiles(sdcard);

        // TO DO crear adaptador a partir del recurso (ArrayAdapter.createFromResource)
        adaptador = new ArrayAdapter(this,R.layout.files_layout,f);

        // TO DO Asignar el adaptador al recurso
        listView.setAdapter(adaptador);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Array ->", String.valueOf(f.size()));
                for (int i = 0; i < f.size(); i++) {
                    Log.i("Item checked ->", String.valueOf(listView.isItemChecked(i)));
                    if (listView.isItemChecked(i)) {
                        getFileStreamPath(f.get(i)).delete();
                    }
                }
                updateData();
            }
        });

        //this.getApplicationContext().finish();
    }

    private void updateData() {
        f = getFiles(sdcard);
        adaptador.clear();
        adaptador.addAll(f);
        adaptador.notifyDataSetChanged();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private ArrayList<String> getFiles(Boolean sdcard){
        ArrayList<String> fileNames = new ArrayList<String>();
        File[] files;
        String estadoTarjetaSD = Environment.getExternalStorageState();
        if(sdcard){
            try {  // Añadir al fichero
                if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                    files = getExternalFilesDirs(null);
                    for (File fl: files){
                        fileNames.add(fl.getName());
                    }
                }
            }catch (Exception e){
                Log.e("FILE I/O", "ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
        else{
            try {  // Añadir al fichero
                if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                    files = getFilesDir().listFiles();
                    for (File fl: files){
                       fileNames.add(fl.getName());
                    }
                }
            }catch (Exception e){
                Log.e("FILE I/O", "ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return fileNames;
    }

}
