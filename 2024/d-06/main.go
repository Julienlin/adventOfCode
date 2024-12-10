package main

import (
	"bufio"
	"fmt"
	"os"
)

type Maps [][]rune

func (m Maps) SizeX() int {
	return len(m)
}

func (m Maps) SizeY() int {
	return len(m[0])
}

func (m Maps) Copy() Maps {
	maps := make(Maps, 0, m.SizeX())
	for _, row := range m {
		rowCopy := make([]rune, len(row))
		copy(rowCopy, row)
		maps = append(maps, rowCopy)
	}
	return maps
}

func (m Maps) IsInMaps(pos []int) bool {
	return pos[0] >= 0 && pos[0] < m.SizeX() && pos[1] >= 0 && pos[1] < m.SizeY()
}

const (
	UP    = '^'
	DOWN  = 'v'
	RIGHT = '>'
	LEFT  = '<'
)

const (
	VISITED_MARK = 'X'
	WALL         = '#'
)

func main() {
	inputFilename := os.Args[1]

	f, err := os.Open(inputFilename)
	if err != nil {
		panic(err)
	}

	maps := make(Maps, 0)

	scanner := bufio.NewScanner(f)

	for scanner.Scan() {
		line := scanner.Text()
		maps = append(maps, []rune(line))
	}

	gurrdPosition, direction := GuardPosition(maps)

	count, countObstruction := ComputeGuardTrajectory(maps, gurrdPosition, direction)
	fmt.Println("part 1", count)
	fmt.Println("part 2", countObstruction)
}

func GuardPosition(maps Maps) ([]int, rune) {
	for i := 0; i < maps.SizeX(); i++ {
		for j := 0; j < maps.SizeY(); j++ {
			pos := maps[i][j]
			if pos == UP || pos == DOWN || pos == LEFT || pos == RIGHT {
				return []int{i, j}, pos
			}
		}
	}
	panic(fmt.Errorf("should not happen"))
}

func ComputeGuardTrajectoryDetectLoop(maps Maps, guardInitialPosition []int, initialDirection rune) int {
	m := maps.Copy()
	currentDirection := initialDirection
	currPosition := guardInitialPosition
	var count int

	obstacles := make(map[int]int, 0)

	for m.IsInMaps(currPosition) {
		fmt.Println(currPosition, string(currentDirection))
		if m[currPosition[0]][currPosition[1]] != VISITED_MARK {
			fmt.Println("Marking", currPosition)
			m[currPosition[0]][currPosition[1]] = VISITED_MARK
			count++
		}
		next := NextPosition(currPosition, currentDirection)

		for m.IsInMaps(next) && m[next[0]][next[1]] == WALL {
			obstacles[next[0]*m.SizeY()+next[1]]++
			currentDirection = NextDirectionAfterWall(currentDirection)
			next = NextPosition(currPosition, currentDirection)
			fmt.Println(next, string(currentDirection))
		}

		fmt.Println("next", next, string(currentDirection))
		currPosition = next

		fmt.Println("tour")
		printMaps(m)
	}

	return count
}

func IsInCycle(obstacles map[int]int) bool {
	for _, obstacleCount := range obstacles {
		if obstacleCount >= 5 {
			return true
		}
	}
	return false
}

func ComputeGuardTrajectory(maps Maps, guardInitialPosition []int, initialDirection rune) (int, int) {
	m := maps.Copy()
	currentDirection := initialDirection
	currPosition := guardInitialPosition
	var count int
	var countObstruction int

	fmt.Println("coucou", currPosition, string(currentDirection))

	fmt.Println(m.SizeX(), m.SizeY())

	fmt.Println("is in map", m.IsInMaps(currPosition))

	for m.IsInMaps(currPosition) {
		fmt.Println(currPosition, string(currentDirection))
		if m[currPosition[0]][currPosition[1]] != VISITED_MARK {
			fmt.Println("Marking", currPosition)
			m[currPosition[0]][currPosition[1]] = VISITED_MARK
			count++
		} else {
			fmt.Println("obstruction", currPosition)
			m[currPosition[0]][currPosition[1]] = '0'

			countObstruction++
		}
		next := NextPosition(currPosition, currentDirection)

		for m.IsInMaps(next) && m[next[0]][next[1]] == WALL {
			currentDirection = NextDirectionAfterWall(currentDirection)
			next = NextPosition(currPosition, currentDirection)
			fmt.Println(next, string(currentDirection))
		}

		fmt.Println("next", next, string(currentDirection))
		currPosition = next

		fmt.Println("tour")
		printMaps(m)
	}

	return count, countObstruction
}

func printMaps(m Maps) {
	for _, line := range m {
		fmt.Println(string(line))
	}
}

func NextDirectionAfterWall(direction rune) rune {
	switch direction {
	case UP:
		return RIGHT
	case RIGHT:
		return DOWN
	case DOWN:
		return LEFT
	default:
		return UP
	}
}

func NextPosition(position []int, direction rune) []int {
	switch direction {
	case UP:
		return []int{position[0] - 1, position[1]}
	case DOWN:
		return []int{position[0] + 1, position[1]}
	case RIGHT:
		return []int{position[0], position[1] + 1}
	default:
		return []int{position[0], position[1] - 1}
	}
}
