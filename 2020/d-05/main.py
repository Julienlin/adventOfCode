#!/usr/bin/python3


def get_seat_id(seat_partition):
    """
    Get seat id.
    """
    row_range = (0, 128)
    column_range = (0, 7)

    for el in seat_partition[0:7]:
        mid = sum(row_range) // 2
        if el == "F":
            row_range = (row_range[0], mid)
        else:
            row_range = (mid, row_range[1])

    for el in seat_partition[7:]:
        mid = sum(column_range) // 2
        if el == "L":
            column_range = column_range[0], mid
        else:
            column_range = mid, column_range[1]
    return row_range[0] * 8 + column_range[1]


def func1(input_filename):
    with open(input_filename, "r") as input_file:
        inputs = input_file.readlines()
    transformed = map(lambda x: get_seat_id(x), inputs)
    return max(transformed)


def func2(input_filename):
    with open(input_filename, "r") as input_file:
        inputs = input_file.readlines()
    transformed = list(map(lambda x: get_seat_id(x), inputs))
    max_id = max(transformed)
    min_id = min(transformed)
    nb_id = len(inputs) + 1
    seat = nb_id * (max_id + min_id) // 2 - sum(transformed)
    return seat


def main():
    input_filename = "input.txt"
    print(f"First Problem anwser : {func1(input_filename)}")
    print(f"Second Problem anwser : {func2(input_filename)}")


if __name__ == "__main__":
    main()
