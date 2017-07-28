package nf.co.rugy.ignition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class StartMenuActivity extends AppCompatActivity {

    public static final String DEBUGTAG = "AHD";
    public RequestQueue queue;
    private Database db = new Database(this);
    private int startingYear = 1995;
    private int endingYear = 2016;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);
        queue = Volley.newRequestQueue(this);

        //translateTexts();

        addStartButtonListener();
    }

    private void translateTexts() {
        List<Song> songs = db.getAllSongs();
        final List<String[]> textArrayList = songs.get(0).getText();
        final String url = "https://translate.yandex.com/?lang=en-de&text=";

        new Thread(new Runnable() {
            @Override
            public void run() {
                translateSong(textArrayList, url);
            }
        }).start();
    }

    private void translateSong(List<String[]> textArrayList, String url) {
        StringBuilder wordsSb = new StringBuilder();
        for (String[] aStringArray : textArrayList) {
            for (String aString : aStringArray) {
                String[] words = aString.split(" ");
                for (String aWord : words) {
                    wordsSb.append(aWord).append("%20");
                    Log.d(DEBUGTAG, wordsSb.toString());

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url +
                            "Test%20Please%20work",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(DEBUGTAG, "Got Response: " + response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(DEBUGTAG, error.getMessage());
                        }
                    });

                    queue.add(stringRequest);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void addStartButtonListener() {
        Button btn = (Button) findViewById(R.id.start_menu_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartMenuActivity.this, SongReaderActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getCharts() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                int year = startingYear;
                while (year <= endingYear) {
                    final int searchYear = year;
                    String url = "http://www.chartsurfer" +
                            ".de/musik/single-charts-deutschland/jahrescharts/hits-" + year +
                            "-2x1.html";

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    getSongs(response, searchYear);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(DEBUGTAG, error.getMessage());
                        }
                    });

                    queue.add(stringRequest);
                    year++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void getSongs(String htmlString, int year) {
        Document doc = Jsoup.parse(htmlString);

        Elements artists = doc.getElementsByClass("artist");
        Elements titles = doc.getElementsByClass("title");

        Song[] songs = new Song[artists.size()];

        for (int i = 0; i < artists.size() && i < titles.size(); i++) {
            String artist = artists.get(i).text();
            String title = titles.get(i).text();

            songs[i] = new Song(artist, title, year, i + 1, null);

            if (db.getSongFromTitle(title) == null) {
                getLyricsHtml(artist, title, songs[i]);
                Log.d(DEBUGTAG, String.format("Artist: %s, Title: %s, Year: %d, Rank: %d", artist,
                        title, songs[i].getYear(), songs[i].getPlace()));
            }
        }
    }

    private String[] getLyricsHtml(String artist, String title, final Song song) {
        String specialCharacters = ".'()$?";

        String artistFormated = artist;
        if (artistFormated.contains("vs.")) {
            artistFormated = artistFormated.substring(0, artistFormated.indexOf("vs.") - 1);
        }
        if (artistFormated.contains("Vs.")) {
            artistFormated = artistFormated.substring(0, artistFormated.indexOf("Vs.") - 1);
        }
        if (artistFormated.contains("feat.")) {
            artistFormated = artistFormated.substring(0, artistFormated.indexOf("feat.") - 1);
        }
        if (artistFormated.contains("Feat.")) {
            artistFormated = artistFormated.substring(0, artistFormated.indexOf("Feat.") - 1);
        }
        if (artistFormated.startsWith("The")) {
            artistFormated = artistFormated.substring(artistFormated.indexOf("The") + 4,
                    artistFormated.length());
        }
        if (artistFormated.contains("&")) {
            artistFormated = artistFormated.substring(0, artistFormated.indexOf("&") - 1);
        }
        for (int i = 0; i < specialCharacters.length(); i++) {
            artistFormated = artistFormated.replace(Character.toString(specialCharacters.charAt
                    (i)), "");
        }
        String[] artistSplit = artistFormated.split(" ");

        String titleFormated = title;
        for (int i = 0; i < specialCharacters.length(); i++) {
            titleFormated = titleFormated.replace(Character.toString(specialCharacters.charAt(i))
                    , "");
        }
        String[] titleSplit = titleFormated.split(" ");

        String domain = "http://www.metrolyrics.com/";
        StringBuilder parameter = new StringBuilder();
        for (String aTitleSplit : titleSplit) {
            parameter.append(aTitleSplit.toLowerCase()).append("-");
        }
        parameter.append("lyrics-");
        for (int i = 0; i < artistSplit.length; i++) {
            parameter.append(artistSplit[i].toLowerCase());
            if (i != artistSplit.length - 1) {
                parameter.append("-");
            }
        }
        parameter.append(".html");
        Log.d(DEBUGTAG, domain + parameter.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, domain + parameter
                .toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<String[]> versesList = getText(response);
                        if (versesList != null) {
                            song.setText(versesList);
                            db.storeSong(song);
                        } else {
                            Log.d(DEBUGTAG, "No Lyrics for: " + song.getArtist() + ", " + song
                                    .getTitle() + " found");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(DEBUGTAG, "No Response For: " + song.getArtist() + ", " + song.getTitle());
            }
        });

        queue.add(stringRequest);

        return null;
    }

    private List<String[]> getText(String htmlString) {
        List<String[]> versesList = null;
        try {
            versesList = new ArrayList<>();
            Document doc = Jsoup.parse(htmlString);

            Elements verses = doc.getElementsByClass("verse");
            for (int i = 0; i < verses.size(); i++) {
                String[] verseSplit = verses.get(i).text().split("<br>");
                versesList.add(verseSplit);
            }
        } catch (Exception e) {
            Log.d(DEBUGTAG, e.getMessage());
        }

        return versesList;
    }
}
