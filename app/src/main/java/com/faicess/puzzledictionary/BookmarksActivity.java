package com.faicess.puzzledictionary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BookmarksActivity extends AppCompatActivity {

    public static final String BOOKMARKS = "BOOKMARKS";
    DictionaryItemAdapter dictionaryItemAdapter;
    RecyclerView dictionaryRecyclerView;
    HomeScreenActivity homeScreenActivityObject;
    Context context;
    Button removeBtn;
    List<String> bookmarkList = new ArrayList<>();

    List<String> words = new ArrayList<>();
    List<String> meanings = new ArrayList<>();

    CoordinatorLayout coordinateLayout;
    Toolbar bookmarkTabToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);


        //change statusBar color
        Window w = getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(getResources().getColor(R.color.blue));

        coordinateLayout = findViewById(R.id.constraintLayout3);
        bookmarkTabToolbar = findViewById(R.id.bookmarkTabToolbar);

        setSupportActionBar(bookmarkTabToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        homeScreenActivityObject = new HomeScreenActivity();
        context = getApplicationContext();

        bookmarkList = getArray();
        SortList(bookmarkList);

        //set the recyclerview
        dictionaryRecyclerView = findViewById(R.id.bookmarksRV);
        dictionaryRecyclerView.setHasFixedSize(true);
        dictionaryRecyclerView.setLayoutManager(new LinearLayoutManager(BookmarksActivity.this));

        attachDatabaseReadListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookmarks_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.resetBookmarksButton:
                if(bookmarkList.size() > 0) {
                    bookmarkList.clear();
                    attachDatabaseReadListener();

                    Snackbar snackbar = Snackbar.make(coordinateLayout, "All Bookmarks Removed.", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(getResources().getColor(R.color.blue));
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bookmarkList = getArray();
                            attachDatabaseReadListener();
                        }
                    });
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.show();

                    return true;
                }
                else{
                    Snackbar snackbar = Snackbar.make(coordinateLayout, "No Bookmarks Found.", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(getResources().getColor(R.color.blue));
                    snackbar.show();
                    return true;
                }

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        this.finish();
        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        startActivity(intent);
    }

    public void attachDatabaseReadListener(){

        words = new ArrayList<>();
        meanings = new ArrayList<>();

        //get words and meanings whose index is stored in bookmarkList
        for (int i = 0; i < bookmarkList.size(); i++){
            words.add(homeScreenActivityObject.GetWordAtIndex(Integer.valueOf(bookmarkList.get(i))));
            meanings.add(homeScreenActivityObject.GetMeaningAtIndex(Integer.valueOf(bookmarkList.get(i))));
        }

        //initialize adapter
        dictionaryItemAdapter = new DictionaryItemAdapter(words, meanings,
                new DictionaryItemAdapter.ItemClickListener() {

                    //listener for item clicks for recyclerview
                    @Override
                    public void OnItemClicked(View view, final int itemPosition) {

                        //get the viewholder at position itemPosition
                        final RecyclerView.ViewHolder viewHolder = dictionaryRecyclerView.findViewHolderForAdapterPosition(itemPosition);

                        //get the removeButton in that particular viewholder
                        removeBtn = viewHolder.itemView.findViewById(R.id.removeButton);

                        //check if the button is visible or not
                        if (removeBtn.getVisibility() == View.GONE){
                            removeBtn.setVisibility(View.VISIBLE);

                            //listener for removeButton clicks
                            removeBtn.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    final String index = bookmarkList.get(itemPosition);
                                    String word = homeScreenActivityObject.GetWordAtIndex(Integer.valueOf(index));
                                    bookmarkList.remove(String.valueOf(index));

                                    Snackbar snackbar = Snackbar.make(coordinateLayout, "'" + word + "'" + " removed from Bookmarks!", Snackbar.LENGTH_LONG);
                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(getResources().getColor(R.color.blue));
                                    snackbar.setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            bookmarkList.add(itemPosition, index);
                                            attachDatabaseReadListener();
                                        }
                                    });
                                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                                    snackbar.show();

                                    removeBtn.setVisibility(View.GONE);
                                    viewHolder.itemView.setVisibility(View.GONE);
                                    attachDatabaseReadListener();
                                }
                            });
                        }
                        else if (removeBtn.getVisibility() == View.VISIBLE){
                            removeBtn.setVisibility(View.GONE);
                        }
                    }
                });

        dictionaryRecyclerView.setAdapter(dictionaryItemAdapter);

        //display no bookmarks found message if adapter has 0 children
        if (dictionaryItemAdapter.getItemCount() == 0){
            findViewById(R.id.noBookmarksTextView).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.noBookmarksTextView).setVisibility(View.GONE);
        }

    }

    private void SortList(List<String> bookmarkList) {
        int min;
        int temp1, temp2;

        for(int i = 0; i < bookmarkList.size(); i++){
            min = Integer.parseInt(bookmarkList.get(i));

            for(int j = i + 1; j < bookmarkList.size(); j++){
                temp1 = Integer.parseInt(bookmarkList.get(j));

                if(temp1 < min){
                    temp2 = temp1;
                    temp1 = min;
                    min = temp2;

                    bookmarkList.set(i, String.valueOf(min));
                    bookmarkList.set(j, String.valueOf(temp1));
                }
            }
        }
    }

    public boolean saveArray() {
        SharedPreferences sp = this.getSharedPreferences(BOOKMARKS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.clear();
        Set<String> set = new HashSet<String>();
        set.addAll(bookmarkList);
        mEdit1.putStringSet("list", set);
        return mEdit1.commit();
    }

    public ArrayList<String> getArray() {
        SharedPreferences sp = this.getSharedPreferences(BOOKMARKS, Activity.MODE_PRIVATE);

        //NOTE: if shared preference is null, the method return empty Hashset and not null
        Set<String> set = sp.getStringSet("list", new HashSet<String>());

        return new ArrayList<String>(set);
    }

    @Override
    protected void onStop() {
        saveArray();
        super.onStop();
    }

}
