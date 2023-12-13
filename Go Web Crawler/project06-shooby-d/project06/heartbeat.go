package project06

import (
	"fmt"
	"log"
	"time"
)

func (s *SearchEngine) HeartbeatRoutine(quit chan struct{}, EC chan extracting, DC chan downloading, LC chan string) {
	// counter is fairly reliable for small delays, but larger delays will cause the counter to be inaccurate due to unpredicatble function scheduling
	// so for longer delays we use the length of the visited map to check for a heartbeat, then fall back to the counter at the end when it actualy works
	log.Println("starting heartbeat routine...")
	s.HeartbeatHelper(quit, EC, DC, LC)
}

func (s *SearchEngine) HeartbeatHelper(quit chan struct{}, EC chan extracting, DC chan downloading, LC chan string) {
	prevSize := len(s.vis)
	defer close(EC)
	defer close(DC)
	defer close(LC)
	for {
		select {
		case <-quit:
			return
		default:
			time.Sleep(5 * time.Second)
			if len(s.vis) == prevSize {
				log.Println("Vis not updated, switching to counter...")
				prevSize = s.hbc
				time.Sleep(1 * time.Second)
				// in the final check, essentially a while loop begins to check the counter
				s.HeartbeatFinalCheck(prevSize)
				close(quit)
			}
			prevSize = len(s.vis)
		}
	}
}

func (s *SearchEngine) HeartbeatFinalCheck(prevSize int) {
	for s.hbc != prevSize {
		prevSize = s.hbc
		time.Sleep(6 * time.Second)
	}
	fmt.Println("Not detecting heartbeat...ending crawl. Check site at: http://localhost:8080/")
}
