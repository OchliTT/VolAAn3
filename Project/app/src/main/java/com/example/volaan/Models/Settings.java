package com.example.volaan.Models;

import android.widget.ImageView;

public class Settings {
    ImageView imageview;
    String description;

    public Settings() {
    }

    public Settings(ImageView imageview, String description) {
        this.imageview = imageview;
        this.description = description;
    }

    public ImageView getImageview() {
        return imageview;
    }

    public void setImageview(ImageView imageview) {
        this.imageview = imageview;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
