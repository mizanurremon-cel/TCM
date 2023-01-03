package com.cel.tcm.ViewModel;

import androidx.lifecycle.LiveData;

import com.cel.tcm.Model.Login_response;
import com.cel.tcm.Repository.Login_repository;

public class ViewModelUtil extends androidx.lifecycle.ViewModel {

    public LiveData<Login_response> getLoginToken(String appID, String userName, String password) {
        return Login_repository.getInstance().getLoginToken(appID,userName, password);
    }
}
