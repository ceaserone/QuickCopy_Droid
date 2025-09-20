package com.quickcopy.fucksociety.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IconPickerActivity extends AppCompatActivity {

    
    private static final String[] EMOJI_PACK = new String[] {
        "🤪","💯","✔️","🤔","🌀","🙂","😍","🤬","🤗",
        "🫤","🤯","😖","😵","☠️","💀","👽",
        "🔥","💥","⚡","🥷",
        "🤘","✌️","🖕","🙏",
        "🍄","🌐","🎱",
        "💻","📱","🔋","💾","🖥️","📡",
        "💰","💳","💵","🪙",
        "📜","📋","📦","🗒️","📃",
        "🔒","🔓","🔐","🔑","🗝️",
        "💣","🪧","🏴‍☠️"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GridView grid = new GridView(this);
        grid.setNumColumns(5);
        grid.setVerticalSpacing(dp(8));
        grid.setHorizontalSpacing(dp(8));
        grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        grid.setPadding(dp(12), dp(12), dp(12), dp(12));
        grid.setAdapter(new EmojiAdapter());

        grid.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            String emoji = EMOJI_PACK[position];
            Intent result = new Intent().putExtra("emoji", emoji);
            setResult(Activity.RESULT_OK, result);
            finish();
        });

        setContentView(grid);
    }

    private int dp(int v) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    private class EmojiAdapter extends BaseAdapter {
        @Override public int getCount() { return EMOJI_PACK.length; }
        @Override public Object getItem(int position) { return EMOJI_PACK[position]; }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = (convertView instanceof TextView) ? (TextView) convertView : new TextView(IconPickerActivity.this);
            tv.setText(EMOJI_PACK[position]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(dp(12), dp(12), dp(12), dp(12));
            tv.setBackgroundResource(android.R.drawable.btn_default_small);
            return tv;
        }
    }
}