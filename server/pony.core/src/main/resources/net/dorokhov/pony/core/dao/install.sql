CREATE TABLE installation (

	id BIGINT IDENTITY,

	creation_date TIMESTAMP NOT NULL,
	update_date TIMESTAMP,

	version VARCHAR(255) NOT NULL
);

CREATE TABLE log_message (

	id BIGINT IDENTITY,

	date TIMESTAMP NOT NULL,

	type TINYINT NOT NULL,

	code VARCHAR(255) NOT NULL,
	text LONGVARCHAR,
	details LONGVARCHAR
);

CREATE INDEX index_log_message_date ON log_message(date);
CREATE INDEX index_log_message_type ON log_message(type);
CREATE INDEX index_log_message_date_type ON log_message(date, type);

CREATE TABLE log_message_argument (

	log_message_id BIGINT NOT NULL,

	value LONGVARCHAR,

	FOREIGN KEY (log_message_id) REFERENCES log_message(id) ON DELETE CASCADE ON UPDATE CASCADE,

	PRIMARY KEY (log_message_id, value)
);

CREATE TABLE config (

	id VARCHAR(255) NOT NULL,

	creation_date TIMESTAMP NOT NULL,
	update_date TIMESTAMP,

	value LONGVARCHAR,

	PRIMARY KEY (id)
);

CREATE TABLE scan_result (

	id BIGINT IDENTITY,

	date TIMESTAMP NOT NULL,

	type VARCHAR(255) NOT NULL,

	duration BIGINT NOT NULL,

	song_size BIGINT NOT NULL,
	artwork_size BIGINT NOT NULL,

	genre_count BIGINT NOT NULL,
	artist_count BIGINT NOT NULL,
	album_count BIGINT NOT NULL,
	song_count BIGINT NOT NULL,
	artwork_count BIGINT NOT NULL,

	found_song_count BIGINT NOT NULL,

	created_artist_count BIGINT NOT NULL,
	updated_artist_count BIGINT NOT NULL,
	deleted_artist_count BIGINT NOT NULL,

	created_album_count BIGINT NOT NULL,
	updated_album_count BIGINT NOT NULL,
	deleted_album_count BIGINT NOT NULL,

	created_genre_count BIGINT NOT NULL,
	updated_genre_count BIGINT NOT NULL,
	deleted_genre_count BIGINT NOT NULL,

	created_song_count BIGINT NOT NULL,
	updated_song_count BIGINT NOT NULL,
	deleted_song_count BIGINT NOT NULL,

	created_artwork_count BIGINT NOT NULL,
	deleted_artwork_count BIGINT NOT NULL
);

CREATE INDEX index_library_scan_result_date ON scan_result(date);

CREATE TABLE scan_result_path (

	scan_result_id BIGINT NOT NULL,

	value VARCHAR(255) NOT NULL,

	FOREIGN KEY (scan_result_id) REFERENCES scan_result(id) ON DELETE CASCADE ON UPDATE CASCADE,

	PRIMARY KEY (scan_result_id, value)
);

CREATE TABLE stored_file (

	id BIGINT IDENTITY,

	date TIMESTAMP NOT NULL,

	name VARCHAR(255) NOT NULL,
	mime_type VARCHAR(255) NOT NULL,
	checksum VARCHAR(255) NOT NULL,
	size BIGINT NOT NULL,
	tag VARCHAR(255),
	path VARCHAR(255) NOT NULL,
	user_data LONGVARCHAR,

	UNIQUE (path),
	UNIQUE (tag, checksum)
);

CREATE INDEX index_stored_file_checksum ON stored_file(checksum);
CREATE INDEX index_stored_file_tag ON stored_file(tag);

CREATE TABLE genre (

	id BIGINT IDENTITY,

	creation_date TIMESTAMP NOT NULL,
	update_date TIMESTAMP,

	name VARCHAR(255),

	artwork_stored_file_id BIGINT,

	FOREIGN KEY (artwork_stored_file_id) REFERENCES stored_file(id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE INDEX index_genre_name ON genre(name);

CREATE TABLE artist (

	id BIGINT IDENTITY,

	creation_date TIMESTAMP NOT NULL,
	update_date TIMESTAMP,

	name VARCHAR(255),

	artwork_stored_file_id BIGINT,

	FOREIGN KEY (artwork_stored_file_id) REFERENCES stored_file(id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE INDEX index_artist_name ON artist(name);

CREATE TABLE album (

	id BIGINT IDENTITY,

	creation_date TIMESTAMP NOT NULL,
	update_date TIMESTAMP,

	name VARCHAR(255),
	year INT,

	artwork_stored_file_id BIGINT,

	artist_id BIGINT NOT NULL,

	FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (artwork_stored_file_id) REFERENCES stored_file(id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE INDEX index_album_name_artist_id ON album(name, artist_id);
CREATE INDEX index_album_artist_id_year_name ON album(artist_id, year, name);

CREATE TABLE song (

	id BIGINT IDENTITY,

	creation_date TIMESTAMP NOT NULL,
	update_date TIMESTAMP,

	path VARCHAR(255) NOT NULL,
	format VARCHAR(255) NOT NULL,
	mime_type VARCHAR(255) NOT NULL,
	size BIGINT NOT NULL,

	duration INT NOT NULL,
	bit_rate BIGINT NOT NULL,

	disc_number INT,
	disc_count INT,

	track_number INT,
	track_count INT,

	name VARCHAR(255),
	genre_name VARCHAR(255),
	artist_name VARCHAR(255),
	album_artist_name VARCHAR(255),
	album_name VARCHAR(255),

	year INT,

	artwork_stored_file_id BIGINT,

	album_id BIGINT NOT NULL,
	genre_id BIGINT NOT NULL,

	FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (genre_id) REFERENCES genre(id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (artwork_stored_file_id) REFERENCES stored_file(id) ON DELETE SET NULL ON UPDATE CASCADE,

	UNIQUE (path)
);

CREATE INDEX index_song_track_number_name ON song(disc_number, track_number, name);

INSERT INTO installation (creation_date, version) VALUES (NOW(), '1.0');