package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strconv"
	"strings"
)

func main() {

	part1("input.txt")
	part2("input.txt")
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

	scanner.Scan()

	line := scanner.Text()

	var sb strings.Builder

	for _, timeString := range strings.Fields(strings.TrimPrefix(line, "Time:")) {
		sb.WriteString(timeString)
	}

	time, err := strconv.Atoi(sb.String())
	if err != nil {
		log.Fatal(err)
	}

	sb.Reset()

	scanner.Scan()

	line = scanner.Text()

	for _, recordString := range strings.Fields(strings.TrimPrefix(line, "Distance:")) {
		sb.WriteString(recordString)
	}

	record, err := strconv.Atoi(sb.String())
	if err != nil {
		log.Fatal(err)
	}

	res := computeNbWaysToBeatRecords(time, record)

	fmt.Println("Part 2: ", res)
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

	scanner.Scan()

	line := scanner.Text()

	times := make([]int, 0, 3)

	for _, timeString := range strings.Fields(strings.TrimPrefix(line, "Time:")) {
		time, err := strconv.Atoi(timeString)
		if err != nil {
			log.Fatal(err)
		}

		times = append(times, time)
	}

	scanner.Scan()

	line = scanner.Text()

	records := make([]int, 0, 3)

	for _, recordString := range strings.Fields(strings.TrimPrefix(line, "Distance:")) {
		record, err := strconv.Atoi(recordString)
		if err != nil {
			log.Fatal(err)
		}

		records = append(records, record)
	}

	n := len(times)

	res := 1

	for i := 0; i < n; i++ {

		nbWays := computeNbWaysToBeatRecords(times[i], records[i])

		if nbWays > 0 {
			res *= nbWays
		}

	}

	fmt.Println("Part 1: ", res)
}

func computeNbWaysToBeatRecords(elapseTime int, record int) int {
	nbWays := 0

	for i := 0; i <= elapseTime; i++ {
		if i*(elapseTime-i) > record {
			nbWays++
		}
	}

	return nbWays
}
