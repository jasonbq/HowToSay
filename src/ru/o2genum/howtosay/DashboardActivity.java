/*
    Copyright (c) 2011, Andrey Moiseev

    Licensed under the Apache License, Version 2.0 (the "License"); you may
    not use this file except in compliance with the License. You may obtain
    a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package ru.o2genum.howtosay;

/**
 * Dashboard activity for normal screens,
 * the whole app logic - for Android 3.0+ Tablets
 * (large and x-large screens)
 *
 * @author Andrey Moiseev
 */

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.content.Context;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.inputmethod.InputMethodManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import ru.o2genum.forvo.Pronunciation;
import ru.o2genum.forvo.Word;
import ru.o2genum.forvo.WordAndPronunciation;
import ru.o2genum.forvo.User;

import com.commonsware.cwac.endless.EndlessAdapter;

import java.util.List;
import java.util.LinkedList;

public class DashboardActivity extends BaseActivity
{
    GridView gridView;
    RelativeLayout layout;

    //Honeycomb
    SearchView searchView;

    LinearLayout content;

    String query = null;

    Word word;

    WordAdapterLargeScreen wordAdapter;
    EndlessWordAdapterLargeScreen endlessWordAdapter;
    PronunciationAdapterLargeScreen pronunciationAdapter;
    EndlessPronunciationAdapterLargeScreen endlessPronunciationAdapter;

    SidebarItemView wordsTab, pronunciationsTab, aboutTab, setApiKeyTab,
                    moreTab;
    ListView wordListView, pronunciationListView;

    // Set API key
    EditText editKey;
    TextView keyStatus;
    Button setButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initializeUI();
        if(!(is11Plus() && hasLargeScreen()))
        if(!isApiKeySet()) {
            Toast.makeText(this, getString(R.string.set_api_key_toast),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        boolean iconified = searchView.isIconified();
        boolean w = wordsTab.isSelected(),
                p = pronunciationsTab.isSelected(),
                a = aboutTab.isSelected(),
                s = setApiKeyTab.isSelected(),
                m = moreTab.isSelected();
        String backupQuery = query;
        super.onConfigurationChanged(newConfiguration);
        if(is11Plus() && hasLargeScreen()) {
            initializeBasicUI();
        }
        initializeUI();
        if(w) {
            wordsTab.setSelected(true);
        } else if(p) {
            pronunciationsTab.setSelected(true);
        } else if(a) {
            aboutTab.setSelected(true);
        } else if(s) {
            setApiKeyTab.setSelected(true);
        } else if(m) {
            moreTab.setSelected(true);
        }

        searchView.setQuery(backupQuery, false);
        query = backupQuery;
        searchView.setIconified(iconified);

        if(wordsTab.isSelected()) {
            wordListView = new ListView(this);
            wordListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
                EndlessWordAdapterLargeScreen a = 
            (EndlessWordAdapterLargeScreen) parent.getAdapter();
        if(!(a.getItemViewType(position) ==
                Adapter.IGNORE_ITEM_VIEW_TYPE)) {
            playSound(
                ((WordAndPronunciation) a.getItem(position))
                .getPronunciation()
                .getAudioURL(Pronunciation.AudioFormat.MP3)
                .toString(), view);
                } else {
                    // Pending view was clicked
                }
            }
        });
            wordListView.setAdapter(endlessWordAdapter);
            content.addView(wordListView);
        } else if(pronunciationsTab.isSelected()) {
            pronunciationListView = new ListView(this);
            pronunciationListView.setAdapter(
                    endlessPronunciationAdapter);
            pronunciationListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                    EndlessPronunciationAdapterLargeScreen a = 
                (EndlessPronunciationAdapterLargeScreen) parent.getAdapter();
            if(!(a.getItemViewType(position) ==
                    Adapter.IGNORE_ITEM_VIEW_TYPE)) {
                playSound(
                    ((Pronunciation) a.getItem(position))
                    .getAudioURL(Pronunciation.AudioFormat.MP3)
                    .toString(), view);
                    } else {
                        // Pending view was clicked
                    }
                }
            });
            content.addView(pronunciationListView);
        } else if(setApiKeyTab.isSelected()) {
            onSetApiKeyTabClick();
        }
    }

    public void initializeUI() {
        if(is11Plus() && hasLargeScreen()) {
            // Honeycomb
            content = (LinearLayout) findViewById(R.id.content);

            final SearchView sv = (SearchView) findViewById(R.id.searchview);
            searchView = sv;
            sv.setSubmitButtonEnabled(true);
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                   if(newText.length() == 0)
                      query = null;
                   else 
                      query = newText;
                   return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(query.length() == 0)
                         return true;
                    if(pronunciationsTab.isSelected()) {
                        DashboardActivity.this.query = query;
                        doPronunciationSearchLargeScreen();
                    } else {
                        DashboardActivity.this.query = query;
                        doWordSearchLargeScreen();
                        if(!DashboardActivity.this.wordsTab.isSelected())
                            DashboardActivity.this.wordsTab.setSelected(true);
                    }
                    if(DashboardActivity.this.query.length() == 0)
                        DashboardActivity.this.query = null;
                    hideKeyboard(sv);
                    return true;
                }
            });
            wordsTab = (SidebarItemView) findViewById(R.id.words_tab);
            pronunciationsTab = (SidebarItemView) findViewById(
                    R.id.pronunciations_tab);
            aboutTab = (SidebarItemView) findViewById(R.id.about_tab);
            setApiKeyTab = (SidebarItemView) findViewById(R.id.set_api_key_tab);
            moreTab = (SidebarItemView) findViewById(R.id.more_tab);
            wordsTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAllSidebarItems();
                    ((SidebarItemView) view).setSelected(true);
                    DashboardActivity.this.onWordsTabClick();
                    sv.setIconified(false);
                }
            });
            pronunciationsTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAllSidebarItems();
                    ((SidebarItemView) view).setSelected(true);
                    DashboardActivity.this.onPronunciationsTabClick();
                    sv.setIconified(false);
                }
            });
            aboutTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAllSidebarItems();
                    ((SidebarItemView) view).setSelected(true);
                    DashboardActivity.this.onAboutTabClick();
                }
            });
            setApiKeyTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAllSidebarItems();
                    ((SidebarItemView) view).setSelected(true);
                    DashboardActivity.this.onSetApiKeyTabClick();
                }
            });
            moreTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAllSidebarItems();
                    ((SidebarItemView) view).setSelected(true);
                    DashboardActivity.this.onMoreTabClick();
                }
            });
        } else {
            layout = (RelativeLayout) inflater.inflate(R.layout.grid, null);
            gridView = (GridView) layout.findViewById(R.id.grid);
            final DashboardAdapter adapter = new DashboardAdapter(this);
            adapter.addAction(new Action(R.drawable.ic_words,
                        getString(R.string.words)) {
                @Override
                public void performAction() {
                    doWordSearch(null);
                }
            });
            TextView textView = (TextView) layout.findViewById(R.id.text_view);
            textView.setText(Html.fromHtml(
                        getString(R.string.forvo_attribution)));
            adapter.addAction(new Action(R.drawable.ic_pronunciations,
                        getString(R.string.pronunciations)) {
                @Override
                public void performAction() {
                    doPronunciationSearch(null);
                }
            });
            adapter.addAction(new Action(R.drawable.ic_about,
                        getString(R.string.about_rate)) {
                @Override
                public void performAction() {
                    DashboardActivity.this.startActivity(new Intent(
                            Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=ru.o2genum.howtosay")));
                }
            });
            adapter.addAction(new Action(R.drawable.ic_key,
                        getString(R.string.dashboard_api_key)) {
                @Override
                public void performAction() {
                    DashboardActivity.this.startActivity(new Intent(
                            DashboardActivity.this, SetApiKeyActivity.class));
                }
            });
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                    adapter.performAction(position);
                }
            });
            setView(layout);
        }
    }

    private void unselectAllSidebarItems() {
        wordsTab.setSelected(false);
        pronunciationsTab.setSelected(false);
        aboutTab.setSelected(false);
        setApiKeyTab.setSelected(false);
        moreTab.setSelected(false);
    }

    // Honeycomb sidebar tabs events
    private void onWordsTabClick() {
        if(query != null && query.length() != 0)
        doWordSearchLargeScreen();
    }

    private void onPronunciationsTabClick() {
        if(query != null && query.length() != 0)
        doPronunciationSearchLargeScreen();
    }

    private void onAboutTabClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://market.android.com/details?id="+
                    "ru.o2genum.howtosay"));;
        startActivity(intent);
    }

    private void onSetApiKeyTabClick() {
        content.removeAllViews();
        View view = inflater.inflate(R.layout.set_api_key, content, true);
        editKey = (EditText) view.findViewById(R.id.key_edit);
        keyStatus = (TextView) view.findViewById(R.id.key_status);
        setButton = (Button) view.findViewById(R.id.set_button);
        loadKeyFromPrefs();
        updateViews();
        setButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                storeKey(editKey.getText().toString().trim());
                loadKeyFromPrefs();
                updateViews();
            }
        });
    }

    private void updateViews() {
        editKey.setText(key, TextView.BufferType.EDITABLE);
        switch(keyState) {
            case KEY_IS_SET:
                keyStatus.setText(getString(R.string.key_is_set),
                        TextView.BufferType.SPANNABLE);
                break;
            case KEY_IS_NOT_SET:
                keyStatus.setText(getString(R.string.key_is_not_set),
                        TextView.BufferType.SPANNABLE);
                break;
            case KEY_SEEMS_WRONG:
                keyStatus.setText(getString(R.string.key_seems_wrong),
                        TextView.BufferType.SPANNABLE);
                break;
        }
    }

    private void onMoreTabClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.more));
        builder.setItems(R.array.more_menu,
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item) {
                    case 0:
                        changeTheme();
                        break;
                    case 1:
                        shareApp();
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Honeycomb search
    private void doWordSearchLargeScreen() {
        word = new Word(query);
        content.removeAllViews();
        wordListView = new ListView(this);
        pronunciationListView = null;
        content.addView(wordListView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
        wordAdapter = new WordAdapterLargeScreen(this, 0);
        endlessWordAdapter = new EndlessWordAdapterLargeScreen(this,
                wordAdapter, R.layout.pendingview);
        wordListView.setAdapter(endlessWordAdapter);
        wordListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
                EndlessWordAdapterLargeScreen a = 
            (EndlessWordAdapterLargeScreen) parent.getAdapter();
        if(!(a.getItemViewType(position) ==
                Adapter.IGNORE_ITEM_VIEW_TYPE)) {
            playSound(
                ((WordAndPronunciation) a.getItem(position))
                .getPronunciation()
                .getAudioURL(Pronunciation.AudioFormat.MP3)
                .toString(), view);
                } else {
                    // Pending view was clicked
                }
            }
        });
    }
    
    class WordAdapterLargeScreen extends ArrayAdapter<WordAndPronunciation> {

        public WordAdapterLargeScreen(Context context, int resId) {
            super(context, resId);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = (View) inflater.inflate(R.layout.word_list_item,
                        null);
            }
            TextView wordView = (TextView) convertView
                .findViewById(R.id.wordview);
            wordView.setText((CharSequence) getItem(position).getWord()
                    .getOriginal());
            TextView pronunciationsNumberView = (TextView) convertView
                .findViewById(R.id.pronunciations_number_view);
            int numPronunciations = getItem(position).getWord()
                .getPronunciationsNumber();
            pronunciationsNumberView.setText((CharSequence)
                    String.format(getResources()
                        .getQuantityString(R.plurals.pronunciation_s,
                        numPronunciations), numPronunciations));
            return convertView;
        }
    }

    class EndlessWordAdapterLargeScreen extends EndlessAdapter {

        List<WordAndPronunciation> cachedResults;

        public EndlessWordAdapterLargeScreen(Context context,
                ListAdapter adapter, int id) {
            super(context, adapter, id);
        }

        @Override
        protected boolean cacheInBackground() throws Exception {
            final int PAGE = 50;
            cachedResults = word.searchPronouncedWords(null, PAGE,
                    getWrappedAdapter().getCount() / PAGE + 1);
            return !(cachedResults.size() < PAGE);
        }

        @Override
        protected void appendCachedData() {
            WordAdapterLargeScreen a = (WordAdapterLargeScreen)
                getWrappedAdapter();
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

    private void doPronunciationSearchLargeScreen() {
        word = new Word(query);
        content.removeAllViews();
        pronunciationListView = new ListView(this);
        wordListView = null;
        content.addView(pronunciationListView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
        pronunciationAdapter = new PronunciationAdapterLargeScreen(this, 0);
        endlessPronunciationAdapter = 
            new EndlessPronunciationAdapterLargeScreen(this,
                    pronunciationAdapter, R.layout.pendingview);
        pronunciationListView.setAdapter(endlessPronunciationAdapter);
        pronunciationListView
            .setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
                EndlessPronunciationAdapterLargeScreen a = 
            (EndlessPronunciationAdapterLargeScreen) parent.getAdapter();
        if(!(a.getItemViewType(position) ==
                Adapter.IGNORE_ITEM_VIEW_TYPE)) {
            playSound(
                ((Pronunciation) a.getItem(position))
                .getAudioURL(Pronunciation.AudioFormat.MP3)
                .toString(), view);
                } else {
                    // Pending view was clicked
                }
            }
        });
    }
    class PronunciationAdapterLargeScreen extends
        ArrayAdapter<Pronunciation> {

        public PronunciationAdapterLargeScreen(Context context, int resId) {
            super(context, resId);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = (View) inflater
                    .inflate(R.layout.pronunciation_list_item, null);
            }
            TextView langView = (TextView) convertView
                .findViewById(R.id.languageview);
            langView.setText((CharSequence) 
                    (getLocalizedLanguageName(getItem(position)
                                              .getLanguage()
                                              .getCode(),
                    getItem(position).getLanguage().getLanguageName())));
            TextView etcView = (TextView) convertView
                .findViewById(R.id.etc_view);
            etcView.setText((CharSequence) (
                        getLocalizedCountryName(getItem(position)
                            .getUser().getCountry()) + ", " +
                        getItem(position).getUser().getUserName() +
                        " (" + (getItem(position).getUser().getSex() ==
                                User.Sex.Male ? getString(R.string.male) :
                                getString(R.string.female)) + ")"));
            TextView rateView = (TextView) convertView
                .findViewById(R.id.rate_view);
            int rate = getItem(position).getRate();
            String rateString = new Integer(rate).toString();
            rateView.setText((CharSequence) (rate > 0 ? "+" + rateString :
                        rateString));
            return convertView;
        }
    }

    class EndlessPronunciationAdapterLargeScreen extends EndlessAdapter {

        List<Pronunciation> cachedResults;

        public EndlessPronunciationAdapterLargeScreen(Context context,
                ListAdapter adapter, int id) {
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
            PronunciationAdapterLargeScreen a = 
                (PronunciationAdapterLargeScreen) getWrappedAdapter();
            for(int i = a.getCount(); i < cachedResults.size(); i++) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_theme:
                changeTheme();
                return true;
            case R.id.share_app:
                shareApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.share_app_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text)
                + " http://bit.ly/htsay");
        startActivity(intent);
    }

    @Override
    public void doSearch(String query) {
        doWordSearch(query);
    }

    class DashboardAdapter extends BaseAdapter {
        private Context context;
        private List<Action> actions;

        public DashboardAdapter(Context context) {
            this.context = context;
            actions = new LinkedList<Action>();
        }

        public void addAction(Action action) {
            actions.add(action);
        }

        public int getCount() {
            return actions.size();
        }

        public Object getItem(int position) {
            return actions.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            DashboardItem item;
            if (convertView == null) {
                item = new DashboardItem(DashboardActivity.this);
            } else {
                item = (DashboardItem) convertView;
            }
            item.setLayoutParams(new GridView.LayoutParams(
                        GridView.LayoutParams.WRAP_CONTENT,
                        GridView.LayoutParams.WRAP_CONTENT));
            item.setImage(actions.get(position).getImage());
            item.setText(actions.get(position).getText());
            item.setPadding(8, 8, 8, 8);
            return item;
        }

        public void performAction(int position) {
            actions.get(position).performAction();
        }

        class DashboardItem extends LinearLayout {
            private ImageView imageView;
            private TextView textView;

            public DashboardItem(Context context) {
                super(context);
                ViewGroup viewGroup = (ViewGroup) DashboardActivity.this.
                    inflater.inflate(R.layout.dashboard_item, null);
                this.addView(viewGroup, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT));
                imageView = (ImageView) findViewById(R.id.imageview);
                textView = (TextView) findViewById(R.id.textview);
            }

            public void setImage(int image) {
                imageView.setImageResource(image);
            }

            public void setText(String text) {
                textView.setText(text);
            }
        }
    }

    abstract class Action {
        private int image;
        private String text;

        public Action(int image, String text) {
            this.image = image;
            this.text = text;
        }

        public abstract void performAction();

        public int getImage() {
            return image;
        }

        public String getText() {
            return text;
        }

    }
}
