package project06

import (
	"sort"
)

type (
	Hits []Hit

	Hit struct {
		Title string
		Rank  float64
		URL   string
	}
)

var TotalURLs int

func (s *SearchEngine) GetHits(term string, useWildcard bool) Hits {

	s.OpenDB()
	defer s.CloseDB()

	if TotalURLs == 0 {
		TotalURLs = s.QueryTotalURLs() // do it once, then store it, if constant searching is needed, then this can be removed
	}

	var results Hits

	for _, pair := range s.QueryURLs(term, useWildcard) {

		freq := s.QueryTermFreqInURL(term, pair.URL, useWildcard)

		tf := s.CalculateTF(freq, s.GetTotalDocWords(pair.URL))
		idf := s.CalculateIDF(s.QueryNumURLsTermIsIn(term, useWildcard))
		tfidf := s.CalculateTFIDF(tf, idf)

		results = append(results, Hit{Title: pair.Title, Rank: tfidf, URL: pair.URL})
	}

	sort.Sort(results)

	return results
}

func (s *SearchEngine) GetBigramHits(term1, term2 string, useWildcard bool) Hits {

	s.OpenDB()
	defer s.CloseDB()

	if TotalURLs == 0 {
		TotalURLs = s.QueryTotalURLs() // do it once, then store it, if constant searching is needed, then this can be removed
	}

	var results Hits

	for _, pair := range s.QueryBigramURLs(term1, term2, useWildcard) {

		freq := s.QueryBigramTermFreqInURL(term1, term2, pair.URL, useWildcard)

		tf := s.CalculateTF(freq, s.GetTotalDocWords(pair.URL))
		idf := s.CalculateIDF(s.QueryNumURLsBigramTermsAreIn(term1, term2, useWildcard))
		tfidf := s.CalculateTFIDF(tf, idf)

		results = append(results, Hit{Title: pair.Title, Rank: tfidf, URL: pair.URL})
	}

	sort.Sort(results)

	return results
}

// Required sorting methods for Hits

func (res Hits) Len() int {
	return len(res)
}

func (res Hits) Less(i, j int) bool {
	// sort in descending order based on TF-IDF score
	if res[i].Rank == res[j].Rank {
		return res[i].Title > res[j].Title
	}
	return res[i].Rank > res[j].Rank
}

func (res Hits) Swap(i, j int) {
	res[i], res[j] = res[j], res[i]
}
