package com.example.smart_mirror.RESULT;

public class Result {

    private String Date;
    private String HairLoss_Score;
    private String NoneHairLoss_Score;
    private String High_Grade;
    private String Middle_Grade;
    private String Low_Grade;
    private String Good_Grade ;
    private int Image;


    /*
    * 고위험 등급
    * */
    public String getHigh_Grade() {
        return High_Grade;
    }

    public void setHigh_Grade(String high_Grade) {
        High_Grade = high_Grade;
    }


    /*
    * 위험 등급
    * */
    public String getMiddle_Grade() {
        return Middle_Grade;
    }

    public void setMiddle_Grade(String middle_Grade) {
        Middle_Grade = middle_Grade;
    }


    /*
    * 경고 등급
    * */
    public String getLow_Grade() {
        return Low_Grade;
    }

    public void setLow_Grade(String low_Grade) {
        Low_Grade = low_Grade;
    }


    /*
    * 좋음 등급
    * */
    public String getGood_Grade() {
        return Good_Grade;
    }

    public void setGood_Grade(String good_Grade) {
        Good_Grade = good_Grade;
    }


    /*
    * 날짜
    * */
    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }


    /*
    * 탈모 비율
    * */
    public String getHairLoss_Score() {
        return HairLoss_Score;
    }

    public void setHairLoss_Score(String hairLoss_Score) {
        HairLoss_Score = hairLoss_Score;
    }


    /*
    * 비탈모 비율
    * */
    public String getNoneHairLoss_Score() {
        return NoneHairLoss_Score;
    }

    public void setNoneHairLoss_Score(String noneHairLoss_Score) {
        NoneHairLoss_Score = noneHairLoss_Score;
    }


    /*
    * 진단 사진
    * */
    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }
}
