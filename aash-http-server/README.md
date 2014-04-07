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
What we call controllers, are in fact functions transforming an `com.ledoyen.scala.httpserver.HttpRequest` into an `com.ledoyen.scala.httpserver.HttpResponse`.

```scala
val server = HttpServer.sync.start
server.registerListener("/hello",
	req => HttpResponse(req.version, StatusCode.OK, "<h1>Hello World !</h1>"))
```

## Statistics

Aash HTTP Server provides a build-in controller to serve statistics.

To activate statistics, use the following system property
```bash
> java -jar aash-http-server -Daash.http.server.statistics.enabled=true
```

From there you can enable, disable or reset statistics :

URL 																	| Action
:-----------------------------------------------------------------------|:--------------------
[http://localhost/stat](http://localhost/stat)							|	displays statistics
[http://localhost/stat?enable=true](http://localhost/stat?enable=true)	|	enable statistics
[http://localhost/stat?enable=false](http://localhost/stat?enable=false)|	disable statistics
[http://localhost/stat?reset=true](http://localhost/stat?reset=true)	|	reset statistics

To change the path where statistics controller is binded, use the following system property
```bash
> java -jar aash-http-server -Daash.http.server.statistics.enabled=true -Daash.http.server.statistics.path=/stat
```

You can also control statistics programmatically :
```scala
server.registerListener("/stat", server.statistics)
server.enableStatistics
server.disableStatistics
server.resetStatistics
```

## Asynchronous IO

Aash HTTP Server provides also (in beta phase for now) an asynchronous implementation, using [AsynchronousServerSocketChannel](http://docs.oracle.com/javase/7/docs/api/java/nio/channels/AsynchronousServerSocketChannel.html).

This server works with a __single thread__ handling HTTP read / write and other processing you may want to run between.

This implementation is meant to be used with asynchronous IO or immediate computation.

If any treatment is hanging (SQL request for example), the server will not be able to treat more than the current connection.

```scala
val server = HttpServer.async.start
// Synchronous listeners works as in the Synchronous server, sync behavior is "emulated"
server.registerListener("/hello",
	req => HttpResponse(req.version, StatusCode.OK, "<h1>Hello World !</h1>"))
// Asynchronous version (example with some file reading)
server.registerAsyncListener("/file",
      (req, callback) => {
        AsyncFiles.read(Paths.get("somefile"),
            content => callback.write(HttpResponse(req.version, StatusCode.OK, content)))
      })
```

You cannot reuse the __core single thread pool__ to delegate tasks as its full time job is to read from / write to the Socket Channel.

When using the __Java NIO__ [AsynchronousFileChannel](http://openjdk.java.net/projects/nio/javadoc/java/nio/channels/AsynchronousFileChannel.html) with custom `ExecutorService`,
as recommended if you want to keep your thread number under control, you will face the spawn of "rogue" Threads created internally by 
[AsynchronousChannelGroupImpl](http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/7-b147/sun/nio/ch/AsynchronousChannelGroupImpl.java#AsynchronousChannelGroupImpl.startThreads%28java.lang.Runnable%29), 
typically named Thread-X.

Those threads will only last during the file reading, so you can see them just when requesting [http://localhost/file](http://localhost/file).

As Thread creation is resource consuming, it will be a great improvement to get rid of it and only use the given `ExecutorService`, as `AsynchronousChannelGroup` does have a 
method `withFixedThreadPool(int nThreads, ThreadFactory threadFactory)` which does not imply the Thread creation side-effect.

However for now (jdk1.8.0_20, `AsynchronousFileChannel` and the `FileSystemProvider` within are not wired to use this method.
