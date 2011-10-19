package ru.o2genum.howtosay;

/**
 * Word search activity
 *
 * @author Andrey Moiseev
 */

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import ru.o2genum.forvo.Pronunciation;
import ru.o2genum.forvo.Word;
import ru.o2genum.forvo.WordAndPronunciation;

import com.commonsware.cwac.endless.EndlessAdapter;

import java.util.List;

public class WordSearchActivity extends BaseActivity {

    private String query;
    private Word word;
    private EndlessWordAdapter endlessWordAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        // Activity was launched with ACTION_SEARCH action?
        // Let's perform search and do everything we need.
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            word = new Word(query);
            setTitle(getString(R.string.searching_for, query));

            WordAdapter wordAdapter = new WordAdapter(this, 0);
            // List view - our key UI element
            ListView lv = new ListView(this);
            endlessWordAdapter = new EndlessWordAdapter(this, wordAdapter,
                        R.layout.pendingview);
            lv.setAdapter(endlessWordAdapter);
            setView(lv);
            registerForContextMenu(lv);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                    EndlessWordAdapter a = (EndlessWordAdapter)
                        parent.getAdapter();
                    if(!(a.getItemViewType(position) == 
                        Adapter.IGNORE_ITEM_VIEW_TYPE)) {
                            playSound(
                                ((WordAndPronunciation) a.getItem(position))
                                .getPronunciation()
                                .getAudioURL(Pronunciation.AudioFormat.MP3)
                                .toString());
                    } else {
                        // Pending view was clicked
                    }
                }
            });

        }
    }

    class WordAdapter extends ArrayAdapter<WordAndPronunciation> {

        public WordAdapter(Context context, int resId) {
            super(context, resId);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
            convertView = (View) inflater.inflate(
                    android.R.layout.simple_list_item_2, null);
            }
            TextView textView = (TextView) convertView.findViewById(
                    android.R.id.text1);
            TextView textView2 = (TextView) convertView.findViewById(
                    android.R.id.text2);
            textView.setText((CharSequence) getItem(position).getWord().
                    getOriginal());
            int numPronunciations = getItem(position).getWord()
                .getPronunciationsNumber();
            textView2.setText((CharSequence) getLocalizedLanguageName(
                    getItem(position).getPronunciation().getLanguage()
                    .getLanguageName()) + ", " +
                    String.format(getResources()
                        .getQuantityString(R.plurals.pronunciation_s,
                        numPronunciations), numPronunciations));
            return convertView;
        }

    }

    class EndlessWordAdapter extends EndlessAdapter {

        List<WordAndPronunciation> cachedResults;

        public EndlessWordAdapter(Context context, ListAdapter adapter,
                int id) {
            super(context, adapter, id);
        }

        @Override
        protected boolean cacheInBackground() throws Exception {
            final int PAGE = 25;
            cachedResults = word.searchPronouncedWords(null, PAGE,
                    getWrappedAdapter().getCount() / PAGE + 1);
            return !(cachedResults.size() < PAGE);
        }

        @Override
        protected void appendCachedData() {
            WordAdapter a = (WordAdapter) getWrappedAdapter();
            for(WordAndPronunciation wap : cachedResults) {
                a.add(wap);
            }
        }

        @Override
        protected boolean onException(View pendingView, Exception ex) {
            toastException(ex);
            return false;
        }
    }

    @Override
    public void doSearch(String query) {
        doWordSearch(query);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.word_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)
            item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.search_pronunciations:
                doPronunciationSearch(((WordAndPronunciation)
                    endlessWordAdapter.getItem(info.position)).getWord()
                    .getOriginal());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
