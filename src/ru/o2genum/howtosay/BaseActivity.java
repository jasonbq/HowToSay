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
 * Base class for app's activities
 * 
 * @author Andrey Moiseev
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.os.Bundle;

import java.util.Arrays;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

import ru.o2genum.forvo.ApiKey;

public abstract class BaseActivity extends Activity {

    protected ActionBar actionBar;
    protected LayoutInflater inflater;
    protected SearchManager searchManager;
    protected MediaPlayer mediaPlayer;
    protected SharedPreferences prefs;

    final String SHARED_PREFS_KEY = "ru.o2genum.howtosay.PREFS";

    final String PREFS_API_KEY = "API_KEY";
    final String PREFS_THEME = "THEME";

    final String[] THEMES = {"Blueberry.Dark", "Blueberry.Light"};
    // Translated names are in arrays.xml in the same order

    @Override
    public void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        loadTheme();
        super.onCreate(savedInstanceState);
        searchManager = (SearchManager) 
            getSystemService(Context.SEARCH_SERVICE);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        // Set API key
        ApiKey.setKey(getApiKey());
        initializeBasicUI();
    }

    @Override
    public void onStop() {
        mediaPlayer.reset();
        super.onStop();
    }

    protected void initializeBasicUI() {
        inflater = (LayoutInflater) getSystemService
                  (Context.LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.base);
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        Intent homeIntent = new Intent(this, DashboardActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        actionBar.setHomeAction(new IntentAction(this, homeIntent,
                    R.drawable.ic_actionbar_home));
        actionBar.addAction(new AbstractAction(R.drawable.ic_actionbar_search) {
            @Override
            public void performAction(View view) {
                doSearch(null);
            }
        });
    }

    public void setView(View view) {
        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        frame.removeAllViews();
        frame.addView(view);
    }

    @Override
    public void setTitle(CharSequence title) {
        actionBar.setTitle(title);
    }

    public void loadTheme() {
        int themeId = getResources().getIdentifier(THEMES[getPreferredTheme()],
                "style", getClass().getPackage().getName());
        setTheme(themeId);
    }


    public void changeTheme() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_theme_title));
        builder.setCancelable(true);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
        });
        builder.setSingleChoiceItems(R.array.theme_names, getPreferredTheme(),
                 new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if(item != getPreferredTheme()) {
                            Toast.makeText(BaseActivity.this,
                                R.string.restart_app_message,
                                    Toast.LENGTH_LONG).show();
                            setPreferredTheme(item);
                        }
                    }
        }).show();
    }

    public int getPreferredTheme() {
        int preferredThemeIndex = Arrays.asList(THEMES)
            .indexOf(prefs.getString(PREFS_THEME, "THEME_IS_NOT_SET"));
        return preferredThemeIndex == -1 ? 0 : preferredThemeIndex;
    }

    public void setPreferredTheme(int preferredThemeIndex) {
        prefs.edit().putString(PREFS_THEME,
                THEMES[preferredThemeIndex]).commit();
    }

    public void doSearch(String query) {
        doWordSearch(query);
    }

    public void doWordSearch(String query) {
        searchManager.startSearch(query, false, new ComponentName(this,
                    WordSearchActivity.class), null, false);
    }

    public void doPronunciationSearch(String query) {
        searchManager.startSearch(query, false, new ComponentName(this,
                    PronunciationSearchActivity.class), null, false);
    }

    public void playSound(String url, View view) {
        final String finalUrl = url;
        final PendingView finalView =
                (PendingView) view.findViewById(R.id.list_item_pending_view);
        if(finalView.getVisibility() == View.INVISIBLE)
            finalView.showAnim();
        (new Thread(new Runnable() {
            public void run() {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setOnBufferingUpdateListener(
                        new MediaPlayer.OnBufferingUpdateListener() {
                            public void onBufferingUpdate(MediaPlayer mp,
                                int percent) {
                            if(percent == 100) {
                                
                    BaseActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            finalView.hideAnim();
                        }
                    });
                }
                }
                });
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(finalUrl);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch(Exception ex) {
                    toastException(ex);
                    BaseActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            finalView.hideAnim();
                        }
                    });
                }
            }
        })).start();
    }

    public void toastException(Exception ex) {
        String msg = getString(R.string.error_exception);
        String msg2 = ex.getClass().getSimpleName() + 
                ((ex.getMessage() != null) ? ": " + ex.getMessage() : "");
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        Toast.makeText(this, msg2, Toast.LENGTH_LONG).show();
    }

    public String getLocalizedLanguageName(String code, String inEnglish) {
        int id = getStrResId(code);
        String result = inEnglish;
        try {
            result = id != 0 ? getString(id) : inEnglish;
        } catch (Resources.NotFoundException ex) {
            // Don't know why, but this exception occurs on
            // Ice Cream Sandwich emulator.
        } finally {
            return result;
        }
    }

    public String getLocalizedCountryName(String inEnglish) {
        int id = getStrResId(inEnglish);
        String result = inEnglish;
        try {
          result = id != 0 ? getString(id) : inEnglish;
        } catch (Resources.NotFoundException ex) {
            // See getLocalizedLanguageName
        } finally {
            return result;
        }
    }

    public int getStrResId(String inEnglish) {
        String result = 
                inEnglish.toLowerCase()
                .replace(' ', '_')
                .replace(")", "")
                .replace("(", "")
                .replace('-', '_')
                .replace(",", "");
        if(result.equals("new")) // "new" is a language code and Java keyword
            result += "_";
        return getResources().getIdentifier(result, "string",
                getClass().getPackage().getName());
    }

    public String getApiKey() {
        String fromPrefs = prefs.getString(PREFS_API_KEY, "");
        fromPrefs = fromPrefs.trim();
        if(fromPrefs.length() == 0) {
            return Secret.apiKey;
        } else {
            return fromPrefs;
        }
    }

    public boolean isApiKeySet() {
        String fromPrefs = prefs.getString(PREFS_API_KEY, "");
        fromPrefs = fromPrefs.trim();
        return fromPrefs.length() != 0;
    }
}
