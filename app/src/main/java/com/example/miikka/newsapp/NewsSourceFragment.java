package com.example.miikka.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Dialog ikkuna lähteiden valitsemiselle. Käyttäjä voi valita useita lähteitä valintaruudun avulla.
 * Avataan MainActivityn kautta.
 */
public class NewsSourceFragment extends DialogFragment {

    private StringBuilder newsSources;
    private Set<String> newsSourcesSet;
    private ImageView bbcImageView;
    private ImageView cnnImageView;
    private ImageView cbsImageView;
    private ImageView nytimesImageView;
    private ImageView redditImageView;
    private ImageView guardianImageView;
    private ImageView washingtonImageView;
    private ImageView vergeImageView;

    private TextView allTextView;
    private TextView bbcTextView;
    private TextView cnnTextView;
    private TextView cbsTextView;
    private TextView nytimesTextView;
    private TextView redditTextView;
    private TextView guardianTextView;
    private TextView washingtonTextView;
    private TextView vergeTextView;

    private CheckBox allCheckBox;
    private CheckBox noneCheckBox;
    private CheckBox bbcCheckBox;
    private CheckBox cnnCheckBox;
    private CheckBox cbsCheckBox;
    private CheckBox nytimesCheckBox;
    private CheckBox redditCheckBox;
    private CheckBox guardianCheckBox;
    private CheckBox washingtonCheckBox;
    private CheckBox vergeCheckBox;

    private SelectedData callback;


    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        ContextThemeWrapper wrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_MaterialComponents_Light_Dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
        View newsSourcesView = getActivity().getLayoutInflater().inflate(
                        R.layout.news_sources, null);
        builder.setView(newsSourcesView);

        builder.setTitle(R.string.menu_source_title);

        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            /**
             * Kuuntelija OK napille. Käyttäjän valitsemat lähteet välitetään MainActivitylle
             * SelectedData rajapinnan metodin onSelectedData avulla. MainActivityn muuttujia
             * asetetaan nulliksi ohjelman logiikkaa varten ja MainActivity:n näkymä päivitetään.
             *
             * @param dialog Dialogi jota klikattiin.
             * @param which Nappi jota painettiin.
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateSources();
                callback.onSelectedData(newsSources.toString()); // muutetaan stringbuilderin data stringiksi

                // asetetaan MainActivityn muuttujia null:ksi, jotta MainActivityn urlForJson toimii.
                if (isAnyChecked()) {
                    ((MainActivity) getActivity()).setCategory(null);
                    ((MainActivity) getActivity()).setCountry(null);
                    ((MainActivity) getActivity()).setSearchWord(null);
                    ((MainActivity) getActivity()).refreshActivity();
                }

            }
        });

        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });


        bbcImageView = (ImageView) newsSourcesView.findViewById(R.id.bbcImageView);
        cnnImageView = (ImageView) newsSourcesView.findViewById(R.id.cnnImageView);
        cbsImageView = (ImageView) newsSourcesView.findViewById(R.id.cbsImageView);
        nytimesImageView = (ImageView) newsSourcesView.findViewById(R.id.nytimesImageView);
        redditImageView = (ImageView) newsSourcesView.findViewById(R.id.redditImageView);
        guardianImageView = (ImageView) newsSourcesView.findViewById(R.id.guardianImageView);
        washingtonImageView = (ImageView) newsSourcesView.findViewById(R.id.washingtonImageView);
        vergeImageView = (ImageView) newsSourcesView.findViewById(R.id.vergeImageView);

        allTextView = (TextView) newsSourcesView.findViewById(R.id.allTextView);

        bbcTextView = (TextView) newsSourcesView.findViewById(R.id.bbcTextView);
        cnnTextView = (TextView) newsSourcesView.findViewById(R.id.cnnTextView);
        cbsTextView = (TextView) newsSourcesView.findViewById(R.id.cbsTextView);
        nytimesTextView = (TextView) newsSourcesView.findViewById(R.id.nytimesTextView);
        redditTextView = (TextView) newsSourcesView.findViewById(R.id.redditTextView);
        guardianTextView = (TextView) newsSourcesView.findViewById(R.id.guardianTextView);
        washingtonTextView = (TextView) newsSourcesView.findViewById(R.id.washingtonTextView);
        vergeTextView = (TextView) newsSourcesView.findViewById(R.id.vergeTextView);

        allCheckBox = (CheckBox) newsSourcesView.findViewById(R.id.allCheckBox);
        noneCheckBox = (CheckBox) newsSourcesView.findViewById(R.id.noneCheckBox);
        bbcCheckBox = (CheckBox) newsSourcesView.findViewById(R.id.bbcCheckBox);
        cnnCheckBox = (CheckBox) newsSourcesView.findViewById(R.id.cnnCheckBox);
        cbsCheckBox = (CheckBox) newsSourcesView.findViewById(R.id.cbsCheckBox);
        nytimesCheckBox = (CheckBox) newsSourcesView.findViewById(R.id.nytimesCheckBox);
        redditCheckBox = (CheckBox) newsSourcesView.findViewById(R.id.redditCheckBox);
        guardianCheckBox = (CheckBox) newsSourcesView.findViewById(R.id.guardianCheckBox);
        washingtonCheckBox = (CheckBox) newsSourcesView.findViewById(R.id.washingtonCheckBox);
        vergeCheckBox = (CheckBox) newsSourcesView.findViewById(R.id.vergeCheckBox);

        /**
         * Kuuntelija allCheckBox valintaruudulle. Jos valintaruutu ruksataan, kaikki lähteiden
         * valintaruudut ruksataan myös.
         */
        allCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allCheckBox.isChecked()){
                    noneCheckBox.setChecked(false);
                    bbcCheckBox.setChecked(true);
                    cnnCheckBox.setChecked(true);
                    cbsCheckBox.setChecked(true);
                    nytimesCheckBox.setChecked(true);
                    redditCheckBox.setChecked(true);
                    guardianCheckBox.setChecked(true);
                    washingtonCheckBox.setChecked(true);
                    vergeCheckBox.setChecked(true);

                }
            }
        });
        /**
         * Kuuntelija noneCheckBoxille. Jos noneCheckBox ruksataan, kaikista lähteistä otetaan
         * valinta pois päältä. Tehty helpottamaan käyttäjäkokemusta, ei kuitenkaan toimi tällä
         * hetkellä täydellisesti sovelluksen logiikassa.
         */
        noneCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noneCheckBox.isChecked()){
                    allCheckBox.setChecked(false);
                    bbcCheckBox.setChecked(false);
                    cnnCheckBox.setChecked(false);
                    cbsCheckBox.setChecked(false);
                    nytimesCheckBox.setChecked(false);
                    redditCheckBox.setChecked(false);
                    guardianCheckBox.setChecked(false);
                    washingtonCheckBox.setChecked(false);
                    vergeCheckBox.setChecked(false);
                    newsSources = null;
                }
            }
        });
        return builder.create();
    }
    /**
     * Metodi liittää rajapinnan implementaation tähän fragmentiin, jonka jälkeen rajapinnan metodeja
     * voidaan kutsua, MainActivitylle tiedon siirtämiseen.
     *
     * @param context onAttachin omaava context.
     */
   @Override
   public void onAttach(Context context){
       super.onAttach(context);

       try{
           callback = (SelectedData) context;
       }
       catch (ClassCastException e){
           Log.d("NewsSourceFragment", "Activity doesn't implement SelectedData interface");
       }
   }

    /**
     * Metodi, jonka avulla muodostetaan newsSources StringBuilder, joka taas ohjaa osaa sovelluksen
     * logiikkasta MainActivityssä.
     * Metodi tarkistaa, mitkä kaikki lähteet ovat valittuina ja muodostaa sen perusteella newsSources
     * StringBuilderin StringSetin avulla.
     */
    private void updateSources(){
        newsSources = new StringBuilder();
        newsSourcesSet = new HashSet<>();

        if(bbcCheckBox.isChecked())
            newsSourcesSet.add("BBC-News");
        if(cnnCheckBox.isChecked())
            newsSourcesSet.add("CNN");
        if(cbsCheckBox.isChecked())
            newsSourcesSet.add("CBS-News");
        if(nytimesCheckBox.isChecked())
            newsSourcesSet.add("The-New-York-Times");
        if(redditCheckBox.isChecked())
            newsSourcesSet.add("Reddit-r-All");
        if(guardianCheckBox.isChecked())
            newsSourcesSet.add("The-Guardian-UK");
        if(washingtonCheckBox.isChecked())
            newsSourcesSet.add("The-Washington-Post");
        if(vergeCheckBox.isChecked())
            newsSourcesSet.add("The-Verge");

        for (String s : newsSourcesSet){
            if (newsSources.length() > 0) {
                newsSources.append(",");
                newsSources.append(s);
            }
            else {
                newsSources.append(s);
            }
        }
    }

    /**
     * Metodi, joka tarkistaa onko yhtään valintaruutua ruksattu. Tarvitaan auttamaan päätöksessä,
     * välitetäänkö OK nappia painettaessa MainActivityn muuttujille null arvot (=selataanko nyt
     * uutislähteitä eikä esim viihde kategoriaa).
     * @return boolean arvo sille, onko jokin valintaruutu ruksattu
     */
    private boolean isAnyChecked() {
        if (allCheckBox.isChecked())
            return true;
        if (noneCheckBox.isChecked())
            return true;
        if (bbcCheckBox.isChecked())
            return true;
        if (cnnCheckBox.isChecked())
            return true;
        if (cbsCheckBox.isChecked())
            return true;
        if (nytimesCheckBox.isChecked())
            return true;
        if (redditCheckBox.isChecked())
            return true;
        if (guardianCheckBox.isChecked())
            return true;
        if (washingtonCheckBox.isChecked())
            return true;
        if (vergeCheckBox.isChecked())
            return true;

    return false;
    }
}
