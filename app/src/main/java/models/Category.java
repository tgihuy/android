package models;

import java.util.List;

public class Category {
    private String name;
    private List<Movie> movies;

    public Category() {
    }

    public Category(String name, List<Movie> movies) {
        this.name = name;
        this.movies = movies;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
