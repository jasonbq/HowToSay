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

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.o2genum.forvo.Word;

public class StartScreenView extends LinearLayout {

    Animation animation;
    LayoutInflater inflater;
    Context context;
    TextView pronunciationsTotalNumberView,
             wordsTotalNumberView;

    public StartScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeUI();
    }

    public StartScreenView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initializeUI();
    }

    private void initializeUI() {
        setVisibility(View.INVISIBLE);
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.start_screen_view, this, true);
        pronunciationsTotalNumberView = (TextView) 
            v.findViewById(R.id.pronunciations_total_num_view);
        wordsTotalNumberView = (TextView)
                  v.findViewById(R.id.words_total_num_view);
        AnimationUtils au = new AnimationUtils();
        animation = au.loadAnimation(context, android.R.anim.fade_in);
        new LoadDataTask().execute();
    }

    class LoadDataTask extends AsyncTask<Void, Void, int[]> {
        @Override
        protected int[] doInBackground(Void ... v) {
                Word word = new Word(null);
                int data[] = new int[3];
                data[0] = 1; // OK
                try {
                    data[1] = word.getTotalPronunciations();
                    data[2] = word.getTotalPronouncedWords();
                } catch (Exception ex) {
                    data[0] = 0; // NOT OK
                    ex.printStackTrace();
                }
            return data;
        }

        @Override
        protected void onPostExecute(int data[]) {
            if(data[0] == 0)
                return;
            pronunciationsTotalNumberView.setText(
                    new Integer(data[1]).toString());
            wordsTotalNumberView.setText(
                    new Integer(data[2]).toString());
            StartScreenView.this.setVisibility(View.VISIBLE);
            StartScreenView.this.startAnimation(animation);
        }
    }
}
