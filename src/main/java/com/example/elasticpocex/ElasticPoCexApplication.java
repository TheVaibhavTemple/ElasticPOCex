package com.example.elasticpocex;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.elasticpocex.supportiveclasses.Product;
import com.example.elasticpocex.supportiveclasses.SomeApplicationData;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.Alias;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@SpringBootApplication
public class ElasticPoCexApplication implements ApplicationRunner{

	public static void main(String[] args) throws Exception{
		SpringApplication.run(ElasticPoCexApplication.class, args);	

	}

	private static void processProduct(Product source) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		RestClient restClient = RestClient.builder(
				new HttpHost("localhost", 9200)).build();


		ElasticsearchTransport transport = new RestClientTransport(
				restClient, new JacksonJsonpMapper());


		ElasticsearchClient client = new ElasticsearchClient(transport);

		client.indices().create(c -> c.index("products"));

		SearchResponse<Product> search = client.search(s -> s
				.index("products")
				.query(q -> q
						.term(t -> t
								.field("name")
								.value(v -> v.stringValue("bicycle"))
								)),
				Product.class);


		for (Hit<Product> hit: search.hits().hits()) {
			processProduct(hit.source());
		}


		CreateIndexResponse createResponse = client.indices().create(
				new CreateIndexRequest.Builder()
				.index("my-index")
				.aliases("foo",
						new Alias.Builder().isWriteIndex(true).build()
						)
				.build()
				);


		CreateIndexResponse createResponse1 = client.indices()
				.create(createIndexBuilder -> createIndexBuilder
						.index("my-index")
						.aliases("foo", aliasBuilder -> aliasBuilder
								.isWriteIndex(true)
								)
						);


		SearchResponse<SomeApplicationData> results = client
				.search(_0 -> _0
						.query(_1 -> _1
								.intervals(_2 -> _2
										.field("my_text")
										.allOf(_3 -> _3
												.ordered(true)
												.intervals(_4 -> _4
														.match(_5 -> _5
																.query("my favorite food")
																.maxGaps(0)
																.ordered(true)
																)
														)
												.intervals(_4 -> _4
														.anyOf(_5 -> _5
																.intervals(_6 -> _6
																		.match(_7 -> _7
																				.query("hot water")
																				)
																		)
																.intervals(_6 -> _6
																		.match(_7 -> _7
																				.query("cold porridge")
																				)
																		)
																)
														)
												)
										)
								),
						SomeApplicationData.class 
						);

	}

}
