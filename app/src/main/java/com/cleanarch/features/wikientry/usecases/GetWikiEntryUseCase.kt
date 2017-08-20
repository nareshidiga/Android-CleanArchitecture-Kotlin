package com.cleanarch.features.wikientry.usecases

import android.util.Log
import com.cleanarch.base.usecases.UseCase
import com.cleanarch.features.wikientry.data.WikiEntryRepo
import com.cleanarch.features.wikientry.entities.WikiEntry
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
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
class GetWikiEntryUseCase @Inject
internal constructor(private val repo: WikiEntryRepo) : UseCase<GetWikiEntryUseCase.Input, WikiEntry>() {

    override fun execute(input: Input, subscriber: DisposableSubscriber<WikiEntry>) {

        Flowable.just(input.title)
                .flatMap<WikiEntry> { title -> repo.getWikiEntry(title) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(input.observerOnScheduler)
                .subscribe(subscriber)

        Log.d(TAG, "called subscribe on getWikiEntry flowable")

        disposables.add(subscriber)
    }

    class Input(val title: String, val observerOnScheduler: Scheduler)

    companion object {

        private val TAG = GetWikiEntryUseCase::class.java.simpleName
    }

}