package com.faicess.puzzledictionary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class FlashCardsActivity extends AppCompatActivity {

    public static final String FLASHCARD_LIST = "FLASHCARD_LIST";
    public static final String CURRENT_LIST_INDEX = "CURRENT_LIST_INDEX";
    ConstraintLayout constraintLayout;
    Toolbar flashCardTabToolbar;
    TextView wordFlashCardTV, meaningFlashCardTV, flashCardIndexTV;
    ImageView bookmarkFlag;

    static HomeScreenActivity homeScreenActivityObject;

    static List<String> flashCardList;
    public static int currentListIndex = 0;
    int randomIndex;

    List<String> bookmarkList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards);

        constraintLayout = findViewById(R.id.flashCardConstraintLayout);
        flashCardTabToolbar = findViewById(R.id.flashCardTabToolbar);
        wordFlashCardTV = findViewById(R.id.wordFlashCardTV);
        meaningFlashCardTV = findViewById(R.id.meaningFlashCardTV);
        flashCardIndexTV = findViewById(R.id.flashCardIndexTV);
        bookmarkFlag = findViewById(R.id.flashCardBookmarkFlag);


        setSupportActionBar(flashCardTabToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        //change statusBar color
        Window w = getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(getResources().getColor(R.color.green));

        homeScreenActivityObject = new HomeScreenActivity();
        flashCardList = new ArrayList<>();

        //load the bookmarks in a list to check for bookmarks
        bookmarkList = getArray(BookmarksActivity.BOOKMARKS);


        //handles initial setup on activity startup
        SetupFlashCards();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.flash_card_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.resetButton:
                ResetFlashCards();
                return true;

            case R.id.bookmarkButton:
                if(bookmarkFlag.getVisibility() == View.INVISIBLE){
                    bookmarkFlag.setVisibility(View.VISIBLE);
                    bookmarkList.add(flashCardList.get(currentListIndex));

                    Snackbar snackbar = Snackbar.make(constraintLayout,
                            "'" + homeScreenActivityObject.GetWordAtIndex(Integer.parseInt(flashCardList.get(currentListIndex))) + "'"
                                    + " added to bookmarks.", Snackbar.LENGTH_SHORT);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(getResources().getColor(R.color.green));
                    snackbar.show();
                }
                else{
                    Snackbar snackbar = Snackbar.make(constraintLayout,
                            "'" + homeScreenActivityObject.GetWordAtIndex(Integer.parseInt(flashCardList.get(currentListIndex))) + "'"
                                    + " is already bookmarked.", Snackbar.LENGTH_SHORT);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(getResources().getColor(R.color.green));
                    snackbar.show();
                }
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gdt.onTouchEvent(event);
    }

    private final GestureDetector gdt = new GestureDetector(new GestureListener());

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private final int SWIPE_MIN_DISTANCE = 120;
        private final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                // ============== Right to left, your code here ==============

                SwipeLeft();
                return true;
            }

            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                // ============== Left to right, your code here ==============

                SwipeRight();
                return true;
            }

            /*if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) >     SWIPE_THRESHOLD_VELOCITY) {
                // Bottom to top, your code here
                return true;
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE &&    Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Top to bottom, your code here
                return true;
            }*/
            else {
                return false;
            }
        }
    }



    public void SetupFlashCards(){

        flashCardList = getArray(FLASHCARD_LIST);
        currentListIndex = LoadInt();

        //if there is no saved current index, then generate a new random one
        if(currentListIndex == -1){
            currentListIndex = 0;
            flashCardList.add(currentListIndex, String.valueOf(new Random().nextInt(HomeScreenActivity.NUMBER_OF_WORDS)));
        }

        //update the bookmark flag
        if(bookmarkList.contains(flashCardList.get(currentListIndex))){
            bookmarkFlag.setVisibility(View.VISIBLE);
        }
        else {
            bookmarkFlag.setVisibility(View.INVISIBLE);
        }

        //set word & meaning on card on activity startup
        wordFlashCardTV.setText(homeScreenActivityObject.GetWordAtIndex(Integer.parseInt(flashCardList.get(currentListIndex))));
        meaningFlashCardTV.setText(homeScreenActivityObject.GetMeaningAtIndex(Integer.parseInt(flashCardList.get(currentListIndex))));
        flashCardIndexTV.setText(String.valueOf(currentListIndex + 1) + "/" + flashCardList.size());
    }


    public void ResetFlashCards(){

        flashCardList.clear();
        currentListIndex = -1;

        saveArray(FLASHCARD_LIST, flashCardList);
        SaveInt(currentListIndex);

        //initialize new flash cards with updated data
        SetupFlashCards();

    }


    public void SwipeLeft(){

        //if current list index is the last index of the list
        if(currentListIndex == flashCardList.size() - 1){

            do {
                //generate a random index from database
                randomIndex = new Random().nextInt(HomeScreenActivity.NUMBER_OF_WORDS);
            }
            //check if the generated index has already been generated before
            while (flashCardList.contains(String.valueOf(randomIndex)));

            //add the new generated index to the list and update the current list index
            ++currentListIndex;
            flashCardList.add(currentListIndex, String.valueOf(randomIndex));

            //update the bookmark flag
            if(bookmarkList.contains(flashCardList.get(currentListIndex))){
                bookmarkFlag.setVisibility(View.VISIBLE);
            }
            else {
                bookmarkFlag.setVisibility(View.INVISIBLE);
            }

            //set the word & meaning on the card
            wordFlashCardTV.setText(homeScreenActivityObject.GetWordAtIndex(Integer.parseInt(flashCardList.get(currentListIndex))));
            meaningFlashCardTV.setText(homeScreenActivityObject.GetMeaningAtIndex(Integer.parseInt(flashCardList.get(currentListIndex))));
            flashCardIndexTV.setText(String.valueOf(currentListIndex + 1) + "/" + flashCardList.size());
        }
        else{

            //update the current list index
            ++currentListIndex;

            //update the bookmark flag
            if(bookmarkList.contains(flashCardList.get(currentListIndex))){
                bookmarkFlag.setVisibility(View.VISIBLE);
            }
            else {
                bookmarkFlag.setVisibility(View.INVISIBLE);
            }

            //set the word & meaning on the card
            wordFlashCardTV.setText(homeScreenActivityObject.GetWordAtIndex(Integer.parseInt(flashCardList.get(currentListIndex))));
            meaningFlashCardTV.setText(homeScreenActivityObject.GetMeaningAtIndex(Integer.parseInt(flashCardList.get(currentListIndex))));
            flashCardIndexTV.setText(String.valueOf(currentListIndex + 1) + "/" + flashCardList.size());
        }
    }




    public void SwipeRight(){

        //if the current list index is not the first index of the list
        if(currentListIndex != 0){


            //update the current list index
            --currentListIndex;

            //update the bookmark flag
            if(bookmarkList.contains(flashCardList.get(currentListIndex))){
                bookmarkFlag.setVisibility(View.VISIBLE);
            }
            else {
                bookmarkFlag.setVisibility(View.INVISIBLE);
            }

            //set the word & meaning on the card
            wordFlashCardTV.setText(homeScreenActivityObject.GetWordAtIndex(Integer.parseInt(flashCardList.get(currentListIndex))));
            meaningFlashCardTV.setText(homeScreenActivityObject.GetMeaningAtIndex(Integer.parseInt(flashCardList.get(currentListIndex))));
            flashCardIndexTV.setText(String.valueOf(currentListIndex + 1) + "/" + flashCardList.size());
        }
        else{

            Snackbar snackbar = Snackbar.make(constraintLayout, "Swipe Left for more cards.", Snackbar.LENGTH_SHORT);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(getResources().getColor(R.color.green));
            snackbar.show();
        }
    }




    public void saveArray(String sharedPrefName, List list) {
        SharedPreferences sp = this.getSharedPreferences(sharedPrefName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.clear();
        Set<String> set = new HashSet<String>(list);
        mEdit1.putStringSet("list", set);
        mEdit1.commit();
    }




    public ArrayList<String> getArray(String sharedPrefName) {
        SharedPreferences sp = this.getSharedPreferences(sharedPrefName, Activity.MODE_PRIVATE);

        //NOTE: if shared preference is null, the method return empty Hashset and not null
        Set<String> set = sp.getStringSet("list", new HashSet<String>());

        return new ArrayList<String>(set);
    }




    @Override
    protected void onStop() {
        saveArray(FLASHCARD_LIST, flashCardList);
        SaveInt(currentListIndex);
        saveArray(BookmarksActivity.BOOKMARKS, bookmarkList);
        super.onStop();
    }




    public void SaveInt(int value){
        SharedPreferences sp = getSharedPreferences(CURRENT_LIST_INDEX, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("current_list_index", value);
        editor.commit();
    }




    //Used to load the current index of flash card
    public int LoadInt(){
        SharedPreferences sp = getSharedPreferences(CURRENT_LIST_INDEX, Activity.MODE_PRIVATE);
        return sp.getInt("current_list_index", -1);
    }




    @Override
    public void onBackPressed() {
        finishAffinity();
        this.finish();
        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        startActivity(intent);
    }

}





