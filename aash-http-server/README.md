# Aash HTTP Server

A simple standalone Scala HTTP server, designed to serve simple requests such as web-services, RSS, ATOM, management pages, etc.

To start a server listening at port 80 :

```bash
> java -jar aash-http-server
```

To change the port, just use the following system property
```bash
> java -jar aash-http-server -Daash.http.server.port=8080
```

## Serving requests

To make the server fits your needs, it allows you to bind paths to controllers.
What we call controllers, are in fact functions transforming an `com.ledoyen.scala.httpserver.HttpRequest` into an `com.ledoyen.scala.httpserver.Httpresponse`.

```scala
val server = new HttpServer(8080).start
server.registerListener("/hello",
	req => new HttpResponse(req.version, StatusCode.OK, "<h1>Hello World !</h1>"))
```

## Statistics

Aash HTTP Server provides a build-in controller to serve statistics.
```scala
server.registerListener("/stat", server.statistics)
```

From there you can enable, disable or reset statistics :

URL 																	| Action
:-----------------------------------------------------------------------|:--------------------
[http://localhost/stat](http://localhost/stat)							|	displays statistics
[http://localhost/stat?enable=true](http://localhost/stat?enable=true)	|	enable statistics
[http://localhost/stat?enable=false](http://localhost/stat?enable=false)|	disable statistics
[http://localhost/stat?reset=true](http://localhost/stat?reset=true)	|	reset statistics

You can also control statistics programmatically :
```scala
val server = new HttpServer(8080).start
server.enableStatistics
server.disableStatistics
server.resetStatistics
```
