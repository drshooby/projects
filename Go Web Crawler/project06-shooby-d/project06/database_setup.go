package project06

import (
	"database/sql"
	"fmt"
	"log"

	_ "github.com/mattn/go-sqlite3"
)

var DBNAME = "project06.db"

func (s *SearchEngine) OpenDB() error {
	db, err := sql.Open("sqlite3", DBNAME)
	if err != nil {
		return fmt.Errorf("error opening database: %v", err)
	}
	s.data.db = db
	log.Println("database opened successfully")
	return nil
}

func (s *SearchEngine) CloseDB() {
	if s.data.db != nil {
		err := s.data.db.Close()
		if err != nil {
			log.Printf("error closing database: %v\n", err)
		} else {
			log.Println("database closed successfully")
		}
	} else {
		log.Println("database already nil, no need to close")
	}
}

func (s *SearchEngine) InitDB() {

	_, err1 := s.data.db.Exec(`
	CREATE TABLE IF NOT EXISTS Terms (
		id INTEGER PRIMARY KEY,
		term TEXT UNIQUE
	);
	
	CREATE INDEX IF NOT EXISTS term_index ON Terms(term);
	`)

	if err1 != nil {
		log.Fatalf("error initializing Terms table: %v\n", err1)
	}

	_, err2 := s.data.db.Exec(`
	CREATE TABLE IF NOT EXISTS URLs (
		id INTEGER PRIMARY KEY, 
		url TEXT UNIQUE,
		title TEXT,
		wordCount INTEGER
	);
	
	CREATE INDEX IF NOT EXISTS url_index ON URLs(url);
	`)

	if err2 != nil {
		log.Fatalf("error initializing URLs table: %v\n", err2)
	}

	_, err3 := s.data.db.Exec(`
	CREATE TABLE IF NOT EXISTS Hits (
		id INTEGER PRIMARY KEY,
		term_id INTEGER,
		url_id INTEGER,
		frequency INTEGER,
		FOREIGN KEY(term_id) REFERENCES Terms(id), 
		FOREIGN KEY(url_id) REFERENCES URLs(id)
	);
	
	CREATE INDEX IF NOT EXISTS term_id_index ON Hits(term_id);
	CREATE INDEX IF NOT EXISTS url_id_index ON Hits(url_id);
	`)

	if err3 != nil {
		log.Fatalf("error initializing Hits table: %v\n", err3)
	}

	_, err4 := s.data.db.Exec(`
	CREATE TABLE IF NOT EXISTS BiHits (
		id INTEGER PRIMARY KEY,
		term1_id INTEGER,
		term2_id INTEGER,
		url_id INTEGER,
		frequency INTEGER,
		FOREIGN KEY(term1_id) REFERENCES Terms(id),
		FOREIGN KEY(term2_id) REFERENCES Terms(id),
		FOREIGN KEY(url_id) REFERENCES URLs(id)
	);
	
	CREATE INDEX IF NOT EXISTS term1_id_index ON BiHits(term1_id);
	CREATE INDEX IF NOT EXISTS term2_id_index ON BiHits(term2_id);
	CREATE INDEX IF NOT EXISTS url_id_index_bi ON BiHits(url_id);
	`)

	if err4 != nil {
		log.Fatalf("error initializing BiHits table: %v\n", err4)
	}

	_, err5 := s.data.db.Exec(`
	CREATE TABLE IF NOT EXISTS Images (
		id INTEGER PRIMARY KEY,
		url_id INTEGER,
		term_id INTEGER,
		description TEXT,
		src_url TEXT,
		FOREIGN KEY(url_id) REFERENCES URLs(id),
		FOREIGN KEY(term_id) REFERENCES Terms(id)
	);

	CREATE INDEX IF NOT EXISTS url_id_index_images ON Images(url_id);
	CREATE INDEX IF NOT EXISTS term_id_index_images ON Images(term_id);
	`)

	if err5 != nil {
		log.Fatalf("error initializing Images table: %v\n", err5)
	}
}

/*
* For Testing:

SELECT U.url, SUM(COALESCE(H.frequency, 0)) AS total_frequency
FROM URLs U
LEFT JOIN Hits H ON U.id = H.url_id
WHERE EXISTS (SELECT 1 FROM Terms T WHERE T.id = H.term_id AND T.term = 'blood')
GROUP BY U.url
ORDER BY U.url;
*/
