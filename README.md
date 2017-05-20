# Crawling Server API

### Request Crawl for a Report:
```
curl -X POST -H "Content-Type: application/json" -d '{
    "report_id": 1,
    "report_type_id": 30,
    "search": {
        "currency": "ARS",
        "destinations": [{
            "city_id": 20,
            "by_site": [{
                "site_id": 10,
                "city_ids": [{
                    "id": 300,
                    "name": "San Carlos de Bariloche"
                }]
            }]
        }],
        "sites": [{
            "id": 10,
            "name": "Despegar.com ARG"
        }, {
            "id": 11,
            "name": "Almundo.com ARG"
        }],
        "parameters": [{
                "checkin": "2017-11-10T18:06:22Z",
                "checkout": "2017-11-20T18:06:22Z",
                "adults": 2
            },
            {
                "checkin": "2017-12-10T18:06:22Z",
                "checkout": "2017-12-20T18:06:22Z",
                "adults": 3
            },
            {
                "checkin": "2018-01-10T18:06:22Z",
                "checkout": "2018-01-20T18:06:22Z",
                "adults": 2
            }
        ]
    }
}' 'http://localhost:8080/request/crawling'
```
