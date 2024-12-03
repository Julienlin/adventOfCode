package main

import (
	"bufio"
	"fmt"
	"os"
	"regexp"
	"strconv"
	"strings"
)

var (
	mulRegexp = regexp.MustCompile(`mul\((\d{1,3}),(\d{1,3})\)`)
	allRegexp = regexp.MustCompile(`do\(\)|don't\(\)|mul\((\d{1,3}),(\d{1,3})\)`)
)

func main() {
	inputFilename := os.Args[1]

	f, err := os.Open(inputFilename)
	if err != nil {
		panic(err)
	}
	defer f.Close()

	scanner := bufio.NewScanner(f)

	lines := make([]string, 0)

	for scanner.Scan() {
		line := scanner.Text()
		lines = append(lines, line)
	}

	if scanner.Err() != nil {
		panic(scanner.Err())
	}

	part1(lines)
	part2(strings.Join(lines, ""))
}

func part1(lines []string) {
	var sum int

	for _, line := range lines {
		matches := mulRegexp.FindAllStringSubmatch(line, -1)

		for _, match := range matches {

			firstOperand, err := strconv.Atoi(match[1])
			if err != nil {
				panic(err)
			}

			secondOperand, err := strconv.Atoi(match[2])
			if err != nil {
				panic(err)
			}

			sum += firstOperand * secondOperand

		}
	}

	fmt.Println("part 1", sum)
}

func shouldSkipInstruction(instruction string) bool {
	return instruction == "don't()"
}

func extractInstruction(line string, doOrDontMatches [][]int, doOrDontIdx int) string {
	instruction := line[doOrDontMatches[doOrDontIdx][0]:doOrDontMatches[doOrDontIdx][1]]
	return instruction
}

func part2(line string) {
	var sum int

	fmt.Println("new Line")

	instructions := allRegexp.FindAllStringSubmatchIndex(line, -1)

	appliedInstruction := "do()"

	for _, instruction := range instructions {
		instructionString := getInstructionString(line, instruction)

		if instructionString == "do()" || instructionString == "don't()" {
			fmt.Println("new applied instruction", instructionString)
			appliedInstruction = instructionString
		} else if appliedInstruction == "do()" {
			firstOperand, err := strconv.Atoi(line[instruction[2]:instruction[3]])
			if err != nil {
				panic(err)
			}

			secondOperand, err := strconv.Atoi(line[instruction[4]:instruction[5]])
			if err != nil {
				panic(err)
			}

			fmt.Println("counting", line[instruction[0]:instruction[1]])
			sum += firstOperand * secondOperand

		} else {
			fmt.Println("skip instruction", line[instruction[0]:instruction[1]], "because of last applied instruction", appliedInstruction)
		}
	}

	fmt.Println("part 2", sum)
}

func getInstructionString(line string, instruction []int) string {
	return line[instruction[0]:instruction[1]]
}

func sortInstruction(mulMatches [][]int, doOrDontMatches [][]int) [][]int {
	sortedInstructions := make([][]int, 0, len(mulMatches)+len(doOrDontMatches))

	var mulIdx, doOrDontIdx int

	for mulIdx < len(mulMatches) && doOrDontIdx < len(doOrDontMatches) {
		if mulMatches[mulIdx][0] < doOrDontMatches[doOrDontIdx][0] {
			sortedInstructions = append(sortedInstructions, mulMatches[mulIdx])
			mulIdx++
		} else {
			sortedInstructions = append(sortedInstructions, doOrDontMatches[doOrDontIdx])
			doOrDontIdx++
		}
	}

	for mulIdx < len(mulMatches) {
		sortedInstructions = append(sortedInstructions, mulMatches[mulIdx])
		mulIdx++
	}

	for doOrDontIdx < len(doOrDontMatches) {
		sortedInstructions = append(sortedInstructions, doOrDontMatches[doOrDontIdx])
		doOrDontIdx++
	}

	fmt.Println(len(sortedInstructions) == len(mulMatches)+len(doOrDontMatches))
	return sortedInstructions
}
