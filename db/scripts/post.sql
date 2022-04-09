create table if not exists post(
	id serial primary key,
	name text,
	link text unique,
	description text,
	created timestamp
);