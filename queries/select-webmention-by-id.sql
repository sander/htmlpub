select rowid as id, cast (strftime("%s", t) as number) as time, source, target, status, message from webmention where rowid = ?;
