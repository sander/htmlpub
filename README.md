# htmlpub

[IndieWeb][iw] ideas implemented in a simple Node.js-based web site.
Work in progress.

## Works

- Serves static pages from `static/` at `http://localhost:8000`.
- Announces a [webmention][wm] endpoint and takes notifications asynchronously
  at `/webmention` (`POST`).
- Publishes webmention status at `/webmention` (`GET`), `/webmention/1`, etc.

## To do

- Offer callback-based API for processing queued mentions.
- Provide sample implementation that integrates mentions from whitelist sources.
- Watch `/static` for changes, notify webmention targets on a page update.

[iw]: http://indiewebcamp.com
[wm]: http://webmention.org/
