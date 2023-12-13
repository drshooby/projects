package project06

import (
	"log"
)

type UrlTitlePair struct {
	URL   string
	Title string
}

func (s *SearchEngine) QueryURLs(term string, useWildcard bool) []UrlTitlePair {
	var results []UrlTitlePair

	if useWildcard {
		return s.QueryURLsWildcard(term, results)
	}

	q := `SELECT U.url, U.title
	FROM Hits AS H
	JOIN Terms AS T ON H.term_id = T.id
	JOIN URLs AS U ON H.url_id = U.id
	WHERE T.term = ?
	GROUP BY U.url, U.title;`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return nil
	}
	defer stmt.Close()

	rows, err := stmt.Query(term)
	if err != nil {
		log.Println("error querying term when searching for url/titles for tfidf: " + err.Error())
		return nil
	}
	defer rows.Close()

	for rows.Next() {
		var res UrlTitlePair
		if err := rows.Scan(&res.URL, &res.Title); err != nil {
			log.Println("error scanning url and title when searching for urls/titles for tfidf: " + err.Error())
			return nil
		}
		results = append(results, res)
	}

	if err := rows.Err(); err != nil {
		log.Println("error iterating over rows when searching for urls/titles for tfidf: " + err.Error())
		return nil
	}

	return results
}

func (s *SearchEngine) QueryTermFreqInURL(term, url string, useWildcard bool) int {
	var termFreq int

	if useWildcard {
		return s.QueryTermFreqInURLWildcard(term, url, termFreq)
	}

	q := `SELECT H.frequency
			FROM Hits AS H
			JOIN Terms AS T ON H.term_id = T.id
			JOIN URLs AS U ON H.url_id = U.id
			WHERE T.term = ? AND U.url = ?;`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return 0
	}
	defer stmt.Close()

	err = stmt.QueryRow(term, url).Scan(&termFreq)
	if err != nil {
		log.Println("error querying term frequency for url in tfidf: " + err.Error())
		return 0
	}

	return termFreq
}

func (s *SearchEngine) QueryNumURLsTermIsIn(term string, useWildcard bool) int {
	var numURLs int

	if useWildcard {
		return s.QueryNumURLsTermIsInWildcard(term, numURLs)
	}

	q := `SELECT COUNT(DISTINCT U.url)
			FROM Hits AS H
			JOIN Terms AS T ON H.term_id = T.id
			JOIN URLs AS U ON H.url_id = U.id
			WHERE T.term = ?;`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return 0
	}
	defer stmt.Close()

	err = stmt.QueryRow(term).Scan(&numURLs)
	if err != nil {
		log.Println("error querying number of urls term is in for tfidf: " + err.Error())
		return 0
	}

	return numURLs
}

func (s *SearchEngine) GetTotalDocWords(url string) int {
	var count int

	tx, err := s.data.db.Begin()
	if err != nil {
		log.Println("error starting a transaction: " + err.Error())
		return 0
	}
	defer tx.Rollback()

	stmt, err := tx.Prepare("SELECT wordCount FROM URLs WHERE url = ?")
	if err != nil {
		log.Println("error preparing statement to get total doc words: " + err.Error())
		return 0
	}
	defer stmt.Close()

	qErr := stmt.QueryRow(url).Scan(&count)
	if qErr != nil {
		log.Printf("error querying row '%s' for total doc words: %v\n", url, qErr.Error())
		return 0
	}

	// Commit the transaction
	err = tx.Commit()
	if err != nil {
		log.Println("error committing the transaction: " + err.Error())
	}

	return count
}

func (s *SearchEngine) QueryTotalURLs() int {
	count := 0
	err := s.data.db.QueryRow("SELECT COUNT(*) FROM URLs").Scan(&count)
	if err != nil {
		log.Println("error querying total urls: " + err.Error())
		return 0
	}
	return count
}

func (s *SearchEngine) AnalyzeOptimalQuerying() {
	_, err := s.data.db.Exec("ANALYZE")
	if err != nil {
		log.Println("error analyzing database: " + err.Error())
		return
	}

	_, err = s.data.db.Exec("VACUUM")
	if err != nil {
		log.Println("error vacuuming database: " + err.Error())
		return
	}
}
