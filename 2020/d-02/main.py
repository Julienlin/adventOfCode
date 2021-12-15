#!/usr/bin/python3

import re

line_regex = re.compile("(\d+)-(\d+) ([a-z]): ([a-z]+)")


def check_password_policy_1(beg, end, letter, password):
    """
    Check the first policy
    """
    inc = 0
    for el in password:
        if el == letter:
            inc += 1
    return 1 if beg <= inc and inc <= end else 0


def parse_line(line):
    """
    docstring
    """
    matches = line_regex.match(line)
    return int(matches.group(1)), int(matches.group(2)), *matches.group(3, 4)


def check_password_policy_2(beg, end, letter, password):
    """
    docstring
    """
    inc = 0

    if password[beg - 1] == letter:
        inc += 1
    if password[end - 1] == letter:
        inc += 1
    return 1 if inc == 1 else 0


def func1(input_filename):
    with open(input_filename, "r") as input_file:
        input = input_file.readlines()

    transformed = map(lambda x: check_password_policy_1(*parse_line(x)), input)

    res = sum(transformed)

    return res


def func2(input_filename):
    with open(input_filename, "r") as input_file:
        input = input_file.readlines()

    transformed = map(lambda x: check_password_policy_2(*parse_line(x)), input)

    res = sum(transformed)

    return res


def main():
    input_filename = "input.txt"

    print(f"First Problem anwser : {func1(input_filename)}")
    print(f"Second Problem anwser : {func2(input_filename)}")


if __name__ == "__main__":
    main()
