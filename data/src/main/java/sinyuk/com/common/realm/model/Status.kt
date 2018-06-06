/*
 *   Copyright 2081 Sinyuk
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package sinyuk.com.common.realm.model

import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import sinyuk.com.common.Source
import java.util.*

/**
 * Created by sinyuk on 2017/3/28.
 *
 */
@RealmClass
open class Status @JvmOverloads constructor(
        @PrimaryKey @Required val id: String,
        var text: String?,
        val agent: String,
        val location: String?,
        val player: Player,
        @Required val createdAt: Date,
        var favorited: Boolean,
        var photos: RealmList<Photo>?,
        var source: Source? = null)



