package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main() {
	inputFilename := os.Args[1]

	part1(inputFilename)
	part2(inputFilename)
}

func part1(inputFilename string) {
	f, err := os.Open(inputFilename)
	if err != nil {
		panic(err)
	}

	scanner := bufio.NewScanner(f)

	var countSafe int

	for scanner.Scan() {
		line := scanner.Text()

		itemsString := strings.Split(line, " ")

		items := make([]int, 0, len(itemsString))

		for _, itemString := range itemsString {
			conv, err := strconv.Atoi(itemString)
			if err != nil {
				panic(err)
			}
			items = append(items, conv)
		}

		if isSafe(items) {
			countSafe++
		}

	}

	fmt.Println("part 1", countSafe)
}

func part2(inputFilename string) {
	f, err := os.Open(inputFilename)
	if err != nil {
		panic(err)
	}

	scanner := bufio.NewScanner(f)

	var countSafe int

	for scanner.Scan() {
		line := scanner.Text()

		itemsString := strings.Split(line, " ")

		items := make([]int, 0, len(itemsString))

		for _, itemString := range itemsString {
			conv, err := strconv.Atoi(itemString)
			if err != nil {
				panic(err)
			}
			items = append(items, conv)
		}

		var i int
		skippedItems := items
		for i = 0; i <= len(items) && !isSafe(skippedItems); i++ {
			skippedItems = skipIdx(items, i)
		}

		if i <= len(items) {
			countSafe++
		}
	}

	fmt.Println("part 2", countSafe)
}

func skipIdx(items []int, skipIdx int) []int {
	if skipIdx < 0 || skipIdx > len(items) {
		return items
	}
	res := make([]int, 0, len(items)-1)
	for i, item := range items {
		if i != skipIdx {
			res = append(res, item)
		}
	}
	return res
}

func isSafe(items []int) bool {
	reportIncr := isIncreasing(items[0], items[1])
	for i := 0; i < len(items)-1; i++ {
		if !isAcceptableDiff(items[i], items[i+1]) {
			return false
		}
		if localIncr := isIncreasing(items[i], items[i+1]); localIncr != reportIncr {
			return false
		}
	}
	return true
}

func isAcceptableDiff(a, b int) bool {
	localDiff := diff(a, b)
	return localDiff >= 1 && localDiff <= 3
}

func diff(a, b int) int {
	return abs(a - b)
}

func isIncreasing(a, b int) bool {
	return b-a > 0
}

func abs(n int) int {
	if n > 0 {
		return n
	}
	return -n
}
