package app.web.diegoflassa_site.littledropsofrain.data.di

import app.web.diegoflassa_site.littledropsofrain.data.interfaces.IluriaProductsService
import app.web.diegoflassa_site.littledropsofrain.data.repository.IluriaProductsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoriesModule {

    @Singleton
    @Provides
    fun provideIluriaProductsRepository(iluriaProductsService: IluriaProductsService): IluriaProductsRepository {
        return IluriaProductsRepository(iluriaProductsService)
    }
}
