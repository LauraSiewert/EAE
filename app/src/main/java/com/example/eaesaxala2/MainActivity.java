package com.example.eaesaxala2;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner mainspinner;
    Spinner subspinner;
    ListView rezeptListe;
    Context ctx = this;
    DatenBankManager db;
    String [] unterkategorie = new String[]{"vegan", "nicht vegan", "vegetarisch", "nicht vegetarisch"};
    String [] hauptkategorie = new String []{"Backen", "Kochen"};
    Spinner mainspinner2;
    Spinner subspinner2;
    ImageButton likeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Meine Rezepte");

        db = new DatenBankManager(this);

        //Hauptkategorie Spinner
        mainspinner = (Spinner) findViewById(R.id.MAIN_SPINNER);
        mainspinner.setOnItemSelectedListener(this);
        setSpinner(mainspinner, hauptkategorie);

        //Unterkategorie Spinner
        subspinner = (Spinner) findViewById(R.id.SUB_SPINNER);
        subspinner.setOnItemSelectedListener(this);

        rezeptListe = (ListView) findViewById(R.id.REZEPT_LISTE);
        registerForContextMenu(rezeptListe);
        //updateListe();

        rezeptListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
                //ID des Eintrags mit übergeben, um dort Details dieses EIntrags zu laden
                TextView idTextView = view.findViewById(R.id.REZEPT_ID_LIST);
                String id = idTextView.getText().toString();

                //ID des Eintrags mit übergeben, um dort Details dieses EIntrags zu laden
                Intent detailR_viewInt = new Intent(getApplicationContext(), DetailRezept.class);
                detailR_viewInt.putExtra("id", id);

                startActivity(detailR_viewInt);
            }
        });

        Cursor test2 = db.selectRezeptByUnterkategorie("vegetarisch");
        Log.d("SL", DatabaseUtils.dumpCursorToString(test2));

        //TO-DO:
        //Bild: Bild wird gemacht, aber nicht wieder gefunden, Bild muss in Liste angezeigt werden
        //Bild muss auch aus der Bibliothek gefunden werden
    }



    public void updateListe() {
        Cursor zeiger = db.selectAllRezepte();
        String[] spalten = new String[] {db.SPALTE_REZEPT_NAME};
        int list = android.R.layout.simple_list_item_1;
        int[] items = new int[] {android.R.id.text1};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, list, zeiger, spalten, items, 0);
        rezeptListe.setAdapter(adapter);
    }

    //Methode um schneller, Liste zu setzen
    public void setListe (ListView list, Cursor cursor, String [] from){
        int itemLayout = android.R.layout.simple_list_item_2;
        int [] to = new int[] {android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(ctx, itemLayout, cursor, from, to, 0);
        list.setAdapter(adapter);
    }


    //Methoden für Spinner

    //Methode um schnell einen  Spinner zu erstellen
    public void setSpinner (Spinner spinner, String [] array){
        int itemRes = android.R.layout.simple_spinner_dropdown_item;
        ArrayAdapter adapter = new ArrayAdapter(ctx, itemRes, array);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int itemRes = android.R.layout.simple_spinner_dropdown_item;
        //Möglichkeiten für den Hauptkategorie-Spinner
        if (parent == mainspinner){
            if (position == 0) {
                //Backen
                String[] inhalt = new String[2];
                inhalt[0] = unterkategorie[0];
                inhalt[1] = unterkategorie[1];
                setSpinner(subspinner, inhalt);
            }
            else {
                //Kochen
                String[] inhalt = new String[3];
                inhalt[0] = unterkategorie[0];
                inhalt[1] = unterkategorie[2];
                inhalt[2] = unterkategorie[1];
                setSpinner(subspinner, inhalt);
                rezeptListe = (ListView) findViewById(R.id.REZEPT_LISTE);
                int itemLayout = R.layout.main_list_item;
                Cursor cursor = db.selectRezeptByHauptKategorie("Kochen");
                String [] from = new String[]{db.SPALTE_REZEPT_BILD, db.SPALTE_REZEPT_NAME, db.SPALTE_REZEPT_ID, db.SPALTE_REZEPT_BEWERTUNG};
                int [] to = new int[]{R.id.REZEPT_BILD,R.id.REZEPT_NAME, R.id.REZEPT_ID_LIST, R.id.REZEPT_BEWERTUNG};
                MainListAdapter mainListAdapter = new MainListAdapter(ctx, itemLayout, cursor, from, to, 0);
                rezeptListe.setAdapter(mainListAdapter);
            }
        }
        //Liste erstellen, um die Rezepte auszugeben
        rezeptListe = (ListView) findViewById(R.id.REZEPT_LISTE);
        int itemLayout = R.layout.main_list_item;
        String [] from = new String[]{db.SPALTE_REZEPT_BILD, db.SPALTE_REZEPT_NAME, db.SPALTE_REZEPT_ID, db.SPALTE_REZEPT_BEWERTUNG};
        int [] to = new int[]{R.id.REZEPT_BILD,R.id.REZEPT_NAME, R.id.REZEPT_ID_LIST, R.id.REZEPT_BEWERTUNG};
        //Möglichkeiten für den Unterkategorien Spinner
        if (parent == subspinner){
            //wenn mainspinner auf Backen ist
            if (mainspinner.getSelectedItem().toString().equals("Backen")){
                if(position==0){
                    Cursor sub00 = db.selectRezeptByUnterkategorieUndHauptkategorie("vegan", "Backen");
                    MainListAdapter mainListAdapter = new MainListAdapter(ctx, itemLayout, sub00, from, to, 0);
                    rezeptListe.setAdapter(mainListAdapter);
                }
                else if (position==1){
                    Cursor sub01 = db.selectRezeptByUnterkategorieUndHauptkategorie("nicht vegan", "Backen");
                    MainListAdapter mainListAdapter = new MainListAdapter(ctx, itemLayout, sub01, from, to, 0);
                    rezeptListe.setAdapter(mainListAdapter);
                }
                else{
                    Cursor cursor = db.selectRezeptByHauptKategorie("Backen");
                    MainListAdapter mainListAdapter = new MainListAdapter(ctx, itemLayout, cursor, from, to, 0);
                    rezeptListe.setAdapter(mainListAdapter);
                }
            }
            //wenn mainspinner auf Kochen ist
            else if (mainspinner.getSelectedItem().toString().equals("Kochen")){
                if(position==0){
                    Cursor sub10 = db.selectRezeptByUnterkategorieUndHauptkategorie("vegan", "Kochen");
                    MainListAdapter mainListAdapter = new MainListAdapter(ctx, itemLayout, sub10, from, to, 0);
                    rezeptListe.setAdapter(mainListAdapter);
                }
                else if (position==1){
                    Cursor sub11 = db.selectRezeptByUnterkategorieUndHauptkategorie("vegetarisch", "Kochen");
                    MainListAdapter mainListAdapter = new MainListAdapter(ctx, itemLayout, sub11, from, to, 0);
                    rezeptListe.setAdapter(mainListAdapter);
                }
                else if (position==2){
                    Cursor sub12 = db.selectRezeptByUnterkategorieUndHauptkategorie("nicht vegan", "Kochen");
                    MainListAdapter mainListAdapter = new MainListAdapter(ctx, itemLayout, sub12, from, to, 0);
                    rezeptListe.setAdapter(mainListAdapter);
                }
                else{
                    Cursor cursor = db.selectRezeptByHauptKategorie("Kochen");
                    MainListAdapter mainListAdapter = new MainListAdapter(ctx, itemLayout, cursor, from, to, 0);
                    rezeptListe.setAdapter(mainListAdapter);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Methoden für die Menüs

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.ITEM_SUCHEN:
                if(!((MainActivity) ctx).isFinishing())
                {
                    Intent search_viewInt = new Intent(MainActivity.this, SearchableActivity.class);
                    startActivity(search_viewInt);
                }
                break;
            case R.id.ITEM_HINZUFUEGEN:
                Intent detail_viewInt = new Intent(getApplicationContext(), BackenOderKochen.class);
                startActivity(detail_viewInt);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start_activity_menu, menu);
        return true;
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ITEM_DELETE:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                db.deleteRezept((int) info.id);
                updateListe();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String rezeptName = db.getRezeptName((int) info.id);

        MenuItem deleteTexView = menu.findItem(R.id.ITEM_DELETE);
        deleteTexView.setTitle(rezeptName + " löschen");
    }

}




