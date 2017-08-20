package com.cleanarch.features.wikientry.data.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.HashMap

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

class WikiEntryApiResponse {

    @SerializedName("batchcomplete")
    @Expose
    var batchcomplete: String? = null

    @SerializedName("query")
    @Expose
    var query: Query? = null


    inner class Query {

        @SerializedName("pages")
        @Expose
        var pages: HashMap<String, Pageval>? = null

    }


    inner class Pageval {

        @SerializedName("pageid")
        @Expose
        var pageid: Int? = null
        @SerializedName("ns")
        @Expose
        var ns: Int? = null
        @SerializedName("title")
        @Expose
        var title: String? = null
        @SerializedName("extract")
        @Expose
        var extract: String? = null

    }

}
