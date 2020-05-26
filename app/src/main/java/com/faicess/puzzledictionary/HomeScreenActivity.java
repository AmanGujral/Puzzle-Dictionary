package com.faicess.puzzledictionary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.CBLocation;

public class HomeScreenActivity extends AppCompatActivity {

    public static final int NUMBER_OF_WORDS = 5014;
    public static int AD_COUNT = 0;
    static List<String> words = new ArrayList<>();
    static List<String> meanings = new ArrayList<>();
    BufferedReader reader;
    ConstraintLayout constraintLayout;
    CardView quickTest,exploreDictionary,flashCards,bookmarks;
    View fullScreenView;
    Toolbar homeToolbar;
    ProgressBar homeProgressBar;
    TextSwitcher textSwitcher;
    String quotesArray[] = {
            "Education is the most powerful weapon which you can use to change the world.",
            "It’s not about ideas. It’s about making ideas happen.",
            "An investment in knowledge pays the best interest.",
            "Develop a passion for learning. If you do, you will never cease to grow.",
            "Education is the foundation upon which we build our future.",
            "The whole purpose of education is to turn mirrors into windows.",
            "There is no greater education than one that is self-driven.",
            "It always seems impossible until it's done.",
            "The past cannot be changed. The future is yet in your power.",
            "With the new day comes new strength and new thoughts.",
            "Optimism is the faith that leads to achievement.",
            "Optimism is the faith that leads to achievement.",
            "Positive anything is better than negative nothing.",
    };
    int quoteArrayIndex = 0;
    int quotesArrayLength = quotesArray.length;
    Random randomNumber = new Random();

    private Animator currentAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Chartboost.startWithAppId(this, "5bba7d92f6c3ac0bb2711b9f", "f6c2925ff0eedc0823084d11718a0b395cb469a1");
        Chartboost.onCreate(this);
        setContentView(R.layout.activity_home_screen);


        //change statusBar color
        final Window w = getWindow();
        //w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(getResources().getColor(R.color.purple2));

        //instantiate views
        homeToolbar = findViewById(R.id.homeToolbar);
        constraintLayout = findViewById(R.id.constraintLayout);
        homeProgressBar = findViewById(R.id.homeProgressBar);
        quickTest = findViewById(R.id.cardView2);
        exploreDictionary = findViewById(R.id.cardView3);
        flashCards = findViewById(R.id.cardView4);
        bookmarks = findViewById(R.id.cardView5);
        textSwitcher = findViewById(R.id.textSwitcher);


        setSupportActionBar(homeToolbar);

        if(Chartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT)){
            //show chartboost ads when ad count is equal to 2
            if(AD_COUNT == 1) {
                AD_COUNT = 0;
                Log.e("show Ad", String.valueOf(AD_COUNT));
                Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);
            }
            else {
                AD_COUNT++;
                Log.e("show Ad", String.valueOf(AD_COUNT));
            }
        }
        else {
            //cache chartboost ads
            Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
        }

        //activity start fading animation
        constraintLayout.setAlpha(0f);
        constraintLayout.setVisibility(View.VISIBLE);
        constraintLayout.animate()
                .alpha(1f)
                .setDuration(1200)
                .setListener(null);


        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {

                TextView quoteTV = new TextView(HomeScreenActivity.this);
                quoteTV.setGravity(Gravity.CENTER);
                quoteTV.setTextSize(17);
                quoteTV.setTypeface(Typeface.SERIF, Typeface.ITALIC);
                quoteTV.setTextColor(getResources().getColor(R.color.lightBlack));
                quoteTV.setMaxLines(4);

                return quoteTV;
            }
        });

        //animations for quotes fade in and fade out
        Animation in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        textSwitcher.setInAnimation(in);
        textSwitcher.setOutAnimation(out);

        textSwitcher.setCurrentText("\"" + quotesArray[randomNumber.nextInt(quotesArrayLength)] + "\"");


        //handles quote change periodically
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int randomIndex;
                do {
                    randomIndex = randomNumber.nextInt(quotesArrayLength);
                }
                while (quoteArrayIndex == randomIndex);

                quoteArrayIndex = randomIndex;
                textSwitcher.setText("\"" + quotesArray[quoteArrayIndex] + "\"");
                handler.postDelayed(this, 15000);
            }
        };
        handler.postDelayed(runnable, 15000);

        //read words and meanings from text files in background
        new BackgroundTask().execute("");


        //empty view that covers whole screen for animation
        fullScreenView = findViewById(R.id.fullScreenView);
        fullScreenView.setVisibility(View.INVISIBLE);
        fullScreenView.setBackgroundColor(getResources().getColor(R.color.white));



        //listener for quickTest button
        quickTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quickTest.setClickable(false);
                Intent quickTestActivityIntent = new Intent(getApplicationContext(), QuickTestActivity.class);
                w.setStatusBarColor(getResources().getColor(R.color.pink));
                textSwitcher.setVisibility(View.INVISIBLE);
                ZoomView(quickTest, getResources().getColor(R.color.pink), quickTestActivityIntent);
            }
        });



        //listener for exploreDictionary button
        exploreDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dictionaryActivityIntent = new Intent(getApplicationContext(), ExploreDictionaryActivity.class);
                exploreDictionary.setClickable(false);
                w.setStatusBarColor(getResources().getColor(R.color.orange));
                textSwitcher.setVisibility(View.INVISIBLE);
                ZoomView(exploreDictionary, getResources().getColor(R.color.orange), dictionaryActivityIntent);
            }
        });



        //listener for flashCards button
        flashCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent flashCardsActivityIntent = new Intent(getApplicationContext(), FlashCardsActivity.class);
                flashCards.setClickable(false);
                w.setStatusBarColor(getResources().getColor(R.color.green));
                textSwitcher.setVisibility(View.INVISIBLE);
                ZoomView(flashCards, getResources().getColor(R.color.green), flashCardsActivityIntent);
            }
        });



        //listener for bookmarks button
        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookmarksActivityIntent = new Intent(getApplicationContext(), BookmarksActivity.class);
                bookmarks.setClickable(false);
                w.setStatusBarColor(getResources().getColor(R.color.blue));
                textSwitcher.setVisibility(View.INVISIBLE);
                ZoomView(bookmarks, getResources().getColor(R.color.blue), bookmarksActivityIntent);
            }
        });

    }



    //parameters --> sourceView => view which has to be enlarged
    //color => the background color of the sourceView
    //intent => after animation which activity to open
    private void ZoomView(final View sourceView, int color, final Intent intent){
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null){
            currentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in view.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the sourceView,
        // and the final bounds are the global visible rectangle of the fullScreenView.
        // Also set the fullScreenView's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        sourceView.getGlobalVisibleRect(startBounds);
        fullScreenView.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        //vertical scale for zooming vertically
        final float verticalScale = (float) startBounds.height() / finalBounds.height();

        //horizontal scale for zooming horizontally
        final float horizontalScale = (float) startBounds.width() / finalBounds.width();

        // Hide the sourceView and all other views and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // sourceView.
        sourceView.setVisibility(View.INVISIBLE);
        findViewById(R.id.mainGridLayout).setVisibility(View.INVISIBLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        fullScreenView.setVisibility(View.VISIBLE);
        fullScreenView.setBackgroundColor(color);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        fullScreenView.setPivotX(0f);
        fullScreenView.setPivotY(0f);

        // Construct and run the parallel animation of the five translation, scale and
        // alpha properties (X, Y, SCALE_X, SCALE_Y and ALPHA).
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(fullScreenView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(fullScreenView, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(fullScreenView, View.SCALE_X, horizontalScale, 1f))
                .with(ObjectAnimator.ofFloat(fullScreenView, View.SCALE_Y, verticalScale, 1f))
                .with(ObjectAnimator.ofFloat(fullScreenView, View.ALPHA, 0.8f));

        set.setDuration(300);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
                startActivity(intent);
            }
        });
        set.start();
        currentAnimator = set;
    }



    public void ReadWords(Context context){

        List<String> list = new ArrayList<>();
        if (words.isEmpty() || words.size() != NUMBER_OF_WORDS) {
            words.clear();
            try {
                final InputStream file = context.getAssets().open("words.txt");
                reader = new BufferedReader(new InputStreamReader(file));
                String line = reader.readLine();
                while (line != null) {
                    list.add(line);
                    line = reader.readLine();
                }
                file.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            words = list;
        }
    }



    public void ReadMeanings(Context context) {

        List<String> list = new ArrayList<>();
        if (meanings.isEmpty() || meanings.size() != NUMBER_OF_WORDS) {
            try {
                final InputStream file = context.getAssets().open("meanings.txt");
                reader = new BufferedReader(new InputStreamReader(file));
                String line = reader.readLine();
                while (line != null) {
                    list.add(line);
                    line = reader.readLine();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            meanings = list;
        }
    }



    public String GetMeaning(String word){
        int index = -1;
        if (!words.isEmpty()){
            for (int i = 0; i < NUMBER_OF_WORDS; i++){
                if (words.get(i).equalsIgnoreCase(word)){
                    index = i;
                }
            }
        }
        else {
            ReadWords(this);
            GetMeaning(word);
        }
        if (!meanings.isEmpty()){
            if (index != -1) {
                return meanings.get(index);
            }
            else {
                return "Word not found!";
            }
        }
        else {
            ReadMeanings(this);
            GetMeaning(word);
        }
        return null;
    }



    public String GetWordAtIndex(int index){

        return words.get(index);

    }



    public String GetMeaningAtIndex(int index){

        return meanings.get(index);

    }


    @Override
    public void onBackPressed() {
        if (Chartboost.onBackPressed()) {
            return;
        }
        else {
            finishAffinity();
            this.finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Chartboost.onStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Chartboost.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Chartboost.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Chartboost.onDestroy(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Chartboost.onPause(this);
    }


    private class BackgroundTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            ReadWords(getApplicationContext());
            ReadMeanings(getApplicationContext());

            return null;
        }

        @Override
        protected void onPreExecute() {
            homeProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            homeProgressBar.setVisibility(View.GONE);
        }
    }
}


