package com.compuality.elasticsearch.client

import org.elasticsearch.client.Client

import javax.inject.Provider

public interface ElasticSearchClientProvider extends Provider<Client> {

}