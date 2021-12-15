#!/usr/bin/python3


aim = 2020


def get_and_sort_input(input_filename):
    """
    Sort the input.
    """
    with open(input_filename) as input_file:
        input = input_file.readlines()
    input = map(lambda x: int(x), input)
    return sorted(input)


def func1(input_filename):
    input = get_and_sort_input(input_filename)
    for el in input:
        for el1 in reversed(input):
            if el + el1 == aim:
                return el * el1
    return 0


def func2(input_filename):
    input = get_and_sort_input(input_filename)
    for smallest in input:
        for biggest in reversed(input):
            middle = aim - smallest - biggest
            if middle in input:
                return middle * smallest * biggest
    return 0


def main():
    input_filename = "input.txt"

    print(f"First Problem anwser : {func1(input_filename)}")
    print(f"Second Problem anwser : {func2(input_filename)}")


if __name__ == "__main__":
    main()
