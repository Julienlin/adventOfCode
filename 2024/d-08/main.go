package main

import (
	"bytes"
	"fmt"
	"io"
	"os"
)

const NOTHING = '.'

type Map [][]rune

func (m Map) value(i, j int) (rune, bool) {
	if !m.isIn(i, j) {
		return NOTHING, false
	}

	return m[i][j], true
}

func (m Map) isIn(i, j int) bool {
	return i >= 0 && i < len(m) && j >= 0 && j < len(m[0])
}

func (m Map) print() {
	for _, line := range m {
		fmt.Println(string(line))
	}
}

func main() {
	inputFilename := os.Args[1]

	fileContent, err := readFileContent(inputFilename)
	if err != nil {
		panic(err)
	}

	part1(fileContent)
	part2(fileContent)
}

func part1(fileContent []byte) {
	pbMap := buildMap(fileContent)
	pairs := makePairs(pbMap)
	fmt.Println("pairs", pairs)

	antinodesCount := make(map[int]bool)

	for _, pair := range pairs {
		for _, antinode := range generateAntinodes(pair[0], pair[1]) {
			if pbMap.isIn(antinode[0], antinode[1]) {
				fmt.Println("antinode", antinode)
				antinodesCount[antinode[0]*len(pbMap)+antinode[1]] = true
				pbMap[antinode[0]][antinode[1]] = '#'
			}
		}
	}

	pbMap.print()

	fmt.Println("antinodesCount", antinodesCount)

	fmt.Println("part1", len(antinodesCount))
}

// TODO: missing 6 elements to ge the answer don't know why
func part2(fileContent []byte) {
	pbMap := buildMap(fileContent)
	pairs := makePairs(pbMap)
	fmt.Println("pairs", pairs)

	antinodesCount := make(map[int]bool)

	for _, pair := range pairs {
		for _, antinode := range generateAntinodesResonantHarmonics(pbMap, pair[0], pair[1]) {
			if pbMap.isIn(antinode[0], antinode[1]) {
				fmt.Println("antinode", antinode)
				antinodesCount[antinode[0]*len(pbMap)+antinode[1]] = true
				antinodesCount[pair[0][0]*len(pbMap)+pair[0][1]] = true
				antinodesCount[pair[1][0]*len(pbMap)+pair[1][1]] = true
				pbMap[antinode[0]][antinode[1]] = '#'
				pbMap[pair[0][0]][pair[0][1]] = '#'
				pbMap[pair[1][0]][pair[1][1]] = '#'
			}
		}
	}

	pbMap.print()

	fmt.Println("antinodesCount", antinodesCount)

	fmt.Println("part2", len(antinodesCount))
}

func readFileContent(inputFilename string) ([]byte, error) {
	f, err := os.Open(inputFilename)
	if err != nil {
		return nil, err
	}

	content, err := io.ReadAll(f)
	if err != nil {
		return nil, err
	}
	return content, nil
}

func buildMap(content []byte) Map {
	byteLines := bytes.Split(content, []byte("\n"))
	res := make(Map, 0, len(byteLines))

	for _, line := range byteLines {
		if len(line) > 0 {
			res = append(res, bytes.Runes(line))
		}
	}

	return res
}

func generateAntinodes(a, b []int) [][]int {
	deltaX := a[0] - b[0]
	deltaY := a[1] - b[1]

	antinodes := make([][]int, 0, 2)

	antinodes = append(antinodes, []int{a[0] + deltaX, a[1] + deltaY}, []int{b[0] - deltaX, b[1] - deltaY})
	return antinodes
}

func generateAntinodesResonantHarmonics(pbMap Map, a, b []int) [][]int {
	deltaX := a[0] - b[0]
	deltaY := a[1] - b[1]

	antinodes := make([][]int, 0, 2)

	cur := []int{a[0] + deltaX, a[1] + deltaY}
	for pbMap.isIn(cur[0], cur[1]) {
		antinodes = append(antinodes, cur)
		cur = []int{cur[0] + deltaX, cur[1] + deltaY}
	}

	cur = []int{b[0] - deltaX, b[1] - deltaY}
	for pbMap.isIn(cur[0], cur[1]) {
		antinodes = append(antinodes, cur)
		cur = []int{cur[0] - deltaX, cur[1] - deltaY}
	}

	return antinodes
}

func makePairs(pbMap Map) [][][]int {
	var countAntennas int
	antennas := make(map[rune][][]int)
	for i := 0; i < len(pbMap); i++ {
		for j := 0; j < len(pbMap[0]); j++ {
			if value, _ := pbMap.value(i, j); value != NOTHING {
				antennas[value] = append(antennas[value], []int{i, j})
				countAntennas++
			}
		}
	}
	fmt.Println("antennas", antennas)

	pairs := make([][][]int, 0, countAntennas*countAntennas)

	for _, listAntennas := range antennas {
		for i := 0; i < len(listAntennas)-1; i++ {
			for j := i + 1; j < len(listAntennas); j++ {
				pairs = append(pairs, [][]int{listAntennas[i], listAntennas[j]})
			}
		}
	}

	return pairs
}
