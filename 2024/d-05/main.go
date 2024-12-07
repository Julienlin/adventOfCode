package main

import (
	"bufio"
	"container/list"
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

	// part1(f)
	// f.Seek(0, 0)
	part2(f)
}

func part1(f *os.File) {
	scanner := bufio.NewScanner(f)

	rules := make(Rule)

	for scanner.Scan() {
		line := scanner.Text()

		if line == "" {
			break
		}

		items := strings.Split(line, "|")
		first := items[0]
		second := items[1]

		// if rules[second] == nil {
		// 	rules[second] = make([]string, 0, 2)
		// }

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

func part2(f *os.File) {
	scanner := bufio.NewScanner(f)
	rules := make(map[int][]int)

	for scanner.Scan() {
		line := scanner.Text()

		if line == "" {
			break
		}

		items := strings.Split(line, "|")

		first, err := strconv.Atoi(items[0])
		if err != nil {
			panic(err)
		}
		second, err := strconv.Atoi(items[1])
		if err != nil {
			panic(err)
		}

		rules[first] = append(rules[first], second)

	}

	pages := make([][]int, 0)
	for scanner.Scan() {
		line := scanner.Text()
		page := strings.Split(line, ",")

		pageInt := make([]int, 0, len(page))
		for _, p := range page {
			n, _ := strconv.Atoi(p)
			pageInt = append(pageInt, n)
		}
		pages = append(pages, pageInt)
	}

	if scanner.Err() != nil {
		panic(scanner.Err())
	}

	var count uint64

	wg := sync.WaitGroup{}

	for _, page := range pages {
		page := page
		ctx, cancelBase := context.WithCancel(context.TODO())

		pairWithIndex := producePairsWithIndex(ctx, page)
		swapInstructions := filterUnsatifiedRule(ctx, rules, pairWithIndex)
		reorderedPage := fixPageOrder(ctx, page, swapInstructions)

		wg.Add(1)
		go func() {
			defer wg.Done()
			defer cancelBase()
			getValueInt(ctx, &count, reorderedPage)
		}()
	}

	wg.Wait()

	fmt.Println("part 2", count)
}

func producePairsWithIndex(ctx context.Context, page []int) <-chan []int {
	out := make(chan []int)

	go func() {
		defer close(out)
		fmt.Println(page)
		for i := 0; i < len(page)-1; i++ {
			for j := i + 1; j < len(page); j++ {
				select {
				case <-ctx.Done():
					return
				default:
					p := []int{page[i], page[j], i, j}
					// fmt.Println(p)
					out <- p
				}
			}
		}
	}()

	return out
}

func filterUnsatifiedRule(ctx context.Context, rules map[int][]int, pairsWithIndexChan <-chan []int) <-chan []int {
	out := make(chan []int)

	go func() {
		defer close(out)
		for {
			select {
			case pairWithIndex, ok := <-pairsWithIndexChan:
				if ok {
					if slices.Contains(rules[pairWithIndex[1]], pairWithIndex[0]) {
						// fmt.Println(pairWithIndex, rules[pairWithIndex[1]])
						out <- []int{pairWithIndex[2], pairWithIndex[3]}
					}
				} else {
					return
				}
			case <-ctx.Done():
				return
			}
		}
	}()
	return out
}

func fixPageOrder(ctx context.Context, page []int, indexToSwap <-chan []int) <-chan []int {
	out := make(chan []int)

	// pageCopy := make([]int, len(page))
	// copy(pageCopy, page)

	elements := make([]*list.Element, 0, len(page))

	linkedlist := list.New()

	for _, p := range page {
		el := linkedlist.PushBack(p)
		elements = append(elements, el)
	}

	go func() {
		hasChanged := false
		for {
			select {
			case pair, ok := <-indexToSwap:
				if ok {
					fmt.Println(pair)
					i := pair[0]
					j := pair[1]
					// pageCopy[i], pageCopy[j] = pageCopy[j], pageCopy[i]
					if !isElementAfterMark(elements[i], elements[j]) {
						linkedlist.MoveAfter(elements[i], elements[j])
						hasChanged = true
					}
				} else {
					indexToSwap = nil
					if hasChanged {

						pageCopy := make([]int, 0, len(page))

						for current := linkedlist.Front(); current != nil; current = current.Next() {
							pageCopy = append(pageCopy, current.Value.(int))
						}
						fmt.Println("before", page)
						fmt.Println("after ", pageCopy)
						out <- pageCopy
					}
					close(out)
				}
			case <-ctx.Done():
				return
			}
		}
	}()

	return out
}

func isElementAfterMark(el *list.Element, mark *list.Element) bool {
	for cur := mark; cur != nil; cur = cur.Next() {
		if cur == el {
			return true
		}
	}
	return false
}

func getValueInt(ctx context.Context, count *uint64, pageChan <-chan []int) {
	for {
		select {
		case page, ok := <-pageChan:
			if ok {
				// fmt.Println(page)
				middleValue := page[len(page)/2]
				atomic.AddUint64(count, uint64(middleValue))
			} else {
				return
			}
		case <-ctx.Done():
			return
		}
	}
}
