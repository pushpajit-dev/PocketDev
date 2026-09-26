package com.noishixzen.pocketcode.network

import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

data class NetlifySite(
    val id: String,
    val name: String,
    val url: String,
    val sslUrl: String,
    val adminUrl: String,
    val deployUrl: String,
    val state: String,
)

class NetlifyClient {

    fun deployZip(token: String, zipFile: File, siteName: String? = null): Result<NetlifySite> {
        return runCatching {
            val urlString = if (siteName.isNullOrBlank()) {
                "https://api.netlify.com/api/v1/sites"
            } else {
                "https://api.netlify.com/api/v1/sites?name=$siteName"
            }
            val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 30_000
                readTimeout = 90_000
                doOutput = true
                setRequestProperty("Content-Type", "application/zip")
                setRequestProperty("Authorization", "Bearer ${token.trim()}")
                setRequestProperty("User-Agent", "PocketDev/4.1")
                setFixedLengthStreamingMode(zipFile.length())
            }

            zipFile.inputStream().use { input ->
                connection.outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            val statusCode = connection.responseCode
            val responseStream: InputStream = if (statusCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream ?: connection.inputStream
            }
            val body = responseStream.bufferedReader().use { it.readText() }

            if (statusCode in 200..299) {
                val json = JSONObject(body)
                val siteId = json.optString("id")
                val name = json.optString("name")
                val siteUrl = json.optString("url")
                val sslUrl = json.optString("ssl_url").ifBlank { siteUrl }
                val adminUrl = json.optString("admin_url").ifBlank { "https://app.netlify.com/sites/$name" }
                val deployUrl = json.optString("deploy_url").ifBlank { sslUrl }
                val state = json.optString("state", "ready")

                NetlifySite(
                    id = siteId,
                    name = name,
                    url = siteUrl,
                    sslUrl = sslUrl,
                    adminUrl = adminUrl,
                    deployUrl = deployUrl,
                    state = state,
                )
            } else {
                val message = runCatching {
                    val errorJson = JSONObject(body)
                    errorJson.optString("message", errorJson.optString("error", "HTTP $statusCode: $body"))
                }.getOrDefault("HTTP $statusCode: $body")
                error(message)
            }
        }
    }

    fun verifyToken(token: String): Boolean {
        if (token.isBlank()) return false
        return runCatching {
            val connection = (URL("https://api.netlify.com/api/v1/user").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
                setRequestProperty("Authorization", "Bearer ${token.trim()}")
                setRequestProperty("User-Agent", "PocketDev/4.1")
            }
            connection.responseCode in 200..299
        }.getOrDefault(false)
    }
}
