package com.faicess.puzzledictionary;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
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

public class ExploreDictionaryActivity extends AppCompatActivity{

    public static final String SHARED_PREFS_NAME = "BOOKMARKS";
    DictionaryItemAdapter dictionaryItemAdapter;
    RecyclerView dictionaryRecyclerView;
    HomeScreenActivity homeScreenActivityObject;
    Button bookmarkBtn;
    Context context;
    List<String> bookmarkList = new ArrayList<>();
    private SearchView searchView;

    CoordinatorLayout coordinateLayout;
    Toolbar exploreTabToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_dictionary);


        exploreTabToolbar = findViewById(R.id.exploreTabToolbar);
        setSupportActionBar(exploreTabToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        //change statusBar color
        Window w = getWindow();
        /*//hide status bar
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(getResources().getColor(R.color.orange));


        coordinateLayout = findViewById(R.id.exploreCoordinatorLayout);
        homeScreenActivityObject = new HomeScreenActivity();
        context = getApplicationContext();



        //set the recyclerview
        dictionaryRecyclerView = findViewById(R.id.dictionaryRV);
        dictionaryRecyclerView.setHasFixedSize(true);
        dictionaryRecyclerView.addOnScrollListener(new MyScrollListener(this) {
            @Override
            public void onMoved(int distance) {
                //do nothing
            }
        });
        dictionaryRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        //Get all the words(their positions) stored in the shared preferences for bookmarks
        bookmarkList = getArray();


        //initialize adapter
        dictionaryItemAdapter = new DictionaryItemAdapter(HomeScreenActivity.words,
                HomeScreenActivity.meanings, new DictionaryItemAdapter.ItemClickListener() {

            // DO ALL THIS WHEN CLICKED ON ADAPTER ITEM

            //listener for item clicks for recyclerview
            @Override
            public void OnItemClicked(View view, final int itemPosition) {

                //get the viewholder at position itemPosition
                RecyclerView.ViewHolder viewHolder = dictionaryRecyclerView.findViewHolderForAdapterPosition(itemPosition);

                //get the bookmarkButton in that particular viewholder
                bookmarkBtn = viewHolder.itemView.findViewById(R.id.bookmarkButton);

                //check if the bookmark button is visible or not
                if (bookmarkBtn.getVisibility() == View.GONE){
                    bookmarkBtn.setVisibility(View.VISIBLE);


                    //listener for bookmarkButton clicks
                    bookmarkBtn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            String word = homeScreenActivityObject.GetWordAtIndex(itemPosition);

                            //Check if the word is already in bookmarks
                            if (!bookmarkList.contains(String.valueOf(itemPosition))){
                                bookmarkList.add(String.valueOf(itemPosition));
                                Snackbar snackbar = Snackbar.make(coordinateLayout, word + " added to Bookmarks!", Snackbar.LENGTH_SHORT);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(getResources().getColor(R.color.orange));
                                snackbar.setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        bookmarkList.remove(String.valueOf(itemPosition));
                                        Snackbar snackbar = Snackbar.make(coordinateLayout, homeScreenActivityObject.GetWordAtIndex(itemPosition)
                                                + " removed from Bookmarks!", Snackbar.LENGTH_SHORT);
                                        View sbView = snackbar.getView();
                                        sbView.setBackgroundColor(getResources().getColor(R.color.orange));
                                        snackbar.show();
                                    }
                                });
                                snackbar.setActionTextColor(getResources().getColor(R.color.white));
                                snackbar.show();
                                bookmarkBtn.setVisibility(View.GONE);
                            }
                            else {
                                Snackbar snackbar = Snackbar.make(coordinateLayout, word + " is already in Bookmarks!", Snackbar.LENGTH_SHORT);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(getResources().getColor(R.color.orange));
                                snackbar.show();
                                bookmarkBtn.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else if (bookmarkBtn.getVisibility() == View.VISIBLE){
                    bookmarkBtn.setVisibility(View.GONE);
                }
            }
        });

        dictionaryRecyclerView.setAdapter(dictionaryItemAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.explore_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.explore_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconifiedByDefault(true);


        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                dictionaryItemAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                dictionaryItemAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.explore_search:
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        else {
            finishAffinity();
            this.finish();
            Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        saveArray();
        super.onStop();
    }


    //function to save the updated bookmark list to shared preferences
    public boolean saveArray() {
        SharedPreferences sp = this.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.clear();
        Set<String> set = new HashSet<String>();
        set.addAll(bookmarkList);
        mEdit1.putStringSet("list", set);
        return mEdit1.commit();
    }

    public ArrayList<String> getArray() {
        SharedPreferences sp = this.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);

        //NOTE: if shared preference is null, the method return empty Hashset and not null
        Set<String> set = sp.getStringSet("list", new HashSet<String>());

        return new ArrayList<String>(set);
    }
}

