package keonheelee.github.io.searchmovie.data;

public class Movie {
    private String imageUrl;
    private String title;
    private int userRating;
    private int year;
    private String director;
    private String actor;
    private String link;

    public Movie(String imageUrl, String title, int userRating, int year,
                 String director, String actor, String link){
        this.imageUrl = imageUrl;
        this.title = title;
        this.userRating = userRating;
        this.year = year;
        this.director = director;
        this.actor = actor;
        this.link = link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getUserRating(){
        return userRating;
    }

    public int getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getActor() {
        return actor;
    }

    public String getLink() {
        return link;
    }
}
