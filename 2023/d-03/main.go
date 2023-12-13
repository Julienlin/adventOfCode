package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strconv"
	"strings"
	"unicode"
)

func main() {

	part1("input.txt")
	part2("input.txt")
}

type coordinate [2]int

func part2(filename string) {

	file, err := os.Open(filename)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	// The bufio.NewScanner() function is called in which the
	// object os.File passed as its parameter and this returns a
	// object bufio.Scanner which is further used on the
	// bufio.Scanner.Split() method.
	scanner := bufio.NewScanner(file)

	characters := make([]rune, 0, 100)

	var lineLength int

	nbLines := 0

	gearsMap := make(map[int][]int)

	for scanner.Scan() {

		line := scanner.Text()

		if len(line) <= 0 {
			continue
		}

		lineLength = len(line)

		for colIdx, char := range []rune(line) {
			if !unicode.IsDigit(char) && char != '.' && !unicode.IsSpace(char) {

				if char == '*' {

					fmt.Printf("gear found at (%v,%v) or %v\n", nbLines, colIdx, nbLines*lineLength+colIdx)
					gearsMap[nbLines*lineLength+colIdx] = make([]int, 0, 2)

				}
			}
		}

		characters = append(characters, []rune(line)...)

		nbLines++

	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	for idx := 0; idx < len(characters); idx++ {

		if unicode.IsDigit(characters[idx]) {
			var sb strings.Builder

			neighbourhoods := make([]coordinate, 0, 8)

			rowIdx := idx / lineLength

			for ; idx/lineLength == rowIdx && idx < len(characters) && unicode.IsDigit(characters[idx]); idx++ {
				sb.WriteRune(characters[idx])

				colIdx := idx % lineLength

				neighbourhoods = append(neighbourhoods, addNeighbourhood(nbLines, lineLength, rowIdx, colIdx)...)
			}

			currNumber, err := strconv.Atoi(sb.String())
			if err != nil {
				log.Fatal(err)
			}

			for _, gearCoord := range findGearinNeighbouhood(characters, lineLength, neighbourhoods) {
				gearX := gearCoord[0]
				gearY := gearCoord[1]

				gearsMap[gearX*lineLength+gearY] = append(gearsMap[gearX*lineLength+gearY], currNumber)

				fmt.Printf("adding to gearMap[%v] value: %v ", gearX*lineLength+gearY, currNumber)
				fmt.Printf("now gearMap[%v]=%v\n", gearX*lineLength+gearY, gearsMap[gearX*lineLength+gearY])

			}

			sb.Reset()
			idx--
		}
	}

	gearRatio := 0

	for _, partNumbers := range gearsMap {
		if len(partNumbers) == 2 {
			gearRatio += partNumbers[0] * partNumbers[1]
		}
	}

	log.Println("part 2:", gearRatio)

}

func findGearinNeighbouhood(characters []rune, lineLength int, neighbours []coordinate) []coordinate {
	gearsCoords := make([]coordinate, 0, 1)
	gearsCoordMap := make(map[int]coordinate)
	for _, coord := range neighbours {
		x := coord[0]
		y := coord[1]
		if characters[x*lineLength+y] == '*' {
			gearsCoordMap[x*lineLength+y] = coord
		}
	}

	for _, coord := range gearsCoordMap {

		gearsCoords = append(gearsCoords, coord)
	}

	return gearsCoords
}

func part1(filename string) {

	file, err := os.Open(filename)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	// The bufio.NewScanner() function is called in which the
	// object os.File passed as its parameter and this returns a
	// object bufio.Scanner which is further used on the
	// bufio.Scanner.Split() method.
	scanner := bufio.NewScanner(file)

	characters := make([]rune, 0, 100)

	var lineLength int

	nbLines := 0

	specialCharactersMap := make(map[rune]bool)

	for scanner.Scan() {

		line := scanner.Text()

		if len(line) <= 0 {
			continue
		}

		lineLength = len(line)

		for _, char := range []rune(line) {
			if !unicode.IsDigit(char) && char != '.' && !unicode.IsSpace(char) {

				if _, ok := specialCharactersMap[char]; !ok {

					specialCharactersMap[char] = true

				}
			}
		}

		characters = append(characters, []rune(line)...)

		nbLines++

	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	specialCharacters := make([]rune, 0, 100)

	for k := range specialCharactersMap {
		specialCharacters = append(specialCharacters, k)
	}

	partNumbers := 0

	for idx := 0; idx < len(characters); idx++ {

		if unicode.IsDigit(characters[idx]) {
			var sb strings.Builder

			neighbourhoods := make([]coordinate, 0, 8)

			rowIdx := idx / lineLength

			for ; idx/lineLength == rowIdx && idx < len(characters) && unicode.IsDigit(characters[idx]); idx++ {
				sb.WriteRune(characters[idx])

				colIdx := idx % lineLength

				neighbourhoods = append(neighbourhoods, addNeighbourhood(nbLines, lineLength, rowIdx, colIdx)...)
			}

			if checkNeighbour(characters, lineLength, specialCharacters, neighbourhoods) {
				currNumber, err := strconv.Atoi(sb.String())

				if err != nil {
					log.Fatal(err)
				}

				// fmt.Print("Found part number:", currNumber, " neighbours: ")
				// printNeighbours(characters, lineLength, neighbourhoods)

				partNumbers += currNumber

			} else {
				// currNumber, err := strconv.Atoi(sb.String())
				//
				// if err != nil {
				// 	log.Fatal(err)
				// }

				// fmt.Print("Found not part number:", currNumber, " neighbours: ")
				// printNeighbours(characters, lineLength, neighbourhoods)
			}

			sb.Reset()
			idx--
		}
	}

	log.Println("part 1:", partNumbers)

}

func printLine(characters []rune, lineLength, line int) {
	for i := 0; i < lineLength; i++ {
		fmt.Print(string(characters[line*lineLength+i]))
	}
	fmt.Println()
}

func printNeighbours(characters []rune, lineLength int, coords []coordinate) {
	fmt.Print("[")

	for _, coord := range coords {
		fmt.Printf("%c ", characters[coord[0]*lineLength+coord[1]])
	}

	fmt.Println("]")
}

func checkNeighbour(characters []rune, lineLength int, specialCharacters []rune, neighbours []coordinate) bool {

	contains := func(chars []rune, char rune) bool {
		for _, c := range chars {
			if c == char {
				return true
			}
		}
		return false
	}

	for _, neighbour := range neighbours {
		x := neighbour[0]
		y := neighbour[1]
		char := characters[x*lineLength+y]

		if contains(specialCharacters, char) {
			return true
		}
	}

	return false

}

func addNeighbourhood(nbLines, lineLength, x, y int) []coordinate {
	neighbours := make([]coordinate, 0, 8)
	if x-1 >= 0 {
		if y-1 >= 0 {
			neighbours = append(neighbours, coordinate{x - 1, y - 1})
		}

		neighbours = append(neighbours, coordinate{x - 1, y})

		if y+1 < lineLength {
			neighbours = append(neighbours, coordinate{x - 1, y + 1})
		}
	}

	if y+1 < lineLength {
		neighbours = append(neighbours, coordinate{x, y + 1})
	}

	if x+1 < nbLines {
		if y+1 < lineLength {
			neighbours = append(neighbours, coordinate{x + 1, y + 1})
		}

		neighbours = append(neighbours, coordinate{x + 1, y})
	}

	if y-1 >= 0 {
		if x+1 < nbLines {
			neighbours = append(neighbours, coordinate{x + 1, y - 1})
		}

		neighbours = append(neighbours, coordinate{x, y - 1})
	}

	return neighbours
}
