package es.upm.miw.ficheros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.angelas.myapplication.DeleteFilesActivity;
import com.example.angelas.myapplication.SettingsActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class FicherosActivity extends AppCompatActivity {

    private String RUTA_FICHERO;
    EditText lineaTexto;
    TextView fichero;
    Button botonAniadir;
    TextView contenidoFichero;
    String fileName;
    Boolean sdcard;

    @Override
    protected void onStart() {
        super.onStart();
        mostrarContenido(contenidoFichero);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficheros);

        fichero          = (TextView) findViewById(R.id.textName);
        lineaTexto       = (EditText) findViewById(R.id.textoIntroducido);
        botonAniadir     = (Button)   findViewById(R.id.botonAniadir);
        contenidoFichero = (TextView) findViewById(R.id.contenidoFichero);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        fileName = sharedPref.getString("pref_save", "") + ".txt";
        sdcard = sharedPref.getBoolean("pref_sync",false);

        Log.i("Fichero-> ",fileName);
        Log.i("SD CARD-> ",sdcard.toString());

        fichero.setText(sharedPref.getString("pref_save", ""));

        if (sdcard) {
            RUTA_FICHERO = getExternalFilesDir(null) + "/" + fileName;
        }
        else {
            RUTA_FICHERO = getFilesDir() + "/" + fileName;
        }

        Log.i("DIR-> ",RUTA_FICHERO);

    }

    /**
     * Al pulsar el botón añadir -> añadir al fichero.
     * Después de añadir -> mostrarContenido()
     *
     * @param v Botón añadir
     */
    public void accionAniadir(View v) {
        /** Comprobar estado SD card **/
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {  // Añadir al fichero
            if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {  /** SD card **/
                // FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_APPEND);
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO, true);
                fos.write(lineaTexto.getText().toString().getBytes());
                fos.write('\n');
                fos.close();
                mostrarContenido(contenidoFichero);
                lineaTexto.setText("");
                Log.i("FICHERO", "Click botón Añadir -> AÑADIR al fichero");
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Se pulsa sobre el textview -> mostrar contenido del fichero
     * Si está vacío -> mostrar un Toast
     *
     * @param textviewContenidoFichero TextView contenido del fichero
     */
    public void mostrarContenido(View textviewContenidoFichero) {
        boolean hayContenido = false;
        File fichero = new File(RUTA_FICHERO);
        String estadoTarjetaSD = Environment.getExternalStorageState();
        contenidoFichero.setText("");
        try {
            if (fichero.exists() &&         /** SD card **/
                    estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                BufferedReader fin = new BufferedReader(new FileReader(new File(RUTA_FICHERO)));
                String linea = fin.readLine();
                while (linea != null) {
                    hayContenido = true;
                    contenidoFichero.append(linea + '\n');
                    linea = fin.readLine();
                }
                fin.close();
                Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    /**
     * Añade el menú con la opcion de vaciar el fichero
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // case 1:
            case R.id.accionVaciar:
                borrarContenido();
                break;
            case R.id.settings:
                showSettings(item);
                break;
            case R.id.eliminarFicheros:
                deleteFiles(item);
                break;
        }

        return true;
    }

    /**
     * Vaciar el contenido del fichero, la línea de edición y actualizar
     *
     */
    public void borrarContenido() {
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {  // Vaciar el fichero
            if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) { /** SD card **/
                // FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE);
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO);
                fos.close();
                Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
                lineaTexto.setText(""); // limpio la linea de edición
                mostrarContenido(contenidoFichero);
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showSettings(MenuItem item){
        Intent nuevoIntent = new Intent(FicherosActivity.this, SettingsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("OPCION_ELEGIDA", String.valueOf(item.getItemId()));
        nuevoIntent.putExtras(bundle);

        startActivity(nuevoIntent);
    }

    public void deleteFiles(MenuItem item){
        Intent nuevoIntent = new Intent(FicherosActivity.this, DeleteFilesActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("OPCION_ELEGIDA", String.valueOf(item.getItemId()));
        nuevoIntent.putExtras(bundle);

        startActivity(nuevoIntent);
    }

}
