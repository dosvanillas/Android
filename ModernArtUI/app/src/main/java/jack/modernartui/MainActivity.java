package jack.modernartui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import java.util.Random;

public class MainActivity extends Activity implements MoreInfo_Dialog.NoticeDialogListener {

    private final String TAG = "MainActivity";

    private final String DIALOG_TAG = "askMOMA";

    private String[] ColorList = new String[5];
    private int[] CList = new int[5];
    private Random rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rand = new Random();
        for(int i = 0; i < 5; i++) {
            randomColorRect(i);
        }

        setColors(0.0);

        ((SeekBar)findViewById(R.id.SeekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setColors(i / 1000.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((View)findViewById(R.id.Rect1)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                randomColorRect(0);
                setColors(((SeekBar)findViewById(R.id.SeekBar)).getProgress() / 1000.0);
                return false;
            }
        });
        ((View)findViewById(R.id.Rect2)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                randomColorRect(1);
                setColors(((SeekBar)findViewById(R.id.SeekBar)).getProgress() / 1000.0);
                return false;
            }
        });
        ((View)findViewById(R.id.Rect3)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                randomColorRect(2);
                setColors(((SeekBar)findViewById(R.id.SeekBar)).getProgress() / 1000.0);
                return false;
            }
        });
        ((View)findViewById(R.id.Rect4)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                randomColorRect(3);
                setColors(((SeekBar)findViewById(R.id.SeekBar)).getProgress() / 1000.0);
                return false;
            }
        });
        ((View)findViewById(R.id.Rect5)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                randomColorRect(4);
                setColors(((SeekBar)findViewById(R.id.SeekBar)).getProgress() / 1000.0);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.info:
                DialogFragment newFrag = new MoreInfo_Dialog();
                newFrag.show(getFragmentManager(), DIALOG_TAG);
                return true;
            case R.id.recolor:
                for(int i = 0; i < 5; i++) {
                    randomColorRect(i);
                }
                ((SeekBar)findViewById(R.id.SeekBar)).setProgress(0);
                setColors(0.0);
                return true;
        }
        return false;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog.getTag().compareTo(DIALOG_TAG) == 0) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.MoMA.org"));
            startActivity(intent);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Do Nothing
    }

    private void setColors(double percent) {
        setShades(percent);
        findViewById(R.id.Rect1).setBackgroundColor(CList[0]);
        findViewById(R.id.Rect2).setBackgroundColor(CList[1]);
        findViewById(R.id.Rect3).setBackgroundColor(CList[2]);
        findViewById(R.id.Rect4).setBackgroundColor(CList[3]);
        findViewById(R.id.Rect5).setBackgroundColor(CList[4]);
    }

    private void setShades(double percent) {
        CList[0] = Color.parseColor(parseStr(ColorList[0], ColorList[1], percent));
        CList[1] = Color.parseColor(parseStr(ColorList[1], ColorList[2], percent));
        CList[2] = Color.parseColor(parseStr(ColorList[2], ColorList[3], percent));
        CList[3] = Color.parseColor(parseStr(ColorList[3], ColorList[4], percent));
        CList[4] = Color.parseColor(parseStr(ColorList[4], ColorList[0], percent));
    }

    private String parseStr(String str, String str2, Double percent) {
        int temp;
        temp = (int)((1 - percent) * Integer.parseInt(str.substring(3, 5), 16) + percent * Integer.parseInt(str2.substring(3, 5), 16));
        str = str.substring(0, 3) + force2Char(Integer.toHexString(temp)) + str.substring(5);
        temp = (int)((1 - percent) * Integer.parseInt(str.substring(5, 7), 16) + percent * Integer.parseInt(str2.substring(5, 7), 16));
        str = str.substring(0, 5) + force2Char(Integer.toHexString(temp)) + str.substring(7);
        temp = (int)((1 - percent) * Integer.parseInt(str.substring(7, 9), 16) + percent * Integer.parseInt(str2.substring(7, 9), 16));
        str = str.substring(0, 7) + force2Char(Integer.toHexString(temp));
        return str;
    }

    private String force2Char(String str) {
        if(str.length() == 1)
            return "0" + str;
        return str;
    }

    private void randomColorRect(int rectId) {
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        CList[rectId] = Color.rgb(r, g, b);
        ColorList[rectId] = "#FF" + force2Char(Integer.toHexString(r)) + force2Char(Integer.toHexString(g)) + force2Char(Integer.toHexString(b));
    }
}
