package com.example.miikka.newsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Luokka Adapter toimii JSON:sta saadun datan välittäjänä MainActivityyn. Sisältää myös rajapinnan,
 * jonka avulla luodaan kuuntelija NewsItemille. Kuuntelijan avulla avataan tarkempaa tietoa
 * sisältävä uutisikkuna.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.NewsViewHolder> {

    private Context context;
    private OnItemClickListener listener; //kuuntelija rajapinnalle
    private ArrayList<NewsItem> newsItemList;

    /**
     * Rajapinta kuuntelijalle, jonka avulla avataan lisää tietoa sisältävä uutisikkuna.
     */
    public interface OnItemClickListener{
        /**
         * Metodin avulla hiiren klikkaus saadaan Adapter luokasta MainActivity luokkaan.
         *
         * @param pos hiiren positio.
         */
        void onItemClick(int pos);
    }

    /**
     * Mukautettu kuuntelijametodi, jonka avulla itse kuuntelija asetetaan rajapintaan.
     *
     * @param l kuuntelija
     */
    public void setOnItemClickListener(OnItemClickListener l){
        listener = l;
    }

    /**
     * Luokan Adapter konstruktori.
     *
     * @param c Käytettävä konteksti
     * @param list NewsItem olioita sisältävä lista
     */
    public Adapter(Context c, ArrayList<NewsItem> list){
        context = c;
        newsItemList = list;
    }

    /**
     * Ylikirjoitettu oletusmetodi.
     *
     * Luo uuden newsViewHolder olion aina tarvittaessa, kutsutaan heti kun adapteri luodaan.
     *
     * Tämän ja onBindViewHolder metodin avulla muodostetaan news_item näkymät.
     *
     * @param viewGroup
     * @param i
     * @return uusi NewsViewHolder, joka palautetaan metodille NewsViewHolder(View itemView).
     */
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, null);
        return new NewsViewHolder(view);
    }

    /**
     * Ylikirjoitettu oletusmetodi.
     *
     * Metodilla sidotaan kukin NewsViewHolder adapteriin. Jokaiselle NewsViewHolderille myös
     * kiinnitetään näkymät niiden arvoihin.
     *
     * @param newsViewHolder Olio, jonka avulla eri arvot kytketään näkymiin.
     * @param i Nykyinen NewsItem newsItemArrayList:ssä
     */
    @Override
    public void onBindViewHolder(NewsViewHolder newsViewHolder, int i) {

        NewsItem item = newsItemList.get(i);

        String imageUrl = item.getImageUrl();
        String title = item.getTitle();
        String publishDate = item.getPublishDate();
        String source = item.getSource();


        newsViewHolder.titleTextView.setText(title);
        newsViewHolder.publishDateTextView.setText(publishDate.substring(0, publishDate.indexOf('T')));
        newsViewHolder.sourceTextView.setText(source);
        newsViewHolder.thumbnailImageView.setImageResource(R.drawable.default_image);
        Picasso.with(context).load(imageUrl).fit().centerInside().into(newsViewHolder.thumbnailImageView);



    }

    /**
     * Ylikirjoitettu oletusmetodi.
     *
     * Metodin avulla varmistetaan, että adapterissa on yhtä monta newsItemiä kuin newsItemList:ssä.
     *
     * @return Palauttaa newsItem olioiden määrän newsItemList:ssä.
     */
    @Override
    public int getItemCount() {
        return newsItemList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{

        //news_item.xml:n eri näkymät
        public ImageView thumbnailImageView;
        public TextView titleTextView;
        public TextView publishDateTextView;
        public TextView sourceTextView;
        public TextView descriptionTextView;
        public TextView authorTextView;

        /**
         * Konstruktori eri View:ien asettamiseen.
         *
         * Konstruktorissa myös rakennetaan kuuntelijan koko View:lle uuden uutisikkunan avaamista
         * varten. Kuuntelija nappaa klikkauksen position ja välittää sen rajapinnalle (onItemClick).
         *
         * @param itemView Käytetään viittausten hakemiseen news_item.xml eri komponentteihin.
         *
         */
        public NewsViewHolder(View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            publishDateTextView = itemView.findViewById(R.id.publishDateTextView);
            sourceTextView = itemView.findViewById(R.id.sourceTextView);
            authorTextView = itemView.findViewById(R.id.moreAuthorTextView);
            descriptionTextView = itemView.findViewById(R.id.moreDescTextView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition(); // itemin sijainnin talennus
                        if (pos != RecyclerView.NO_POSITION){ // tarkistetaan että sijainti on kunnollinen
                            listener.onItemClick(pos);

                        }
                    }
                }
            });
        }
    }
}
