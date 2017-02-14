package com.soldiersofmobile.todoekspert;

interface LoginCallback {
    void showProgress(boolean enabled);

    void loginSuccess();

    void handleError(String error);

    void showUsernameEmptyError();

    void showPasswordLengthError(int length, int minPasswordLength);
}
