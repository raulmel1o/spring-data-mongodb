/*
 * Copyright 2019-2022 the original author or authors.
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
package org.springframework.data.mongodb.util.json;

import static org.assertj.core.api.Assertions.*;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bson.BsonBinary;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.junit.jupiter.api.Test;
import org.springframework.data.spel.EvaluationContextProvider;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Unit tests for {@link ParameterBindingJsonReader}.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @author Rocco Lagrotteria
 */
class ParameterBindingJsonReaderUnitTests {

	@Test
	void bindUnquotedStringValue() {

		Document target = parse("{ 'lastname' : ?0 }", "kohlin");
		assertThat(target).isEqualTo(new Document("lastname", "kohlin"));
	}

	@Test
	void bindQuotedStringValue() {

		Document target = parse("{ 'lastname' : '?0' }", "kohlin");
		assertThat(target).isEqualTo(new Document("lastname", "kohlin"));
	}

	@Test
	void bindUnquotedIntegerValue() {

		Document target = parse("{ 'lastname' : ?0 } ", 100);
		assertThat(target).isEqualTo(new Document("lastname", 100));
	}

	@Test
	void bindMultiplePlacholders() {

		Document target = parse("{ 'lastname' : ?0, 'firstname' : '?1' }", "Kohlin", "Dalinar");
		assertThat(target).isEqualTo(Document.parse("{ 'lastname' : 'Kohlin', 'firstname' : 'Dalinar' }"));
	}

	@Test
	void bindQuotedIntegerValue() {

		Document target = parse("{ 'lastname' : '?0' }", 100);
		assertThat(target).isEqualTo(new Document("lastname", "100"));
	}

	@Test
	void bindValueToRegex() {

		Document target = parse("{ 'lastname' : { '$regex' : '^(?0)'} }", "kohlin");
		assertThat(target).isEqualTo(Document.parse("{ 'lastname' : { '$regex' : '^(kohlin)'} }"));
	}

	@Test
	void bindValueToMultiRegex() {

		Document target = parse(
				"{'$or' : [{'firstname': {'$regex': '.*?0.*', '$options': 'i'}}, {'lastname' : {'$regex': '.*?0xyz.*', '$options': 'i'}} ]}",
				"calamity");
		assertThat(target).isEqualTo(Document.parse(
				"{ \"$or\" : [ { \"firstname\" : { \"$regex\" : \".*calamity.*\" , \"$options\" : \"i\"}} , { \"lastname\" : { \"$regex\" : \".*calamityxyz.*\" , \"$options\" : \"i\"}}]}"));
	}

	@Test
	void bindMultipleValuesToSingleToken() {

		Document target = parse("{$where: 'return this.date.getUTCMonth() == ?2 && this.date.getUTCDay() == ?3;'}", 0, 1, 2,
				3, 4);
		assertThat(target)
				.isEqualTo(Document.parse("{$where: 'return this.date.getUTCMonth() == 2 && this.date.getUTCDay() == 3;'}"));
	}

	@Test
	void bindValueToDbRef() {

		Document target = parse("{ 'reference' : { $ref : 'reference', $id : ?0 }}", "kohlin");
		assertThat(target).isEqualTo(Document.parse("{ 'reference' : { $ref : 'reference', $id : 'kohlin' }}"));
	}

	@Test
	void bindToKey() {

		Document target = parse("{ ?0 : ?1 }", "firstname", "kaladin");
		assertThat(target).isEqualTo(Document.parse("{ 'firstname' : 'kaladin' }"));
	}

	@Test
	void bindListValue() {

		//
		Document target = parse("{ 'lastname' : { $in : ?0 } }", Arrays.asList("Kohlin", "Davar"));
		assertThat(target).isEqualTo(Document.parse("{ 'lastname' : { $in : ['Kohlin', 'Davar' ]} }"));
	}

	@Test
	void bindListOfBinaryValue() {

		//
		byte[] value = "Kohlin".getBytes(StandardCharsets.UTF_8);
		List<byte[]> args = Collections.singletonList(value);

		Document target = parse("{ 'lastname' : { $in : ?0 } }", args);
		assertThat(target).isEqualTo(new Document("lastname", new Document("$in", args)));
	}

	@Test
	void bindExtendedExpression() {

		Document target = parse("{'id':?#{ [0] ? { $exists :true} : [1] }}", true, "firstname", "kaladin");
		assertThat(target).isEqualTo(Document.parse("{ \"id\" : { \"$exists\" : true}}"));
	}

	// {'id':?#{ [0] ? { $exists :true} : [1] }}

	@Test
	void bindDocumentValue() {

		//
		Document target = parse("{ 'lastname' : ?0 }", new Document("$eq", "Kohlin"));
		assertThat(target).isEqualTo(Document.parse("{ 'lastname' : { '$eq' : 'Kohlin' } }"));
	}

	@Test
	void arrayWithoutBinding() {

		//
		Document target = parse("{ 'lastname' : { $in : [\"Kohlin\", \"Davar\"] } }");
		assertThat(target).isEqualTo(Document.parse("{ 'lastname' : { $in : ['Kohlin', 'Davar' ]} }"));
	}

	@Test
	void bindSpEL() {

		// "{ arg0 : ?#{[0]} }"
		Document target = parse("{ arg0 : ?#{[0]} }", 100.01D);
		assertThat(target).isEqualTo(new Document("arg0", 100.01D));
	}

	@Test // DATAMONGO-2315
	void bindDateAsDate() {

		Date date = new Date();
		Document target = parse("{ 'end_date' : { $gte : { $date : ?0 } } }", date);

		assertThat(target).isEqualTo(Document.parse("{ 'end_date' : { $gte : { $date : " + date.getTime() + " } } } "));
	}

	@Test // DATAMONGO-2315
	void bindQuotedDateAsDate() {

		Date date = new Date();
		Document target = parse("{ 'end_date' : { $gte : { $date : '?0' } } }", date);

		assertThat(target).isEqualTo(Document.parse("{ 'end_date' : { $gte : { $date : " + date.getTime() + " } } } "));
	}

	@Test // DATAMONGO-2315
	void bindStringAsDate() {

		Document target = parse("{ 'end_date' : { $gte : { $date : ?0 } } }", "2019-07-04T12:19:23.000Z");

		assertThat(target).isEqualTo(Document.parse("{ 'end_date' : { $gte : { $date : '2019-07-04T12:19:23.000Z' } } } "));
	}

	@Test // DATAMONGO-2315
	void bindNumberAsDate() {

		Long time = new Date().getTime();
		Document target = parse("{ 'end_date' : { $gte : { $date : ?0 } } }", time);

		assertThat(target).isEqualTo(Document.parse("{ 'end_date' : { $gte : { $date : " + time + " } } } "));
	}

	@Test // DATAMONGO-2418
	void shouldNotAccessSpElEvaluationContextWhenNoSpElPresentInBindableTarget() {

		Object[] args = new Object[] { "value" };
		EvaluationContext evaluationContext = new StandardEvaluationContext() {

			@Override
			public TypedValue getRootObject() {
				throw new RuntimeException("o_O");
			}
		};

		ParameterBindingJsonReader reader = new ParameterBindingJsonReader("{ 'name':'?0' }",
				new ParameterBindingContext((index) -> args[index], new SpelExpressionParser(), evaluationContext));
		Document target = new ParameterBindingDocumentCodec().decode(reader, DecoderContext.builder().build());

		assertThat(target).isEqualTo(new Document("name", "value"));
	}

	@Test // DATAMONGO-2476
	void bindUnquotedParameterInArray() {

		Document target = parse("{ 'name' : { $in : [?0] } }", "kohlin");
		assertThat(target).isEqualTo(new Document("name", new Document("$in", Collections.singletonList("kohlin"))));
	}

	@Test // DATAMONGO-2476
	void bindMultipleUnquotedParameterInArray() {

		Document target = parse("{ 'name' : { $in : [?0,?1] } }", "dalinar", "kohlin");
		assertThat(target).isEqualTo(new Document("name", new Document("$in", Arrays.asList("dalinar", "kohlin"))));
	}

	@Test // DATAMONGO-2476
	void bindUnquotedParameterInArrayWithSpaces() {

		Document target = parse("{ 'name' : { $in : [ ?0 ] } }", "kohlin");
		assertThat(target).isEqualTo(new Document("name", new Document("$in", Collections.singletonList("kohlin"))));
	}

	@Test // DATAMONGO-2476
	void bindQuotedParameterInArray() {

		Document target = parse("{ 'name' : { $in : ['?0'] } }", "kohlin");
		assertThat(target).isEqualTo(new Document("name", new Document("$in", Collections.singletonList("kohlin"))));
	}

	@Test // DATAMONGO-2476
	void bindQuotedMulitParameterInArray() {

		Document target = parse("{ 'name' : { $in : ['?0,?1'] } }", "dalinar", "kohlin");
		assertThat(target)
				.isEqualTo(new Document("name", new Document("$in", Collections.singletonList("dalinar,kohlin"))));
	}

	@Test // DATAMONGO-1894
	void discoversNoDependenciesInExpression() {

		String json = "{ $and : [?#{ [0] == null  ? { '$where' : 'true' } : { 'v1' : { '$in' : {[0]} } } }]}";

		ExpressionDependencies expressionDependencies = new ParameterBindingDocumentCodec()
				.captureExpressionDependencies(json, it -> new Object(), new SpelExpressionParser());

		assertThat(expressionDependencies).isEqualTo(ExpressionDependencies.none());
	}

	@Test // DATAMONGO-1894
	void discoversCorrectlyDependenciesInExpression() {

		String json = "{ hello: ?#{hasRole('foo')} }";

		ExpressionDependencies expressionDependencies = new ParameterBindingDocumentCodec()
				.captureExpressionDependencies(json, it -> new Object(), new SpelExpressionParser());

		assertThat(expressionDependencies).isNotEmpty();
		assertThat(expressionDependencies.get()).hasSize(1);
	}

	@Test // DATAMONGO-2523
	void bindSpelExpressionInArrayCorrectly/* closing bracket must not have leading whitespace! */() {

		Document target = parse("{ $and : [?#{ [0] == null  ? { '$where' : 'true' } : { 'v1' : { '$in' : {[0]} } } }]}", 1);

		assertThat(target).isEqualTo(Document.parse("{\"$and\": [{\"v1\": {\"$in\": [1]}}]}"));
	}

	@Test // DATAMONGO-2545
	void shouldABindArgumentsViaIndexInSpelExpressions() {

		Object[] args = new Object[] { "yess", "nooo" };
		StandardEvaluationContext evaluationContext = (StandardEvaluationContext) EvaluationContextProvider.DEFAULT
				.getEvaluationContext(args);

		ParameterBindingJsonReader reader = new ParameterBindingJsonReader(
				"{ 'isBatman' : ?#{ T(" + this.getClass().getName() + ").isBatman() ? [0] : [1] }}",
				new ParameterBindingContext((index) -> args[index], new SpelExpressionParser(), evaluationContext));
		Document target = new ParameterBindingDocumentCodec().decode(reader, DecoderContext.builder().build());

		assertThat(target).isEqualTo(new Document("isBatman", "nooo"));
	}

	@Test // DATAMONGO-2545
	void shouldAllowMethodArgumentPlaceholdersInSpelExpressions/*becuase this worked before*/() {

		Object[] args = new Object[] { "yess", "nooo" };
		StandardEvaluationContext evaluationContext = (StandardEvaluationContext) EvaluationContextProvider.DEFAULT
				.getEvaluationContext(args);

		ParameterBindingJsonReader reader = new ParameterBindingJsonReader(
				"{ 'isBatman' : ?#{ T(" + this.getClass().getName() + ").isBatman() ? '?0' : '?1' }}",
				new ParameterBindingContext((index) -> args[index], new SpelExpressionParser(), evaluationContext));
		Document target = new ParameterBindingDocumentCodec().decode(reader, DecoderContext.builder().build());

		assertThat(target).isEqualTo(new Document("isBatman", "nooo"));
	}

	@Test // DATAMONGO-2545
	void shouldAllowMethodArgumentPlaceholdersInQuotedSpelExpressions/*because this worked before*/() {

		Object[] args = new Object[] { "yess", "nooo" };
		StandardEvaluationContext evaluationContext = (StandardEvaluationContext) EvaluationContextProvider.DEFAULT
				.getEvaluationContext(args);

		ParameterBindingJsonReader reader = new ParameterBindingJsonReader(
				"{ 'isBatman' : \"?#{ T(" + this.getClass().getName() + ").isBatman() ? '?0' : '?1' }\" }",
				new ParameterBindingContext((index) -> args[index], new SpelExpressionParser(), evaluationContext));
		Document target = new ParameterBindingDocumentCodec().decode(reader, DecoderContext.builder().build());

		assertThat(target).isEqualTo(new Document("isBatman", "nooo"));
	}

	@Test // DATAMONGO-2545
	void evaluatesSpelExpressionDefiningEntireQuery() {

		Object[] args = new Object[] {};
		StandardEvaluationContext evaluationContext = (StandardEvaluationContext) EvaluationContextProvider.DEFAULT
				.getEvaluationContext(args);
		evaluationContext.setRootObject(new DummySecurityObject(new DummyWithId("wonderwoman")));

		String json = "?#{  T(" + this.getClass().getName()
				+ ").isBatman() ? {'_class': { '$eq' : 'region' }} : { '$and' : { {'_class': { '$eq' : 'region' } }, {'user.supervisor':  principal.id } } } }";

		ParameterBindingJsonReader reader = new ParameterBindingJsonReader(json,
				new ParameterBindingContext((index) -> args[index], new SpelExpressionParser(), evaluationContext));
		Document target = new ParameterBindingDocumentCodec().decode(reader, DecoderContext.builder().build());

		assertThat(target)
				.isEqualTo(new Document("$and", Arrays.asList(new Document("_class", new Document("$eq", "region")),
						new Document("user.supervisor", "wonderwoman"))));
	}

	@Test // GH-3871
	public void capturingExpressionDependenciesShouldNotThrowParseErrorForSpelOnlyJson() {

		Object[] args = new Object[] { "1", "2" };
		String json = "?#{ true ? { 'name': #name } : { 'name' : #name + 'trouble' } }";

		new ParameterBindingDocumentCodec().captureExpressionDependencies(json, (index) -> args[index],
				new SpelExpressionParser());
	}

	@Test // GH-3871
	public void bindEntireQueryUsingSpelExpressionWhenEvaluationResultIsJsonString() {

		Object[] args = new Object[] { "expected", "unexpected" };
		String json = "?#{ true ? \"{ 'name': ?0 }\" : \"{ 'name' : ?1 }\" }";
		StandardEvaluationContext evaluationContext = (StandardEvaluationContext) EvaluationContextProvider.DEFAULT
				.getEvaluationContext(args);

		ParameterBindingJsonReader reader = new ParameterBindingJsonReader(json,
				new ParameterBindingContext((index) -> args[index], new SpelExpressionParser(), evaluationContext));

		Document target = new ParameterBindingDocumentCodec().decode(reader, DecoderContext.builder().build());
		assertThat(target).isEqualTo(new Document("name", "expected"));
	}

	@Test // GH-3871
	public void throwsExceptionWhenbindEntireQueryUsingSpelExpressionResultsInInvalidJsonString() {

		Object[] args = new Object[] { "expected", "unexpected" };
		String json = "?#{ true ? \"{ 'name': ?0 { }\" : \"{ 'name' : ?1 }\" }";
		StandardEvaluationContext evaluationContext = (StandardEvaluationContext) EvaluationContextProvider.DEFAULT
				.getEvaluationContext(args);

		ParameterBindingJsonReader reader = new ParameterBindingJsonReader(json,
				new ParameterBindingContext((index) -> args[index], new SpelExpressionParser(), evaluationContext));

		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new ParameterBindingDocumentCodec().decode(reader, DecoderContext.builder().build()));
	}

	@Test // GH-3871
	public void bindEntireQueryUsingSpelExpressionWhenEvaluationResultIsJsonStringContainingUUID() {

		Object[] args = new Object[] { "UUID('cfbca728-4e39-4613-96bc-f920b5c37e16')", "unexpected" };
		String json = "?#{ true ? \"{ 'name': ?0 }\" : \"{ 'name' : ?1 }\" }";
		StandardEvaluationContext evaluationContext = (StandardEvaluationContext) EvaluationContextProvider.DEFAULT
				.getEvaluationContext(args);

		ParameterBindingJsonReader reader = new ParameterBindingJsonReader(json,
				new ParameterBindingContext((index) -> args[index], new SpelExpressionParser(), evaluationContext));

		Document target = new ParameterBindingDocumentCodec().decode(reader, DecoderContext.builder().build());

		assertThat(target.get("name")).isInstanceOf(BsonBinary.class);
	}

	@Test // GH-3871
	void bindEntireQueryUsingSpelExpression() {

		Object[] args = new Object[] { "region" };
		StandardEvaluationContext evaluationContext = (StandardEvaluationContext) EvaluationContextProvider.DEFAULT
				.getEvaluationContext(args);
		evaluationContext.setRootObject(new DummySecurityObject(new DummyWithId("wonderwoman")));

		String json = "?#{  T(" + this.getClass().getName() + ").applyFilterByUser('?0' ,principal.id) }";

		ParameterBindingJsonReader reader = new ParameterBindingJsonReader(json,
				new ParameterBindingContext((index) -> args[index], new SpelExpressionParser(), evaluationContext));
		Document target = new ParameterBindingDocumentCodec().decode(reader, DecoderContext.builder().build());

		assertThat(target)
				.isEqualTo(new Document("$and", Arrays.asList(new Document("_class", new Document("$eq", "region")),
						new Document("user.supervisor", "wonderwoman"))));
	}

	@Test // GH-3871
	void bindEntireQueryUsingParameter() {

		Object[] args = new Object[] { "{ 'itWorks' : true }" };
		StandardEvaluationContext evaluationContext = (StandardEvaluationContext) EvaluationContextProvider.DEFAULT
				.getEvaluationContext(args);

		String json = "?0";

		ParameterBindingJsonReader reader = new ParameterBindingJsonReader(json,
				new ParameterBindingContext((index) -> args[index], new SpelExpressionParser(), evaluationContext));
		Document target = new ParameterBindingDocumentCodec().decode(reader, DecoderContext.builder().build());

		assertThat(target).isEqualTo(new Document("itWorks", true));
	}

	@Test // DATAMONGO-2571
	void shouldParseRegexCorrectly() {

		Document target = parse("{ $and: [{'fieldA': {$in: [/ABC.*/, /CDE.*F/]}}, {'fieldB': {$ne: null}}]}");
		assertThat(target)
				.isEqualTo(Document.parse("{ $and: [{'fieldA': {$in: [/ABC.*/, /CDE.*F/]}}, {'fieldB': {$ne: null}}]}"));
	}

	@Test // DATAMONGO-2571
	void shouldParseRegexWithPlaceholderCorrectly() {

		Document target = parse("{ $and: [{'fieldA': {$in: [/?0.*/, /CDE.*F/]}}, {'fieldB': {$ne: null}}]}", "ABC");
		assertThat(target)
				.isEqualTo(Document.parse("{ $and: [{'fieldA': {$in: [/ABC.*/, /CDE.*F/]}}, {'fieldB': {$ne: null}}]}"));
	}

	@Test // DATAMONGO-2633
	void shouldParseNestedArrays() {

		Document target = parse("{ 'stores.location' : { $geoWithin: { $centerSphere: [ [ ?0, 48.799029 ] , ?1 ] } } }",
				1.948516D, 0.004D);
		assertThat(target).isEqualTo(Document
				.parse("{ 'stores.location' : { $geoWithin: { $centerSphere: [ [ 1.948516, 48.799029 ] , 0.004 ] } } }"));
	}

	@Test // GH-3633
	void parsesNullValue() {

		Document target = parse("{ 'parent' : null }");
		assertThat(target).isEqualTo(new Document("parent", null));
	}

	private static Document parse(String json, Object... args) {

		ParameterBindingJsonReader reader = new ParameterBindingJsonReader(json, args);
		return new ParameterBindingDocumentCodec().decode(reader, DecoderContext.builder().build());
	}

	// DATAMONGO-2545
	public static boolean isBatman() {
		return false;
	}

	public static String applyFilterByUser(String _class, String username) {
		switch (username) {
			case "batman":
				return "{'_class': { '$eq' : '" + _class + "' }}";
			default:
				return "{ '$and' : [ {'_class': { '$eq' : '" + _class + "' } }, {'user.supervisor':  '" + username + "' } ] }";
		}
	}

	@Data
	@AllArgsConstructor
	public static class DummySecurityObject {
		DummyWithId principal;
	}

	@Data
	@AllArgsConstructor
	public static class DummyWithId {
		String id;
	}

}
