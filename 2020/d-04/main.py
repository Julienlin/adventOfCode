#!/usr/bin/python3

import re

byr = "byr"
iyr = "iyr"
eyr = "eyr"
hgt = "hgt"
hcl = "hcl"
ecl = "ecl"
pid = "pid"
cid = "cid"

all_keys = {byr, iyr, eyr, hgt, hcl, ecl, pid, cid}

required_keys_1 = {byr, iyr, eyr, hgt, hcl, ecl, pid}

key, value = 0, 1

hair_color_re = re.compile(r"#[0-9abcdef]{6}")

eye_color = {"amb", "blu", "brn", "gry", "grn", "hzl", "oth"}

pid_re = re.compile(r"\d{9}")


def gets_inputs(input_filename):
    """
    Parse input file and separate input by newline.
    """
    with open(input_filename, "r") as input_file:
        input = input_file.read()
    inputs = input.split("\n\n")
    return inputs


def parse_input(input):
    """
    Parse input to get usable data.
    """
    inputs = input.replace("\n", " ").split(" ")
    res = {}
    for el in inputs:
        key_value = el.split(":")
        if key_value[key] in all_keys:
            res[key_value[key]] = key_value[value]

    # print(res)
    return res


def is_valid_1(passport):
    """
    Check whether the passport is valid.
    """
    return passport.keys() >= required_keys_1


def is_valid_2(passport):
    """
    Check whether the passport is valid.
    """
    if passport.keys() >= required_keys_1:
        byr_value = int(passport[byr])
        if 1920 > byr_value or 2002 < byr_value:
            return False
        iyr_value = int(passport[iyr])
        if 2010 > iyr_value or 2020 < iyr_value:
            return False
        eyr_value = int(passport[eyr])
        if eyr_value < 2020 or eyr_value > 2030:
            return False
        hgt_value = int(passport[hgt][0 : len(passport[hgt]) - 2])
        hgt_unit = passport[hgt][len(passport[hgt]) - 2 :]
        print(passport[hgt], hgt_value, hgt_unit)
        if hgt_unit != "cm" and hgt_unit != "in":
            return False
        if hgt_unit == "cm" and (hgt_value < 150 or hgt_value > 193):
            return False
        if hgt_unit == "in" and (hgt_value < 59 or hgt_value > 76):
            return False
        if not hair_color_re.fullmatch(passport[hcl]):
            return False
        if not passport[ecl] in eye_color:
            return False
        if not pid_re.fullmatch(passport[pid]):
            return False
        return True
    return False


def func1(input_filename):
    inputs = gets_inputs(input_filename)
    # print(inputs)
    inc = 0
    for input in inputs:
        passport = parse_input(input)
        # print(passport, is_valid(passport))
        if is_valid_1(passport):
            inc += 1
    return inc


def func2(input_filename):
    inputs = gets_inputs(input_filename)
    # print(inputs)
    inc = 0
    for input in inputs:
        passport = parse_input(input)
        if is_valid_1(passport):
            print(passport, is_valid_2(passport))
        if is_valid_2(passport):
            inc += 1
    return inc


def main():
    input_filename = "input.txt"

    print(f"First Problem anwser : {func1(input_filename)}")
    print(f"Second Problem anwser : {func2(input_filename)}")


if __name__ == "__main__":
    main()
