package com.abelgas.mobile.utils

import android.content.Context
import android.content.SharedPreferences
import com.abelgas.mobile.models.LoginData
import com.google.gson.Gson

class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "UserAuthPrefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_DATA = "user_data"
    }

    fun saveToken(token: String, userData: LoginData) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USER_DATA, gson.toJson(userData))
            apply()
        }
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getUserData(): LoginData? {
        val json = prefs.getString(KEY_USER_DATA, null)
        return if (json != null) {
            gson.fromJson(json, LoginData::class.java)
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun clearToken() {
        prefs.edit().clear().apply()
    }
}