/*
 * Copyright (C) 2013 Michael Zehrer <zehrer@zepan.org>
 *
 * This file is part of pinboard-river.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zepan.elasticsearch.river.pinboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.river.AbstractRiverComponent;
import org.elasticsearch.river.River;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 19.01.13
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
public class PinboardRiver extends AbstractRiverComponent implements River {

    private volatile BulkProcessor bulkProcessor;
    private final Client client;
    private RiverSettings riverSettings;

    private final String indexName;
    private final String typeName;
    private final String settings;
    private final String mapping;

    private final String pinboardAPIToken;

    private final int fetchInterval;

    @Inject
    protected PinboardRiver(RiverName riverName, RiverSettings riverSettings, Client client) {
        super(riverName, riverSettings);
        this.client = client;
        this.riverSettings = riverSettings;

        String user = null;
        String token = null;
        int fetchInterval = 60;
        if (riverSettings.settings().containsKey("pinboard")) {
            Map<String, Object> pinboardSettings = (Map<String, Object>) riverSettings.settings().get("pinboard");
            user = XContentMapValues.nodeStringValue(pinboardSettings.get("pinboardUser"), user);
            token = XContentMapValues.nodeStringValue(pinboardSettings.get("pinboardAPIToken"), token);
            fetchInterval = XContentMapValues.nodeIntegerValue(pinboardSettings.get("fetchInterval"), fetchInterval);
        }
        this.pinboardAPIToken = token;
        this.fetchInterval = fetchInterval;

        int maxConcurrentBulk = 10;
        int bulkSize = 100;
        String type = "import";
        String mapping = null;
        String settings = null;
        if (riverSettings.settings().containsKey("index")) {
            Map<String, Object> indexSettings = (Map<String, Object>) riverSettings.settings().get("index");
            type = XContentMapValues.nodeStringValue(indexSettings.get("type"), type);
            bulkSize = XContentMapValues.nodeIntegerValue(indexSettings.get("bulk_size"), bulkSize);
            maxConcurrentBulk = XContentMapValues.nodeIntegerValue(indexSettings.get("max_concurrent_bulk"), maxConcurrentBulk);
            settings = XContentMapValues.nodeStringValue(indexSettings.get("settings"), settings);
            mapping = XContentMapValues.nodeStringValue(indexSettings.get("mapping"), mapping);
        }
        this.settings = settings;
        this.mapping = mapping;
        this.typeName = type;
        this.indexName = "pinboard-" + user;

        this.bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                logger.info("Going to execute new bulk composed of {} actions", request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                logger.info("Executed bulk composed of {} actions", request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                logger.warn("Error executing bulk", failure);
            }
        }).setBulkActions(bulkSize).setConcurrentRequests(maxConcurrentBulk).build();

    }

    @java.lang.Override
    public void start() {

        logger.info("Starting Pinboard river");
        logger.info("Pinboard index will be refreshed every {} seconds", this.fetchInterval);

        if (!client.admin().indices().prepareExists(indexName).execute().actionGet().exists()) {

            CreateIndexRequestBuilder createIndexRequest = client.admin().indices()
                    .prepareCreate(indexName);

            if (settings != null) {
                createIndexRequest.setSettings(settings);
            }
            if (mapping != null) {
                createIndexRequest.addMapping(typeName, mapping);
            }

            createIndexRequest.execute().actionGet();
        }

        Timer timer = new Timer("Printer");
        MyTask t = new MyTask();
        timer.schedule(t, 0, this.fetchInterval * 1000);

    }

    class MyTask extends TimerTask {
        //times member represent calling times.
        private int times = 0;


        public void run() {

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            try {

                logger.debug("Starting to fetch from Pinboard");

                Gson gson = new Gson();
                String url = "https://api.pinboard.in/v1/posts/all?format=json&auth_token=" + pinboardAPIToken;
                logger.debug("Url is: {}", url);
                String responseBody = getHTML(url);

                Type collectionType = new TypeToken<Collection<Bookmark>>() {
                }.getType();
                Collection<Bookmark> bookmarks = gson.fromJson(responseBody, collectionType);

                logger.debug("Fetched " + bookmarks.size() + " bookmarks from Pinboard");

                int i = 1;
                for (Bookmark bookmark : bookmarks) {
                    String id = bookmark.getHash();
                    logger.info("Adding bookmark {} {}", i, bookmark.getHref());
                    StringWriter jsonWriter = new StringWriter();
                    mapper.writeValue(jsonWriter, bookmark);
                    bulkProcessor.add(Requests.indexRequest(indexName).type(typeName).id(id).source(jsonWriter.toString()));
                    i++;
                }

            } catch (Exception e) {
                logger.error("Error fetching from pinboard: " + e.getMessage());
            }

        }
    }

    private String getHTML(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @java.lang.Override
    public void close() {
        logger.info("Closing pinboard river");
        bulkProcessor.close();
    }
}
