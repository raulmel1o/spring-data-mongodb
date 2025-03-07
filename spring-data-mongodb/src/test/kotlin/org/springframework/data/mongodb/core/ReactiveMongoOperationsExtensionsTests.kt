/*
 * Copyright 2017-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mongodb.core

import example.first.First
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.query.NearQuery
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import reactor.core.publisher.Mono

/**
 * @author Sebastien Deleuze
 * @author Christoph Strobl
 * @author Mark Paluch
 * @author Wonwoo Lee
 */
class ReactiveMongoOperationsExtensionsTests {

	val operations = mockk<ReactiveMongoOperations>(relaxed = true)

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `indexOps(KClass) extension should call its Java counterpart`() {

		operations.indexOps(First::class)
		verify { operations.indexOps(First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `indexOps() with reified type parameter extension should call its Java counterpart`() {

		operations.indexOps<First>()
		verify { operations.indexOps(First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `execute(ReactiveCollectionCallback) with reified type parameter extension should call its Java counterpart`() {

		val collectionCallback = mockk<ReactiveCollectionCallback<First>>()

		operations.execute(collectionCallback)
		verify { operations.execute(First::class.java, collectionCallback) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `createCollection(KClass) extension should call its Java counterpart`() {

		operations.createCollection(First::class)
		verify { operations.createCollection(First::class.java) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `createCollection(KClass, CollectionOptions) extension should call its Java counterpart`() {

		val collectionOptions = mockk<CollectionOptions>()

		operations.createCollection(First::class, collectionOptions)
		verify { operations.createCollection(First::class.java, collectionOptions) }
	}

	@Test // DATAMONGO-1689
	fun `createCollection() with reified type parameter extension should call its Java counterpart`() {

		operations.createCollection<First>()
		verify { operations.createCollection(First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `createCollection(CollectionOptions) with reified type parameter extension should call its Java counterpart`() {

		val collectionOptions = mockk<CollectionOptions>()

		operations.createCollection<First>(collectionOptions)
		verify { operations.createCollection(First::class.java, collectionOptions) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `collectionExists(KClass) extension should call its Java counterpart`() {

		operations.collectionExists(First::class)
		verify { operations.collectionExists(First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `collectionExists() with reified type parameter extension should call its Java counterpart`() {

		operations.collectionExists<First>()
		verify { operations.collectionExists(First::class.java) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `dropCollection(KClass) extension should call its Java counterpart`() {

		operations.dropCollection(First::class)
		verify { operations.dropCollection(First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `dropCollection() with reified type parameter extension should call its Java counterpart`() {

		operations.dropCollection<First>()
		verify { operations.dropCollection(First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `findAll() with reified type parameter extension should call its Java counterpart`() {

		operations.findAll<First>()
		verify { operations.findAll(First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `findAll(String) with reified type parameter extension should call its Java counterpart`() {

		val collectionName = "foo"

		operations.findAll<First>(collectionName)
		verify { operations.findAll(First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	fun `findOne(Query) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()

		operations.findOne<First>(query)
		verify { operations.findOne(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `findOne(Query, String) with reified type parameter extension should call its Java counterpart`() {

		val collectionName = "foo"
		val query = mockk<Query>()

		operations.findOne<First>(query, collectionName)
		verify { operations.findOne(query, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `exists(Query, KClass) extension should call its Java counterpart`() {

		val query = mockk<Query>()

		operations.exists(query, First::class)
		verify { operations.exists(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `exists(Query) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()

		operations.exists<First>(query)
		verify { operations.exists(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `find(Query) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()

		operations.find<First>(query)
		verify { operations.find(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `find(Query, String) with reified type parameter extension should call its Java counterpart`() {

		val collectionName = "foo"
		val query = mockk<Query>()

		operations.find<First>(query, collectionName)
		verify { operations.find(query, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	fun `findById(Any) with reified type parameter extension should call its Java counterpart`() {

		val id = 1L

		operations.findById<First>(id)
		verify { operations.findById(id, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `findById(Any, String) with reified type parameter extension should call its Java counterpart`() {

		val collectionName = "foo"
		val id = 1L

		operations.findById<First>(id, collectionName)
		verify { operations.findById(id, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `geoNear(Query) with reified type parameter extension should call its Java counterpart`() {

		val query = NearQuery.near(0.0, 0.0)

		operations.geoNear<First>(query)
		verify { operations.geoNear(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `geoNear(Query, String) with reified type parameter extension should call its Java counterpart`() {

		val collectionName = "foo"
		val query = NearQuery.near(0.0, 0.0)

		operations.geoNear<First>(query, collectionName)
		verify { operations.geoNear(query, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	fun `findAndModify(Query, Update, FindAndModifyOptions) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()
		val options = mockk<FindAndModifyOptions>()

		operations.findAndModify<First>(query, update, options)
		verify { operations.findAndModify(query, update, options, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `findAndModify(Query, Update, FindAndModifyOptions, String) with reified type parameter extension should call its Java counterpart`() {

		val collectionName = "foo"
		val query = mockk<Query>()
		val update = mockk<Update>()
		val options = mockk<FindAndModifyOptions>()

		operations.findAndModify<First>(query, update, options, collectionName)
		verify { operations.findAndModify(query, update, options, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	fun `findAndRemove(Query) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()

		operations.findAndRemove<First>(query)
		verify { operations.findAndRemove(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `findAndRemove(Query, String) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val collectionName = "foo"

		operations.findAndRemove<First>(query, collectionName)
		verify { operations.findAndRemove(query, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	fun `count() with reified type parameter extension should call its Java counterpart`() {

		operations.count<First>()
		verify { operations.count(any<Query>(), eq(First::class.java)) }
	}

	@Test // DATAMONGO-1689
	fun `count(Query) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()

		operations.count<First>(query)
		verify { operations.count(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `count(Query, String) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val collectionName = "foo"

		operations.count<First>(query, collectionName)
		verify { operations.count(query, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `count(Query, KClass) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()

		operations.count(query, First::class)
		verify { operations.count(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `count(Query, KClass, String) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val collectionName = "foo"

		operations.count(query, First::class, collectionName)
		verify { operations.count(query, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `insert(Collection, KClass) extension should call its Java counterpart`() {

		val collection = listOf(First(), First())

		operations.insert(collection, First::class)
		verify { operations.insert(collection, First::class.java) }
	}

	@Test // DATAMONGO-2208
	fun `insert(Collection) with reified type parameter extension should call its Java counterpart`() {

		val collection = listOf(First(), First())

		operations.insert<First>(collection)
		verify { operations.insert(collection, First::class.java) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `insertAll(Mono, KClass) extension should call its Java counterpart`() {

		val collection = Mono.just(listOf(First(), First()))

		operations.insertAll(collection, First::class)
		verify { operations.insertAll(collection, First::class.java) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `upsert(Query, Update, KClass) extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()

		operations.upsert(query, update, First::class)
		verify { operations.upsert(query, update, First::class.java) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `upsert(Query, Update, KClass, String) extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()
		val collectionName = "foo"

		operations.upsert(query, update, First::class, collectionName)
		verify { operations.upsert(query, update, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	fun `upsert(Query, Update) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()

		operations.upsert<First>(query, update)
		verify { operations.upsert(query, update, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `upsert(Query, Update, String) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()
		val collectionName = "foo"

		operations.upsert<First>(query, update, collectionName)
		verify { operations.upsert(query, update, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `updateFirst(Query, Update, KClass) extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()

		operations.updateFirst(query, update, First::class)
		verify { operations.updateFirst(query, update, First::class.java) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `updateFirst(Query, Update, KClass, String) extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()
		val collectionName = "foo"

		operations.updateFirst(query, update, First::class, collectionName)
		verify { operations.updateFirst(query, update, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	fun `updateFirst(Query, Update) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()

		operations.updateFirst<First>(query, update)
		verify { operations.updateFirst(query, update, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `updateFirst(Query, Update, String) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()
		val collectionName = "foo"

		operations.updateFirst<First>(query, update, collectionName)
		verify { operations.updateFirst(query, update, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `updateMulti(Query, Update, KClass) extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()

		operations.updateMulti(query, update, First::class)
		verify { operations.updateMulti(query, update, First::class.java) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `updateMulti(Query, Update, KClass, String) extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()
		val collectionName = "foo"

		operations.updateMulti(query, update, First::class, collectionName)
		verify { operations.updateMulti(query, update, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	fun `updateMulti(Query, Update) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()

		operations.updateMulti<First>(query, update)
		verify { operations.updateMulti(query, update, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `updateMulti(Query, Update, String) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val update = mockk<Update>()
		val collectionName = "foo"

		operations.updateMulti<First>(query, update, collectionName)
		verify { operations.updateMulti(query, update, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `remove(Query, KClass) extension should call its Java counterpart`() {

		val query = mockk<Query>()

		operations.remove(query, First::class)
		verify { operations.remove(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	@Suppress("DEPRECATION")
	fun `remove(Query, KClass, String) extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val collectionName = "foo"

		operations.remove(query, First::class, collectionName)
		verify { operations.remove(query, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	fun `remove(Query) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()

		operations.remove<First>(query)
		verify { operations.remove(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `remove(Query, String) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val collectionName = "foo"

		operations.remove<First>(query, collectionName)
		verify { operations.remove(query, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1689
	fun `findAllAndRemove(Query) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()

		operations.findAllAndRemove<First>(query)
		verify { operations.findAllAndRemove(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `tail(Query) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()

		operations.tail<First>(query)
		verify { operations.tail(query, First::class.java) }
	}

	@Test // DATAMONGO-1689
	fun `tail(Query, String) with reified type parameter extension should call its Java counterpart`() {

		val query = mockk<Query>()
		val collectionName = "foo"

		operations.tail<First>(query, collectionName)
		verify { operations.tail(query, First::class.java, collectionName) }
	}

	@Test // DATAMONGO-1761
	@Suppress("DEPRECATION")
	fun `findDistinct(String, KClass) should call java counterpart`() {

		operations.findDistinct<String>("field", First::class)
		verify { operations.findDistinct("field", First::class.java, String::class.java) }
	}

	@Test // DATAMONGO-1761
	@Suppress("DEPRECATION")
	fun `findDistinct(Query, String, KClass) should call java counterpart`() {

		val query = mockk<Query>()

		operations.findDistinct<String>(query, "field", First::class)
		verify { operations.findDistinct(query, "field", First::class.java, String::class.java) }
	}

	@Test // DATAMONGO-1761
	@Suppress("DEPRECATION")
	fun `findDistinct(Query, String, String, KClass) should call java counterpart`() {

		val query = mockk<Query>()

		operations.findDistinct<String>(query, "field", "collection", First::class)
		verify { operations.findDistinct(query, "field", "collection", First::class.java, String::class.java) }
	}

	@Test // DATAMONGO-1761
	fun `findDistinctImplicit(Query, String) should call java counterpart`() {

		val query = mockk<Query>()

		operations.findDistinct<String, First>(query, "field")
		verify { operations.findDistinct(query, "field", First::class.java, String::class.java) }
	}

	@Test // DATAMONGO-1761
	fun `findDistinct(Query, String, String) should call java counterpart`() {

		val query = mockk<Query>()

		operations.findDistinct<String, First>(query, "field", "collection")
		verify { operations.findDistinct(query, "field", "collection", First::class.java, String::class.java) }
	}

	@Test // DATAMONGO-1761
	@Suppress("DEPRECATION")
	fun `findDistinct(Query, String,  KClass) should call java counterpart`() {

		val query = mockk<Query>()

		operations.findDistinct<String>(query, "field", First::class)
		verify {
			operations.findDistinct(
				query,
				"field",
				First::class.java,
				String::class.java
			)
		}
	}

	@Test  // #893
	fun `aggregate(TypedAggregation, String, KClass) should call java counterpart`() {

		val aggregation = mockk<TypedAggregation<String>>()

		operations.aggregate<First>(aggregation, "foo")
		verify { operations.aggregate(aggregation, "foo", First::class.java) }
	}

	@Test // #893
	fun `aggregate(TypedAggregation, KClass) should call java counterpart`() {

		val aggregation = mockk<TypedAggregation<String>>()

		operations.aggregate<First>(aggregation)
		verify { operations.aggregate(aggregation, First::class.java) }
	}

	@Test // #893
	fun `aggregate(Aggregation, KClass) should call java counterpart`() {

		val aggregation = mockk<Aggregation>()

		operations.aggregate<String, First>(aggregation)
		verify {
			operations.aggregate(
				aggregation,
				String::class.java,
				First::class.java
			)
		}
	}

	@Test // #893
	fun `aggregate(Aggregation, String) should call java counterpart`() {

		val aggregation = mockk<Aggregation>()

		operations.aggregate<First>(aggregation, "foo")
		verify { operations.aggregate(aggregation, "foo", First::class.java) }
	}
}
