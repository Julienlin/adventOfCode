from copy import deepcopy


def add_to_adj(adj, START, i, j):
    if i not in adj:
        adj[i] = []
    if j not in adj[i] and j != START:
        adj[i].append(j)


def get_neighboorhood(adj: dict[str, list[str]], stack: list[str], neighboor: str):
    count_lower = max(
        (stack.count(el) for el in stack if el.islower())
    )
    # print(f"stack : {stack}")
    # print(f"head : {neighboor}")
    # print(f"count_lower : {count_lower}")
    # print()
    return [
        neig
        for neig in adj[neighboor]
        if neig != "start" and neig.isupper() or (neig.islower() and stack.count(neig) < (3 - count_lower))
    ]


if __name__ == "__main__":
    with open("input.txt", "r") as f:
        edges = [line.strip().split("-") for line in f.readlines()]

    adj: dict[str, list[str]] = {}
    START = "start"
    END = "end"

    # fill adjacent list
    for i, j in edges:
        add_to_adj(adj, START, i, j)

        add_to_adj(adj, START, j, i)

    # We do not need it
    del adj[END]

    # We remove start node in each

    stack: list[str] = []
    neig_stack = []

    stack.append(START)
    neig_stack.append(deepcopy(adj[START]))

    count = 0

    while stack:
        head = stack[-1]
        neighboors = neig_stack[-1]
        if neighboors:
            neighboor = neighboors.pop()
            stack.append(neighboor)
            if neighboor == END:
                count += 1
                neig_stack.append([])
            else:
                neig_stack.append(get_neighboorhood(adj, stack, neighboor))
        else:
            stack.pop()
            neig_stack.pop()

    print(count)
