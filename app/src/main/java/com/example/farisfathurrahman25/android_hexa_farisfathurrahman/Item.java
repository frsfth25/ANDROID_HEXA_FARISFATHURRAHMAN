package com.example.farisfathurrahman25.android_hexa_farisfathurrahman;

public class Item
{
    private String name;
    private long salary;
    private String imageURL;

    public Item() {
    }

    public Item(String name, long salary, String imageURL) {
        this.name = name;
        this.salary = salary;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
