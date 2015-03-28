package com.example.catherinaxu.mycityfinder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewEntryDescriptionActivity extends Activity {

    private static final int GET_DESCRIPTION = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry_description);

        getActionBar().hide();

        //sets font of title
        TextView title = (TextView) findViewById(R.id.title);
        Typeface font_bold = Typeface.createFromAsset(getAssets(), "ostrich-black.ttf");
        title.setTypeface(font_bold);

        //sets font of buttons
        Typeface font_reg = Typeface.createFromAsset(getAssets(), "ostrich-regular.ttf");
        Button button1 = (Button) findViewById(R.id.record);
        button1.setTypeface(font_reg);
    }

    public void setDescription(View view) {
        EditText edittext = (EditText) findViewById(R.id.info);
        String text = edittext.getText().toString();
        if (text.equals("")) {
            Toast.makeText(this, "Please enter your description.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Entry successfully created!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("description", text);
            setResult(GET_DESCRIPTION, intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_entry_description, menu);
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
}
