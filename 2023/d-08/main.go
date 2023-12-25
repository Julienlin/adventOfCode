package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"regexp"
	"strings"
)

const RIGHT = 0
const LEFT = 1

const BEGIN = "AAA"
const END = "ZZZ"

type Network map[string][2]string

func main() {
	part1("input.txt")
	part2_lcm("input.txt")

}

func part1(filename string) {
	network, instructions := readFile(filename)

	nbSteps := navigate(network, instructions, BEGIN, END)

	fmt.Println("Part1: ", nbSteps)
}

func readFile(filename string) (Network, []int) {

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

	r := regexp.MustCompile(`(?P<Label>\w+) = \((?P<Left>\w{3}), (?P<Right>\w{3})\)`)

	network := make(Network)
	instructions := make([]int, 0, 100)

	if scanner.Scan() {
		line := scanner.Text()
		// fmt.Println("instructions : ", line)
		for _, instruction := range []rune(line) {
			if instruction == 'R' {
				instructions = append(instructions, 1)
			} else {
				instructions = append(instructions, 0)
			}
		}

		// fmt.Println("instructions: ", instructions)
	}

	for scanner.Scan() {

		line := scanner.Text()

		if len(line) <= 0 {
			continue
		}

		matches := r.FindStringSubmatch(line)

		label := matches[r.SubexpIndex("Label")]
		right := matches[r.SubexpIndex("Right")]
		left := matches[r.SubexpIndex("Left")]

		network[label] = [2]string{left, right}

		// fmt.Println("network:", network)

	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	return network, instructions
}

func navigate(network Network, instructions []int, begin, end string) int {
	stepCounter := 0
	hasReachedEnd := false
	currPos := begin
	for !hasReachedEnd {

		for _, instruction := range instructions {

			currPos = network[currPos][instruction]

			stepCounter++
			if currPos == end {
				hasReachedEnd = true
			}
		}

	}

	return stepCounter
}

func part2(filename string) {
	network, instructions := readFile(filename)
	begins := findsBegins(network)

	currPoses := make(map[string]string, len(begins))

	for _, begin := range begins {
		currPoses[begin] = begin
	}

	shouldStop := false

	stepCounter := 0
	for !shouldStop {
		for _, instruction := range instructions {
			for begin, currPos := range currPoses {
				currPoses[begin] = network[currPos][instruction]
			}

			stepCounter++

			if haveAllReachedEnds(currPoses) {
				shouldStop = true
			}

		}
		fmt.Println("stepCounter:", stepCounter)
	}

	fmt.Println("Part 2:", stepCounter)

}

func part2_lcm(filename string) {
	network, instructions := readFile(filename)
	begins := findsBegins(network)

	currPoses := make(map[string]string, len(begins))

	for _, begin := range begins {
		currPoses[begin] = begin
	}

	nbsteps := make(map[string]int, len(begins))

	shouldStop := false

	nbEndedPaths := 0
	stepCounter := 0
	for !shouldStop {
		for _, instruction := range instructions {
			for begin, currPos := range currPoses {
				currPoses[begin] = network[currPos][instruction]
			}

			stepCounter++

			for begin, curPos := range currPoses {
				if _, ok := nbsteps[begin]; !ok && strings.HasSuffix(curPos, "Z") {
					nbsteps[begin] = stepCounter
					nbEndedPaths++
				}
			}

			if nbEndedPaths >= len(begins) {
				shouldStop = true
			}

		}
		fmt.Println("nbEndedPaths:", nbEndedPaths)
	}

	nbstepsValues := make([]int, 0, len(nbsteps))

	for _, nbstep := range nbsteps {
		nbstepsValues = append(nbstepsValues, nbstep)
	}

	res := nbstepsValues[0]
	for _, nbstep := range nbstepsValues[1:] {
		res = lcm(res, nbstep)
	}

	fmt.Println("Part 2:", res)

}

func gcd(a, b int) int {
	for b != 0 {
		a, b = b, a%b
	}
	return a
}

func lcm(a, b int) int {
	return a / gcd(a, b) * b
}

func haveAllReachedEnds(currPoses map[string]string) bool {
	for _, currPos := range currPoses {
		if !strings.HasSuffix(currPos, "Z") {
			return false
		}
	}
	return true
}

func findsBegins(network Network) []string {
	res := make([]string, 0, 10)

	for key := range network {
		if strings.HasSuffix(key, "A") {
			res = append(res, key)
		}
	}

	return res
}
