package com.cleanarch.features.wikientry.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cleanarch.app.AppDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WikiEntryDaoTest {

    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    @Before fun initDb() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After fun closeDb() {
        database.close()
    }

    @Test fun testInsertAndGetWikiEntry() {

        database.wikiEntryDao().insert(WIKI_ENTRY)

        database.wikiEntryDao()
            .getByTitle(WIKI_ENTRY.title)
            .test() // assertValue asserts that there was only one emission
            .assertValue{
                it.size == 1 &&
                        it[0].pageId == WIKI_ENTRY.pageId &&
                        it[0].extract == WIKI_ENTRY.extract
            }

    }

    @Test fun testGetWikiEntryWhenNoneInserted() {
        database.wikiEntryDao()
            .getByTitle(WIKI_ENTRY.title)
            .test() // assertValue asserts that there was only one emission
            .assertValue{
                it.isEmpty()
            }
    }

    @Test fun testDeleteAndGetWikiEntry() {

        database.wikiEntryDao().insert(WIKI_ENTRY)
        database.wikiEntryDao().delete(WIKI_ENTRY)

        database.wikiEntryDao()
            .getByTitle(WIKI_ENTRY.title)
            .test() // assertValue asserts that there was only one emission
            .assertValue{
                it.isEmpty()
            }
    }

    companion object {
        private val WIKI_ENTRY = WikiEntryTable(pageId = 17867,
            title = "London",
            extract = "The City of London, its ancient core and financial centre, " +
                    "was founded by the Romans as Londinium "
        )
    }
}