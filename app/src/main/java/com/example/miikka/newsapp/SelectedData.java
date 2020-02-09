package com.example.miikka.newsapp;

/**
 * Rajapinta sisältää metodit tiedon välittämiseen NewsSourceFragment -> MainActivity sekä
 * SearchWordFragment -> MainActivity.
 */
public interface SelectedData {
    void onSelectedData(String string);
    void onSelectedSearchWord(String string);
}
