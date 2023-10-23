#include <iostream>
using namespace std;

void calculCerc() {
    float raza;
    cin >> raza;

    float razaDublu = raza * 2;
    float razaLaA2a = raza*raza;
    float perimetru = 3.14*razaDublu;
    
    float aria = 3.14*razaLaA2a;
    
    cout << perimetru << endl;
    cout << aria << endl;
}

int main()
{
    calculCerc();
    return 0;
}