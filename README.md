# core.async.http.client

core.async.http.client is an HTTP client for clojure and clojurescript.

It uses core.async channels to return the responses.

[![Build Status](https://travis-ci.org/danpersa/core.async.http.client.svg?branch=master)](https://travis-ci.org/danpersa/core.async.http.client)
[![Clojars Project](https://img.shields.io/clojars/v/core.async.http.client.svg)](https://clojars.org/core.async.http.client)

## Installation

core.async.http.client is available from [Clojars](https://clojars.org/groups/core.async.http.client)

With Leiningen/Boot:

```
[core.async.http.client "0.2.0-SNAPSHOT"]
```

# Usage examples
## GET

```
(http/get "http://www.example.com/resources/id")
```

## POST

```
(http/post "http://site.com/resources" {:body "hello"})
```

## Inspiration

Got some inspiration from clj-http. I'm trying to keep the API as compatible as possible.
Some more inspiration from httpurr to use protocols for multiple clients.

## License

Released under the [MIT License](http://www.opensource.org/licenses/mit-license.php)
