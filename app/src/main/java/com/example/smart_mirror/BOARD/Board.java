package com.example.smart_mirror.BOARD;

public class Board {
    private String Title,  Content;
    private int Image;

//    public Board(String title, String content, int image) {
//        Title = title;
//        Content = content;
//        Image = image;
//    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }
}

