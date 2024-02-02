DROP TABLE IF EXISTS character;
CREATE TABLE character (
    id SERIAL PRIMARY KEY,
    version INT,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    species VARCHAR(255) NOT NULL,
    type VARCHAR(255),
    origin_name VARCHAR(255),
    origin_url VARCHAR(255),
    location_name VARCHAR(255),
    location_url VARCHAR(255),
    image VARCHAR(255) NOT NULL,
    episode VARCHAR(255) ARRAY,
    url VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL
);


