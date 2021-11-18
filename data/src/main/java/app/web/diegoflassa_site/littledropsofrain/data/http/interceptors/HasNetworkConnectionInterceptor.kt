package app.web.diegoflassa_site.littledropsofrain.data.http.interceptors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import app.web.diegoflassa_site.littledropsofrain.data.helpers.noConnectivityResponse
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

internal class HasNetworkConnectionInterceptor(private val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        // Original request
        val original: Request = chain.request()
        val isNetworkConnected = isInternetAvailable(context)
        if (isNetworkConnected) return chain.proceed(chain.request())
        return noConnectivityResponse(original)
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val result: Boolean
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }
}
