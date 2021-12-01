/*
 * Copyright 2021 The Little Drops of Rain Project
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.web.diegoflassa_site.littledropsofrain.data.di

import app.web.diegoflassa_site.littledropsofrain.data.dao.*
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.IluriaProductsService
import app.web.diegoflassa_site.littledropsofrain.data.repository.*
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

    @Singleton
    @Provides
    fun provideProductsRepository(productsDao: ProductsDao): ProductsRepository {
        return ProductsRepository(productsDao)
    }

    @Singleton
    @Provides
    fun provideUsersRepository(usersDao: UsersDao): UsersRepository {
        return UsersRepository(usersDao)
    }

    @Singleton
    @Provides
    fun provideMessagesRepository(messagesDao: MessagesDao): MessagesRepository {
        return MessagesRepository(messagesDao)
    }

    @Singleton
    @Provides
    fun provideFilesRepository(filesDao: FilesDao): FilesRepository {
        return FilesRepository(filesDao)
    }

    @Singleton
    @Provides
    fun provideCategoriesRepository(categoriesDao: CategoriesDao): CategoriesRepository {
        return CategoriesRepository(categoriesDao)
    }
}
