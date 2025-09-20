package com.quickcopy.fucksociety.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quickcopy.fucksociety.R;
import com.quickcopy.fucksociety.core.Prefs;
import com.quickcopy.fucksociety.model.Profile;

public class PopupActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean copiedAndClosing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        String id = getIntent().getStringExtra("profile_id");
        Profile p = Prefs.loadProfile(this, id);
        if (p == null) {
            Toast.makeText(this, "Profile missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvEmoji = findViewById(R.id.tvEmoji);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvEmoji.setText((p.emoji == null || p.emoji.isEmpty()) ? "⭐" : p.emoji);
        tvTitle.setText((p.name == null || p.name.isEmpty()) ? getString(R.string.popup_default_title) : p.name);

        ImageButton btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> finish());

        LinearLayout container = findViewById(R.id.containerRows);
        LayoutInflater inf = LayoutInflater.from(this);

        int rows = 0;
        for (int i = 0; i < 5; i++) {
            final String hint = p.hints[i];
            final String value = p.values[i];
            if ((hint == null || hint.isEmpty()) && (value == null || value.isEmpty())) continue;

            final LinearLayout row = (LinearLayout) inf.inflate(R.layout.item_row, container, false);
            TextView tvHint = row.findViewById(R.id.tvHint);
            TextView tvMask = row.findViewById(R.id.tvMask);
            ImageButton btnCopy = row.findViewById(R.id.btnCopy);

            tvHint.setText((hint == null || hint.isEmpty()) ? ("Item " + (i + 1)) : hint);
            tvMask.setText("••••");

            btnCopy.setOnClickListener(v -> {
                if (value == null || value.isEmpty()) return;
                copyToClipboard(value);
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
                if (!copiedAndClosing) {
                    copiedAndClosing = true;
                    handler.postDelayed(this::finish, 1200);
                }
            });

            container.addView(row);
            rows++;
        }

        if (rows == 0) {
            Toast.makeText(this, "Nothing to copy..?", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("quickcopy", text));
    }
}
