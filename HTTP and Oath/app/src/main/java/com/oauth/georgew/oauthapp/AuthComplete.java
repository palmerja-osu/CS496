package com.oauth.georgew.oauthapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

public class AuthComplete extends AppCompatActivity {

    private static final String TAG = AuthComplete.class.getSimpleName();
    private AuthorizationService completeAuthorizationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_complete);
        Log.d(TAG, "Got this far yo");

        completeAuthorizationService = new AuthorizationService(this);
        Uri redirectUri = getIntent().getData();
        AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException exception = AuthorizationException.fromIntent(getIntent());

        if (response != null){
            final AuthState authState = new AuthState(response, exception);
            completeAuthorizationService.performTokenRequest(response.createTokenExchangeRequest(),
                    new AuthorizationService.TokenResponseCallback(){
                        @Override
                        public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException authorizationException){
                            authState.update(tokenResponse, authorizationException);
                            SharedPreferences authorizationPreferences = getSharedPreferences("auth", MODE_PRIVATE);
                            authorizationPreferences.edit().putString("stateJson", authState.jsonSerializeString()).apply();
                            finish();
                        }
                    });
        }
    }
}
