package main

import (
	"container/list"
	"fmt"
	"io"
	"os"
	"strconv"
)

func main() {
	inputFilename := os.Args[1]
	// part1(inputFilename)
	part2(inputFilename)
}

func part1(inputFilename string) {
	fileContent, err := readFileContent(inputFilename)
	if err != nil {
		panic(err)
	}

	expandedDiskMap := expandDiskMapLL(string(fileContent))
	fmt.Println("expandedDiskMap", expandedDiskMap.Len())
	// fmt.Println("expandedDiskMap", expandedDiskMap)
	compactedDisk := compactDiskLL(expandedDiskMap)
	fmt.Println("compactedDisk", compactedDisk.Len())
	// fmt.Println("compactedDisk  ", compactedDisk)
	checksum := ComputeDiskMapChecksumLL(compactedDisk)
	fmt.Println("part1", checksum)
}

func part2(inputFilename string) {
	fileContent, err := readFileContent(inputFilename)
	if err != nil {
		panic(err)
	}

	expandedDiskMap := expandDiskMapLL(string(fileContent))
	fmt.Println("expandedDiskMap", expandedDiskMap.Len())
	// fmt.Println("expandedDiskMap", expandedDiskMap)
	compactedDisk := compactDiskLeftMostFileLL(expandedDiskMap)
	fmt.Println("compactDiskLeftMostFileLL", compactedDisk.Len())
	// fmt.Println("compactedDisk  ", compactedDisk)
	checksum := ComputeDiskMapChecksumLL(compactedDisk)
	fmt.Println("part2", checksum)
}

func readFileContent(inputFilename string) ([]byte, error) {
	f, err := os.Open(inputFilename)
	if err != nil {
		return nil, err
	}

	content, err := io.ReadAll(f)
	if err != nil {
		return nil, err
	}
	return content, nil
}

const FreeBlock = '.'

func expandDiskMapLL(diskMap string) *list.List {
	disk := list.New()

	var fileId int

	for idx, sizeString := range diskMap {
		size, _ := strconv.Atoi(string(sizeString))
		if idx%2 == 0 {
			for i := 0; i < size; i++ {
				disk.PushBack(fileId)
				// fmt.Println("add element", disk.Back().Value)
			}
			fileId++
		} else {
			for i := 0; i < size; i++ {
				disk.PushBack(-1)
				// fmt.Println("add element", disk.Back().Value)
			}
		}
	}

	return disk
}

func compactDiskLL(disk *list.List) *list.List {
	currFree, currFreeIdx := nextFreedIdxLL(disk.Back(), disk.Len()-1)

	for i, curr := 0, disk.Front(); i < currFreeIdx && curr != nil; i, curr = i+1, curr.Next() {
		if curr.Value.(int) == -1 {

			currNext := curr.Next()
			freePrev := currFree.Prev()

			disk.MoveBefore(currFree, currNext)
			disk.MoveAfter(curr, freePrev)

			curr = currNext.Prev()
			currFree, currFreeIdx = nextFreedIdxLL(freePrev, currFreeIdx-1)
		}
	}

	return disk
}

func displayDisk(disk *list.List) {
	for curr := disk.Front(); curr != nil; curr = curr.Next() {
		value := curr.Value.(int)
		if value > -1 {
			fmt.Print(curr.Value)
		} else {
			fmt.Print(".")
		}
	}
	fmt.Println()
}

func nextFreedIdxLL(curr *list.Element, idx int) (*list.Element, int) {
	for curr.Value.(int) == -1 {
		curr = curr.Prev()
		idx--
	}
	return curr, idx
}

func ComputeDiskMapChecksumLL(disk *list.List) uint64 {
	var checksum uint64
	for i, curr := 0, disk.Front(); curr != nil; i, curr = i+1, curr.Next() {
		if value := curr.Value.(int); value >= 0 {
			fmt.Println("value", value, "i", i)
			checksum += uint64(i * value)
		}
	}
	return checksum
}

func compactDiskLeftMostFileLL(disk *list.List) *list.List {
	visitedFile := make(map[int]struct{})
	for diskBlock := disk.Back(); diskBlock != nil; {
		if fileId := diskBlock.Value.(int); fileId > -1 {

			size := fileSize(diskBlock, fileId)

			if _, visited := visitedFile[fileId]; visited {
				fmt.Println("Skipping fileId", fileId)
				diskBlock = moveDiskblockRef(size, diskBlock)
			} else {
				visitedFile[fileId] = struct{}{}

				free := nextFreedIdxFromBeginLL(disk, diskBlock, size)
				if free != nil {
					fmt.Println("size", size, "fileId", fileId)
					for i := 0; i < size; i++ {

						diskBlockPrev := diskBlock.Prev()
						freeNext := free.Next()

						disk.MoveAfter(free, diskBlockPrev)
						disk.MoveBefore(diskBlock, freeNext)

						diskBlock = diskBlockPrev
						free = freeNext
					}

					// displayDisk(disk)
				} else {
					fmt.Println("not moving fileId", fileId, "of size", size)
					diskBlock = moveDiskblockRef(size, diskBlock)
				}
			}
		} else {
			diskBlock = diskBlock.Prev()
		}
	}

	return disk
}

func moveDiskblockRef(size int, diskBlock *list.Element) *list.Element {
	for i := 0; i < size; i++ {
		diskBlock = diskBlock.Prev()
	}
	return diskBlock
}

func fileSize(diskBlock *list.Element, fileId int) int {
	var fileSize int
	for currdB := diskBlock; currdB != nil && currdB.Value.(int) == fileId; currdB = currdB.Prev() {
		fileSize++
	}
	return fileSize
}

func nextFreedIdxFromBeginLL(disk *list.List, endMark *list.Element, minSize int) *list.Element {
	for diskBlock := disk.Front(); diskBlock != nil && diskBlock != endMark; diskBlock = diskBlock.Next() {
		if freeBlockId := diskBlock.Value.(int); freeBlockId == -1 {
			var freeSpaceSize int
			for curr := diskBlock; curr.Value.(int) == freeBlockId; curr = curr.Next() {
				freeSpaceSize++
			}

			if freeSpaceSize >= minSize {
				return diskBlock
			}
		}
	}
	return nil
}
