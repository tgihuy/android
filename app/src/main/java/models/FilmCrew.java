package models;

import java.util.List;

public class FilmCrew {
    private String id;
    private String name;
    private String role;
    private String image;
    private List<Movie> movieList;

    public FilmCrew() {
    }

    public FilmCrew(String id, String name, String role, String image, List<Movie> movieList) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.image = image;
        this.movieList = movieList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }
}
