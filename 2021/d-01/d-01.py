def main():
    with open("input.txt") as f:
        lines = f.readlines()

    m = list(map(int, lines))
    print(len(m))
    m = transform(m)
    print(len(m))

    return compute(m)


def compute(m):
    count = 0
    for index, value in enumerate(m[1:]):
        print(index, value, m[index])
        if value > m[index]:
            count += 1
    return count


def transform(lst: list[int]) -> list[int]:
    return [sum(value) for value in zip(lst, lst[1:], lst[2:])]


if __name__ == "__main__":
    print(main())
