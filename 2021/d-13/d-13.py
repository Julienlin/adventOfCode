import numpy as np


if __name__ =="__main__":
    with open("test.txt") as f:
        data = [ line.strip() for line in f.readlines()]
        space = data.index(" ")
        grid