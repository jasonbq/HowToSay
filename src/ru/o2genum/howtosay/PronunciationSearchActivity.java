package ru.o2genum.howtosay;

/**
 * Pronunciation search activity
 *
 * @author Andrey Moiseev
 */

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import ru.o2genum.forvo.Pronunciation;
import ru.o2genum.forvo.Word;
import ru.o2genum.forvo.User;

import com.commonsware.cwac.endless.EndlessAdapter;

import java.util.List;

public class PronunciationSearchActivity extends BaseActivity {

    private String query;
    private Word word;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        // Activity was launched with ACTION_SEARCH action?
        // Let's perform search and do everything we need.
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            word = new Word(query);
            setTitle(getString(R.string.pronunciations_of, query));

            PronunciationAdapter pronunciationAdapter =
                new PronunciationAdapter(this, 0);
            // List view - our key UI element
            ListView lv = new ListView(this);
            lv.setAdapter(new EndlessPronunciationAdapter(this,
                        pronunciationAdapter, R.layout.pendingview));
            setView(lv);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                    EndlessPronunciationAdapter a =
                        (EndlessPronunciationAdapter) parent.getAdapter();
                    if(!(a.getItemViewType(position) == 
                        Adapter.IGNORE_ITEM_VIEW_TYPE)) {
                            playSound(
                                ((Pronunciation) a.getItem(position))
                                .getAudioURL(Pronunciation.AudioFormat.MP3)
                                .toString());
                    } else {
                        // Pending view was clicked
                    }
                }
            });

        }
    }

    class PronunciationAdapter extends ArrayAdapter<Pronunciation> {

        public PronunciationAdapter(Context context, int resId) {
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
            textView.setText((CharSequence) 
                    (getLocalizedLanguageName(getItem(position).getLanguage()
                                              .getLanguageName())));
            textView2.setText((CharSequence) 
                    (getItem(position).getUser().getUserName() +
                     " (" + (getItem(position).getUser().getSex() ==
                     User.Sex.Male ? getString(R.string.male) :
                     getString(R.string.female)) + ", " +
                     getLocalizedCountryName(getItem(position).getUser()
                         .getCountry()) + ")"));
            return convertView;
        }

    }

    class EndlessPronunciationAdapter extends EndlessAdapter {

        List<Pronunciation> cachedResults;

        public EndlessPronunciationAdapter(Context context, ListAdapter adapter,
                int id) {
            super(context, adapter, id);
        }

        @Override
        protected boolean cacheInBackground() throws Exception {
            final int LIMIT = 50;
            cachedResults = word.getPronunciations(null, null, -1,
                    Word.Order.RateDesc,
                    getWrappedAdapter().getCount() + LIMIT);
            return !(cachedResults.size() < LIMIT);
        }

        @Override
        protected void appendCachedData() {
            PronunciationAdapter a = (PronunciationAdapter) getWrappedAdapter();
            for(int i = a.getCount(); i < cachedResults.size(); i++)  {
                a.add(cachedResults.get(i));
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
        doPronunciationSearch(query);
    }
}
