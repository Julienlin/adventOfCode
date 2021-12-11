import numpy as np


def update_explosions(grid: np.ndarray, explosion_indexes: list[tuple[int, int]]):
    n, m = grid.shape[0] - 1, grid.shape[1] - 1

    while explosion_indexes:

        ix, iy = explosion_indexes.pop()

        # top left corner
        if ix > 0 and iy > 0:
            top_left = ix - 1, iy - 1
            assert n >= top_left[0] >=0 and m >= top_left[1] >=0
            if grid[top_left[0], top_left[1]] < 10:
                grid[top_left[0], top_left[1]] += 1
                if grid[top_left[0], top_left[1]] == 10:
                    explosion_indexes.insert(0, top_left)

        # top
        if ix > 0:
            top = ix - 1, iy
            assert n >= top[0] >=0 and m >= top[1] >=0
            if grid[top[0], top[1]] < 10:
                grid[top[0], top[1]] += 1
                if grid[top[0], top[1]] == 10:
                    explosion_indexes.insert(0, (top[0], top[1]))

        # top right corner
        if ix > 0 and iy < m:
            top_right = ix - 1, iy + 1
            assert n >= top_right[0] >=0 and m >= top_right[1] >=0
            if grid[top_right[0], top_right[1]] < 10:
                grid[top_right[0], top_right[1]] += 1
                if grid[top_right[0], top_right[1]] == 10:
                    explosion_indexes.insert(0, (top_right[0], top_right[1]))

        # right
        if iy < m:
            right = ix, iy + 1
            assert n >= right[0] >=0 and m >= right[1] >=0
            if grid[right[0], right[1]] < 10:
                grid[right[0], right[1]] += 1
                if grid[right[0], right[1]] == 10:
                    explosion_indexes.insert(0, ([right[0], right[1]]))

        # bottom right
        if ix < n and iy < m:
            bottom_right = ix + 1, iy + 1
            assert n >= bottom_right[0] >=0 and m >= bottom_right[1] >=0
            if grid[bottom_right[0], bottom_right[1]] < 10:
                grid[bottom_right[0], bottom_right[1]] += 1
                if grid[bottom_right[0], bottom_right[1]] == 10:
                    explosion_indexes.insert(0, ([bottom_right[0], bottom_right[1]]))

        # bottom
        if ix < n:
            bottom = ix + 1, iy
            assert n >= bottom[0] >=0 and m >= bottom[1] >=0
            if grid[bottom[0], bottom[1]] < 10:
                grid[bottom[0], bottom[1]] += 1
                if grid[bottom[0], bottom[1]] == 10:
                    explosion_indexes.insert(0, (bottom[0], bottom[1]))

        # bottom left
        if ix < n and iy > 0:
            bottom_left = ix + 1, iy - 1
            assert n >= bottom_left[0] >=0 and m >= bottom_left[1] >=0
            if grid[bottom_left[0], bottom_left[1]] < 10:
                grid[bottom_left[0], bottom_left[1]] += 1
                if grid[bottom_left[0], bottom_left[1]] == 10:
                    explosion_indexes.insert(0, ([bottom_left[0], bottom_left[1]]))
        # left
        if iy > 0:
            left = ix, iy - 1
            assert n >= left[0] >=0 and m >= left[1] >=0
            if grid[left[0], left[1]] < 10:
                grid[left[0], left[1]] += 1
                if grid[left[0], left[1]] == 10:
                    explosion_indexes.insert(0, (left[0], left[1]))

        # print(f"explosion indexes : {explosion_indexes}")
        # print_grid(grid)
        # print()


def print_grid(grid):
    for row in grid:
        print(("{:2d} " * grid.shape[1]).format(*row))

    print()


if __name__ == "__main__":

    with open("input.txt", "r") as f:
        grid: np.ndarray = np.array(
            [np.array(list(map(int, line.strip()))) for line in f.readlines()]
        )

    # print_grid(grid)

    explosion_count = 0
    for i in range(1000):
        grid: np.ndarray = grid + 1
        explosion_indexes: list[tuple[int, int]] = list(zip(*np.where(grid == 10)))

        # print(f"explosion indexes : {explosion_indexes}")

        # print(f"Before update step : {i+1}")
        # print_grid(grid)

        update_explosions(grid, explosion_indexes)

        # print(f"After update step : {i+1}")
        # print_grid(grid)

        explosion_count += np.sum(grid == 10)
        grid[grid == 10] = 0

        if (grid == 0).all():
            print_grid(grid)
            print(i+1)
            break
    # print_grid(grid)
    # print(explosion_count)
