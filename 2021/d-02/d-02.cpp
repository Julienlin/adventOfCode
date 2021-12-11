#include <iostream>
#include <fstream>
#include <string>

int main()
{
    // Create a text string, which is used to output the text file
    std::string myText;

    // Read from the text file
    std::ifstream MyReadFile("input.txt");

    int horizontal = 0, depth = 0, aim = 0;
    std::string forward = "forward", up = "up";

    // Use a while loop together with the getline() function to read the file line by line
    while (getline(MyReadFile, myText))
    {
        std::string delimiter = " ";
        std::string operation = myText.substr(0, myText.find(delimiter));
        std::string number = myText.substr(myText.find(delimiter), myText.length());
        std::size_t found = operation.find(forward);
        if (found != std::string::npos)
        {
            horizontal += stoi(number);
            depth += aim * stoi(number);
        }
        else
        {
            found = operation.find(up);
            if (found != std::string::npos){

                // depth -= stoi(number);
                aim -= stoi(number);
            }
            else{

                // depth += stoi(number);
                aim += stoi(number);
            }
        }
    }

    std::cout << depth * horizontal << std::endl;


    // Close the file
    MyReadFile.close();
}