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

	fmt.Println(lastIncrement([]int{0, 3, 6, 9, 12, 15}))
	fmt.Println(lastIncrement([]int{1, 3, 6, 10, 15, 21}))
	fmt.Println(lastIncrement([]int{10, 13, 16, 21, 30, 45}))

	// part1("test.txt")
	// part1("input.txt")

	fmt.Println(firstIncrement([]int{0, 3, 6, 9, 12, 15}))
	fmt.Println(firstIncrement([]int{1, 3, 6, 10, 15, 21}))
	fmt.Println(firstIncrement([]int{10, 13, 16, 21, 30, 45}))

	part2("test.txt")
	part2("input.txt")

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

	res := 0

	for scanner.Scan() {

		line := scanner.Text()

		if len(line) <= 0 {
			continue
		}

		suiteStr := strings.Fields(line)

		suite := make([]int, 0, len(suiteStr))

		for _, val := range suiteStr {
			el, err := strconv.Atoi(val)
			if err != nil {
				log.Fatal(err)
			}

			suite = append(suite, el)
		}

		res += lastIncrement(suite)

	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	fmt.Println("Part 1:", res)

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

	res := 0

	for scanner.Scan() {

		line := scanner.Text()

		if len(line) <= 0 {
			continue
		}

		suiteStr := strings.Fields(line)

		suite := make([]int, 0, len(suiteStr))

		for _, val := range suiteStr {
			el, err := strconv.Atoi(val)
			if err != nil {
				log.Fatal(err)
			}

			suite = append(suite, el)
		}

		res += firstIncrement(suite)

	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	fmt.Println("Part 1:", res)

}

func lastIncrement(suite []int) int {
	inter := make([]int, 0, len(suite)-1)
	for i := 1; i < len(suite); i++ {
		inter = append(inter, suite[i]-suite[i-1])
	}

	if isAllNull(inter) {
		return suite[len(suite)-1]
	}

	return suite[len(suite)-1] + lastIncrement(inter)

}

func isAllNull(s []int) bool {
	for _, val := range s {
		if val != 0 {
			return false
		}
	}
	return true
}

func firstIncrement(suite []int) int {
	inter := make([]int, 0, len(suite)-1)
	for i := 1; i < len(suite); i++ {
		inter = append(inter, suite[i]-suite[i-1])
	}

	if isAllNull(inter) {
		return suite[0]
	}

	return suite[0] - firstIncrement(inter)

}
