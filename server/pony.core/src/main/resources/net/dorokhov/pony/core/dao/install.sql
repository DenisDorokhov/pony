CREATE TABLE installation (

	id BIGINT IDENTITY,

	creation_date TIMESTAMP NOT NULL,
	update_date TIMESTAMP NOT NULL,

	version VARCHAR(255) NOT NULL
);

CREATE TABLE config (

	id VARCHAR(255) NOT NULL,

	creation_date TIMESTAMP NOT NULL,
	update_date TIMESTAMP NOT NULL,

	value CLOB,

	PRIMARY KEY (id)
);

INSERT INTO installation (creation_date, update_date, version) VALUES (NOW(), NOW(), '1.0');