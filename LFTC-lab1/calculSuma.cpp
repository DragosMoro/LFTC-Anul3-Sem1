#include <iostream>
using namespace std;

// Functia pentru calculul sumei a n numere citite de la tastatura
void calculSuma() {
    int n;
    cin >> n;
    
    int sum = 0;
    int i=0;
    while(i<n){
        int num;                                    
        cin >> num;
        sum = sum+ num;
        i=i+1;
    }
    
    cout <<sum << endl;
}

int main()
{
    calculSuma();
    return 0;

}