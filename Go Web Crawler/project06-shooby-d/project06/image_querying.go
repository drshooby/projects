package project06

import "log"

type (
	ImageHit struct {
		SRC string // url of image
		ALT string // description
	}

	ImageHits []ImageHit
)

func (s *SearchEngine) GetImageHits(query string, useWildcard bool) ImageHits {
	s.OpenDB()
	defer s.CloseDB()

	var results ImageHits
	if useWildcard {
		results = s.ImageWildcardSearch(query)
	} else {
		results = s.ImageSearch(query)
	}
	return results
}

func (s *SearchEngine) ImageSearch(term string) ImageHits {
	s.OpenDB()
	defer s.CloseDB()

	rows, err := s.data.db.Query(`
		SELECT DISTINCT I.description, I.src_url
		FROM Images I
		JOIN Terms T ON I.term_id = T.id
		WHERE T.term LIKE ?
	`, term)

	if err != nil {
		log.Println("Error querying database for images:", err)
		return nil
	}
	defer rows.Close()

	var results ImageHits

	for rows.Next() {
		var (
			src string
			alt string
		)
		if err := rows.Scan(&alt, &src); err != nil {
			log.Println("Error scanning row for images:", err)
			continue
		}

		results = append(results, ImageHit{src, alt})
	}
	return results
}

func (s *SearchEngine) ImageWildcardSearch(term string) []ImageHit {
	rows, err := s.data.db.Query(`
		SELECT DISTINCT I.description, I.src_url
		FROM Images I
		JOIN Terms T ON I.term_id = T.id
		WHERE T.term LIKE ?
	`, term+"%")

	if err != nil {
		log.Println("Error querying database for images:", err)
		return nil
	}
	defer rows.Close()

	var results ImageHits

	for rows.Next() {
		var (
			src string
			alt string
		)
		if err := rows.Scan(&alt, &src); err != nil {
			log.Println("Error scanning row for images in wildcard:", err)
			continue
		}

		results = append(results, ImageHit{src, alt})
	}
	return results
}
