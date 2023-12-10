package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strconv"
	"strings"
)

type Range struct {
	DestinationStart int
	SourceStart      int
	Length           int
}

func (r Range) mapSource(source int) (int, bool) {
	dist := source - r.SourceStart

	if dist < 0 || dist > r.Length {
		return -1, false
	}

	return r.DestinationStart + dist, true
}

type Mapper struct {
	Ranges []Range
}

func (mapper Mapper) mapSource(source int) int {
	for _, r := range mapper.Ranges {
		if dest, isInRange := r.mapSource(source); isInRange {
			return dest
		}
	}

	return source
}

func readSeeds(line string) []int {
	seeds := make([]int, 0, 100)

	seedsString := strings.TrimPrefix(line, "seeds: ")

	for _, seedString := range strings.Fields(seedsString) {
		seed, err := strconv.Atoi(seedString)
		if err != nil {
			log.Fatal(err)
		}
		seeds = append(seeds, seed)
	}

	return seeds
}

func readRange(line string) Range {

	values := strings.Fields(line)

	destinatinoStart, err := strconv.Atoi(values[0])
	if err != nil {
		log.Fatal(err)
	}

	sourceStart, err := strconv.Atoi(values[1])
	if err != nil {
		log.Fatal(err)
	}

	length, err := strconv.Atoi(values[2])
	if err != nil {
		log.Fatal(err)
	}

	return Range{DestinationStart: destinatinoStart, SourceStart: sourceStart, Length: length}

}

func LocationNumber(mappers []Mapper, seed int) int {
	destination := seed

	for _, mapper := range mappers {
		destination = mapper.mapSource(destination)
	}

	return destination
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

	mappers := make([]Mapper, 0, 6)

	var seeds []int

	if scanner.Scan() {
		seeds = readSeeds(scanner.Text())
	}

	for scanner.Scan() {

		line := scanner.Text()

		log.Println("line: ", line)

		var mapper Mapper

		if strings.Contains(line, "-") { //we are reading a new mapper
			// go to the next line
			scanner.Scan()
			line = scanner.Text()

			mapper = Mapper{}

			for ; len(line) > 0; line = scanner.Text() {
				log.Println("line with range: ", line)
				mapper.Ranges = append(mapper.Ranges, readRange(line))
				scanner.Scan()

			}

			log.Println("mapper: ", mapper)
			log.Println("last line after mapper: ", scanner.Text())
			mappers = append(mappers, mapper)

		}
	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	locationNumbers := make([]int, 0, len(seeds))

	for _, seed := range seeds {
		destination := LocationNumber(mappers, seed)
		log.Printf("seed %v has destination %v", seed, destination)
		locationNumbers = append(locationNumbers, destination)
	}

	minLocation := locationNumbers[0]

	for _, location := range locationNumbers[1:] {
		if minLocation > location {
			minLocation = location
		}
	}

	fmt.Println("part 1: ", minLocation)

}

type seedRange struct {
	Start  int
	Length int
}

func readRangeSeeds(line string) []seedRange {
	seedRanges := make([]seedRange, 0, 100)

	seedsString := strings.TrimPrefix(line, "seeds: ")

	seedRangesString := strings.Fields(seedsString)

	for idx := 0; idx < len(seedRangesString)-1; idx += 2 {
		seedRangeStart, err := strconv.Atoi(seedRangesString[idx])
		if err != nil {
			log.Fatal(err)
		}

		seedRangeLength, err := strconv.Atoi(seedRangesString[idx+1])
		if err != nil {
			log.Fatal(err)
		}

		seedRanges = append(seedRanges, seedRange{Start: seedRangeStart, Length: seedRangeLength})

	}

	return seedRanges
}

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

	mappers := make([]Mapper, 0, 6)

	var seedRanges []seedRange

	if scanner.Scan() {
		seedRanges = readRangeSeeds(scanner.Text())
	}

	log.Println("seed ranges: ", seedRanges)

	for scanner.Scan() {

		line := scanner.Text()

		log.Println("line: ", line)

		var mapper Mapper

		if strings.Contains(line, "-") { //we are reading a new mapper
			// go to the next line
			scanner.Scan()
			line = scanner.Text()

			mapper = Mapper{}

			for ; len(line) > 0; line = scanner.Text() {
				log.Println("line with range: ", line)
				mapper.Ranges = append(mapper.Ranges, readRange(line))
				scanner.Scan()

			}

			log.Println("mapper: ", mapper)
			log.Println("last line after mapper: ", scanner.Text())
			mappers = append(mappers, mapper)

		}
	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	minLocation := LocationNumber(mappers, seedRanges[0].Start)

	for _, sr := range seedRanges {
		for i := 0; i < sr.Length; i++ {
			seed := sr.Start + i

			destination := LocationNumber(mappers, seed)
			if minLocation > destination {
				minLocation = destination
			}
		}
	}

	fmt.Println("part 2: ", minLocation)
}

func main() {
	part1("test.txt")
	part2("test.txt")
}
