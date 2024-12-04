package main

import (
	"bufio"
	"bytes"
	"fmt"
	"io"
	"os"
)

func main() {
	inputFilename := os.Args[1]

	f, err := os.Open(inputFilename)
	if err != nil {
		panic(err)
	}

	lineCounts, err := lineCounter(f)
	if err != nil {
		panic(err)
	}

	f.Seek(0, 0)

	scanner := bufio.NewScanner(f)

	lines := make([][]rune, 0, lineCounts)

	for scanner.Scan() {
		lines = append(lines, []rune(scanner.Text()))
	}

	if scanner.Err() != nil {
		panic(scanner.Err())
	}

	part1(lines)
	part2(lines)
}

func lineCounter(r io.Reader) (int, error) {
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

func part1(lines [][]rune) {
	var countXmas int
	for i := 0; i < len(lines); i++ {
		for j := 0; j < len(lines[0]); j++ {
			if lines[i][j] == 'X' {
				countXmas += searchForXmas(lines, i, j)
			}
		}
	}

	fmt.Println("part 1", countXmas)
}

func searchForXmas(lines [][]rune, i, j int) int {
	var countXmas int

	searchFuncs := []func([][]rune, int, int) bool{
		searchForXmasHorizontallyLeft,
		searchForXmasHorizontallyRight,
		searchForXmasVerticallyUp,
		searchForXmasVerticallyBottom,
		searchForXmasDiagonallyLeftDown,
		searchForXmasDiagonallyRightDown,
		searchForXmasDiagonallyRightUp,
		searchForXmasDiagonallyLeftUp,
	}

	for _, f := range searchFuncs {
		if f(lines, i, j) {
			countXmas++
		}
	}

	return countXmas
}

var XMAS = []rune("XMAS")

func searchForXmasHorizontallyRight(lines [][]rune, i, j int) bool {
	return searchHorizontallyRightFor(XMAS, lines, i, j)
}

func searchForXmasHorizontallyLeft(lines [][]rune, i, j int) bool {
	return searchHorizontallyLeftFor(XMAS, lines, i, j)
}

func searchForXmasVerticallyBottom(lines [][]rune, i, j int) bool {
	return searchVerticallyBottomFor(XMAS, lines, i, j)
}

func searchForXmasVerticallyUp(lines [][]rune, i, j int) bool {
	return searchVerticallyUpFor(XMAS, lines, i, j)
}

func searchForXmasDiagonallyRightDown(lines [][]rune, i, j int) bool {
	return searchDiagonallyRightDownFor(XMAS, lines, i, j)
}

func searchForXmasDiagonallyLeftDown(lines [][]rune, i, j int) bool {
	return searchDiagonallyLeftDownFor(XMAS, lines, i, j)
}

func searchForXmasDiagonallyLeftUp(lines [][]rune, i, j int) bool {
	return searchDiagonallyLeftUpFor(XMAS, lines, i, j)
}

func searchForXmasDiagonallyRightUp(lines [][]rune, i, j int) bool {
	return searchDiagonallyRightUpFor(XMAS, lines, i, j)
}

func searchHorizontallyRightFor(pattern []rune, lines [][]rune, i, j int) bool {
	for idx, char := range pattern {
		if j+idx >= len(lines[0]) || lines[i][j+idx] != char {
			return false
		}
	}
	return true
}

func searchHorizontallyLeftFor(pattern []rune, lines [][]rune, i, j int) bool {
	for idx, char := range pattern {
		if j-idx < 0 || lines[i][j-idx] != char {
			return false
		}
	}
	return true
}

func searchVerticallyBottomFor(pattern []rune, lines [][]rune, i, j int) bool {
	for idx, char := range pattern {
		if i+idx >= len(lines) || lines[i+idx][j] != char {
			return false
		}
	}
	return true
}

func searchVerticallyUpFor(pattern []rune, lines [][]rune, i, j int) bool {
	for idx, char := range pattern {
		if i-idx < 0 || lines[i-idx][j] != char {
			return false
		}
	}
	return true
}

func searchDiagonallyRightDownFor(pattern []rune, lines [][]rune, i, j int) bool {
	for idx, char := range pattern {
		if j+idx >= len(lines[0]) || i+idx >= len(lines) || i+idx < 0 || j+idx < 0 || lines[i+idx][j+idx] != char {
			return false
		}
	}
	return true
}

func searchDiagonallyLeftDownFor(pattern []rune, lines [][]rune, i, j int) bool {
	for idx, char := range pattern {
		if j-idx < 0 || i+idx >= len(lines) || j-idx >= len(lines[0]) || i+idx < 0 || lines[i+idx][j-idx] != char {
			return false
		}
	}
	return true
}

func searchDiagonallyLeftUpFor(pattern []rune, lines [][]rune, i, j int) bool {
	for idx, char := range pattern {
		if j-idx < 0 || i-idx < 0 || j-idx >= len(lines[0]) || i-idx >= len(lines) || lines[i-idx][j-idx] != char {
			return false
		}
	}
	return true
}

func searchDiagonallyRightUpFor(pattern []rune, lines [][]rune, i, j int) bool {
	for idx, char := range pattern {
		if j+idx >= len(lines[0]) || i-idx < 0 || j+idx < 0 || i-idx >= len(lines) || lines[i-idx][j+idx] != char {
			return false
		}
	}
	return true
}

var MAS = []rune("MAS")

func part2(lines [][]rune) {
	var countXmas int
	for i := 0; i < len(lines); i++ {
		for j := 0; j < len(lines[0]); j++ {
			if lines[i][j] == 'A' {
				isTrueForFirstDiag := searchDiagonallyLeftUpFor(MAS, lines, i+1, j+1) || searchDiagonallyRightDownFor(MAS, lines, i-1, j-1)
				isTrueForSecondDiag := searchDiagonallyRightUpFor(MAS, lines, i+1, j-1) || searchDiagonallyLeftDownFor(MAS, lines, i-1, j+1)

				if isTrueForFirstDiag && isTrueForSecondDiag {
					countXmas++
				}
			}
		}
	}

	fmt.Println("part 2", countXmas)
}
