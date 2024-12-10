package main

import (
	"fmt"
	"io"
	"os"
	"strconv"
	"strings"
)

func main() {
	inputFilename := os.Args[1]

	f, err := os.Open(inputFilename)
	if err != nil {
		panic(err)
	}

	inputContents, err := io.ReadAll(f)
	if err != nil {
		panic(err)
	}

	lines := strings.Split(string(inputContents), "\n")

	inputs := make([][]int, 0, len(lines))

	for _, line := range lines {
		operands := strings.Split(strings.ReplaceAll(line, ":", ""), " ")
		if len(operands) > 2 {
			arr := make([]int, 0, len(operands))
			for _, input := range operands {
				converted, _ := strconv.Atoi(input)
				arr = append(arr, converted)
			}

			inputs = append(inputs, arr)
		}
	}

	var count int
	var countPart2 int

	for _, input := range inputs {
		expected := input[0]
		cum := input[1]
		operands := input[2:]

		if evaluateEquation(expected, cum, operands) {
			count += expected
		}

		if evaluateEquationPart2(expected, cum, operands) {
			countPart2 += expected
		}
	}

	fmt.Println("part 1", count)
	fmt.Println("part 2", countPart2)
}

func evaluateEquation(expected int, cum int, operands []int) bool {
	if len(operands) <= 0 {
		return expected == cum
	}

	addition := cum + operands[0]
	multiplication := cum * operands[0]
	return evaluateEquation(expected, addition, operands[1:]) || evaluateEquation(expected, multiplication, operands[1:])
}

func evaluateEquationPart2(expected int, cum int, operands []int) bool {
	if len(operands) <= 0 {
		return expected == cum
	}

	addition := cum + operands[0]
	multiplication := cum * operands[0]
	concat := concatenateInt(cum, operands[0])
	return evaluateEquationPart2(expected, addition, operands[1:]) || evaluateEquationPart2(expected, multiplication, operands[1:]) || evaluateEquationPart2(expected, concat, operands[1:])
}

func concatenateInt(a, b int) int {
	aStr := strconv.Itoa(a)
	bStr := strconv.Itoa(b)
	converted, _ := strconv.Atoi(strings.Join([]string{aStr, bStr}, ""))
	return converted
}
