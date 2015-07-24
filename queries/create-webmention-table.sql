create table if not exists webmention(
  source,
  target,
  t timestamp default current_timestamp,
  status default 202,
  message
);
