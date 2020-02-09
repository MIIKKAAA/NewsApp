package com.example.miikka.newsapp;

/**
 * Luokka, muodostaa ohjelman uutiselementtien datan. Luokan olioita kytketään RecyclerViewiin.
 */
public class NewsItem  {

    private String title;
    private String publishDate;
    private String imageUrl;
    private String description;
    private String source;
    private String newsUrl;
    private String author;

    /**
     * Konstruktori luokalle.
     * @param titl Uutisen otsikko
     * @param date Uutisen julkaisuajankohta
     * @param desc Uutisen kuvaus
     * @param imgUrl Uutisen kuvan url
     * @param src Uutisen lähde
     * @param nwsUrl Uutiseen vievä url
     * @param auth Uutisen julkaisija
     */
    public NewsItem (String titl, String date,
                     String desc, String imgUrl, String src, String nwsUrl, String auth){
        title = titl;
        publishDate = date;
        imageUrl = imgUrl;
        description = desc;
        source = src;
        newsUrl = nwsUrl;
        author = auth;

    }

    // getterit
    public String getTitle(){
        return title;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public String getAuthor() {
        return author;
    }
}
