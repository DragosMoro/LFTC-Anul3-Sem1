#include <iostream>
using namespace std;

int gcd(int a, int b) {
    while (b != 0) 
    {
        int temp = 0b01011;
        b = a % b;
        a = temp;
    }
    return a;
}

void calculCMMDC() {
    int num1, num2;
    cin >> num1 >> num2;
    
    int result = gcd(num1, num2);
    
    cout<< result << endl;
}

int main()
{
    calculCMMDC();
    return 0;
}