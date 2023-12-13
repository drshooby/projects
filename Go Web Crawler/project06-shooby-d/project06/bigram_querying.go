package project06

import (
	"log"
)

func (s *SearchEngine) QueryBigramURLs(t1, t2 string, useWildcard bool) []UrlTitlePair {
	var results []UrlTitlePair

	if useWildcard {
		return s.QueryBigramURLsWildcard(t1, t2, results)
	}

	q := `SELECT DISTINCT URLs.url, URLs.title
	FROM URLs
	JOIN BiHits ON URLs.id = BiHits.url_id
	JOIN Terms AS Term1 ON BiHits.term1_id = Term1.id
	JOIN Terms AS Term2 ON BiHits.term2_id = Term2.id
	WHERE (Term1.term = ? AND Term2.term = ?)
	OR (Term1.term = ? AND Term2.term = ?);`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return nil
	}
	defer stmt.Close()

	rows, err := stmt.Query(t1, t2, t2, t1)
	if err != nil {
		log.Println("bigram: error querying term when searching for url/titles for tfidf: " + err.Error())
		return nil
	}
	defer rows.Close()

	for rows.Next() {
		var res UrlTitlePair
		if err := rows.Scan(&res.URL, &res.Title); err != nil {
			log.Println("bigram: error scanning url and title when searching for urls/titles for tfidf: " + err.Error())
			return nil
		}
		results = append(results, res)
	}

	if err := rows.Err(); err != nil {
		log.Println("bigram: error iterating over rows when searching for urls/titles for tfidf: " + err.Error())
		return nil
	}

	return results
}

func (s *SearchEngine) QueryBigramTermFreqInURL(t1, t2, url string, useWildcard bool) int {
	var termFreq int

	if useWildcard {
		return s.QueryBigramTermFreqInURLWildcard(t1, t2, url, termFreq)
	}

	q := `SELECT BH.frequency
	FROM BiHits AS BH
	JOIN Terms AS Term1 ON BH.term1_id = Term1.id
	JOIN Terms AS Term2 ON BH.term2_id = Term2.id
	JOIN URLs AS U ON BH.url_id = U.id
	WHERE (Term1.term = ? AND Term2.term = ?)
	OR (Term1.term = ? AND Term2.term = ?)
	AND U.url = ?;`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return 0
	}
	defer stmt.Close()

	err = stmt.QueryRow(t1, t2, t2, t1, url).Scan(&termFreq)
	if err != nil {
		log.Println("bigram: error querying term frequency for url in tfidf: " + err.Error())
		return 0
	}

	return termFreq
}

func (s *SearchEngine) QueryNumURLsBigramTermsAreIn(t1, t2 string, useWildcard bool) int {
	var numURLs int

	if useWildcard {
		return s.QueryNumURLsBigramTermsAreInWildcard(t1, t2, numURLs)
	}

	q := `SELECT COUNT(DISTINCT U.url)
	FROM BiHits AS BH
	JOIN Terms AS Term1 ON BH.term1_id = Term1.id
	JOIN Terms AS Term2 ON BH.term2_id = Term2.id
	JOIN URLs AS U ON BH.url_id = U.id
	WHERE (Term1.term = ? AND Term2.term = ?)
	OR (Term1.term = ? AND Term2.term = ?);`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return 0
	}
	defer stmt.Close()

	err = stmt.QueryRow(t1, t2, t2, t1).Scan(&numURLs)
	if err != nil {
		log.Println("bigram: error querying number of urls term is in for tfidf: " + err.Error())
		return 0
	}

	return numURLs
}
