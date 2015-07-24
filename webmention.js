var bodyParser = require('body-parser');
var express = require('express');
var request = require('request');
var fs = require('fs');
var sqlite3 = require('sqlite3').verbose();

function slurp(filename) {
  return fs.readFileSync(filename, { encoding: 'utf-8' });
}

function curry(fn) {
  var args = Array.prototype.slice.call(arguments, 1);
  return function() {
    return fn.apply(this, args.concat(Array.prototype.slice.call(arguments)));
  };
}

var sql = {
  create: slurp('queries/create-webmention-table.sql'),
  insert: slurp('queries/insert-webmention.sql'),
  selectAll: slurp('queries/select-webmentions.sql'),
  selectById: slurp('queries/select-webmention-by-id.sql')
};

function handleNotification(state, req, res) {
  var source = req.body.source;
  var target = req.body.target;

  state.db.run(sql.insert, [source, target], function(err) {
    if (err)
      res.status(500).end('error: could not store');
    else
      res.status(202).end(state.config.endpoint + '/' + this.lastID);
  });
}

function showInfo(state, req, res) {
  var id = +req.params.id;
  state.db.get(sql.selectById, id, function(err, row) {
    if (err) res.status(500).end('unknown error');
    else if (!row) res.status(404).end('not found');
    else res.end(JSON.stringify(row, null, 2));
  });
}

function showAll(state, req, res) {
  state.db.all(sql.selectAll, function(err, rows) {
    if (err) console.log('error', err);
    res.end(JSON.stringify(rows, null, 2));
  });
}

function announce(state, req, res, next) {
  res.set('Link', '<' + state.config.endpoint + '>; rel="webmention"');
  next();
}

function initState(config) {
  return {
    db: new sqlite3.Database(config.database),
    config: {
      endpoint: config.endpoint
    }
  };
}

function createTableOrDie(state) {
  state.db.run(sql.create, function(err) {
    if (err) {
      console.log('error creating table:', err);
      process.exit(1);
    }
  });
}

exports.middleware = function(config) {
  var state = initState(config);
  var app = express();

  createTableOrDie(state);

  app.use(bodyParser.urlencoded({ extended: false }));
  app.post(state.config.endpoint, curry(handleNotification, state));
  app.get(state.config.endpoint + '/:id(\\d+)', curry(showInfo, state));
  app.get(state.config.endpoint, curry(showAll, state))
  app.use(curry(announce, state));

  return app;
}
