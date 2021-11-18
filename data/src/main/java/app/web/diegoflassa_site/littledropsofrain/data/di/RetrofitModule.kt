package app.web.diegoflassa_site.littledropsofrain.data.di

import app.web.diegoflassa_site.littledropsofrain.data.http.buildRetrofitClientApi
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.IluriaProductsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient()
        return buildRetrofitClientApi(okHttpClient)
    }

    @Provides
    fun provideIluriaProductsService(retrofit: Retrofit): IluriaProductsService {
        return retrofit.create(IluriaProductsService::class.java)
    }
}
