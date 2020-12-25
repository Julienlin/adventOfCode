#!/usr/bin/python3


import requests
import os
import argparse


class Day:
    """
    Represente un jour dans l'adventOfCode.
    """

    source_template = """#!/usr/bin/python3

def func1(input_filename):
    pass

def func2(input_filename):
    pass

def main():
    input_filename = "input.txt"

    #print(f"First Problem anwser : {func1(input_filename)}")
    #print(f"Second Problem anwser : {func2(input_filename)}")

if __name__ == "__main__":
    main()
"""

    def __init__(self, day, directory=""):
        """
        Init function.
        """
        self.day = day
        if not directory:
            self.directory = f"d-{day:02d}"
        else:
            self.directory = directory

    def create_dir(self):
        """
        Create the directory containing the day problem.
        """
        if not os.path.isdir(self.directory):
            os.mkdir(self.directory)
        return self

    def put_template(self):
        """
        Put template file into the directory.
        """
        template_filename = "main.py"
        template_path = os.path.join(self.directory, template_filename)
        if not os.path.isfile(template_path):
            with open(template_path, "w") as template:
                template.write(self.source_template)
        return self

    def download_input(self):
        """
        Download the input of the day.
        At the moment this function does not work.
        Maybe the url doesn't work it more like :

        https://adventofcode.com/2020/day/1/input
        +
        add cookie with session
        """
        with open(".credentials", "r") as credentials_file:
            credentials = credentials_file.readline()
            response = requests.get(
                f"https://adventofcode.com/2020/day/{self.day}/input",
                cookies={"session": credentials},
            )
        with open(os.path.join(self.directory, "input.txt"), "w") as input_file:
            input_file.write(response.text)
        return self


if __name__ == "__main__":

    parser = argparse.ArgumentParser()
    parser.add_argument("day", type=int)
    arg = parser.parse_args()
    Day(arg.day).create_dir().put_template().download_input()

    # Day(2).create_dir().put_template().download_input()