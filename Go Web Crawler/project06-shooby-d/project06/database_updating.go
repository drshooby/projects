package project06

import (
	"database/sql"
	"log"

	_ "github.com/mattn/go-sqlite3"
)

type BiHits struct {
	// bigram is two terms
	t1 string
	t2 string
}

func (s *SearchEngine) UpdateFreq(words []string, URL, title string, im_data []imageData) {
	tx, err := s.data.db.Begin()
	if err != nil {
		log.Println("error beginning transaction: " + err.Error())
		return
	}
	defer tx.Rollback()

	// insert the URL and title
	if err := s.InsertURLAndTitleInTransaction(tx, URL, title); err != nil {
		log.Println("error inserting url and title: " + err.Error())
		return
	}

	var wordCount int

	var imageTermsToInsert []string
	/*
	* The reason behind the repeated code is because
	* we need to insert the image terms into the Terms table
	* to get the ids of the terms, which are needed to insert into the Images table.

	* imageTermsToInsert doesn't care about the relationship between terms and alt text/src url,
	* so it remains separated from the image insertion code.
	 */
	for _, img := range im_data {
		for _, word := range img.alt_parts {
			cleaned, err := CleanWords(word)
			if err != nil {
				log.Println("error with CleanWords for images:" + err.Error())
				continue
			}
			if _, ok := StopWords[cleaned]; ok {
				continue
			}
			imageTermsToInsert = append(imageTermsToInsert, cleaned)
		}
	}

	// regular hits variables
	var termsToInsert []string
	var termsToInsertBiHits []BiHits

	// bigram hits variables
	var bi_counter int
	var first_word, second_word string

	// process words
	for _, word := range words {
		cleaned, err := CleanWords(word)
		if err != nil {
			log.Println("error with CleanWords:" + err.Error())
			continue
		}
		if _, ok := StopWords[cleaned]; ok {
			continue
		}
		wordCount++

		// process bigrams
		switch bi_counter {
		case 0:
			first_word = cleaned
			bi_counter++
		case 1:
			second_word = cleaned
			termsToInsertBiHits = append(termsToInsertBiHits, BiHits{first_word, second_word})
			first_word = second_word
			bi_counter = 1
		}

		// process regular hits
		termsToInsert = append(termsToInsert, cleaned)
	} // end loop

	// process leftover word
	if bi_counter == 1 {
		termsToInsertBiHits = append(termsToInsertBiHits, BiHits{first_word, ""})
	}

	termsToInsert = append(termsToInsert, imageTermsToInsert...) // add image terms to regular terms, because the image table needs the ids of the terms

	// update the word count for the current URL
	if err := s.UpdateWordCountInTransaction(tx, URL, wordCount); err != nil {
		log.Println("error updating word count: " + err.Error())
		return
	}

	// insert terms
	if err := s.InsertTermsInTransaction(tx, termsToInsert); err != nil {
		log.Println("error inserting terms: " + err.Error())
		return
	}

	// insert images
	if err := s.InsertImagesInTransaction(tx, URL, im_data); err != nil {
		log.Println("error inserting images: " + err.Error())
		return
	}

	// insert hits
	if err := s.InsertHitsInTransaction(tx, URL, termsToInsert); err != nil {
		log.Println("error inserting hits: " + err.Error())
		return
	}

	// insert bigram hits
	if err := s.InsertBigramHitsInTransaction(tx, URL, termsToInsertBiHits); err != nil {
		log.Println("error inserting bigram hits: " + err.Error())
		return
	}

	if err := tx.Commit(); err != nil {
		log.Println("error committing transaction: " + err.Error())
		return
	}
}

func (s *SearchEngine) InsertTermsInTransaction(tx *sql.Tx, terms []string) error {

	for _, term := range terms {
		s.hbc++
		_, insErr := s.prep.stmtInsertTerm.Exec(term)
		if insErr != nil {
			return insErr
		}

		var termID int
		qErr := s.prep.stmtSelectTermID.QueryRow(term).Scan(&termID)
		if qErr != nil {
			return qErr
		}
		s.hbc++
		s.data.termIDs[term] = termID
	}
	return nil
}

func (s *SearchEngine) InsertImagesInTransaction(tx *sql.Tx, URL string, im_data []imageData) error {

	urlID := s.data.urlIDs[URL]

	for _, img := range im_data {
		for _, word := range img.alt_parts {
			s.hbc++
			cleaned, err := CleanWords(word)
			if err != nil {
				log.Println("error with CleanWords in transaction:" + err.Error())
				continue
			}
			if _, ok := StopWords[cleaned]; ok {
				continue
			}
			termID, exists := s.data.termIDs[cleaned]
			if !exists {
				log.Printf("Term not found in transaction: %s\n", cleaned)
				continue
			}
			_, insErr := s.prep.stmtInsertImage.Exec(urlID, termID, img.alt, img.src)
			if insErr != nil {
				return insErr
			}
		}
	}
	return nil
}

func (s *SearchEngine) InsertURLAndTitleInTransaction(tx *sql.Tx, URL, title string) error {

	res, execErr := s.prep.stmtInsertURLandTitle.Exec(URL, title)
	if execErr != nil {
		return execErr
	}

	id, idErr := res.LastInsertId()
	if idErr != nil {
		return idErr
	}

	s.data.urlIDs[URL] = int(id)

	return nil
}

func (s *SearchEngine) UpdateWordCountInTransaction(tx *sql.Tx, URL string, wc int) error {
	urlID := s.data.urlIDs[URL]

	_, err := s.prep.stmtUpdateWordCount.Exec(wc, urlID)
	if err != nil {
		return err
	}
	return nil
}

func (s *SearchEngine) InsertHitsInTransaction(tx *sql.Tx, URL string, words []string) error {

	if len(words) == 0 {
		return nil
	}

	for _, word := range words {
		s.hbc++ // update heartbeat counter because this process can take a while
		termID, termEx := s.data.termIDs[word]
		urlID, urlEx := s.data.urlIDs[URL]

		if !termEx || !urlEx {
			continue
		}

		var frequency int
		qErr := s.prep.stmtSelectHitFreq.QueryRow(termID, urlID).Scan(&frequency) // check if the term and url pair already exist
		s.hbc++

		if qErr != nil { // if it doesn't exist, insert it
			_, insErr := s.prep.stmtInsertHits.Exec(termID, urlID)
			if insErr != nil {
				return insErr
			}
		} else { // if it does exist, update the frequency
			_, updErr := s.prep.stmtUpdateHitFreq.Exec(termID, urlID)
			if updErr != nil {
				return updErr
			}
		}
	}

	return nil
}

func (s *SearchEngine) InsertBigramHitsInTransaction(tx *sql.Tx, URL string, bigrams []BiHits) error {

	if len(bigrams) == 0 {
		return nil
	}

	for _, bigram := range bigrams {
		s.hbc++ // update heartbeat counter because this process can take a while
		termID_1, termEx_1 := s.data.termIDs[bigram.t1]
		termID_2, termEx_2 := s.data.termIDs[bigram.t2]

		urlID, urlEx := s.data.urlIDs[URL]

		if !termEx_1 || !termEx_2 || !urlEx {
			continue
		}

		var frequency int
		qErr := s.prep.stmtSelectBiHitFreq.QueryRow(termID_1, termID_2, urlID).Scan(&frequency) // check if the term and url pair already exist
		s.hbc++

		if qErr != nil { // if it doesn't exist, insert it
			_, insErr := s.prep.stmtInsertBiHits.Exec(termID_1, termID_2, urlID)
			if insErr != nil {
				return insErr
			}
		} else { // if it does exist, update the frequency
			_, updErr := s.prep.stmtUpdateBiHitFreq.Exec(termID_1, termID_2, urlID)
			if updErr != nil {
				return updErr
			}
		}
	}
	return nil
}
