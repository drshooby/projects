package project06

func (s *SearchEngine) CalculateTF(termFreq, totalDocWords int) float64 {
	return float64(termFreq) / float64(totalDocWords)
}

func (s *SearchEngine) CalculateIDF(docsWithTerm int) float64 {
	df := float64(docsWithTerm) / float64(TotalURLs) // or len(s.vis) if the db is being made from the beginning
	return 1.0 / df
}

func (s *SearchEngine) CalculateTFIDF(tf float64, idf float64) float64 {
	return tf * idf
}
