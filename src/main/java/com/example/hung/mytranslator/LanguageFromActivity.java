package com.example.hung.mytranslator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

public class LanguageFromActivity extends AppCompatActivity {
    private ViewFlipper viewFlipper;
    private String language_choice = "English";
    private float y1 = 0;               // used to determine swipe direction

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_from);

        Button button_cht, button_chs, button_yue, button_ja, button_ko, button_ms,button_en,
                button_fr, button_es,button_nl;

        // viewflipper handling
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper_language_from);

        // buttons handling
        button_cht = (Button) findViewById(R.id.button_from_cht);
        button_chs = (Button) findViewById(R.id.button_from_chs);
        button_yue = (Button) findViewById(R.id.button_from_yue);
        button_ja = (Button) findViewById(R.id.button_from_ja);
        button_ko = (Button) findViewById(R.id.button_from_ko);
        //button_ms = (Button) findViewById(R.id.button_from_ms);
        button_en = (Button) findViewById(R.id.button_from_en);
        button_fr = (Button) findViewById(R.id.button_from_fr);
        button_es = (Button) findViewById(R.id.button_from_es);
        button_nl = (Button) findViewById(R.id.button_from_nl);

        setButtons(button_cht);
        setButtons(button_chs);
        setButtons(button_yue);
        setButtons(button_ja);
        setButtons(button_ko);
        //setButtons(button_ms);
        setButtons(button_en);
        setButtons(button_fr);
        setButtons(button_es);
        setButtons(button_nl);
    }

    private void setButtons(Button button){
        final String button_text = button.getText().toString();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                language_choice = button_text;
                Log.i("Choice of language", language_choice);
                runLanguageTo();
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event){
        float y2, y3;
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                y2 = event.getY();
                y3 = y1 - y2;
                switchImage(y3);
                break;
            default:
                // do nothing
        }
        return false;
    }

    private void switchImage(float number){
        // move animations towards bottom
        if (number > 0){
            if (viewFlipper.getDisplayedChild() == viewFlipper.getChildCount() - 1)
                return;
            viewFlipper.setInAnimation(this, R.anim.slide_in_from_bottom);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_to_top);
            viewFlipper.showNext();
        }

        // move animation towards top
        else if (number < 0){
            if (viewFlipper.getDisplayedChild() == 0)
                return;
            viewFlipper.setInAnimation(this, R.anim.slide_in_from_bottom);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_to_top);
            viewFlipper.showPrevious();
        }
    }

    private void runLanguageTo(){
        Log.d("runLanguageTo", "Moving to LanguageTo");
        Log.d("Language Choice", language_choice);

        Intent intent = new Intent(LanguageFromActivity.this, LanguageToActivity.class);
        intent.putExtra("language_from", language_choice);
        intent.setType("text/plain");

        // prevents new stacks of activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Log.d("in Language From", "Intent contains" + intent.getStringExtra("language_from"));
        startActivity(intent);

    }
}
