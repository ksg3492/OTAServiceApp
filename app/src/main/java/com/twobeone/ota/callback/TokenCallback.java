package com.twobeone.ota.callback;

public interface TokenCallback {
    void onSuccess(String token);

    void onFail();
}
