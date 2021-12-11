if __name__ == "__main__":
    with open("input.txt") as f:
        data = [line.strip() for line in f.readlines()]

    # score = {")": 3, "]": 57, "}": 1197, ">": 25137}

    # score = {")": 1, "]": 2, "}": 3, ">": 4}
    score = {"(": 1, "[": 2, "{": 3, "<": 4}
    res = []
    for line in data:
        stack = []
        incorrect = False
        for el in line:
            if el in "([{<":
                stack.append(el)
            else:
                head = stack[-1]
                if (
                    head == "("
                    and el != ")"
                    or head == "["
                    and el != "]"
                    or head == "{"
                    and el != "}"
                    or head == "<"
                    and el != ">"
                ):
                    incorrect = True
                    break
                else:
                    stack.pop()
        if not incorrect:
            sub_res = 0
            for el in reversed(stack):
                sub_res *= 5
                sub_res += score[el]
            res.append(sub_res)
            res.sort()
    print(res[len(res) // 2])
