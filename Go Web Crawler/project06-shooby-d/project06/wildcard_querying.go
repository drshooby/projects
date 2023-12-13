package project06

import (
	"log"
)

func (s *SearchEngine) QueryURLsWildcard(term string, results []UrlTitlePair) []UrlTitlePair {

	q := `SELECT U.url, U.title
	FROM Hits AS H
	JOIN Terms AS T ON H.term_id = T.id
	JOIN URLs AS U ON H.url_id = U.id
	WHERE T.term LIKE ?
	GROUP BY U.url, U.title;`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return nil
	}
	defer stmt.Close()

	rows, err := stmt.Query(term + "%")
	if err != nil {
		log.Println("wildcard: error querying term when searching for url/titles for tfidf: " + err.Error())
		return nil
	}
	defer rows.Close()

	for rows.Next() {
		var res UrlTitlePair
		if err := rows.Scan(&res.URL, &res.Title); err != nil {
			log.Println("wildcard: error scanning url and title when searching for urls/titles for tfidf: " + err.Error())
			return nil
		}
		results = append(results, res)
	}

	if err := rows.Err(); err != nil {
		log.Println("wildcard: error iterating over rows when searching for urls/titles for tfidf: " + err.Error())
		return nil
	}

	return results
}

func (s *SearchEngine) QueryTermFreqInURLWildcard(term, url string, termFreq int) int {

	q := `SELECT H.frequency
			FROM Hits AS H
			JOIN Terms AS T ON H.term_id = T.id
			JOIN URLs AS U ON H.url_id = U.id
			WHERE T.term LIKE ? AND U.url = ?;`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return 0
	}
	defer stmt.Close()

	err = stmt.QueryRow(term+"%", url).Scan(&termFreq)
	if err != nil {
		log.Println("wildcard: error querying term frequency for url in tfidf: " + err.Error())
		return 0
	}

	return termFreq
}

func (s *SearchEngine) QueryNumURLsTermIsInWildcard(term string, numURLs int) int {

	q := `SELECT COUNT(DISTINCT U.url)
			FROM Hits AS H
			JOIN Terms AS T ON H.term_id = T.id
			JOIN URLs AS U ON H.url_id = U.id
			WHERE T.term LIKE ?;`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return 0
	}
	defer stmt.Close()

	err = stmt.QueryRow(term + "%").Scan(&numURLs)
	if err != nil {
		log.Println("wildcard: error querying number of urls term is in for tfidf: " + err.Error())
		return 0
	}

	return numURLs
}

func (s *SearchEngine) QueryBigramURLsWildcard(t1, t2 string, results []UrlTitlePair) []UrlTitlePair {

	q := `SELECT DISTINCT URLs.url, URLs.title
	FROM URLs
	JOIN BiHits ON URLs.id = BiHits.url_id
	JOIN Terms AS Term1 ON BiHits.term1_id = Term1.id
	JOIN Terms AS Term2 ON BiHits.term2_id = Term2.id
	WHERE (Term1.term LIKE ? AND Term2.term LIKE ?)
	OR (Term1.term LIKE ? AND Term2.term LIKE ?);`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return nil
	}
	defer stmt.Close()

	t1 = t1 + "%"
	t2 = t2 + "%"

	rows, err := stmt.Query(t1, t2, t2, t1)
	if err != nil {
		log.Println("bigram wildcard: error querying term when searching for url/titles for tfidf: " + err.Error())
		return nil
	}
	defer rows.Close()

	for rows.Next() {
		var res UrlTitlePair
		if err := rows.Scan(&res.URL, &res.Title); err != nil {
			log.Println("bigram wildcard: error scanning url and title when searching for urls/titles for tfidf: " + err.Error())
			return nil
		}
		results = append(results, res)
	}

	if err := rows.Err(); err != nil {
		log.Println("bigram wildcard: error iterating over rows when searching for urls/titles for tfidf: " + err.Error())
		return nil
	}

	return results
}

func (s *SearchEngine) QueryBigramTermFreqInURLWildcard(t1, t2, url string, termFreq int) int {

	q := `SELECT BH.frequency
	FROM BiHits AS BH
	JOIN Terms AS Term1 ON BH.term1_id = Term1.id
	JOIN Terms AS Term2 ON BH.term2_id = Term2.id
	JOIN URLs AS U ON BH.url_id = U.id
	WHERE (Term1.term LIKE ? AND Term2.term LIKE ?)
	OR (Term1.term LIKE ? AND Term2.term LIKE ?)
	AND U.url = ?;`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return 0
	}
	defer stmt.Close()

	t1 = t1 + "%"
	t2 = t2 + "%"

	err = stmt.QueryRow(t1, t2, t2, t1, url).Scan(&termFreq)
	if err != nil {
		log.Println("bigram wildcard: error querying term frequency for url in tfidf: " + err.Error())
		return 0
	}

	return termFreq
}

func (s *SearchEngine) QueryNumURLsBigramTermsAreInWildcard(t1, t2 string, numURLs int) int {

	q := `SELECT COUNT(DISTINCT U.url)
	FROM BiHits AS BH
	JOIN Terms AS Term1 ON BH.term1_id = Term1.id
	JOIN Terms AS Term2 ON BH.term2_id = Term2.id
	JOIN URLs AS U ON BH.url_id = U.id
	WHERE (Term1.term LIKE ? AND Term2.term LIKE ?)
	OR (Term1.term LIKE ? AND Term2.term LIKE ?);`

	stmt, err := s.data.db.Prepare(q)
	if err != nil {
		return 0
	}
	defer stmt.Close()

	t1 = t1 + "%"
	t2 = t2 + "%"

	err = stmt.QueryRow(t1, t2, t2, t1).Scan(&numURLs)
	if err != nil {
		log.Println("bigram wildcard: error querying number of urls term is in for tfidf: " + err.Error())
		return 0
	}

	return numURLs
}
