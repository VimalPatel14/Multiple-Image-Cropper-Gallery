package com.vimal.cusromgallerywithcropper.model;

import java.io.Serializable;

public class GalleryImage implements Serializable {

    private String imagepath;
    private boolean autocrop = false;

    public GalleryImage(String imagepath, boolean autocrop) {
        this.imagepath = imagepath;
        this.autocrop = autocrop;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public boolean getAutocrop() {
        return autocrop;
    }

    public void setAutocrop(boolean autocrop) {
        this.autocrop = autocrop;
    }
}
