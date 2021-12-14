from collections import Counter
from tqdm import tqdm, trange


def part_1(polymer, data, step=10):
    for _ in range(step):
        new_polymer = polymer[0]
        for beg, end in zip(polymer, polymer[1:]):
            new_polymer += data[beg + end] + end

        polymer = new_polymer

    c = Counter(polymer)

    common = c.most_common()

    print(common[0][1] - common[-1][1])


def part_2(polymer: list[str], data: dict[str, str], step=40):
    pairs = [(beg, end) for beg, end in zip(polymer, polymer[1:])]
    counts = Counter(polymer)
    pred_pair_counter = Counter({tuple(list(pair)): 0 for pair in data.keys()})
    for pair in pairs:
        pred_pair_counter[pair] += 1

    pairs = list(set(pairs))

    for _ in trange(step):
        new_pairs: list[tuple[str, str]] = []
        pair_counter = Counter({tuple(list(pair)): 0 for pair in data.keys()})
        for pair in tqdm(pairs, leave=False):
            el = data["".join(pair)]
            counts[el] += pred_pair_counter[pair]

            # Add left pair
            left = (pair[0], el)
            if left not in new_pairs:
                new_pairs.append(left)
            pair_counter[left] += pred_pair_counter[pair]

            # Add right pair
            right = (el, pair[1])
            if right not in new_pairs:
                new_pairs.append(right)
            pair_counter[right] += pred_pair_counter[pair]

        pred_pair_counter = pair_counter
        pairs = new_pairs

    commons = counts.most_common()
    print(commons[0][1] - commons[-1][1])


if __name__ == "__main__":
    with open("input.txt") as f:
        polymer = list(f.readline().strip())
        f.readline()
        data = [line.strip().split("->") for line in f.readlines()]
        data = {line[0].strip(): line[1].strip() for line in data}

    # part_1(polymer, data)
    part_2(polymer, data)
