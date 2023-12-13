package project06

import (
	"database/sql"
	"log"
)

type PreparedStatements struct {
	stmtInsertTerm        *sql.Stmt
	stmtInsertURLandTitle *sql.Stmt
	stmtInsertImage       *sql.Stmt
	stmtSelectTermID      *sql.Stmt
	stmtUpdateWordCount   *sql.Stmt
	stmtInsertHits        *sql.Stmt
	stmtSelectHitFreq     *sql.Stmt
	stmtUpdateHitFreq     *sql.Stmt
	stmtInsertBiHits      *sql.Stmt
	stmtSelectBiHitFreq   *sql.Stmt
	stmtUpdateBiHitFreq   *sql.Stmt
}

func (s *SearchEngine) Prepare() error {

	var err error

	s.prep.stmtInsertTerm, err = s.data.db.Prepare("INSERT OR IGNORE INTO Terms (term) VALUES (?)")
	if err != nil {
		return err
	}

	s.prep.stmtSelectTermID, err = s.data.db.Prepare("SELECT id FROM Terms WHERE term = ?")
	if err != nil {
		return err
	}

	s.prep.stmtInsertURLandTitle, err = s.data.db.Prepare("INSERT INTO URLs (url, title) VALUES (?, ?)")
	if err != nil {
		return err
	}

	s.prep.stmtInsertImage, err = s.data.db.Prepare("INSERT INTO Images (url_id, term_id, description, src_url) VALUES (?, ?, ?, ?)")
	if err != nil {
		return err
	}

	s.prep.stmtUpdateWordCount, err = s.data.db.Prepare("UPDATE URLs SET wordCount = ? WHERE id = ?")
	if err != nil {
		return err
	}

	s.prep.stmtInsertHits, err = s.data.db.Prepare("INSERT OR IGNORE INTO Hits (term_id, url_id, frequency) VALUES (?, ?, 1)")
	if err != nil {
		return err
	}

	s.prep.stmtSelectHitFreq, err = s.data.db.Prepare("SELECT frequency FROM Hits WHERE term_id = ? AND url_id = ?")
	if err != nil {
		return err
	}

	s.prep.stmtUpdateHitFreq, err = s.data.db.Prepare("UPDATE Hits SET frequency = frequency + 1 WHERE term_id = ? AND url_id = ?")
	if err != nil {
		return err
	}

	s.prep.stmtInsertBiHits, err = s.data.db.Prepare("INSERT OR IGNORE INTO BiHits (term1_id, term2_id, url_id, frequency) VALUES (?, ?, ?, 1)")
	if err != nil {
		return err
	}

	s.prep.stmtSelectBiHitFreq, err = s.data.db.Prepare("SELECT frequency FROM BiHits WHERE term1_id = ? AND term2_id = ? AND url_id = ?")
	if err != nil {
		return err
	}

	s.prep.stmtUpdateBiHitFreq, err = s.data.db.Prepare("UPDATE BiHits SET frequency = frequency + 1 WHERE term1_id = ? AND term2_id = ? AND url_id = ?")
	if err != nil {
		return err
	}

	log.Println("prepared statements successfully")

	return nil
}

func (s *SearchEngine) ClosePreparedStatements() {
	closeStatement := func(stmt *sql.Stmt, name string) {
		if stmt != nil {
			if err := stmt.Close(); err != nil {
				log.Printf("error closing %s statement: %v", name, err)
			}
		}
	}

	// close statements but check if they are not nil first, just in case
	closeStatement(s.prep.stmtInsertTerm, "stmtInsertTerm")
	closeStatement(s.prep.stmtSelectTermID, "stmtSelectTermID")
	closeStatement(s.prep.stmtInsertURLandTitle, "stmtInsertURLandTitle")
	closeStatement(s.prep.stmtInsertImage, "stmtInsertImage")
	closeStatement(s.prep.stmtUpdateWordCount, "stmtUpdateWordCount")
	closeStatement(s.prep.stmtInsertHits, "stmtInsertHits")
	closeStatement(s.prep.stmtSelectHitFreq, "stmtSelectHitFreq")
	closeStatement(s.prep.stmtUpdateHitFreq, "stmtUpdateHitFreq")
	closeStatement(s.prep.stmtInsertBiHits, "stmtInsertBiHits")
	closeStatement(s.prep.stmtSelectBiHitFreq, "stmtSelectBiHitFreq")
	closeStatement(s.prep.stmtUpdateBiHitFreq, "stmtUpdateBiHitFreq")
}
