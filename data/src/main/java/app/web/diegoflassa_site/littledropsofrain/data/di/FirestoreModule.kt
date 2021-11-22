package app.web.diegoflassa_site.littledropsofrain.data.di

import app.web.diegoflassa_site.littledropsofrain.data.dao.FilesDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessagesDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductsDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.UsersDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule {

    @Provides
    fun provideUserDao(): UsersDao {
        return UsersDao
    }

    @Provides
    fun provideFilesDao(): FilesDao {
        return FilesDao
    }

    @Provides
    fun provideMessageDao(): MessagesDao {
        return MessagesDao
    }

    @Provides
    fun provideProductsDao(): ProductsDao {
        return ProductsDao
    }
}
