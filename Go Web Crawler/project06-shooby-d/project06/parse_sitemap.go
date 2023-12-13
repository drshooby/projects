package project06

import (
	"encoding/xml"
	"io"
	"log"
	"net/http"
)

// for https://www.dvc.edu/sitemap.xml
type (
	URL struct {
		Loc string `xml:"loc"`
	}

	SitemapIndex struct {
		XMLName xml.Name `xml:"urlset"`
		URLs    []URL    `xml:"url"`
	}

	Sitemap struct {
		index *SitemapIndex
		sm    string
	}
)

func (sm *Sitemap) DownloadSiteMap() []byte {

	if rsp, err := http.Get(sm.sm); err == nil {
		defer rsp.Body.Close()

		if rsp.StatusCode == 200 {
			if bts, err := io.ReadAll(rsp.Body); err == nil {
				return bts
			} else {
				log.Printf("unable to read body of sitemap: %v.\n", err)
			}
		} else {
			log.Printf("download of sitemap failed...received status code %s for url: %s.\n", rsp.Status, sm.sm)
		}
	} else {
		log.Printf("download of sitemap failed...unable to get url %s: %v", sm.sm, err)
	}
	return nil
}

func (s *SearchEngine) ParseSitemap() []string {

	body := s.sitemap.DownloadSiteMap()

	if body == nil {
		log.Println("unable to download sitemap")
		return nil
	}

	s.sitemap.index = &SitemapIndex{}

	if err := xml.Unmarshal(body, s.sitemap.index); err != nil {
		log.Printf("unable to unmarshal sitemap: %v\n", err)
	}

	var urls []string
	for _, url := range s.sitemap.index.URLs {
		if IsHTML(url.Loc) && s.IsAllowedLink(url.Loc, s.ua) {
			urls = append(urls, url.Loc)
		}
	}

	log.Println("parsed sitemap successfully with", len(urls), "urls")

	return urls
}
