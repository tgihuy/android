package models;

import java.io.Serializable;
import java.util.List;

public class Movie implements Serializable {
    private String name;
    private String description;
    private String link;
    private String image;
    private String director;
    private String releaseDate;
    private int categoryId;
    private String addedBy;
    private List<FilmCrew> filmCrews;

    public Movie() {
    }

    public Movie(String name, String description, String link, String image, String director, String releaseDate, int categoryId,String addedBy) {
        this.name = name;
        this.description = description;
        this.link = link;
        this.image = image;
        this.director = director;
        this.releaseDate = releaseDate;
        this.categoryId = categoryId;
        this.addedBy = addedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public List<FilmCrew> getFilmCrews() {
        return filmCrews;
    }

    public void setFilmCrews(List<FilmCrew> filmCrews) {
        this.filmCrews = filmCrews;
    }
    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }
}
