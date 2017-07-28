package nf.co.rugy.ignition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreas on 05.07.2017.
 */

public class Song {

    private int Id;
    private String artist;
    private String title;
    private int year;
    private int place;
    private List<String[]> text;
    private List<String[]> textGerman;

    public Song() {

    }

    public Song(String artist, String title, int year, int place, List<String[]> text) {
        this.artist = artist;
        this.title = title;
        this.year = year;
        this.place = place;
        this.text = text;
    }

    public int getID() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public List<String[]> getText() {
        return text;
    }

    public void setText(List<String[]> text) {
        this.text = text;
    }

    public String getStringText() {
        StringBuilder stringTextSb = new StringBuilder();

        for (String[] aStringArray : text) {
            for (String aString : aStringArray) {
                stringTextSb.append(aString + "<br>");
            }
            stringTextSb.append("<verse>");
        }

        return stringTextSb.toString();
    }

    public void setStringText(String stringText) {
        List<String[]> versesList = new ArrayList<>();

        String[] verse = stringText.split("<verse>");
        for (String aString : verse) {
            String[] segment = aString.split("<br>");
            versesList.add(segment);
        }

        text = versesList;
    }

    public List<String[]> getTextGerman() {
        return textGerman;
    }

    public void setTextGerman(List<String[]> textGerman) {
        this.textGerman = textGerman;
    }
}
