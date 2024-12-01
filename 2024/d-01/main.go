package main

import (
	"bufio"
	"bytes"
	"fmt"
	"io"
	"os"
	"slices"
	"strconv"
	"strings"
)

func main() {
	inputFilename := os.Args[1]

	f, err := os.Open(inputFilename)
	if err != nil {
		panic(err)
	}
	defer func() {
		err := f.Close()
		if err != nil {
			fmt.Printf("error when closing file: %s", err.Error())
		}
	}()

	lineCount, err := LineCounter(f)
	if err != nil {
		panic(err)
	}

	left := make([]int, 0, lineCount)
	right := make([]int, 0, lineCount)

	_, err = f.Seek(0, 0)
	if err != nil {
		panic(err)
	}

	scanner := bufio.NewScanner(f)

	for scanner.Scan() {
		line := scanner.Text()
		// fmt.Println("line:", line)

		items := strings.Split(line, "   ")

		// fmt.Println("items[0]", items[0], "items[1]", items[1])
		leftInt, err := strconv.Atoi(items[0])
		if err != nil {
			panic(err)
		}
		left = append(left, leftInt)

		rightInt, err := strconv.Atoi(items[1])
		if err != nil {
			panic(err)
		}
		right = append(right, rightInt)
	}

	if err := scanner.Err(); err != nil {
		panic(err)
	}

	slices.Sort(left)
	slices.Sort(right)

	part1(left, right)
	part2(left, right)
}

func part1(left []int, right []int) {
	var sumDiff int

	for i := 0; i < len(left); i++ {
		sumDiff += int(abs(left[i] - right[i]))
	}

	fmt.Println("part1", sumDiff)
}

func part2(left, right []int) {
	var similarityScore int

	for i := 0; i < len(left); i++ {
		var occurence int
		curr := left[i]

		var j int
		for j = 0; j < len(right); j++ {
			if curr == right[j] {
				occurence++
			}
		}

		currScore := curr * occurence
		similarityScore += currScore

		for i+1 < len(left) && curr == left[i+1] {
			i++
			similarityScore += currScore
		}
	}

	fmt.Println("part2", similarityScore)
}

func abs(n int) int {
	if n > 0 {
		return n
	}
	return -n
}

func LineCounter(r io.Reader) (int, error) {
	buf := make([]byte, 32*1024)
	count := 0
	lineSep := []byte{'\n'}

	for {
		c, err := r.Read(buf)
		count += bytes.Count(buf[:c], lineSep)

		switch {
		case err == io.EOF:
			return count, nil

		case err != nil:
			return count, err
		}
	}
}
