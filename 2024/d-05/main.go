package main

import (
	"bufio"
	"context"
	"fmt"
	"os"
	"slices"
	"strconv"
	"strings"
	"sync"
	"sync/atomic"
)

type Rule map[string][]string

type Result string

const (
	VALID   Result = "VALID"
	INVALID Result = "INVALID"
)

func main() {
	inputFilename := os.Args[1]

	f, err := os.Open(inputFilename)
	if err != nil {
		panic(err)
	}
	defer f.Close()

	scanner := bufio.NewScanner(f)

	rules := make(Rule)

	for scanner.Scan() {
		line := scanner.Text()

		if line == "" {
			// end of the first part
			break
		}

		items := strings.Split(line, "|")
		first := items[0]
		second := items[1]

		if rules[second] == nil {
			rules[second] = make([]string, 0, 2)
		}

		rules[first] = append(rules[first], second)
	}

	if scanner.Err() != nil {
		panic(scanner.Err())
	}

	pages := make([][]string, 0)
	for scanner.Scan() {
		line := scanner.Text()
		page := strings.Split(line, ",")
		pages = append(pages, page)
	}

	if scanner.Err() != nil {
		panic(scanner.Err())
	}

	var count uint64

	wg := sync.WaitGroup{}

	for _, page := range pages {
		page := page
		ctx, cancelBase := context.WithCancel(context.TODO())

		pairChan := producePairs(ctx, page)
		checkChan := checkPairs(ctx, rules, pairChan)

		wg.Add(1)
		go func() {
			defer cancelBase()
			defer wg.Done()
			getValue(ctx, page, &count, checkChan)
		}()
	}

	wg.Wait()

	fmt.Println("part 1", count)
}

func producePairs(ctx context.Context, page []string) <-chan []string {
	out := make(chan []string)

	go func() {
		defer close(out)
		for i := 0; i < len(page)-1; i++ {
			for j := i + 1; j < len(page); j++ {
				select {
				case <-ctx.Done():
					fmt.Println("producePairs cancelled")
					return
				default:
					pair := []string{page[i], page[j]}
					fmt.Println("pairs", pair, page)
					out <- pair
				}
			}
		}
	}()

	return out
}

func checkPairs(ctx context.Context, rules Rule, pairsChan <-chan []string) <-chan Result {
	out := make(chan Result)

	go func() {
		for {
			select {
			case pair, ok := <-pairsChan:
				if ok {
					if slices.Contains(rules[pair[1]], pair[0]) {
						fmt.Println(pair, rules[pair[1]])
						out <- INVALID
						return
					}
				} else {
					out <- VALID
					return
				}
			case <-ctx.Done():
				return
			}
		}
	}()
	return out
}

func getValue(ctx context.Context, page []string, counter *uint64, checkChan <-chan Result) {
	for {
		select {
		case result := <-checkChan:
			if result == VALID {
				fmt.Println("received from resultCheckChan which means an invalid pair has been found", page)
				if err := incrementCounter(page, counter); err != nil {
					panic(err)
				}
			}
			return
		case <-ctx.Done():
			fmt.Println("getValue cancelled")
			return
		}
	}
}

func incrementCounter(page []string, counter *uint64) error {
	middleValueString := page[len(page)/2]
	middleValue, err := strconv.ParseUint(middleValueString, 0, 0)
	if err != nil {
		return err
	}

	fmt.Println("middleValue", middleValue, len(page)/2, page)
	atomic.AddUint64(counter, middleValue)
	return nil
}
