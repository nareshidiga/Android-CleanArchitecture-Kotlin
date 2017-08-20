package com.cleanarch.features.wikientry.data

import android.util.Log
import com.cleanarch.app.AppDatabase
import com.cleanarch.features.wikientry.data.local.WikiEntryTable
import com.cleanarch.features.wikientry.data.remote.WikiApiService
import com.cleanarch.features.wikientry.data.remote.WikiEntryApiResponse
import com.cleanarch.features.wikientry.entities.WikiEntry
import io.reactivex.Flowable
import io.reactivex.functions.Function
import javax.inject.Inject
import javax.inject.Singleton

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

@Singleton
class WikiEntryRepoImpl @Inject
constructor(private val appDatabase: AppDatabase, private val wikiApiService: WikiApiService) : WikiEntryRepo {

    override fun getWikiEntry(title: String): Flowable<WikiEntry> {

        val local = fetchFromLocal(title)
        val remote = fetchFromRemote(title)
        return Flowable.merge(local, remote).firstElement().toFlowable()
    }


    private fun fetchFromLocal(title: String): Flowable<WikiEntry> {

        val entries = appDatabase.wikiEntryDao().getByTitle(title)
        return entries.flatMap(Function<List<WikiEntryTable>, Flowable<WikiEntry>> { wikiEntryTables ->

            if (!wikiEntryTables.isEmpty()) {
                val firstEntry = wikiEntryTables[0]
                Log.d(TAG, "Found and sending entry from local")
                return@Function Flowable.just(WikiEntry(firstEntry.pageId,
                        firstEntry.title!!, firstEntry.extract!!))
            }

            Log.d(TAG, "Returning flowable with invalid entry from local")
            Flowable.empty<WikiEntry>()
        })

    }


    private fun fetchFromRemote(title: String): Flowable<WikiEntry> {

        Log.d(TAG, "fetchFromRemote enter")
        val getRequest = wikiApiService.getWikiEntry(title)
        return getRequest.flatMap { wikiEntryApiResponse ->

            Log.d(TAG, "received response from remote")
            val pageValIterator = wikiEntryApiResponse.query!!.pages!!.values.iterator()
            val pageVal = pageValIterator.next()

            if (invalidResult(pageVal)) {
                Log.d(TAG, "Sending error from remote")
                Flowable.error(NoResultFound())
            } else {
                val wikiEntry = WikiEntry(pageVal.pageid!!, pageVal.title!!, pageVal.extract!!)
                addNewEntryToLocalDB(wikiEntry)
                Log.d(TAG, "Sending entry from remote")
                Flowable.just(wikiEntry)
            }
        }
    }


    private fun addNewEntryToLocalDB(wikiEntry: WikiEntry) {
        appDatabase.beginTransaction()
        try {
            val newEntry = WikiEntryTable()
            newEntry.pageId = wikiEntry.pageId
            newEntry.title = wikiEntry.title
            newEntry.extract = wikiEntry.extract

            val entryDao = appDatabase.wikiEntryDao()
            entryDao.insert(newEntry)
            appDatabase.setTransactionSuccessful()
        } finally {
            appDatabase.endTransaction()
        }
        Log.d(TAG, "added new entry into app database table")
    }

    private fun invalidResult(pageVal: WikiEntryApiResponse.Pageval): Boolean {
        return pageVal.pageid == null || pageVal.pageid!! <= 0 ||
                pageVal.title == null || pageVal.title!!.isEmpty() ||
                pageVal.extract == null || pageVal.extract!!.isEmpty()
    }

    companion object {

        private val TAG: String = WikiEntryRepoImpl::class.java.simpleName!!
    }

}