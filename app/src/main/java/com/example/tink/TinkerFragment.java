package com.example.tink;

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
import com.example.StatusBarTool;
import com.example.kotlindemo.R;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;


public class TinkerFragment extends Fragment {

    private static final byte[] EMPTY_ASSOCIATED_DATA = new byte[0];

    private App mApplication;
    private EditText mPlaintextView;
    private EditText mCiphertextView;

    public TinkerFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tink, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        StatusBarTool.adaptSysBar(view);
        if (getActivity() == null) return;
        mApplication = (App) getActivity().getApplicationContext();
        mPlaintextView = view.findViewById(R.id.plaintext);
        mCiphertextView = view.findViewById(R.id.ciphertext);
        Button mEncryptButton = view.findViewById(R.id.encrypt_button);
        mEncryptButton.setOnClickListener(v -> attemptEncrypt());
        Button mDecryptButton = view.findViewById(R.id.decrypt_button);
        mDecryptButton.setOnClickListener(v -> attemptDecrypt());
    }

    private void attemptEncrypt() {
        mPlaintextView.setError(null);
        mCiphertextView.setError(null);
        mCiphertextView.setText("");

        try {
            byte[] plainText = mPlaintextView.getText().toString().getBytes(StandardCharsets.UTF_8);
            byte[] cipherText = mApplication.aead.encrypt(plainText, EMPTY_ASSOCIATED_DATA);
            mCiphertextView.setText(base64Encode(cipherText));
        } catch (GeneralSecurityException | IllegalArgumentException e) {
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
            byte[] cipherText = base64Decode(mCiphertextView.getText().toString());
            byte[] plainText = mApplication.aead.decrypt(cipherText, EMPTY_ASSOCIATED_DATA);
            mPlaintextView.setText(new String(plainText, StandardCharsets.UTF_8));
        } catch (GeneralSecurityException | IllegalArgumentException e) {
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