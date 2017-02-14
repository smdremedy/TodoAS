package com.soldiersofmobile.todoekspert;

interface LoginCallback {
    void showProgress(boolean enabled);
    void handleError(String error);
    void updateProgress(int progress);
}
