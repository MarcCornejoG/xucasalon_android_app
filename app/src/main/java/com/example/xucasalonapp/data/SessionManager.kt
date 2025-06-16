package com.example.xucasalonapp.data

import android.content.Context
import com.example.xucasalonapp.ui.login.data.model.AuthUser
import com.example.xucasalonapp.ui.login.data.model.FirebaseUser
import com.example.xucasalonapp.ui.usuario.data.model.Cliente
import com.google.gson.Gson

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "key_token"
        private const val KEY_CLIENTE_JSON = "key_cliente_json"
        private const val KEY_FIREBASE_USER_JSON = "key_firebase_user_json"
        private const val KEY_AUTH_TYPE = "key_auth_type"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUser(cliente: Cliente) {
        val json = Gson().toJson(cliente)
        prefs.edit()
            .putString(KEY_CLIENTE_JSON, json)
            .apply()
    }

    fun getUser(): Cliente? {
        val json = prefs.getString(KEY_CLIENTE_JSON, null) ?: return null
        return Gson().fromJson(json, Cliente::class.java)
    }

    fun saveGoogleUser(firebaseUser: FirebaseUser) {
        val json = Gson().toJson(firebaseUser)
        prefs.edit()
            .putString(KEY_FIREBASE_USER_JSON, json)
            .putString(KEY_AUTH_TYPE, "google")
            .apply()
    }

    fun getGoogleUser(): FirebaseUser? {
        val json = prefs.getString(KEY_FIREBASE_USER_JSON, null) ?: return null
        return Gson().fromJson(json, FirebaseUser::class.java)
    }

    // MÉTODO ACTUALIZADO: Ahora prioriza datos completos del Cliente
    fun getCurrentAuthUser(): AuthUser? {
        val cliente = getUser()
        if (cliente != null) {
            return AuthUser.TraditionalUser(cliente)
        }

        val firebaseUser = getGoogleUser()
        return if (firebaseUser != null) {
            AuthUser.FirebaseAuthUser(firebaseUser)
        } else null
    }

    fun saveHybridUser(cliente: Cliente, firebaseUser: FirebaseUser) {
        val clienteJson = Gson().toJson(cliente)
        val firebaseJson = Gson().toJson(firebaseUser)

        prefs.edit()
            .putString(KEY_CLIENTE_JSON, clienteJson)
            .putString(KEY_FIREBASE_USER_JSON, firebaseJson)
            .putString(KEY_AUTH_TYPE, "hybrid")
            .apply()
    }

    fun getAuthType(): String? = prefs.getString(KEY_AUTH_TYPE, null)

    fun isLoggedIn(): Boolean {
        return when (getAuthType()) {
            "traditional" -> getToken() != null && getUser() != null
            "google" -> getGoogleUser() != null
            "hybrid" -> getUser() != null && getGoogleUser() != null
            else -> false
        }
    }

    fun hasCompleteUserProfile(): Boolean {
        return getUser() != null
    }

    fun isGoogleUser(): Boolean {
        val authType = getAuthType()
        return authType == "google" || authType == "hybrid"
    }

    fun needsProfileCompletion(): Boolean {
        return getAuthType() == "google" && getUser() == null
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun clearFirebaseData() {
        prefs.edit()
            .remove(KEY_FIREBASE_USER_JSON)
            .putString(KEY_AUTH_TYPE, "traditional")
            .apply()
    }

    fun getCompleteUserInfo(): Pair<Cliente?, FirebaseUser?> {
        return Pair(getUser(), getGoogleUser())
    }

    fun SessionManager.handleFirebaseToHybridTransition(cliente: Cliente) {
        val firebaseUser = getGoogleUser()
        if (firebaseUser != null) {
            saveHybridUser(cliente, firebaseUser)
        } else {
            prefs.edit().putString(KEY_AUTH_TYPE, "traditional").apply()
            saveUser(cliente)
        }
    }
}


