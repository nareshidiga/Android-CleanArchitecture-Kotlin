package com.cleanarch.features.wikientry.usecases

import com.cleanarch.features.wikientry.data.WikiEntryRepo
import com.cleanarch.features.wikientry.entities.WikiEntry
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subscribers.DisposableSubscriber
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class GetWikiEntryUseCaseTest {

    @Mock
    private lateinit var mockRepo: WikiEntryRepo

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testGetWikiEntryUseCase() {

        val useCase = GetWikiEntryUseCase(mockRepo)
        useCase.execute(
            GetWikiEntryUseCase.Input(WIKI_QUERY, TestScheduler()),
            UseCaseSubscriber()
        )
        verify(mockRepo).getWikiEntry(WIKI_QUERY)
    }

    inner class UseCaseSubscriber : DisposableSubscriber<WikiEntry>() {
        override fun onNext(wikiEntry: WikiEntry) {
        }
        override fun onError(e: Throwable) {
        }
        override fun onComplete() {
        }
    }

    companion object {
        private const val WIKI_QUERY = "TestTitle"
    }
}