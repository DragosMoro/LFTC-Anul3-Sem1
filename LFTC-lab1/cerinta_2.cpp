#include <iostream>
using namespace std;

// Functia pentru calculul perimetrului si ariei cercului
void calculCerc() {
    float raza;
    cout << "Introduceti raza cercului: ";
    cin >> raza;

    float razaDublu = raza * 2;
    float razaLaA2a = raza*raza;
    float perimetru = 3.14*razaDublu;
    
    float aria = 3.14*razaLaA2a;
    
    cout << perimetru << endl;
    cout << aria << endl;
}

// Functia pentru calculul CMMDC a 2 numere naturale
int gcd(int a, int b) {
    while (b != 0) {
        int temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}

void calculCMMDC() {
    int num1, num2;
    cout << "Introduceti doua numere naturale: ";
    cin >> num1 >> num2;
    
    int result = gcd(num1, num2);
    
    cout << "CMMDC al numerelor este: " << result << endl;
}

// Functia pentru calculul sumei a n numere citite de la tastatura
void calculSuma() {
    int n;
    cout << "Introduceti numarul de numere pentru suma: ";
    cin >> n;
    
    int sum = 0;
    int i=0;
    while(i<n){
        int num;
        cout << "Introduceti numarul "<< ": ";
        cin >> num;
        sum += num;
        i++;
    }
    
    cout << "Suma numerelor este: " << sum << endl;
}

int main() {
    cout << "1. Calculul perimetrului si ariei cercului" << endl;
    calculCerc();
    cout << "2. Calculul CMMDC a 2 numere naturale" << endl;
    calculCMMDC();
    cout << "3. Calculul sumei a n numere" << endl;
    calculSuma();
    return 0;
}
