package com.example.tink;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.App;
import com.example.kotlindemo.MainActivity;
import com.example.kotlindemo.R;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

//import com.google.crypto.tink.subtle.Base64;

public class TinkFragment extends Fragment {

    private static final byte[] EMPTY_ASSOCIATED_DATA = new byte[0];

    private App mApplication;
    private EditText mPlaintextView;
    private EditText mCiphertextView;

    public TinkFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tink, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() == null) return;
        mApplication = (App) getActivity().getApplicationContext();
        mPlaintextView = view.findViewById(R.id.plaintext);
        mCiphertextView = view.findViewById(R.id.ciphertext);
        Button mEncryptButton = view.findViewById(R.id.encrypt_button);
        mEncryptButton.setOnClickListener(
                v -> attemptEncrypt());
        Button mDecryptButton = view.findViewById(R.id.decrypt_button);
        mDecryptButton.setOnClickListener(
                v -> attemptDecrypt());
        mApplication.startActivity(new Intent(mApplication, MainActivity.class));
    }

    private void attemptEncrypt() {
        mPlaintextView.setError(null);
        mCiphertextView.setError(null);
        mCiphertextView.setText("");

        try {
            byte[] plaintext = mPlaintextView.getText().toString().getBytes("UTF-8");
            byte[] ciphertext = mApplication.aead.encrypt(plaintext, EMPTY_ASSOCIATED_DATA);
            mCiphertextView.setText(base64Encode(ciphertext));
        } catch (UnsupportedEncodingException | GeneralSecurityException | IllegalArgumentException e) {
            mCiphertextView.setError(
                    String.format("%s: %s", getString(R.string.error_cannot_encrypt), e.toString()));
            mPlaintextView.requestFocus();
        }
    }

    private void attemptDecrypt() {
        mPlaintextView.setError(null);
        mPlaintextView.setText("");
        mCiphertextView.setError(null);

        try {
            byte[] ciphertext = base64Decode(mCiphertextView.getText().toString());
            byte[] plaintext = mApplication.aead.decrypt(ciphertext, EMPTY_ASSOCIATED_DATA);
            mPlaintextView.setText(new String(plaintext, "UTF-8"));
        } catch (UnsupportedEncodingException | GeneralSecurityException | IllegalArgumentException e) {
            mPlaintextView.setError(
                    String.format("%s: %s", getString(R.string.error_cannot_decrypt), e.toString()));
            mCiphertextView.requestFocus();
        }
    }

    private static String base64Encode(final byte[] input) {
        return Base64.encodeToString(input, Base64.DEFAULT);
    }

    private static byte[] base64Decode(String input) {
        return Base64.decode(input, Base64.DEFAULT);
    }
}