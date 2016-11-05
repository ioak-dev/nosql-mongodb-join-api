# Ceres, an abstract map-reduce framework for NoSQL

High performance MapReduce framework in Java, that adds relational database capabilities to any NoSql database like MongoDb. Allows to perform simple to complex relational queries with no coding involved. Define queries in Json format and the framework will take care of the rest. Highly modular and easy to add into any existing Java project.

Visit our [website](http://codesunday.com/nosql-mongodb-join-api/getting-started/) for more detailed and up to date documentation and usage guide


## Getting Started
It's as easy as 1-2-3!

- Update database connect details
	- Define the database connect details in application-context.json file
	- Get a client instance
```
// application-context.json
{
	"type": "database-instance",
	"key": "sakila",
	"value": {
		"uri": "mongodb://localhost:27017",
		"database": "sakila"
	}
}

// get client instance
String databaseDriverName = "mongo"; // based on what NoSQL database to connect to
String databaseInstanceName = "sakila"; // key specified in application-context.json
String applicationContextPath = "resources/application-context.json";
CeresClient client = CeresClient.getInstance(applicationContextPath, databaseDriverName, databaseInstanceName);
```

- Define queries
	- Query is written in Json format with following details specified as Json attributes. Framework uses easy to follow and synonymous keywords as attributes. Refer to query syntax section and the included example queries for more details
	- Framework supports maintenance of queries in multiple ways. Queries can be maintained in database as a collection, as a set of files in file system or construct a query at runtime. You can choose, one or more methods based on your convenience

```
client.addQueriesFromFilesystem(queryPath); // to load from Json files maintained in file system
client.addQueriesFromDatabase(queryJson); // to load from queries maintained in database table/collection. Specify a base query that will return the list of queries from a database table
client.addTemplatesFromFilesystem(templatePath); // optional. when you want to specify the output structure of a query, you provide a template in Json format
```

- Run queries
	- Run a query by calling find method on the client. Context and query name will uniquely identify the query. 

```
Map<String, Object> parameters = new HashMap();
parameters.put("city", "Lethbridge");
parameters.put("country", "Canada");
Result result = client.find("default", "test_query_name", parameters);
List<JSONObject> output = result.getView();

// If you define more than one output templates, multiple views will be generated with their own Json output structure. 
// You can access each of them by passing the view name. Refer to Query Template section for more details.
// getView() will get you the data when there is no projection defined or when only one output structure is specified. This is the common use case
// Concept of multiple views as output is powerful and it is made possible with the help of Proteus library
```


## Documentation
Refer to the [documentation](http://codesunday.com/nosql-mongodb-join-api/documentation) for more details