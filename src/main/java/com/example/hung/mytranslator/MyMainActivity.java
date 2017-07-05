package com.example.hung.mytranslator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.*;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/*
    IMPORTANT!! DO NOT SHARE THIS (Sensitive information)
    1. Copy jar file into library folder "libs" (MyTranslator > app > libs)
    2. Right click and select "Add as Library"
 */
public class MyMainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    // layout objects
    private TextView fromLanguage, toLanguage, resultText;
    private EditText editText;                                              // allows user to type text instead of mic
    private Button btn_translate;
    private ImageButton fromButton, toButton, swapButton, restartButton;

    // String variables
    private String id;                  // id to use translate
    private String secret;              // secret to use translate
    private String myText;
    private String translatedText = "";

    // language maps
    private Map<String, Locale> localeLanguageMap = new HashMap<>();    // android locale language
    private Map<String, Language> toLanguageMap = new HashMap<>();      // bing language list
    private Map<String, String> extraLanguageMap = new HashMap<>();     // android intent extras language list

    // variables used to speech
    private TextToSpeech tts;
    private final int RESULT_SPEECH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup variables
        id = getResources().getString(R.string.me) + '_' + getResources().getString(R.string.app_name2);
        secret = myString() + getResources().getString(R.string.app_name2) + "App";
        setObjects();
        tts = new TextToSpeech(this, this);

        /*
            The following instructions get intent from LanguageFrom and LanguageTo activities
            If intent is empty, then use default values
        */
        String temp_from = "English";
        String temp_to = "Chinese Simplified";

        Intent intent = this.getIntent();
        temp_from = intent.getStringExtra("language_from");
        temp_to = intent.getStringExtra("language_to");

        if (temp_from == null || temp_to == null){
            temp_from = "English";
            temp_to = "Chinese Simplified";
        }

        fromLanguage.setText(temp_from);
        toLanguage.setText(temp_to);
        new translator().execute();

    }

    private void setObjects(){
        fromLanguage = (TextView) findViewById(R.id.main_fromLanguage);
        toLanguage = (TextView) findViewById(R.id.main_toLanguage);
        fromButton = (ImageButton) findViewById(R.id.from_audio);
        toButton = (ImageButton) findViewById(R.id.to_audio);
        btn_translate = (Button) findViewById(R.id.button_translate);
        swapButton = (ImageButton) findViewById(R.id.swap_img);
        editText = (EditText) findViewById(R.id.main_editText);
        resultText = (TextView) findViewById(R.id.main_result);
        restartButton = (ImageButton) findViewById(R.id.return_img);

        setMaps();
        setButtons();
    }

    private void setButtons(){
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapLanguages();
            }
        });
        btn_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new translator().execute();
            }
        });
        fromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceRecord();
            }
        });
        toButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToVoice();
            }
        });
        try{
            if (myText.equals("0"))
                id = getResources().getString(R.string.me);
        }catch(Exception e) {
            secret = "denhC" + secret;
        }
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.shutdown();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    private void setMaps(){

        localeLanguageMap.put("English", Locale.ENGLISH);
        localeLanguageMap.put("Chinese Simplified", Locale.SIMPLIFIED_CHINESE);
        localeLanguageMap.put("Cantonese", new Locale("yue"));
        localeLanguageMap.put("Chinese Traditional", Locale.TRADITIONAL_CHINESE);
        localeLanguageMap.put("Korean", Locale.KOREAN);
        localeLanguageMap.put("Japanese", Locale.JAPANESE);
        localeLanguageMap.put("Malaysian", new Locale("MALAYSIAN"));
        localeLanguageMap.put("French", Locale.FRENCH);
        localeLanguageMap.put("German", Locale.GERMAN);
        localeLanguageMap.put("Dutch", new Locale("nl"));
        localeLanguageMap.put("Spanish", new Locale("SPANISH"));

        toLanguageMap.put("English", Language.ENGLISH);
        toLanguageMap.put("Chinese Simplified", Language.CHINESE_SIMPLIFIED);
        toLanguageMap.put("Chinese Traditional", Language.CHINESE_TRADITIONAL);
        toLanguageMap.put("Cantonese", Language.CHINESE_TRADITIONAL);
        toLanguageMap.put("Korean", Language.KOREAN);
        toLanguageMap.put("Japanese", Language.JAPANESE);
        toLanguageMap.put("Malaysian", Language.MALAY);
        toLanguageMap.put("French", Language.FRENCH);
        toLanguageMap.put("German", Language.GERMAN);
        toLanguageMap.put("Dutch", Language.DUTCH);
        toLanguageMap.put("Spanish", Language.SPANISH);

        extraLanguageMap.put("English", "en_GB");
        extraLanguageMap.put("Chinese Simplified", "zh");
        extraLanguageMap.put("Cantonese", "yue_HK");
        extraLanguageMap.put("Chinese Traditional", "zh");
        extraLanguageMap.put("Korean", "ko_KR");
        extraLanguageMap.put("Japanese", "ja_JP");
        extraLanguageMap.put("French", "fr_FR");
        extraLanguageMap.put("German", "de_DE");
        extraLanguageMap.put("Dutch", "nl_NL");
        extraLanguageMap.put("Spanish", "es_US");

        // save for future language support
//        extraLanguageMap.put("Malaysian", );
//        extraLanguageMap.put("English (Australia)", "en_AU");
//        extraLanguageMap.put("Vietnamese (Vietnam)", "vi_VN");
//        extraLanguageMap.put("Kurdish","ku");
//        extraLanguageMap.put("Polish (Poland)","pl_PL");
//        extraLanguageMap.put("Portuguese (Brazil)","pt_BR");
//        extraLanguageMap.put("Danish (Denmark)","da_DK");
//        extraLanguageMap.put("Tamil","ta");
//        extraLanguageMap.put("Albanian","sq");
//        extraLanguageMap.put("Welsh","cy");
//        extraLanguageMap.put("Japanese (Japan)","ja_JP");
//        extraLanguageMap.put("Turkish (Turkey)","tr_TR");
//        extraLanguageMap.put("English (United States)","en_US");
//        extraLanguageMap.put("Russian (Russia)","ru_RU");
//        extraLanguageMap.put("Indonesian (Indonesia)","in_ID");
//        extraLanguageMap.put("Hindi (India)","hi_IN");
//        extraLanguageMap.put("Cantonese (Hong Kong)","yue_HK");
//        extraLanguageMap.put("Korean (South Korea)","ko_KR");
//        extraLanguageMap.put("Bengali (Bangladesh)","bn_BD");
//        extraLanguageMap.put("Serbian","sr");
//        extraLanguageMap.put("Finnish (Finland)","fi_FI");
//        extraLanguageMap.put("English (India)","en_IN");
//        extraLanguageMap.put("Italian (Italy)","it_IT");
//        extraLanguageMap.put("Spanish (Mexico)","es_MX");
//        extraLanguageMap.put("Czech","cs");
//        extraLanguageMap.put("Catalan","ca");
//        extraLanguageMap.put("Croatian","hr");
//        extraLanguageMap.put("German (Germany)","de_DE");
//        extraLanguageMap.put("Spanish (United States)","es_US");
//        extraLanguageMap.put("Chinese (China)","zh_CH");
//        extraLanguageMap.put("Slovak","sk");
//        extraLanguageMap.put("Spanish (Spain)","es_ES");
//        extraLanguageMap.put("Thai (Thailand)","th_TH");
//        extraLanguageMap.put("Chinese (Taiwan)","zh_TW");
//        extraLanguageMap.put("Swedish (Sweden)","sv_SE");
//        extraLanguageMap.put("Swahili","sw");
//        extraLanguageMap.put("Norwegian BokmÃ¥l (Norway)","nb_NO");
//        extraLanguageMap.put("French (Belgium)","fr_BE");
//        extraLanguageMap.put("English (United Kingdom)","en_GB");
//        extraLanguageMap.put("Dutch (Netherlands)","nl_NL");
//        extraLanguageMap.put("Latin","la");
//        extraLanguageMap.put("Bosnian","bs");
//        extraLanguageMap.put("Portuguese (Portugal)","pt_PT");
//        extraLanguageMap.put("Hungarian (Hungary)","hu_HU");
//        extraLanguageMap.put("French (France)","fr_FR");
    }

    private void swapLanguages(){
        CharSequence temp = fromLanguage.getText();
        fromLanguage.setText(toLanguage.getText());
        toLanguage.setText(temp);
        Log.d("Language change!!!", toLanguage.getText().toString());
        tts.setLanguage(localeLanguageMap.get(toLanguage.getText().toString()));
    }

    private void voiceRecord()
    {
        //Specify language
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        setExtraIntentLanguage(intent, extraLanguageMap.get(fromLanguage.getText().toString()));

        try {
            startActivityForResult(intent, RESULT_SPEECH);
            editText.setText("");
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }

    private void setExtraIntentLanguage(Intent intent, String language){
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,language);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS, language);
    }

    // used to change edit text to spoken words
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    editText.setText(text.get(0));
                }
                break;
            }

        }
    }

    private void textToVoice(){
        Log.d("textToVoice", "Clicked on textToVoice");
        tts.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private class translator extends AsyncTask<Void, Void, Void> {

        String inText = editText.getText().toString();
        @Override
        protected Void doInBackground(Void... params) {
            try {
                translatedText = translate(inText);
            } catch (Exception e) {
                e.printStackTrace();
                translatedText = e.toString();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.d("Translator: ", "Translated phrase: " + translatedText);
            super.onPostExecute(result);
        }
    }

    private String translate(String inText){

        String translatedString = "";
        Language temp_lang = toLanguageMap.get(toLanguage.getText().toString());

        try {
            //using id and secret for Bing translate
            Translate.setClientId(id);
            Translate.setClientSecret(secret);

            // this is the actual translation instruction using Bing translate
            translatedString = Translate.execute(inText, temp_lang);
            resultText.setText(translatedString);

            Log.d("Translation: ", "Original phrase: " + inText);
            Log.d("Translation: ", "Translated language: " + temp_lang.toString());
            Log.d("Translation: ", "Translated phrase: " + translatedString);
        }catch (Exception e){
            Log.e("Translation: ", "Original phrase: " + inText);
            Log.e("Translation: ", "Translated language: " + temp_lang.toString());
            Log.e("Translation: ", "Translated phrase: " + translatedString);
        }
        return translatedString;
    }

    private String myString(){return "hi#001My";}

    // used to set language for tts
    @Override
    public void onInit(int status){
        Log.d("Speech", "OnInit - Status ["+status+"]");
        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speech", "Success!");
            tts.setLanguage(localeLanguageMap.get(toLanguage.getText().toString()));
        }
    }
}
