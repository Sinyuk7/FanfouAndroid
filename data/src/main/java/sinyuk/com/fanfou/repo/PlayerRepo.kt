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

package sinyuk.com.fanfou.repo

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import sinyuk.com.common.AppExecutors
import sinyuk.com.common.Fanfou
import sinyuk.com.common.Promise
import sinyuk.com.common.api.NetworkBoundResource
import sinyuk.com.common.api.RateLimiter
import sinyuk.com.common.realm.Default
import sinyuk.com.common.realm.InMemory
import sinyuk.com.common.realm.RealmLiveData
import sinyuk.com.common.realm.model.Player
import sinyuk.com.common.realm.model.SOURCE_FANFOU
import sinyuk.com.fanfou.api.FanfouAPI
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by sinyuk on 2018/6/6.
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│        _______. __  .__   __. ____    ____  __    __   __  ___   │
│       /       ||  | |  \ |  | \   \  /   / |  |  |  | |  |/  /   │
│      |   (----`|  | |   \|  |  \   \/   /  |  |  |  | |  '  /    │
│       \   \    |  | |  . `  |   \_    _/   |  |  |  | |    <     │
│   .----)   |   |  | |  |\   |     |  |     |  `--'  | |  .  \    │
│   |_______/    |__| |__| \__|     |__|      \______/  |__|\__\   │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
 */
@Singleton
@Fanfou
class PlayerRepo @Inject constructor(
        private val api: FanfouAPI,
        private val appExecutors: AppExecutors,
        @Fanfou private val rateLimiter: RateLimiter<String>,
        @Default private val defaultRealm: RealmConfiguration,
        @InMemory private val memoryRealm: RealmConfiguration) {

    /**
     *
     */
    fun loadUser(id: String): LiveData<Promise<RealmResults<Player>>> {
        return object : NetworkBoundResource<Player, Player>(appExecutors) {
            override fun onFetchFailed() {

            }

            override fun saveCallResult(item: Player?) {
                if (item != null) {
                    item.source = SOURCE_FANFOU
                    Realm.getInstance(defaultRealm).executeTransaction { it.insertOrUpdate(item) }
                }
            }

            override fun shouldFetch(data: RealmResults<Player>?) =
                    data == null || data.isEmpty() || rateLimiter.shouldFetch(RateLimiter.FANFOU)


            override fun loadFromDb(): RealmLiveData<Player> {
                val result = Realm.getInstance(defaultRealm)
                        .where(Player::class.java)
                        .equalTo("source", SOURCE_FANFOU)
                        .equalTo("id", id)
                        .findAll()
                return RealmLiveData(result)
            }

            override fun createCall() = api.showUser(id)

        }.asLiveData()
    }

    fun loadFriends(id: String? = null, count: Int, page: Int) = api.friends(id, count, page)
}