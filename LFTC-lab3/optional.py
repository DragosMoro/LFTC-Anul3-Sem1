def citeste_gramatica(filename):
    neterminale = set()
    terminale = set()
    simbol_start = None
    productii = []

    with open(filename, 'r') as file:
        for line in file:
            line = line.strip()
            if not line:
                continue

            parts = line.split(' => ')
            if len(parts) != 2:
                print(f"Format invalid pe linia: {line}")
                continue

            stanga, dreapta = parts[0], parts[1]

            neterminale.add(stanga)


            for simbol in dreapta.split(' '):
                if simbol.isalpha() and simbol.islower():
                    terminale.add(simbol)
                elif simbol == 'epsilon':
                    terminale.add('epsilon')


            if simbol_start is None:
                simbol_start = stanga

            productii.append((stanga, dreapta))

    return neterminale, terminale, simbol_start, productii


def afiseaza_gramatica(neterminale, terminale, simbol_start, productii):
    print(f"Neterminale: {', '.join(sorted(neterminale))}")
    print(f"Terminale: {', '.join(sorted(terminale))}")
    print(f"Simbol de start: {simbol_start}\n")

    print("Reguli de productie:")
    for productie in productii:
        print(f"{productie[0]} => {productie[1]}")


filename = 'gramatica.txt'  
neterminale, terminale, simbol_start, productii = citeste_gramatica(filename)
afiseaza_gramatica(neterminale, terminale, simbol_start, productii)
