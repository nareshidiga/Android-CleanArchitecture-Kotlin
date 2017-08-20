package com.cleanarch.features.wikientry.data.local

import android.arch.persistence.room.*
import io.reactivex.Flowable

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

@Dao
interface WikiEntryDao {

    @get:Query("SELECT * FROM WikiEntryTable")
    val all: Flowable<List<WikiEntryTable>>

    @Query("SELECT * FROM WikiEntryTable WHERE title = :wikiTitle COLLATE NOCASE")
    fun getByTitle(wikiTitle: String): Flowable<List<WikiEntryTable>>


    @Query("SELECT * FROM WikiEntryTable WHERE pageid IN (:wikiPageIds)")
    fun loadAllByIds(wikiPageIds: IntArray): Flowable<List<WikiEntryTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wikiEntry: WikiEntryTable)

    @Delete
    fun delete(wikiEntry: WikiEntryTable)
}