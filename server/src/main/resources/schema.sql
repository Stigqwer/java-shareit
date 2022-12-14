DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  varchar(512)                            NOT NULL,
    requestor_id INTEGER REFERENCES users (id),
    created_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_requests PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(255)                            NOT NULL,
    description  VARCHAR(512)                            NOT NULL,
    is_available boolean                                 NOT NULL,
    owner_id     INTEGER REFERENCES users (id),
    request_id   INTEGER REFERENCES requests (id),
    CONSTRAINT pk_items PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    INTEGER REFERENCES items (id),
    booker_id  INTEGER REFERENCES users (id),
    status     VARCHAR(255),
    CONSTRAINT pk_bookings PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text         VARCHAR(255),
    item_id      INTEGER REFERENCES items (id),
    author_id    INTEGER REFERENCES users (id),
    created_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);
