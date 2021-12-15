#!/usr/bin/python3


def get_inputs(input_filename):
    """
    docstring
    """
    with open(input_filename) as input_file:
        input = input_file.read()
    inputs = input.split("\n\n")
    return inputs

def get_set_from_input(input):
    """
    docstring
    """
    input = input.replace("\n","")
    return set(input)

def get_anwsers_set_from_input(input):
    """
    docstring
    """
    inputs = input.split("\n")
    return list(map(lambda x: set(x), inputs))

def func1(input_filename):
    """
    Function for part 1
    """
    inputs = get_inputs(input_filename)
    # print(inputs)
    # print(len(get_set_from_input(inputs[0])))
    inc = 0
    for input in inputs:
        inc += len(get_set_from_input(input))
    return inc

def func2(input_filename):
    """
    Function for part 2
    """
    inputs = get_inputs(input_filename)
    # print(inputs)
    # print(len(get_set_from_input(inputs[0])))
    inc = 0
    # print(get_anwsers_set_from_input(inputs[0]))
    # print(len(set.intersection(*get_anwsers_set_from_input(inputs[0]))))
    for input in inputs:
        inc += len(set.intersection(*get_anwsers_set_from_input(input)))
    return inc


if __name__ == "__main__":
    input_filename = "input.txt"

    print(f"Answer for func1 : {func1(input_filename)}")
    print(f"Answer for func2 : {func2(input_filename)}")