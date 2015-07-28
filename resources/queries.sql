-- name: create-table!
create table if not exists webmention(
  source,
  target,
  t timestamp default current_timestamp,
  status default 202,
  message
);

-- name: insert-webmention<!
insert into webmention(source, target) values (:source, :target);

-- name: select-webmentions
select
  rowid as id,
  cast (strftime("%s", t) as number) as time,
  source,
  target,
  status,
  message
from webmention;

-- name: select-webmention
select
  rowid as id,
  cast (strftime("%s", t) as number) as time,
  source,
  target,
  status,
  message
from webmention
where rowid = :id;
