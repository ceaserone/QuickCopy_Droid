package com.quickcopy.fucksociety.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.text.Html;
import android.text.method.LinkMovementMethod;

import androidx.appcompat.app.AppCompatActivity;

import com.quickcopy.fucksociety.R;
import com.quickcopy.fucksociety.core.Prefs;
import com.quickcopy.fucksociety.model.Profile;

import java.util.UUID;

public class ConfiguratorActivity extends AppCompatActivity {

    private static final int REQ_PICK_EMOJI = 1001;

    private String chosenEmoji = null;
    private EditText etName;
    private EditText[] etHints = new EditText[5];
    private EditText[] etValues = new EditText[5];
    private Button btnPickIcon;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurator);

        btnPickIcon = findViewById(R.id.btnPickIcon);
        etName = findViewById(R.id.etName);

        etHints[0] = findViewById(R.id.etHint1);
        etHints[1] = findViewById(R.id.etHint2);
        etHints[2] = findViewById(R.id.etHint3);
        etHints[3] = findViewById(R.id.etHint4);
        etHints[4] = findViewById(R.id.etHint5);

        etValues[0] = findViewById(R.id.etValue1);
        etValues[1] = findViewById(R.id.etValue2);
        etValues[2] = findViewById(R.id.etValue3);
        etValues[3] = findViewById(R.id.etValue4);
        etValues[4] = findViewById(R.id.etValue5);

        btnPickIcon.setOnClickListener(v -> {
            Intent i = new Intent(this, IconPickerActivity.class);
            startActivityForResult(i, REQ_PICK_EMOJI);
        });

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveProfile());

        // ---- Footer hyperlinks
        TextView footer = findViewById(R.id.tvFooter);
        if (footer != null) {
            String html = "🌀 Made By: <a href='https://synacknetwork.com'>SynAckNetwork.com ~ DEVNet!</a><br>" +
                          "✉️ Email Us: <a href='mailto:devnet@synacknetwork.com'>devnet@synacknetwork.com</a><br>" +
                          "📲 Made on an Android with AndroidIDE<br>" +
                          "✔️ Check out the <a href='https://github.com/ceaserone/QuickCopy_Droid'>GitHub repo</a><br>" +
                          "💯 FUCK Society......🤪<br>";
            footer.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
            footer.setMovementMethod(LinkMovementMethod.getInstance());
        }
        // ----------androidide is buggy so lets try to ignore all errors if possible

        refreshPickButtonLabel();
    }

    @Override @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_EMOJI && resultCode == Activity.RESULT_OK && data != null) {
            String e = data.getStringExtra("emoji");
            if (e != null && !e.trim().isEmpty()) {
                chosenEmoji = e.trim();
                Toast.makeText(this, "Icon: " + chosenEmoji, Toast.LENGTH_SHORT).show();
                refreshPickButtonLabel();
            }
        }
    }

    private void refreshPickButtonLabel() {
        if (btnPickIcon == null) return;
        btnPickIcon.setText(
                (chosenEmoji == null || chosenEmoji.isEmpty())
                        ? getString(R.string.pick_icon)
                        : getString(R.string.pick_icon) + "  " + chosenEmoji
        );
    }

    private void saveProfile() {
        String name = safe(etName);

        if ((chosenEmoji == null || chosenEmoji.isEmpty()) && name.isEmpty()) {
            Toast.makeText(this, "Pick an icon, and name youe shortcut!", Toast.LENGTH_SHORT).show();
            return;
        }

        Profile p = new Profile(UUID.randomUUID().toString());
        p.emoji = chosenEmoji;
        p.name = name;

        for (int i = 0; i < 5; i++) {
            p.hints[i] = safe(etHints[i]);
            p.values[i] = safe(etValues[i]);
        }

        Prefs.saveProfile(this, p);
        createShortcut(this, p);

        Toast.makeText(this, "Shortcut created", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String safe(EditText e) {
        return e == null ? "" : e.getText().toString().trim();
    }

    private void createShortcut(Context ctx, Profile p) {
        android.content.pm.ShortcutManager sm = ctx.getSystemService(android.content.pm.ShortcutManager.class);
        if (sm == null || !sm.isRequestPinShortcutSupported()) {
            Toast.makeText(ctx, getString(R.string.shortcut_not_supported), Toast.LENGTH_SHORT).show();
            return;
        }

        Icon icon;
        if (p.emoji != null && !p.emoji.isEmpty()) {
            icon = Icon.createWithBitmap(renderEmojiIcon(p.emoji));
        } else if (p.name != null && p.name.length() >= 2) {
            String letters = p.name.substring(0, 2).toUpperCase();
            icon = Icon.createWithBitmap(renderTextIcon(letters));
        } else {
            icon = Icon.createWithResource(ctx, R.drawable.ic_copy_24);
        }

        String label = (p.name == null || p.name.isEmpty()) ? "" : p.name;

        Intent intent = new Intent(ctx, PopupActivity.class)
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .putExtra("profile_id", p.id)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        android.content.pm.ShortcutInfo shortcut = new android.content.pm.ShortcutInfo.Builder(ctx, p.id)
                .setShortLabel(label)
                .setIcon(icon)
                .setIntent(intent)
                .build();

        sm.requestPinShortcut(shortcut, null);
    }

    private Bitmap renderEmojiIcon(String emoji) {
        int size = 192;
        Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setColor(Color.parseColor("#2A2A2A"));
        c.drawRoundRect(0, 0, size, size, 36, 36, bg);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextSize(132f);
        p.setTextAlign(Paint.Align.CENTER);
        p.setColor(Color.WHITE);
        float x = size / 2f;
        float y = size / 2f - (p.ascent() + p.descent()) / 2f;
        c.drawText(emoji, x, y, p);
        return b;
    }

    private Bitmap renderTextIcon(String letters) {
        int size = 192;
        Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setColor(Color.parseColor("#4FC3F7"));
        c.drawRoundRect(0, 0, size, size, 36, 36, bg);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextSize(96f);
        p.setColor(Color.BLACK);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextAlign(Paint.Align.CENTER);
        float x = size / 2f;
        float y = size / 2f - (p.ascent() + p.descent()) / 2f;
        c.drawText(letters, x, y, p);
        return b;
    }
}