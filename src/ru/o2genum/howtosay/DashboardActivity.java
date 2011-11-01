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
 * Dashboard activity
 *
 * @author Andrey Moiseev
 */

import android.app.SearchManager;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.LinkedList;

public class DashboardActivity extends BaseActivity
{
    GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initializeUI();
        if(!isApiKeySet()) {
            Toast.makeText(this, getString(R.string.set_api_key_toast),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        initializeUI();
    }

    public void initializeUI() {
        gridView = (GridView) inflater.inflate(R.layout.grid, null);
        final DashboardAdapter adapter = new DashboardAdapter(this);
        adapter.addAction(new Action(R.drawable.ic_words,
                    getString(R.string.words)) {
            @Override
            public void performAction() {
                doWordSearch(null);
            }
        });
        adapter.addAction(new Action(R.drawable.ic_pronunciations,
                    getString(R.string.pronunciations)) {
            @Override
            public void performAction() {
                doPronunciationSearch(null);
            }
        });
        adapter.addAction(new Action(R.drawable.ic_forvo,
                    getString(R.string.forvo_com)) {
            @Override
            public void performAction() {
                DashboardActivity.this.startActivity(new Intent(
                        Intent.ACTION_VIEW, Uri.parse("http://www.forvo.com")));
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
        setView(gridView);
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
