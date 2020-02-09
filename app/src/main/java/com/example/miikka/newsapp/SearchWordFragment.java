package com.example.miikka.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

/**
 * Dialog ikkuna hakusanan asettamiselle. Avataan MainActivityssä. Käyttäjä voi tämän avulla
 * hake uutisia haluamallaan hakusanalla.
 */
public class SearchWordFragment extends DialogFragment {

    private TextView searchWordTitleTextView;
    private TextInputEditText searchWordTextInput;
    private TextView onlyEnglishTextView;
    private String searchWord;
    private SelectedData callback;

    /**
     * Dialogin oletusmetodi. Luodaan dialogi, asetetaan näkymät ja napit.
     *
     * @param bundle
     * @return palauttaa rakennetun dialogin.
     */
    public Dialog onCreateDialog(Bundle bundle) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_MaterialComponents_Light_Dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
        View searchWordView = getActivity().getLayoutInflater().inflate(
                R.layout.search_word, null);
        builder.setView(searchWordView);

        searchWordTitleTextView = searchWordView.findViewById(R.id.searchWordTitleTextView);
        searchWordTextInput = searchWordView.findViewById(R.id.searchWordTextInput);
        onlyEnglishTextView = searchWordView.findViewById(R.id.onlyEnglishTextView);

        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            /**
             * Kuuntelija OK napille. Kuuntelijassa käyttäjän valitsema hakusana välitetään
             * MainActivityyn rajapinnan SelectedData metodin onSelectedSearchWord metodin avulla.
             * MainActivityn muuttujia asetetaan nulliksi ohjelman logiikkaa varten ja MainActivity:n
             * näkymä päivitetään.
             *
             * @param dialog Dialogi jota klikattiin.
             * @param which Nappi jota painettiin.
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity)getActivity()).setCategory(null);
                ((MainActivity)getActivity()).setCountry(null);
                ((MainActivity)getActivity()).setNewsSources(null);

                searchWord = searchWordTextInput.getText().toString();
                callback.onSelectedSearchWord(searchWord);
                ((MainActivity)getActivity()).refreshActivity();
            }
        });

        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
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
            Log.d("SearchWordFragment", "Activity doesn't implement SelectedData interface");
        }
    }

}

