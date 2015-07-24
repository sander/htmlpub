var express = require('express');
var request = require('request');
var serveIndex = require('serve-index');

var webmention = require('./webmention');

var config = {
  port: 8000,
  endpoint: '/webmention',
  database: 'webmention.db'
};

var app = express();
var server = app.listen(config.port);

app.use(webmention.middleware(config));

app.use(express.static('static', {}));
app.use(serveIndex('static', { icons: true, view: 'details' }));

// Test
if (false)
request.post('http://localhost:8000/webmention', {
  form: {
    source: 'http://localhost:8000/2015/hello.html',
    target: 'http://localhost:8000/2015/response.html'
  }
}, function(err, res, body) {
  console.log(res.statusCode, err, body);
});
