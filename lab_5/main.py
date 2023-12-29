def citire_gramatica(nume_fisier):
    gramatica = {}
    with open(nume_fisier, 'r') as f:
        for linie in f:
            linie = linie.strip()
            stanga, dreapta = linie.split("->")
            productii = dreapta.split("|")
            gramatica[stanga.strip()] = [prod.strip() for prod in productii]
    return gramatica

def elimina_recursivitate_stanga(gramatica):
    for non_terminal in list(gramatica.keys()):
        productii = gramatica[non_terminal]
        recursiv = [prod for prod in productii if prod[0] == non_terminal]
        non_recursiv = [prod for prod in productii if prod[0] != non_terminal]
        if recursiv:
            gramatica[non_terminal] = [prod + non_terminal + "'" for prod in non_recursiv]
            gramatica[non_terminal + "'"] = [prod[1:] + non_terminal + "'" for prod in recursiv] + ["ε"]
    return gramatica

def inchidere(elemente, gramatica):
    adaugate = True
    while adaugate:
        adaugate = False
        for (stanga, dreapta, lookahead) in list(elemente):
            punct = dreapta.find('.')
            if punct < len(dreapta) - 1 and dreapta[punct + 1] in gramatica:
                for prod in gramatica[dreapta[punct + 1]]:
                    nou = (dreapta[punct + 1], '.' + prod, lookahead)
                    if nou not in elemente:
                        elemente.add(nou)
                        adaugate = True
    return elemente

def goto(elemente, simbol, gramatica):
    nou_set = set()
    for (stanga, dreapta, lookahead) in elemente:
        punct = dreapta.find('.')
        if punct < len(dreapta) - 1 and dreapta[punct + 1] == simbol:
            nou_set.add((stanga, dreapta[:punct] + dreapta[punct + 1] + '.' + dreapta[punct + 2:], lookahead))
    return inchidere(nou_set, gramatica)

def construieste_automat(gramatica, simbol_start):
    stari = []
    stari_noi = [inchidere({(simbol_start, '.' + gramatica[simbol_start][0], '$')}, gramatica)]
    tranzitii = {}

    while stari_noi:
        stare = stari_noi.pop()
        stari.append(stare)

        for simbol in gramatica:
            noua_stare = goto(stare, simbol, gramatica)
            if noua_stare and noua_stare not in stari:
                stari_noi.add(noua_stare)
            if noua_stare:
                tranzitii[(stare, simbol)] = noua_stare

    return stari, tranzitii

def construieste_tabel_parsing(stari, tranzitii, gramatica, simbol_start):
    tabel_parsing = {}
    for stare in stari:
        for stanga, dreapta, lookahead in stare:
            if dreapta[-1] == '.':
                for simbol in lookahead:
                    if (stanga, dreapta, lookahead) == (simbol_start, gramatica[simbol_start][0] + '.', '$'):
                        tabel_parsing[(stare, simbol)] = ('ACCEPT', )
                    else:
                        tabel_parsing[(stare, simbol)] = ('REDUCE', stanga, dreapta[:-1])
            else:
                simbol_urmator = dreapta[dreapta.find('.') + 1]
                if simbol_urmator in tranzitii and (stare, simbol_urmator) in tranzitii:
                    tabel_parsing[(stare, simbol_urmator)] = ('SHIFT', tranzitii[(stare, simbol_urmator)])
    return tabel_parsing

def parse(secventa, tabel_parsing, stare_initiala):
    stiva = [stare_initiala]
    i = 0
    productii_aplicate = []
    while i < len(secventa):
        stare = stiva[-1]
        simbol = secventa[i]
        if (stare, simbol) in tabel_parsing:
            actiune = tabel_parsing[(stare, simbol)]
            if actiune[0] == 'SHIFT':
                stiva.append(actiune[1])
                i += 1
            elif actiune[0] == 'REDUCE':
                stanga, dreapta = actiune[1], actiune[2]
                productii_aplicate.append((stanga, dreapta))
                stiva = stiva[:-len(dreapta)]
                stiva.append(tabel_parsing[(stiva[-1], stanga)][1])
            elif actiune[0] == 'ACCEPT':
                return 'Secventa este acceptata. Producțiile aplicate sunt: ' + ', '.join(['{} -> {}'.format(stanga, dreapta) for stanga, dreapta in productii_aplicate])
        else:
            return 'Eroare de sintaxa la simbolul "{}" la pozitia {}'.format(simbol, i)
    return 'Secventa nu este acceptata'

def main():
    # Citirea inputului
    gramatica = citire_gramatica('file.txt')
    secventa = 'abc'  # Aici trebuie să înlocuiți cu secvența dvs. de intrare

    # Preprocesarea gramaticii
    gramatica_preprocesata = elimina_recursivitate_stanga(gramatica)

    # Construirea automatului de stări
    stari, tranzitii = construieste_automat(gramatica_preprocesata, 'S')  # 'S' este simbolul de start

    # Construirea tabelului de parsing
    tabel_parsing = construieste_tabel_parsing(stari, tranzitii, gramatica_preprocesata, 'S')

    # Efectuarea analizei sintactice
    rezultat = parse(secventa, tabel_parsing, stari)

    # Afișarea rezultatelor
    print(rezultat)

if __name__ == '__main__':
    main()