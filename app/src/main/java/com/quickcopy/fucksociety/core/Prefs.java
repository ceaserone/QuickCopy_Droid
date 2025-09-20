package com.quickcopy.fucksociety.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.quickcopy.fucksociety.model.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Prefs {
    private static final String FILE = "quickcopy_profiles";
    private static final String TAG = "Prefs";

    public static void saveProfile(Context ctx, Profile p) {
        SharedPreferences sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        String base = p.id + "_";
        e.putString(base + "emoji", p.emoji == null ? "" : p.emoji);
        e.putString(base + "name",  p.name  == null ? "" : p.name);
        for (int i = 0; i < 5; i++) {
            e.putString(base + "hint"  + i, p.hints[i]  == null ? "" : p.hints[i]);
            e.putString(base + "value" + i, p.values[i] == null ? "" : p.values[i]);
        }
        e.putString("last_id", p.id);
        e.apply();
    }

    public static Profile loadProfile(Context ctx, String id) {
        if (id == null || id.isEmpty()) return null;

        SharedPreferences sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        String base = id + "_";

        boolean hasNew =
                sp.contains(base + "emoji") ||
                sp.contains(base + "name")  ||
                sp.contains(base + "value0") ||
                sp.contains(base + "hint0");
        if (hasNew) {
            Profile p = new Profile(id);
            p.emoji = sp.getString(base + "emoji", "");
            p.name  = sp.getString(base + "name", "");
            for (int i = 0; i < 5; i++) {
                p.hints[i]  = sp.getString(base + "hint"  + i, "");
                p.values[i] = sp.getString(base + "value" + i, "");
            }
            return p;
        }

        String legacy = sp.getString(id, null);
        if (legacy != null) {
            try {
                Profile p = parseLegacyJson(id, legacy);
                if (p != null) {
                    saveProfile(ctx, p);
                    Log.d(TAG, "Migrated legacy profile " + id + " to new format");
                    return p;
                }
            } catch (Exception ex) {
                Log.w(TAG, "Failed to parse legacy profile " + id + ": " + ex.getMessage());
            }
        }
        return null;
    }

    private static Profile parseLegacyJson(String id, String json) throws JSONException {
        JSONObject o = new JSONObject(json);
        Profile p = new Profile(id);
        p.emoji = optString(o, "emoji");
        p.name  = optString(o, "name");

        if (o.has("hints") && o.get("hints") instanceof JSONArray) {
            JSONArray ha = o.getJSONArray("hints");
            for (int i = 0; i < Math.min(5, ha.length()); i++) {
                p.hints[i] = ha.optString(i, "");
            }
        } else {
            for (int i = 0; i < 5; i++) p.hints[i] = optString(o, "hint" + i);
        }

        if (o.has("values") && o.get("values") instanceof JSONArray) {
            JSONArray va = o.getJSONArray("values");
            for (int i = 0; i < Math.min(5, va.length()); i++) {
                p.values[i] = va.optString(i, "");
            }
        } else {
            for (int i = 0; i < 5; i++) p.values[i] = optString(o, "value" + i);
        }
        return p;
    }

    private static String optString(JSONObject o, String key) {
        return o.has(key) && !o.isNull(key) ? o.optString(key, "") : "";
    }
}
