package com.example.ultimamilla.propuesta.Shared

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log

class TokenShared(context: Context) {

    private val preferences = context.getSharedPreferences(
        "session",
        Context.MODE_PRIVATE
    )

    fun guardarToken(token: String) {
        preferences.edit()
            .putString("token", token)
            .apply()
    }

    fun obtenerToken(): String? {
        return preferences.getString("token", null)
    }

    fun eliminarToken() {
        preferences.edit()
            .remove("token")
            .apply()
    }
}

class FolioShared(context: Context){
    private val sp: SharedPreferences = context.getSharedPreferences("folio_saved", MODE_PRIVATE)
    private val edit: SharedPreferences.Editor = sp.edit()
    private val clear: SharedPreferences.Editor = edit.clear()

    fun saveFolio(folio:String){

        clear.apply()
        edit.putString("folio",folio).apply()
    }
    fun clearFolio(){
        clear.apply()
    }

    fun getFolio():String{
        return sp.getString("folio","null").toString()
    }
}

class UserShared(context: Context){
    private val sp: SharedPreferences = context.getSharedPreferences("user_saved", MODE_PRIVATE)
    private val edit: SharedPreferences.Editor = sp.edit()
    private val clear: SharedPreferences.Editor = edit.clear()

    fun saveUser(user: String) {
        val editor = sp.edit()
        editor.putString("user", user)
        editor.apply()
        Log.d("UserShared", "Usuario guardado: $user")
    }

    fun getUser(): String {
        val user = sp.getString("user", "") ?: ""
        Log.d("UserShared", "Usuario recuperado: $user")
        return user
    }

    fun clearUser(){
        clear.apply()
    }
}

class SharedPref(context: Context){
    private val spOficina: SharedPreferences = context.getSharedPreferences("oficina_saved", MODE_PRIVATE)
    private val spUser: SharedPreferences = context.getSharedPreferences("user_saved", MODE_PRIVATE)
    private val spFolio: SharedPreferences = context.getSharedPreferences("folio_saved", MODE_PRIVATE)
    private val spToken: SharedPreferences = context.getSharedPreferences("token_saved", MODE_PRIVATE)

    fun clearShared(){
        spToken.edit().clear().apply()
        spFolio.edit().clear().apply()
        spOficina.edit().clear().apply()
        spUser.edit().clear().apply()
    }

}

class ServerShared(context: Context){
    private val sp: SharedPreferences = context.getSharedPreferences("serverURL", MODE_PRIVATE)
    private val edit: SharedPreferences.Editor = sp.edit()
    private val clear: SharedPreferences.Editor = edit.clear()

    fun saveServerURL(serverURL:String){

        clear.apply()
        edit.putString("serverURL",serverURL).apply()
    }
    fun clearServerURL(){
        clear.apply()
    }

    fun getServerURL():String{
        return sp.getString("serverURL","").toString()
    }
}

class OficinaShared(context: Context){
    private val sp: SharedPreferences = context.getSharedPreferences("oficina_saved", MODE_PRIVATE)
    private val edit : SharedPreferences.Editor = sp.edit()
    private val clear : SharedPreferences.Editor = edit.clear()

    fun saveOficina(oficina:String){
        clear.apply()
        edit.putString("oficina", oficina).apply()
    }
    fun getOficina():String{
        return sp.getString("oficina","null").toString()
    }
    fun clearOficina(){
        clear.apply()
    }
}


