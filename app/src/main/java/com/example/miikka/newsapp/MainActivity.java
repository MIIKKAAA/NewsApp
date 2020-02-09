package com.example.miikka.newsapp;

import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Sovelluksen pääluokka. Sisältää JSON Datan fetchaamisen, käyttöliittymän ja sen komponentit sekä
 * suuren osan ohjelman logiikasta.
 */
public class MainActivity extends AppCompatActivity implements SelectedData, Adapter.OnItemClickListener{

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_DESC = "description";
    public static final String INTENT_DATE = "publishDate";
    public static final String INTENT_IMGURL = "imageUrl";
    public static final String INTENT_SOURCE = "source";
    public static final String INTENT_NEWSURL = "newsUrl";
    public static final String INTENT_AUTHOR = "author";
    private boolean refreshBool = true; // päivitetäänkö mainactivity vai ei
    private RecyclerView recyclerView;
    private Adapter adapter;
    private ArrayList<NewsItem> newsItemList;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView headerView;
    private String category;
    private String country;
    private String categoryForJson;
    private String url = "https://newsapi.org/v2/top-headlines?country=us&pageSize=40&apiKey=d51feed9d99647708454fc5e0caa8a75";
    private boolean preferencesChanged = true;
    private final String CATEGORIES = "pref_category";
    private final String COUNTRIES = "pref_country";
    private String newsSources;
    private String sourcesForHeader;
    private String searchWord;
    SharedPreferences sharedPref;

    public MainActivity(){
    }

    /**
     * Ylikirjoitettu oletusmetodi. Kutsutaan käynnistettäessä. Metodissa tehdään perusalustukset,
     * kytketään muuttujat elementteihin ja haetaan JSOn data parseJSON metodin avulla.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(preferencesChangeListener);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        recyclerView = findViewById(R.id.recycler_view);
        headerView = findViewById(R.id.headerTextView);
        newsItemList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        setHeaderTextView(url, headerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        parseJSON(); // haetaan JSON data alkuasetuksilla

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                refreshActivity();
            }
        });
    }
    /**
     * Ylikirjoitettu oletusmetodi. Haetaan oletusarvot SharedPreferencesille.
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesChanged){
            updateCategory(PreferenceManager.getDefaultSharedPreferences(this));
            if ((category != null) && (category.equals("All"))){
                category = null; // asetetaan nulliksi, kaikki kategoriat = ei kategorioita
            }
            updateCountry(PreferenceManager.getDefaultSharedPreferences(this));
            preferencesChanged = false;
        }
    }

    /**
     * Ylikirjoitettu oletusmetodi. Käytetään MainActivityn näkymän päivittämiseen.
     * refeshBool boolean muuttujan avulla tarkistetaan, tullaanko ulos sellaisesta activitystä,
     * joka haluaa päivittää MainActivityä.
     */
    @Override
    protected void onRestart(){
        super.onRestart();
        if (refreshBool) {
            refreshActivity();
        }
        refreshBool = false;
    }

    /**
     * Ylikirjoitettu oletusmetodi. Luo options menun.
     *
     * @param menu Sovelluksen menu.
     * @return Palauttaa vahvistuksen menun luonnista.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Ylikirjoitettu oletusmetodi. Sisältää logiikan menun eri kuvakkaiden painalluksille.
     * categories_and_language avaa uuden preferences ikkunan.
     * sources avaa uuden NewsSourceFragmentin dialogin, jossa käyttäjä voi valita halutut lähteet.
     * searchword avaa uuden SearchWordFragment dialogin, jossa käyttäjä voi syöttää hakusanan.
     * NewsAPI:n rajoitusten takia haun voi tehdä vain englannin kielisiin artikkeleihin.
     *
     * @param item Alkio (kuvake), jota käyttäjä painaa
     * @return Palauttaa vahvistuksen valinnasta (bool).
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch(item.getItemId()) {
            case R.id.categories_and_language:
                Intent preferencesIntent = new Intent(this, SettingsActivity.class);
                startActivity(preferencesIntent);
                return super.onOptionsItemSelected(item);
            case R.id.sources:
                NewsSourceFragment sourceFragment = new NewsSourceFragment();
                sourceFragment.show(getSupportFragmentManager(), "source dialog");
                return true;
            case R.id.searchword:
                SearchWordFragment searchFragment = new SearchWordFragment();
                searchFragment.show(getSupportFragmentManager(), "search dialog");
                return true;
        }

        return true;
    }

    /**
     * Rajapinnan SelectedData ylikirjoitettu metodi.
     * Metodin avulla välitetään dataa NewsSourceFragmentistä MainActivityyn.
     *
     * @param string NewsSourceFragmentista saatu string alkio (newsSources).
     */
    @Override
    public void onSelectedData(String string){
        newsSources = string;
        if (newsSources == "")
            newsSources = null;
    }

    /**
     * Rajapinnan SelectedData ylikirjoitettu metodi.
     * Metodin avulla välitetään dataa SearchWordFragmentista MainActivityyn.
     *
     * @param string SearchWordFragmentista saatu string alkio (searchWord).
     */
    @Override
    public void onSelectedSearchWord(String string){
        searchWord = string;
    }

    /**
     * Metodi, jonka avulla MainActivityn näkymä päivitetään.
     * Tyhjentää newsListin, huomauttaa adapteria muutoksista ja fetchaa uuden JSON datan parseJSON
     * metodin avulla. Asettaa myös ylätunnuksen näkymälle.
     */
    public void refreshActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                newsItemList.clear();
                adapter.notifyDataSetChanged();
                parseJSON();
                setHeaderTextView(url, headerView);
                swipeRefreshLayout.setRefreshing(false);

                System.out.println("kategoria: " +category);
                System.out.println("maa: " +country);
                System.out.println("lähteet: "+newsSources);
                System.out.println("jsonkategoria: "+categoryForJson);
            }
        }, 2000);
    }

    /**
     * Metodi päivittää SharedPreferencsin kautta valitun vaihtoehdon country muuttujaan.
     * Asettaa myös refreshBool = true, jotta onRestart() älyää päivittää MainActivityn näkymän.
     *
     * @param sharedPreferences SharedPreferences, josta valittu vaihtoehto haetaan.
     */
    public void updateCountry(SharedPreferences sharedPreferences){
        country = sharedPreferences.getString(COUNTRIES, null);
        refreshBool = true;
    }
    /**
     * Metodi päivittää SharedPreferencsin kautta valitun vaihtoehdon category muuttujaan.
     * Asettaa myös refreshBool = true, jotta onRestart() älyää päivittää MainActivityn näkymän.
     * Metodiin lisätty logiikka JSON url:lle välitettävän categoryForJson muuttujan asettamiseen.
     *
     * @param sharedPreferences SharedPreferences, josta valittu vaihtoehto haetaan.
     */
    public void updateCategory(SharedPreferences sharedPreferences){
        category = sharedPreferences.getString(CATEGORIES, null);

        //määritellään categoryForJson SharedPreferencesistä saadun category muuttujan avulla.
        if (category.equals("Kaikki") || category.equals("All"))
            categoryForJson = null;
        if (category.equals("Viihde") || category.equals("Entertainment"))
            categoryForJson = "entertainment";
        else if (category.equals("Yleinen") || category.equals("General"))
            categoryForJson = "general";
        else if (category.equals("Terveys") || category.equals("Health"))
            categoryForJson = "health";
        else if (category.equals("Bisnes") || category.equals("Business"))
            categoryForJson = "business";
        else if (category.equals("Tiede") || category.equals("Science"))
            categoryForJson = "science";
        else if (category.equals("Urheilu") || category.equals("Sports"))
            categoryForJson = "sports";
        else if (category.equals("Teknologia") || category.equals("Technology"))
            categoryForJson = "technology";

        refreshBool = true;

    }

    /**
     * Asettaa ylätunnuksen vastaamaan käyttäjän valitsemia hakutuloksia.
     *
     * @param url JSON datan sisältävä url. Tämän avulla valitaan oikea teksti ylätunnukseen.
     * @param view Näkymä, johon teksti asetetaan. Tässä tapauksessa activity_mainin headerView.
     */
    private void setHeaderTextView(String url, TextView view){

        if (url.equals("https://newsapi.org/v2/top-headlines?country=" + country + "&pageSize=40&apiKey=d51feed9d99647708454fc5e0caa8a75")){
            view.setText(getText(R.string.top_news) + " - "+ country.toUpperCase());
        }
        else if (url.equals("https://newsapi.org/v2/top-headlines?country=" + country + "&pageSize=40&category=" + categoryForJson + "&apiKey=d51feed9d99647708454fc5e0caa8a75")){
            view.setText(getText(R.string.top_news) + " (" + category + ")" + " - " + country.toUpperCase());
        }
        else if (url.equals("https://newsapi.org/v2/everything?q=" + searchWord + "&pageSize=40&apiKey=d51feed9d99647708454fc5e0caa8a75")){
            view.setText(getText(R.string.top_news_search) + searchWord);
        }
        else if (url.equals("https://newsapi.org/v2/top-headlines?category=" + categoryForJson + "&country=us&pageSize=40&apiKey=d51feed9d99647708454fc5e0caa8a75")){
            view.setText(getText(R.string.top_news) + " - US");
        }
        if (url.equals("https://newsapi.org/v2/top-headlines?country=us&pageSize=40&apiKey=d51feed9d99647708454fc5e0caa8a75")){
            view.setText(getText(R.string.top_news)+" - "+getText(R.string.country_english));
        }

        if (newsSources != null) {
            if (url.equals("https://newsapi.org/v2/top-headlines?sources=" + newsSources.toLowerCase() + "&apiKey=d51feed9d99647708454fc5e0caa8a75")) {
                sourcesForHeader = newsSources.replace(",", ", ");
                view.setText(getText(R.string.top_news_sources) +  sourcesForHeader.replace("-", " "));
            }
        }

    }

    /**
     * Metodi, jolla haetaan JSON data News API:lta. Saa kelpaavan url:n urlForJSON metodin avulla.
     * Hakee JSON olioita sisältävän JSON taulukon nimeltä articles. Taulukon jokainen olio käydään
     * läpi ja tarvittavat tiedot poimitaan olioista. Tiedoilla muodostetaan uusia NewsItem olioita,
     * jotka vuorostaan lisätään newsItemList taulukkoon. Taulukon avulla luodaan uusi RecyclerViewiä
     * käyttävä adapteri.
     */
    private void parseJSON(){
        url = urlForJSON();
        System.out.println(url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("articles");
                    for (int i = 0; i <jsonArray.length(); i++){
                        JSONObject article = jsonArray.getJSONObject(i);

                        JSONObject src = article.getJSONObject("source");
                        String title = article.getString("title");
                        String description = article.getString("description");
                        if (description.equals("null")){
                            description = getString(R.string.newsitem_no_desc);
                        }
                        String publishDate = article.getString("publishedAt");
                        String imageUrl = article.getString("urlToImage");
                        String source = src.getString("name");
                        String newsUrl = article.getString("url");
                        String author = article.getString("author");
                        if (author.equals("null")){
                            author = getString(R.string.newsitem_no_author);
                        }
                        if (imageUrl.equals("null")){
                            imageUrl = "http://saveabandonedbabies.org/wp-content/uploads/2015/08/default.png";
                        }


                        newsItemList.add(new NewsItem(title, publishDate,
                                description, imageUrl, source, newsUrl, author));
                    }

                    adapter = new Adapter(MainActivity.this, newsItemList);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(MainActivity.this); // asetetaan kuuntelija itemeille

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    /**
     * Sovelluksen logiikka oikeanalaisen JSON urlin muodostamiseen käyttäjän tekemien valintojen
     * perusteella.
     * @return Palauttaa urlin JSON datasta. Välitetään parseJSON metodille.
     */
    private String urlForJSON(){

        // jos kaikki haut null (alussa)
        if (searchWord == null && country == null && categoryForJson == null && newsSources == null){
            url = "https://newsapi.org/v2/top-headlines?country=us&pageSize=40&apiKey=d51feed9d99647708454fc5e0caa8a75";
            return url;
        }

        if (newsSources != null) {
            url = "https://newsapi.org/v2/top-headlines?sources=" + newsSources.toLowerCase() + "&apiKey=d51feed9d99647708454fc5e0caa8a75";
            return url;
        }
        if (newsSources == ""){
            url = "https://newsapi.org/v2/top-headlines?country=us&pageSize=40&apiKey=d51feed9d99647708454fc5e0caa8a75";
            return url;
        }
        if (searchWord != null) {
            url = "https://newsapi.org/v2/everything?q=" + searchWord + "&pageSize=40&apiKey=d51feed9d99647708454fc5e0caa8a75";
            return url;
        }
        if (!(categoryForJson == null) && (country == null)) {
            url = "https://newsapi.org/v2/top-headlines?category=" + categoryForJson + "&country=us&pageSize=40&apiKey=d51feed9d99647708454fc5e0caa8a75";
            return url;
        }
        else if ((categoryForJson == null) && !(country == null)) {
            url = "https://newsapi.org/v2/top-headlines?country=" + country + "&pageSize=40&apiKey=d51feed9d99647708454fc5e0caa8a75";
            return url;
        }
        else if (!(categoryForJson == null) && !(country == null)) {
            url = "https://newsapi.org/v2/top-headlines?country=" + country + "&pageSize=40&category=" + categoryForJson + "&apiKey=d51feed9d99647708454fc5e0caa8a75";
            return url;
        }


        return url;
    }

    /**
     * Kuuntelija SharedPreferences muutoksille.
     * Kun käyttäjä haluaa muokata kategorioita, kutsutaan CATEGORIES = pref_category muuttujaa.
     * Käyttäjän tehtyä valinnat, päivitetään kategoria updateCategory metodin avulla, asetetaan
     * muuttujia nulliksi urlForJSON logiikkaa varten ja näytetään viesti käyttäjälle.
     * Samat toimenpiteet toistetaan kun halutaan muokata maita, COUNTRIES = pref_country.
     */
    private OnSharedPreferenceChangeListener preferencesChangeListener =
            new OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    preferencesChanged = true;

                    if (key.equals(CATEGORIES)){

                        newsSources = null; // jos valittu yksikin kategoria niin ei huomioida lähteitä
                        searchWord = null; // jos valittu yksikin kategoria niin ei huomioida hakusanaa
                        Toast.makeText(MainActivity.this,
                                R.string.category_changed,
                                Toast.LENGTH_SHORT).show();
                    }

                    else if (key.equals(COUNTRIES)){
                        updateCountry(sharedPreferences);
                        newsSources = null; // jos valittu yksikin maa niin ei huomioida lähteitä
                        searchWord = null; // jos valittu yksikin maa niin ei huomioida hakusanaa
                        Toast.makeText(MainActivity.this,
                                R.string.country_changed,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };

    /**
     * Metodi, jonka avulla välitetään JSON:sta saadut tiedot MoreNewsInfoActivity luokalle.
     * Muodostetaan Intent, joka välittää tiedot avaimien perusteella. refreshBool asetetaan falseksi,
     * koska MainActivityä ei haluta päivittää kun tarkastellaan vain tarkemmin uutista.
     * @param pos hiiren positio.
     */
    @Override
    public void onItemClick(int pos) {

        refreshBool = false;
        Intent moreIntent = new Intent(this, MoreNewsInfoActivity.class);
        NewsItem clickedItem = newsItemList.get(pos);

        moreIntent.putExtra(INTENT_DATE, clickedItem.getPublishDate());
        moreIntent.putExtra(INTENT_TITLE, clickedItem.getTitle());
        moreIntent.putExtra(INTENT_SOURCE, clickedItem.getSource());
        moreIntent.putExtra(INTENT_DESC, clickedItem.getDescription());
        moreIntent.putExtra(INTENT_IMGURL, clickedItem.getImageUrl());
        moreIntent.putExtra(INTENT_NEWSURL, clickedItem.getNewsUrl());
        moreIntent.putExtra(INTENT_AUTHOR, clickedItem.getAuthor());

        startActivity(moreIntent);
    }




    public void setNewsSources(String newsSources) {
        this.newsSources = newsSources;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }


    public String getCountry() {
        return country;
    }

    public String getUrl() {
        return url;
    }


}
