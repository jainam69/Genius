package com.example.genius.Model;

import java.io.Serializable;

public class CategoryModel implements Serializable {
    public long getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(long categoryID) {
        CategoryID = categoryID;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public long CategoryID;
    public String Category;

    public CategoryModel() {
    }

    public CategoryModel(long categoryID) {
        CategoryID = categoryID;
    }
}
