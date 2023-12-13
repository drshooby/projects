package project06

import (
	"database/sql"
	"log"
	"net/url"
	"time"
)

var StopWords map[string]struct{}

type (
	AgentDisallowRules map[string][]string
	AgentDelays        map[string]time.Duration

	Robots struct {
		AgentDisallowRules
		AgentDelays
	}

	Data struct {
		db      *sql.DB
		termIDs map[string]int
		urlIDs  map[string]int
	}

	SearchEngine struct { // main struct for the whole crawler
		ua          string
		host        *url.URL
		vis         map[string]struct{}
		hbc         int // heartbeat counter
		downloaders int
		extractors  int
		robotsTxt   Robots
		sitemap     *Sitemap
		data        Data
		prep        PreparedStatements
	}
)

func NewSearchEngine(d, e int) *SearchEngine {
	s := SearchEngine{
		ua:          "",
		host:        nil,
		vis:         make(map[string]struct{}),
		hbc:         0,
		downloaders: d,
		extractors:  e,
		robotsTxt: Robots{
			make(AgentDisallowRules),
			make(AgentDelays),
		},
		sitemap: &Sitemap{
			index: nil,
			sm:    "",
		},
		data: Data{
			termIDs: make(map[string]int),
			urlIDs:  make(map[string]int),
			db:      nil,
		},
	}

	StopWords = CreateStopWordsSet()

	db, err := sql.Open("sqlite3", DBNAME)
	if err != nil {
		log.Fatalf("fatal error opening database in SearchEngine construction: %v", err)
	}
	s.data.db = db

	s.InitDB()

	if err := s.Prepare(); err != nil {
		log.Fatalf("fatal error preparing statements in SearchEngine construction: %v", err)
	}

	log.Println("database intialized successfully")

	return &s
}
