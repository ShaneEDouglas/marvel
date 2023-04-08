package com.example.marvel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Headers
import okhttp3.internal.http2.Header
import okio.HashingSink.Companion.md5
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import okhttp3.*
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient

import okhttp3.Request
import okio.IOException


var marUrl = ""

fun md5(input: String): String {
    return try {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        Base64.encodeToString(digest, Base64.NO_WRAP)
    } catch (e: NoSuchAlgorithmException) {
        ""
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var rvCharacters: RecyclerView
    private lateinit var charactersAdapter: marveldapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvCharacters = findViewById(R.id.Marvellay)
        rvCharacters.layoutManager = LinearLayoutManager(this)

        val characters = mutableListOf<MarvelCharacter>()
        charactersAdapter = marveldapter(characters)

        rvCharacters.adapter = charactersAdapter

        fun parseCharacterData(results: JSONArray) {
            val characters = mutableListOf<MarvelCharacter>()

            for (i in 0 until results.length()) {
                val characterJson = results.getJSONObject(i)
                val id = characterJson.getInt("id")
                val name = characterJson.getString("name")
                val description = characterJson.getString("description")
                val thumbnail = characterJson.getJSONObject("thumbnail")
                val imageUrl =
                    thumbnail.getString("path") + "." + thumbnail.getString("extension")

                val character = MarvelCharacter(id, name, description, imageUrl)
                characters.add(character)
            }

            charactersAdapter.updateData(characters)

        }

        fun getmarurl() {


            val pubkey = "0136a7cff700d1737e2a96a7820e2253"
            val priKey = "dc622f842f3d815195ddc4e5167ddc11453b6de9"
            val timestamp = System.currentTimeMillis()
            val hash = md5("$timestamp$pubkey$priKey")

            val url = "https://gateway.marvel.com/v1/public/characters".toHttpUrlOrNull()
                ?.newBuilder()
                ?.addQueryParameter("apikey",pubkey)
                ?.addQueryParameter("ts", timestamp.toString())
                ?.addQueryParameter("hash", hash)
                ?.build()

            val request = Request.Builder()
                .url(url!!)
                .build()

            val client = OkHttpClient()


            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody)
                    Log.d("Api_Response","Response: $responseBody")
                    val data = jsonResponse.getJSONObject("data")
                    val results = data.getJSONArray("results")
                    parseCharacterData(results)
                } catch (e: IOException) {
                    // Handle the failure and alert the user
                }
            }
        }

        getmarurl()

            }
        }




