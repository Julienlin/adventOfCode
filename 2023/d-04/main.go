package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"sort"
	"strconv"
	"strings"
)

func main() {

	part1()
	part2()

}

func part1() {
	file, err := os.Open("input.txt")
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	// The bufio.NewScanner() function is called in which the
	// object os.File passed as its parameter and this returns a
	// object bufio.Scanner which is further used on the
	// bufio.Scanner.Split() method.
	scanner := bufio.NewScanner(file)

	globalScore := 0

	for scanner.Scan() {

		line := scanner.Text()

		if len(line) <= 0 {
			continue
		}

		card := extractCardInfo(line)

		cardScore := card.ComputeCardScore()

		globalScore += cardScore

	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	fmt.Println("part 1: ", globalScore)

}

func part2() {

	file, err := os.Open("input.txt")
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	// The bufio.NewScanner() function is called in which the
	// object os.File passed as its parameter and this returns a
	// object bufio.Scanner which is further used on the
	// bufio.Scanner.Split() method.
	scanner := bufio.NewScanner(file)

	cards := make([]cardInfo, 0, 1024)

	for scanner.Scan() {

		line := scanner.Text()

		if len(line) <= 0 {
			continue
		}

		card := extractCardInfo(line)

		card.CountWinningNumbers()

		cards = append(cards, card)

	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	globalScore := 0

	for idx := 0; idx < len(cards); idx++ {
		nbCopies := cards[idx].CountWinningNumbers()
		cards[idx].Copies++
		for i := 0; i < cards[idx].Copies; i++ {
			for next := 1; next <= nbCopies && idx+next < len(cards); next++ {
				cards[next+idx].Copies++
			}
		}
		globalScore += cards[idx].Copies
	}

	fmt.Println("Part2: ", globalScore)

}

type cardInfo struct {
	WinningNumbers []int
	Numbers        []int
	count          *int
	Copies         int
}

func (card *cardInfo) CountWinningNumbers() int {

	if card.count != nil {
		return *card.count
	}

	count := 0
	idxWN, idxN := 0, 0

	for idxWN < len(card.WinningNumbers) && idxN < len(card.Numbers) {
		currentWinningNumber := card.WinningNumbers[idxWN]
		currentNumber := card.Numbers[idxN]

		if currentWinningNumber == currentNumber {
			// currentNumber is a winning number
			count++
			idxWN++
			idxN++
			// fmt.Println("idxWN: ", idxWN, " idxN: ", idxN, " currentWinningNumber: ", currentWinningNumber, " currentNumber: ", currentNumber, " count: ", count)

		} else if currentWinningNumber < currentNumber {
			// currentWinningNumber is too small
			// meaning that there is no more WinningNumbers below currentWinningNumber
			idxWN++
		} else {
			// currentWinningNumber is too big
			// meaning that the currentNumber is not winning
			idxN++
		}
	}

	card.count = new(int)
	*card.count = count

	return count

}

func (card cardInfo) ComputeCardScore() int {
	count := card.CountWinningNumbers()

	if count <= 0 {
		return 0
	}

	score := 1 << (count - 1)
	// fmt.Println("Scored ", score, "Points")

	return score
}

func extractCardInfo(line string) cardInfo {

	// removes prefix
	numbersInfo := strings.Split(line, ":")[1]

	//extracting winning numbers and numbers
	numbersList := strings.Split(strings.TrimSpace(numbersInfo), "|")

	fromStringToIntArray := func(stringToBeConverted string) []int {

		elements := strings.Fields(strings.TrimSpace(stringToBeConverted))

		res := make([]int, 0, len(elements))

		for _, element := range elements {
			conv, err := strconv.Atoi(element)
			if err != nil {
				log.Fatal(err)
			}

			res = append(res, conv)

		}

		return res
	}

	winningNumbers := fromStringToIntArray(numbersList[0])
	sort.Ints(winningNumbers)
	// fmt.Println(winningNumbers)

	// fmt.Println("CPOICOU")

	numbers := fromStringToIntArray(numbersList[1])
	sort.Ints(numbers)
	// fmt.Println(numbers)

	card := cardInfo{
		WinningNumbers: winningNumbers,
		Numbers:        numbers,
	}

	return card

}
