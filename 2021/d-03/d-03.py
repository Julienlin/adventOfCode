from os import openpty
import numpy as np


if __name__ == "__main__":
    with open("input.txt", "r") as f:
        data = np.array([list(map(int, list(line.strip()))) for line in f.readlines()])

    max_bits, nb_bits = data.shape
    one: np.ndarray = np.sum(data, axis=0)
    zero = max_bits - one

    power_two = np.ones((nb_bits - 1,)) * 2
    power_two = np.cumprod(power_two)
    power_two: np.ndarray = np.concatenate(([1], power_two))
    power_two = np.flip(power_two)

    rate_gamma = (one - zero) >= 0
    rate_gamme = rate_gamma.astype(int)
    epsilon_rate = 1 - rate_gamma
    epsilon_rate = epsilon_rate.astype(float)
    epsilon_rate *= power_two
    rate_gamma = rate_gamma * power_two

    print(rate_gamma)
    print(epsilon_rate)

    print(rate_gamma.sum() * epsilon_rate.sum())
