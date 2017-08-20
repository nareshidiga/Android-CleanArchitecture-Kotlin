package com.cleanarch.features.wikientry.presentation

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.cleanarch.R
import com.cleanarch.app.CleanArchApp
import com.cleanarch.features.wikientry.entities.WikiEntry
import com.cleanarch.features.wikientry.usecases.GetWikiEntryUseCase
import dagger.Lazy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

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

class WikiEntryViewModel(application: Application) : AndroidViewModel(application) {

    private var wikiEntry: MutableLiveData<WikiEntry>

    @Inject
    lateinit var getWikiEntryUseCase: Lazy<GetWikiEntryUseCase>

    init {
        (application as CleanArchApp).wikiEntryComponent!!.inject(this)
        wikiEntry = MutableLiveData<WikiEntry>()
    }

    internal fun getWikiEntry(): LiveData<WikiEntry> {
        return wikiEntry
    }

    internal fun loadWikiEntry(title: String) {

        getWikiEntryUseCase.get().execute(
                GetWikiEntryUseCase.Input(title, AndroidSchedulers.mainThread()),
                UseCaseSubscriber())
    }


    override fun onCleared() {
        super.onCleared()
        // remove subscriptions if any
        getWikiEntryUseCase.get().cancel()
        Log.d(TAG, "onCleared")
    }

     inner class UseCaseSubscriber : DisposableSubscriber<WikiEntry>() {

        override fun onNext(wikiEntry: WikiEntry) {

            Log.d(TAG, "Received response for wikiEntry")
            this@WikiEntryViewModel.wikiEntry.value = wikiEntry
        }

        override fun onError(e: Throwable) {

            Log.d(TAG, "Received error: " + e.toString())
            this@WikiEntryViewModel.wikiEntry.value = WikiEntry(-1, "",
                    getApplication<Application>().getString(R.string.no_results_found))
        }

        override fun onComplete() {
            Log.d(TAG, "onComplete called")
        }
    }

    companion object {
        private val TAG = WikiEntryViewModel::class.java.simpleName
    }
}
