package com.example.miikka.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.miikka.newsapp.MainActivity.INTENT_AUTHOR;
import static com.example.miikka.newsapp.MainActivity.INTENT_DATE;
import static com.example.miikka.newsapp.MainActivity.INTENT_DESC;
import static com.example.miikka.newsapp.MainActivity.INTENT_IMGURL;
import static com.example.miikka.newsapp.MainActivity.INTENT_NEWSURL;
import static com.example.miikka.newsapp.MainActivity.INTENT_SOURCE;
import static com.example.miikka.newsapp.MainActivity.INTENT_TITLE;

/**
 * Luokka uutisen tarkempien tietojen näyttämiseen. Sisältää muun muassa uutisen kuvauksen.
 * Saa tiedot MainActivityltä Intent:n avulla.
 */
public class MoreNewsInfoActivity extends AppCompatActivity {
    /**
     * Ylikirjoitettu oletusmetodi.
     * Vastaanotetaan MainActivityltä saadut tiedot Intent:n avulla ja liitetään ne osaksi näkymiä.
     * Lisätään myös nappi, jolla voidaan avata uutinen selaimessa.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_news_info);

        Intent i = getIntent();

        final String newsUrl = i.getStringExtra(INTENT_NEWSURL);
        String title = i.getStringExtra(INTENT_TITLE);
        String description = i.getStringExtra(INTENT_DESC);
        String publishDate = i.getStringExtra(INTENT_DATE);
        String source = i.getStringExtra(INTENT_SOURCE);
        String imageUrl = i.getStringExtra(INTENT_IMGURL);
        String author = i.getStringExtra(INTENT_AUTHOR);

        ImageView moreImageView = (ImageView) findViewById(R.id.moreImageView);
        TextView moreTitleTextView = (TextView) findViewById(R.id.moreTitleTextView);
        TextView moreSourceTextView = (TextView) findViewById(R.id.moreSourceTextView);
        TextView morePublishDateTextView = (TextView) findViewById(R.id.morePublishDateTextView);
        TextView moreDescriptionTextView = (TextView) findViewById(R.id.moreDescTextView);
        TextView moreAuthorTextView = (TextView) findViewById(R.id.moreAuthorTextView);
        Button openNewsButton = (Button) findViewById(R.id.goToNewsButton);
        moreImageView.setImageResource(R.drawable.default_image);
        Picasso.with(this).load(imageUrl).fit().centerInside().into(moreImageView);

        moreDescriptionTextView.setText(description);
        moreAuthorTextView.setText(author);
        moreTitleTextView.setText(title);
        moreSourceTextView.setText(source);
        morePublishDateTextView.setText(publishDate.substring(0, publishDate.indexOf('T')));

        openNewsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Kuuntelija napille, jonka avulla avataan uutinen selaimessa.
             * @param v Näkymä, jossa nappia painettu
             */
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl));
                startActivity(i);
            }
        });


    }
}
