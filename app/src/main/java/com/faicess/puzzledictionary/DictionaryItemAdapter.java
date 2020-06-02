package com.faicess.puzzledictionary;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import com.faicess.puzzledictionary.HomeScreenActivity;

public class DictionaryItemAdapter extends RecyclerView.Adapter<DictionaryItemAdapter.ViewHolder> implements Filterable{

    private static List<String> words = new ArrayList<>();
    private static List<String> meanings = new ArrayList<>();
    private static List<String> wordsTemp = new ArrayList<>();
    private static List<String> meaningsTemp = new ArrayList<>();
    private static ItemClickListener itemClickListener;
    private HomeScreenActivity homeScreenActivityObject;

    int i = 1;
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    /*words = wordsTemp;
                    meanings = meaningsTemp;
                    Log.e("filter empty", String.valueOf(i++));*/
                }
                else {
                    List<String> filteredList = new ArrayList<>();
                    meanings.clear();
                    for (String row : words){

                        if(row.toLowerCase().contains(charString.toLowerCase())){
                            Log.e("filter not empty", String.valueOf(i++));
                            filteredList.add(row);
                            meanings.add(homeScreenActivityObject.GetMeaning(row));
                        }
                    }
                    wordsTemp = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = wordsTemp;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                words = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ItemClickListener{
        void OnItemClicked(View view, int itemPosition);
    }

    public DictionaryItemAdapter(List<String> wordList, List<String> meaningList, ItemClickListener listener){
        words = wordList;
        meanings = meaningList;
        wordsTemp = words;
        meaningsTemp = meaningList;
        DictionaryItemAdapter.itemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dictionary_item_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.word.setText(words.get(position));
        holder.meaning.setText(meanings.get(position));
    }

    @Override
    public int getItemCount() {
        /*if(words != null) {
            return words.size();
        }
        else return 5;*/
        return words.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView word, meaning;
        Button bookmarkBtn, removeBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            word = itemView.findViewById(R.id.wordTextView);
            meaning = itemView.findViewById(R.id.meaningTextView);
            bookmarkBtn = itemView.findViewById(R.id.bookmarkButton);
            removeBtn = itemView.findViewById(R.id.removeButton);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.OnItemClicked(view, getAdapterPosition());
        }
    }
}
