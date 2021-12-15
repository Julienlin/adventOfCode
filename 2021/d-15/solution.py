import networkx as nx
from networkx.algorithms.shortest_paths.weighted import (
    dijkstra_path,
    dijkstra_path_length,
)

if __name__ == "__main__":
    G = nx.DiGraph()
    with open("input.txt") as f:
        grid = [list(map(int, line.strip())) for line in f.readlines()]

    len_grid= len(grid)
    # print(len_grid)
    n = 4
    for line in grid:
        buf = list(map(lambda x: x+1 if x+1 < 10 else 1, line))
        for _ in range(n):
            line += buf
            buf = list(map(lambda x: x+1 if x+1 < 10 else 1, buf))

    for _ in range(n):
        for line in grid[-len_grid:]:
            buf = list(map(lambda x: x+1 if x+1 < 10 else 1, line))
            grid.append(buf)


    len_col = len(grid[0])
    # print(len(grid), len_col)
    for i in range(len(grid)):
        for j in range(len_col):
            beg = i * len_col + j
            edges = []
            if j < len_col - 1:
                end = i * len_col + j + 1
                # print(f"(beg, end, grid[i][j+1]) : {(beg, end, grid[i][j+1])}")
                edges.append((beg, end, grid[i][j + 1]))
            if i < len(grid) - 1:
                end = (i + 1) * len_col + j
                # print(f"(beg, end, grid[i+1][j]) : {(beg, end, grid[i+1][j])}")
                edges.append((beg, end, grid[i + 1][j]))
            if j > 0:
                end = i * len_col + j - 1
                edges.append((beg, end, grid[i][j - 1]))
            if i > 0:
                end = (i - 1) * len_col + j
                edges.append((beg, end, grid[i - 1][j]))

            G.add_weighted_edges_from(edges)

    # for n, nbrs in G.adj.items():
    #     for nbr, eattr in nbrs.items():
    #         wt = eattr["weight"]
    #         print(f"({n}, {nbr}, {wt})")

    print(dijkstra_path_length(G, 0, len(grid) * len_col - 1))
    shortest_path = dijkstra_path(G, 0, len(grid) * len_col - 1)
    # print(shortest_path, len(shortest_path))
