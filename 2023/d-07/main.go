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

func main() {
	// part1("input.txt")
	part2("input.txt")

	// hand := Hand{Cards: []int{14, 1, 12, 8, 13}, Bid: 5}
	//
	// fmt.Println(countPairs(hand.Cards), countCard(hand.Cards, convertCardPart2(joker)), hand.GetHandTypePart2())

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
			cards = append(cards, convertCard(card))
		}

		hand := Hand{Cards: cards, Bid: bid}

		//fmt.Println(hand)

		hands = append(hands, hand)

	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	sort.Sort(ByHandType(hands))

	sum := 0

	for i, hand := range hands {

		// fmt.Println(hand.Cards, hand.Bid, "rank:", i)

		fmt.Printf("nb pairs %v, %v : %v, sum = %v + %v * %v\n", countPairs(hand.Cards), hand.GetHandType(), hand.Cards, sum, hand.Bid, i+1)
		sum += hand.Bid * (i + 1)

	}

	fmt.Println("Part1: ", sum)

}

func convertCard(card rune) int {
	switch card {
	case 'A':
		return 14
	case 'K':
		return 13
	case 'Q':
		return 12
	case 'J':
		return 11
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

type Hand struct {
	Cards    []int
	Bid      int
	handType *HandType
}

type HandType struct {
	FiveOfKind  bool
	FourOfKind  bool
	FullHouse   bool
	ThreeOfKind bool
	TwoPairs    bool
	OnePair     bool
	HighCard    bool
}

func (h *Hand) GetHandType() HandType {
	if h.handType != nil {
		return *h.handType
	}

	cards := make([]int, len(h.Cards))

	copy(cards, h.Cards)

	sort.SliceStable(cards, func(i, j int) bool {
		return cards[i] < cards[j]
	})

	if isFiveOfKind(h.Cards) {
		h.handType = &HandType{FiveOfKind: true}
	} else if isFourOfKind(h.Cards) {
		h.handType = &HandType{FourOfKind: true}
	} else if isFullHouse(h.Cards) {
		h.handType = &HandType{FullHouse: true}
	} else if isThreeOfKind(h.Cards) {
		h.handType = &HandType{ThreeOfKind: true}
	} else if pairs := countPairs(h.Cards); pairs > 0 {
		if pairs >= 2 {
			h.handType = &HandType{TwoPairs: true}
		} else {
			h.handType = &HandType{OnePair: true}
		}
	} else if isHighCard(cards) {
		h.handType = &HandType{HighCard: true}
	} else {
		h.handType = &HandType{}
	}

	return *h.handType

}

func isNbOfKind(nb int) func([]int) bool {
	return func(cards []int) bool {

		for i := 0; i < len(cards); i++ {
			c := cards[i]
			if countCard(cards, c) == nb {
				return true
			}

			for i < len(cards)-1 && cards[i+1] == c {
				i++
			}

		}

		return false
	}
}

var isFiveOfKind = isNbOfKind(5)
var isFourOfKind = isNbOfKind(4)
var isThreeOfKind = isNbOfKind(3)

func isFullHouse(cards []int) bool {
	return isThreeOfKind(cards) && countPairs(cards) == 1

}

func countPairs(cards []int) int {
	pairLabel := make(map[int]bool)
	countPairs := 0

	for i := 0; i < len(cards); i++ {
		c := cards[i]
		// fmt.Printf("card : %v, countPair: %v\n", c, countCard(cards, c))
		if countCard(cards, c) == 2 {
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

func isTwoPair(cards []int) bool {
	return countPairs(cards) == 2
}

func isOnePair(cards []int) bool {
	return countPairs(cards) == 1
}

func isHighCard(cards []int) bool {
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

func countCard(cards []int, card int) int {
	count := 0
	for _, c := range cards {
		if c == card {
			count++
		}
	}

	return count
}

type ByHandType []Hand

func (a ByHandType) Len() int      { return len(a) }
func (a ByHandType) Swap(i, j int) { a[i], a[j] = a[j], a[i] }

func (a ByHandType) Less(i, j int) bool {
	handTypeI := a[i].GetHandType()
	handTypeJ := a[j].GetHandType()

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

func (h *Hand) Less(h2 Hand) bool {
	for i := 0; i < len(h.Cards); i++ {
		if h.Cards[i] > h2.Cards[i] {
			return false
		} else if h.Cards[i] < h2.Cards[i] {
			return true
		}
	}

	return false
}
