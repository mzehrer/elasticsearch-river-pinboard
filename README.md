elasticsearch-river-pinboard
============================

An ElasticSearch River for Pinboard

Setting it up is as simple as executing the following against elasticsearch:

    curl -XPUT localhost:9200/_river/pinboard_river/_meta -d '
    {
        "type" : "pinboard",
        "pinboard" : {
            "pinboardUser" : "mzehrer",
            "pinboardAPIToken" : "mzehrer:5B6D91360F1749AE3EF0"
        }
    }'

