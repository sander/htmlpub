var express = require('express');
var serveIndex = require('serve-index');

var config = require('./config');
var webmention = require('./webmention');

var app = express();
var server = app.listen(config.port);

app.use(webmention.middleware(config));
app.use(express.static('static', {}));
app.use(serveIndex('static', { icons: true, view: 'details' }));
