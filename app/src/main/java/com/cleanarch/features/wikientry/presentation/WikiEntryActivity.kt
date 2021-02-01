package com.cleanarch.features.wikientry.presentation


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.cleanarch.R
import com.cleanarch.app.CleanArchApp
import com.cleanarch.features.wikientry.entities.WikiEntry
import kotlinx.android.synthetic.main.activity_main.*

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

class WikiEntryActivity : AppCompatActivity() {

    private val TAG = WikiEntryActivity::class.java.simpleName
    private lateinit var  wikiEntryViewModel: WikiEntryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // WikiEntry feature component scope start here
        (application as CleanArchApp).buildWikiEntryComponent()

        setContentView(R.layout.activity_main)

        submitButton.setOnClickListener(submitButtonOnClickListener)

        wikiEntryViewModel = ViewModelProviders.of(this).get(WikiEntryViewModel::class.java)
        wikiEntryViewModel.getWikiEntry().observe(this, Observer<WikiEntry> { wikiEntry ->
            Log.d(TAG, "received update for wikiEntry")
            entryDetails.text = wikiEntry!!.extract
            progressBar.hide()
        })

        Observer<WikiEntry> {
            wikiEntry: WikiEntry? ->
            entryDetails.text = wikiEntry!!.extract
            progressBar.hide()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // WikiEntry feature component scope ends here
        (application as CleanArchApp).releaseWikiEntryComponent()
    }

    private val submitButtonOnClickListener = View.OnClickListener { v ->

        progressBar.show()
        val imm = this@WikiEntryActivity.baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
        wikiEntryViewModel.loadWikiEntry(entryTitle.text.toString())
    }
}
