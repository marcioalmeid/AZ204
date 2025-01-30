 package com.mamflix;

public class Movie {
    private String id;
    private String title;
    private String video;
    private String thumb;

    private int year;

    public Movie() {
        this.id = java.util.UUID.randomUUID().toString(); // Garante um ID único se não for fornecido
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }
    
    public int getYear() { return year; }
    public String getThumb() { return thumb; }
    public void setThumb(String thumb) { this.thumb = thumb; }
    
    public void setYear(int year) { this.year = year; }
}