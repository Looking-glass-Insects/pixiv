package com.eps3rd.pixiv.api;

import com.eps3rd.pixiv.Common;
import com.eps3rd.pixiv.models.UserModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

/**
 * 全局自动刷新Token的拦截器
 */
public class TokenInterceptor implements Interceptor {

    private static final String TOKEN_ERROR_1 = "Error occurred at the OAuth process";
    private static final String TOKEN_ERROR_2 = "Invalid refresh token";
    private static final int TOKEN_LENGTH = 50;

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (isTokenExpired(response)) {
            response.close();
            String newToken = getNewToken(request.header("Authorization"));
            Request newRequest = chain.request()
                    .newBuilder()
                    .header("Authorization", newToken)
                    .build();
            return chain.proceed(newRequest);
        }
        return response;
    }


    /**
     * 根据Response，判断Token是否失效
     *
     * @param response
     * @return
     */
    private boolean isTokenExpired(Response response) {
        final String body = Common.Companion.getResponseBody(response);
        if (response.code() == 400) {
            if (body.contains(TOKEN_ERROR_1)) {
                return true;
            } else if(body.contains(TOKEN_ERROR_2)){
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 同步请求方式，获取最新的Token，解决多并发请求多次刷新token的问题
     *
     * @return
     */
    private synchronized String getNewToken(String tokenForThisRequest) throws IOException {
        UserModel userModel =  UserHandle.INSTANCE.getUserModel();
        if (userModel.getResponse().getAccess_token().equals(tokenForThisRequest) ||
                tokenForThisRequest.length() != TOKEN_LENGTH ||
                userModel.getResponse().getAccess_token().length() != TOKEN_LENGTH) {
            Call<UserModel> call = UserHandle.INSTANCE.getApi().accountApi.refreshToken(
                    BasePixivApi.CLIENT_ID,
                    BasePixivApi.CLIENT_SECRET,
                    "refresh_token",
                    userModel.getResponse().getRefresh_token(),
                    userModel.getResponse().getDevice_token(),
                    Boolean.TRUE,
                    Boolean.TRUE);
            UserModel newUser = call.execute().body();
            if (newUser != null) {
                newUser.getResponse().getUser().setPassword(
                        userModel.getResponse().getUser().getPassword()
                );
                newUser.getResponse().getUser().setIs_login(true);
            }
            UserHandle.INSTANCE.setUserModel(newUser);
            return newUser.getResponse().getAccess_token();
        } else {
            return userModel.getResponse().getAccess_token();
        }
    }
}
