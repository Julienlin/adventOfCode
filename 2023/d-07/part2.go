package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"reflect"
	"regexp"
	"sort"
	"strconv"
)

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

	hands := make([]Hand, 0, 100)

	r := regexp.MustCompile(`(?P<Cards>\w+) (?P<Bid>\d+)`)

	for scanner.Scan() {

		line := scanner.Text()

		if len(line) <= 0 {
			continue
		}

		matches := r.FindStringSubmatch(line)

		bid, err := strconv.Atoi(matches[r.SubexpIndex("Bid")])

		if err != nil {
			log.Fatal(err)
		}

		cardsString := matches[r.SubexpIndex("Cards")]

		cards := make([]int, 0, len(cardsString))
		for _, card := range []rune(cardsString) {
			cards = append(cards, convertCardPart2(card))
		}

		hand := Hand{Cards: cards, Bid: bid}

		//fmt.Println(hand)

		hands = append(hands, hand)

	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	sort.Sort(ByHandTypePart2(hands))

	sum := 0

	for i, hand := range hands {

		// fmt.Println(hand.Cards, hand.Bid, "rank:", i)

		fmt.Printf("nb pairs %v, %v : %v, sum = %v + %v * %v\n", countPairsPart2(hand.Cards), hand.GetHandTypePart2(), hand.Cards, sum, hand.Bid, i+1)
		sum += hand.Bid * (i + 1)

	}

	fmt.Println("Part2: ", sum)
}

const joker = 'J'

func convertCardPart2(card rune) int {

	switch card {
	case 'A':
		return 14
	case 'K':
		return 13
	case 'Q':
		return 12
	case joker:
		return 1
	case 'T':
		return 10
	default:
		num, err := strconv.Atoi(string(card))
		if err != nil {
			log.Fatal(err)
		}
		return num
	}
}

type ByHandTypePart2 []Hand

func (a ByHandTypePart2) Len() int      { return len(a) }
func (a ByHandTypePart2) Swap(i, j int) { a[i], a[j] = a[j], a[i] }

func (a ByHandTypePart2) Less(i, j int) bool {
	handTypeI := a[i].GetHandTypePart2()
	handTypeJ := a[j].GetHandTypePart2()

	if reflect.DeepEqual(handTypeI, handTypeJ) {
		return a[i].Less(a[j])
	}

	if handTypeI.FiveOfKind {
		return false
	}

	if handTypeJ.FiveOfKind {
		return true
	}

	if handTypeI.FourOfKind {
		return false
	}

	if handTypeJ.FourOfKind {
		return true
	}

	if handTypeI.FullHouse {
		return false
	}

	if handTypeJ.FullHouse {
		return true
	}

	if handTypeI.ThreeOfKind {
		return false
	}

	if handTypeJ.ThreeOfKind {
		return true
	}

	if handTypeI.TwoPairs {
		return false
	}

	if handTypeJ.TwoPairs {
		return true
	}

	if handTypeI.OnePair {
		return false
	}

	if handTypeJ.OnePair {
		return true
	}

	if handTypeI.HighCard {
		return false
	}

	if handTypeJ.HighCard {
		return true
	}

	return a[i].Less(a[j])
}

func (h *Hand) GetHandTypePart2() HandType {
	if h.handType != nil {
		return *h.handType
	}

	cards := make([]int, len(h.Cards))

	copy(cards, h.Cards)

	sort.SliceStable(cards, func(i, j int) bool {
		return cards[i] < cards[j]
	})

	if isFiveOfKindPart2(cards) {
		h.handType = &HandType{FiveOfKind: true}
	} else if isFourOfKindPart2(cards) {
		h.handType = &HandType{FourOfKind: true}
	} else if isFullHousePart2(cards) {
		h.handType = &HandType{FullHouse: true}
	} else if isThreeOfKindPart2(cards) {
		h.handType = &HandType{ThreeOfKind: true}
	} else if isTwoPairPart2(cards) {
		h.handType = &HandType{TwoPairs: true}
	} else if isOnePairPart2(cards) {
		h.handType = &HandType{OnePair: true}
	} else if isHighCardPart2(cards) {
		h.handType = &HandType{HighCard: true}
	} else {
		h.handType = &HandType{}
	}

	return *h.handType

}

func isNbOfKindPart2(nb int) func([]int) bool {
	return func(cards []int) bool {

		for i := 0; i < len(cards); i++ {
			c := cards[i]
			if countCardPart2(cards, c) == nb {
				return true
			}

			for i < len(cards)-1 && cards[i+1] == c {
				i++
			}

		}

		return false
	}
}

var isFiveOfKindPart2 = isNbOfKindPart2(5)
var isFourOfKindPart2 = isNbOfKindPart2(4)
var isThreeOfKindPart2 = isNbOfKindPart2(3)

func isFullHousePart2(cards []int) bool {
	// cardCount := make(map[int]int)
	// for i := 0; i < len(cards); i++ {
	// 	c := cards[i]
	//
	// 	cardCount[c] = countCardPart2(cards, c)
	//
	// 	for i < len(cards)-1 && cards[i+1] == c {
	// 		i++
	// 	}
	//
	// }

	return isFullHouse(cards) || countPairs(cards) == 2 && countCard(cards, convertCardPart2(joker)) == 1

}

func countPairsPart2(cards []int) int {
	pairLabel := make(map[int]bool)
	countPairs := 0

	for i := 0; i < len(cards); i++ {
		c := cards[i]
		// fmt.Printf("card : %v, countPair: %v\n", c, countCard(cards, c))
		if countCardPart2(cards, c) == 2 {
			if _, ok := pairLabel[c]; !ok {
				pairLabel[c] = true
				countPairs++
			}
		}

		for i < len(cards)-1 && cards[i+1] == c {
			i++
		}

	}

	return countPairs

}

func isTwoPairPart2(cards []int) bool {
	// return countPairsPart2(cards) == 2
	return countPairs(cards) == 2 || countPairs(cards) == 1 && countCard(cards, convertCardPart2(joker)) == 1
}

func isOnePairPart2(cards []int) bool {
	return countPairs(cards) == 1 || countPairs(cards) == 0 && countCard(cards, convertCardPart2(joker)) == 1
}

func isHighCardPart2(cards []int) bool {
	cardLabel := make(map[int]bool)

	for _, c := range cards {
		if _, ok := cardLabel[c]; ok {
			return false
		} else {
			cardLabel[c] = true
		}
	}

	return true
}

func countCardPart2(cards []int, card int) int {
	count := 0
	joker := convertCardPart2('J')
	for _, c := range cards {
		if c == card || c == joker {
			count++
		}
	}

	return count
}
