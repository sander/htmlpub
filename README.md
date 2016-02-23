# htmlpub

[IndieWeb][iw] ideas implemented in a Clojure/Ring web app.
Work in progress.

## How to run

- Download code and install [Leiningen][lein].
- `lein ring server-headless` to run a development server.
- Browse to [localhost:3000/webmention/](http://localhost:3000/webmention/).

## How to deploy

- `lein ring uberwar` to create a warfile in `target/`.
- Install and run [Jetty][jetty].
- Put the warfile in Jetty’s `webapps/` folder, e.g. named `htmlpub.war`.
- Browse to e.g. [localhost:8080/htmlpub/webmention/](localhost:8080/htmlpub/webmention/).
- Install a web server (e.g. [nginx][nginx]) that proxies Jetty and adds a webmention Link http header to static files.

## Works

- Creates a [webmention][wm] endpoint and takes notifications asynchronously
  at `/webmention/` (`POST`).
- Publishes webmention status at `/webmention/` (`GET`), `/webmention/1`, etc.

## To do

- Notifications to the site author.

[iw]: http://indiewebcamp.com
[wm]: http://webmention.org/
[lein]: http://leiningen.org
[jetty]: http://www.eclipse.org/jetty/
[nginx]: http://nginx.org
