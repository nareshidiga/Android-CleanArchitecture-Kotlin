package com.cleanarch.features.wikientry

import com.cleanarch.app.AppDatabase
import com.cleanarch.features.wikientry.data.WikiEntryRepoImpl
import com.cleanarch.features.wikientry.data.local.WikiEntryDao
import com.cleanarch.features.wikientry.data.remote.WikiApiService
import com.cleanarch.features.wikientry.data.WikiEntryRepo
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/*
 * Copyright (C) 2017 Naresh Gowd Idiga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Module
class WikiEntryModule {

    @WikiEntryScope
    @Provides
    internal fun provideWikiEntryDao(db: AppDatabase): WikiEntryDao {
        return db.wikiEntryDao()
    }

    @WikiEntryScope
    @Provides
    internal fun provideWikiApiService(): WikiApiService {
        return Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WikiApiService::class.java)
    }

    @WikiEntryScope
    @Provides
    internal fun provideWikiEntryRepo(appDatabase: AppDatabase, apiService: WikiApiService): WikiEntryRepo {
        return WikiEntryRepoImpl(appDatabase, apiService)
    }
}