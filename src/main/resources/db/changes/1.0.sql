create table if not exists users (
    id serial primary key,
    tg_id bigint unique not null,
    config jsonb not null
);

create table if not exists profiles (
    id serial primary key,
    user_id int not null references users(id),
    name varchar(255) not null,
    addresses text not null,
    unique(user_id, name)
);