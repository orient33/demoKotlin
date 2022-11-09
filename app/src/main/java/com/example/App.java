package com.example;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.Config;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.config.TinkConfig;
import com.google.crypto.tink.integration.android.AndroidKeysetManager;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author dundongfang on 2018/9/3.
 */
public class App extends Application {

    private static final String TAG = App.class.toString();
    private static final String PREF_FILE_NAME = "hello_world_pref";
    private static final String TINK_KEYSET_NAME = "hello_world_keyset";
    private static final String MASTER_KEY_URI = "android-keystore://hello_world_master_key";
    public Aead aead;

    private final Configuration mConfig = new Configuration();

    @Override
    public final void onCreate() {
        super.onCreate();
        Injector.sContext = getApplicationContext();
        try {
            Config.register(TinkConfig.LATEST);
            aead = AeadFactory.getPrimitive(getOrGenerateNewKeysetHandle());
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        mConfig.updateFrom(getResources().getConfiguration());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int diff = mConfig.updateFrom(newConfig);
        TopUtilKt.log("onConfig Change. 0x" + Integer.toHexString(diff), "df");
    }

    private KeysetHandle getOrGenerateNewKeysetHandle() throws IOException, GeneralSecurityException {
        return new AndroidKeysetManager.Builder()
                .withSharedPref(getApplicationContext(), TINK_KEYSET_NAME, PREF_FILE_NAME)
                .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .getKeysetHandle();
    }
}
