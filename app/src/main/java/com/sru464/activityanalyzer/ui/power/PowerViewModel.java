package com.sru464.activityanalyzer.ui.power;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PowerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PowerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("1.2");
    }

    public LiveData<String> getText() {
        return mText;
    }
}