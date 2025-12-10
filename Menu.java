class Menu extends Program{
    void aideMenu() {
        println("1.Cases\n2.Combat\n3.Bonus");
    }

    void aideCases() {
        println("Dans ce jeu, il existe 4 types de cases.\nLe premier type est bien sur la case vide. Son numéro est inscrit à l'intérieur.\nEnsuite, nous avons la case où se trouve le joueur. Elle est marquée par un 'X'.\n");
    }

    void aide() {
        aideMenu();
        int saisie;
        do{
            print("Veuillez choisir un nombre entre 1 et 3 : ");
            saisie = readInt();
        } while(saisie<1 || saisie>3);
        if (saisie==1) {
            aideCases();
        }
    }

    void afficherMenu() {
        println("1.Jouer\n2.Aide\n3.Option");
    }

    void algorithm() {
        afficherMenu();
        int saisie;
        do{
            print("Veuillez choisir un nombre entre 1 et 3 : ");
            saisie = readInt();
        } while (saisie<1 || saisie>3);
        if (saisie==2) {
            aide();
        }
    }
}

class Plateau {
    void remplacer(String plateau, int _case, String _contenu) {
        // TODO Boucle sur plateau; quand on trouve un cas où plateau[i] + plateau[i+1] == _case (ou == "0"+_case si (_case <= 9)), on s'arrête et on garde l'index
        // Ensuite on utilisera l'index pour remplacer avec _contenu
    }
}

class Jeu {
    boolean fini = false;
    void jouer() {
        while (!fini) {

        }
    }
}