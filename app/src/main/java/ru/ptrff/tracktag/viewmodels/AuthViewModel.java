package ru.ptrff.tracktag.viewmodels;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.api.MapsRepository;
import ru.ptrff.tracktag.api.dto.LoginRequest;
import ru.ptrff.tracktag.api.dto.LoginResponse;
import ru.ptrff.tracktag.api.dto.RegisterRequest;
import ru.ptrff.tracktag.api.dto.RegisterResponse;
import ru.ptrff.tracktag.data.UserData;

public class AuthViewModel extends ViewModel {

    private final MutableLiveData<Boolean> loggedIn = new MutableLiveData<>();
    private final MutableLiveData<Integer> authError = new MutableLiveData<>();
    private MapsRepository repo;

    public AuthViewModel() {
        repo = new MapsRepository();
    }

    @SuppressLint("CheckResult")
    public void register(String login, String password) {
        repo
                .register(new RegisterRequest(login, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        registerResponse -> handleRegisterResponse(registerResponse, login, password),
                        throwable -> Log.e(getClass().getCanonicalName(), throwable.toString())
                );
    }

    @SuppressLint("CheckResult")
    public void login(String login, String password) {
        repo
                .login(new LoginRequest(login, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loginResponse -> handleLoginResponse(loginResponse, login),
                        throwable -> Log.e(getClass().getCanonicalName(), throwable.toString())
                );
    }

    private void handleRegisterResponse(RegisterResponse registerResponse, String login, String password) {
        if (registerResponse.getId() != null && registerResponse.getUsername() != null) {
            UserData.getInstance().setUserId(registerResponse.getId());
            UserData.getInstance().setUserName(registerResponse.getUsername());
            login(login, password);
        } else if (registerResponse.getDetail() != null
                && registerResponse.getDetail().equals("REGISTER_USER_ALREADY_EXISTS")) {
            authError.postValue(R.string.user_exists);
        } else if (registerResponse.getDetail() != null
                && registerResponse.getDetail().equals("REGISTER_INVALID_PASSWORD")) {
            authError.postValue(R.string.password_too_short);
        } else {
            authError.postValue(R.string.unknown_error);
        }
    }

    private void handleLoginResponse(LoginResponse loginResponse, String login) {
        if (Objects.equals(loginResponse.getTokenType(), "bearer")) {
            UserData.getInstance().setLoggedIn(true);
            UserData.getInstance().setAccessToken(loginResponse.getAccessToken());
            UserData.getInstance().setUserName(login);
            loggedIn.postValue(true);
        } else if (loginResponse.getDetail() != null
                && loginResponse.getDetail().equals("LOGIN_BAD_CREDENTIALS")) {
            authError.postValue(R.string.login_bad_credentials);
        } else if (loginResponse.getDetail() != null
                && loginResponse.getDetail().equals("LOGIN_USER_NOT_VERIFIED")) {
            authError.postValue(R.string.login_user_not_verified);
        } else {
            authError.postValue(R.string.unknown_error);
        }
    }

    public MutableLiveData<Boolean> getLoggedIn() {
        return loggedIn;
    }

    public MutableLiveData<Integer> getAuthError() {
        return authError;
    }
}
