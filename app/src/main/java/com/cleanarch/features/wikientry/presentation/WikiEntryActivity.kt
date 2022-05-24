package com.cleanarch.features.wikientry.presentation


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.cleanarch.app.CleanArchApp
import com.cleanarch.databinding.ActivityMainBinding
import com.cleanarch.features.wikientry.entities.WikiEntry

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
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // WikiEntry feature component scope start here
        (application as CleanArchApp).buildWikiEntryComponent()

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.submitButton.setOnClickListener(submitButtonOnClickListener)

        wikiEntryViewModel = ViewModelProvider(this).get(WikiEntryViewModel::class.java)
        wikiEntryViewModel.getWikiEntry().observe(this, Observer<WikiEntry> { wikiEntry ->
            Log.d(TAG, "received update for wikiEntry")
            binding.entryDetails.text = wikiEntry?.extract
            binding.progressBar.hide()
        })

        Observer<WikiEntry> {
            wikiEntry: WikiEntry? ->
            binding.entryDetails.text = wikiEntry?.extract
            binding.progressBar.hide()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // WikiEntry feature component scope ends here
        (application as CleanArchApp).releaseWikiEntryComponent()
    }

    private val submitButtonOnClickListener = View.OnClickListener { v ->
        binding.progressBar.show()
        val inputMethodManager = this@WikiEntryActivity.baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
        wikiEntryViewModel.loadWikiEntry(binding.entryTitle.text.toString())
    }
}
