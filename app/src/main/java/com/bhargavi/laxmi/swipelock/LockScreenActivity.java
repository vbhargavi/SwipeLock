package com.bhargavi.laxmi.swipelock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bhargavi.laxmi.swipelock.data.ImageDataManager;

public class LockScreenActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_CHANGE_PIN = "is_change_pin";

    public static final String PIN = "com.bhargavi.laxmi.piclock.Pin";
    public static final String SHARED_PREEF = "com.bhargavi.laxmi.piclock.PREFRENCES";
    private SharedPreferences sharedPref;

    private Button clearButton;
    private ImageButton doneButton;
    private TextView createTextView;
    private View buttonsView;

    private EditText pinTxt;

    private String savedPin;

    private boolean isChangePin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        sharedPref = this.getSharedPreferences(SHARED_PREEF, Context.MODE_PRIVATE);
        savedPin = sharedPref.getString(PIN, null);

        pinTxt = (EditText) findViewById(R.id.edittext);

        clearButton = (Button) findViewById(R.id.clear);
        doneButton = (ImageButton) findViewById(R.id.button_done);
        createTextView = (TextView) findViewById(R.id.create_textview);
        buttonsView = findViewById(R.id.buttons_view);

        if (getIntent().getExtras() != null) {
            isChangePin = getIntent().getBooleanExtra(EXTRA_CHANGE_PIN, false);
        }

        if (!isChangePin) {
            getSupportLoaderManager().initLoader(500, null, this);
        } else {
            createTextView.setVisibility(View.VISIBLE);
            buttonsView.setVisibility(View.VISIBLE);
            createTextView.setText(R.string.enter_new_pin);
        }

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(pinTxt.getText())) {
                    String text = pinTxt.getText().toString();
                    text = text.substring(0, text.length() - 1);
                    pinTxt.setText(text);
                    pinTxt.setSelection(text.length());
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(pinTxt.getText()) && pinTxt.getText().length() >= 4) {
                    String text = pinTxt.getText().toString();

                    if (savedPin == null || isChangePin) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(PIN, text);
                        editor.commit();
                        if (!isChangePin) {
                            Intent intent = new Intent(LockScreenActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    } else if (savedPin.equals(text)) {
                        Intent intent = new Intent(LockScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LockScreenActivity.this);
                        alertDialogBuilder.setTitle("Wrong Pin").setMessage("Please enter the correct PIN");
                        alertDialogBuilder.setPositiveButton(getString(android.R.string.ok), null);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lock_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_0) {
            pinTxt.append("0");
        }
        else if (v.getId() == R.id.button_1) {
            pinTxt.append("1");
        }
        else if (v.getId() == R.id.button_2) {
            pinTxt.append("2");
        }
        else if (v.getId() == R.id.button_3) {
            pinTxt.append("3");
        }
        else if (v.getId() == R.id.button_4) {
            pinTxt.append("4");
        }
        else if (v.getId() == R.id.button_5) {
            pinTxt.append("5");
        }
        else if (v.getId() == R.id.button_6) {
            pinTxt.append("6");
        }
        else if (v.getId() == R.id.button_7) {
            pinTxt.append("7");
        }
        else if (v.getId() == R.id.button_8) {
            pinTxt.append("8");
        }
        else if (v.getId() == R.id.button_9) {
            pinTxt.append("9");
        }


    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.Media.SIZE};

        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        ImageDataManager.getInstance().loadData(data);
        getSupportLoaderManager().destroyLoader(500);

        if (savedPin == null) {
            createTextView.setVisibility(View.VISIBLE);
        }
        buttonsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
