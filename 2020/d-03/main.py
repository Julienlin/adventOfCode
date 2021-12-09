#!/usr/bin/python3


horizontal, verticale = 1, 0
tree = "#"


def get_maps(input_filename):
    """
    Get Maps from input_filename.
    """
    with open(input_filename, "r") as input_file:
        input = input_file.readlines()
    return input


def get_nb_trees(maps, slope):
    nb_trees = 0
    final_stage = len(maps)
    horizontal_len = len(maps[0]) - 1
    cur_pos = [0, 0]
    while cur_pos[verticale] < final_stage:
        if maps[cur_pos[verticale]][cur_pos[horizontal] % horizontal_len] == tree:
            nb_trees += 1
        cur_pos[horizontal] += slope[horizontal]
        cur_pos[verticale] += slope[verticale]
    return nb_trees


def func1(input_filename):
    maps = get_maps(input_filename)
    slope = 1, 3
    return get_nb_trees(maps, slope)


def func2(input_filename):
    maps = get_maps(input_filename)
    slopes=[(1, 1), (1, 3), (1, 5), (1, 7), (2, 1)]
    tot_nb_trees = 1
    for slope in slopes:
        nb_trees = get_nb_trees(maps, slope)
        tot_nb_trees *= nb_trees
    return tot_nb_trees


def main():
    input_filename = "input.txt"

    print(f"First Problem anwser : {func1(input_filename)}")
    print(f"Second Problem anwser : {func2(input_filename)}")


if __name__ == "__main__":
    main()
