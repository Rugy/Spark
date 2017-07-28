package nf.co.rugy.ignition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SongReaderActivity extends AppCompatActivity {

    private Database db = new Database(this);
    private static final String DEBUGTAG = "AHDR";
    private List<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_reader);

        songs = db.getAllSongs();

        addRerollButtonListener();

    }

    private void addRerollButtonListener() {
        final int size = songs.size();
        final TextView textView = (TextView) findViewById(R.id.song_reader_scrollView_textView);
        final TextView titleTextView = (TextView) findViewById(R.id
                .song_reader_textView_title);
        Button btn = (Button) findViewById(R.id.song_reader_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int) (Math.random() * size);
                Song song = db.getSongFromId(id);

                StringBuilder songTextFormatted = new StringBuilder();
                List<String[]> textArrayList = song.getText();

                for (String[] aStringArray : textArrayList) {
                    for (String aString : aStringArray) {
                        songTextFormatted.append(aString).append("<br>");
                    }
                    songTextFormatted.append("<br>");
                }

                textView.setText(Html.fromHtml(songTextFormatted.toString()).toString());
                titleTextView.setText("Artist: " + song.getArtist() + ", Title: " + song.getTitle
                        ());
                Toast.makeText(SongReaderActivity.this, "New Song: " + song.getTitle() + " by: "
                        + song.getArtist(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
