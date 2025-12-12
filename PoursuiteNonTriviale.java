import extensions.File;

class PoursuiteNonTriviale extends Program {
    int choixNombre(int min, int max) {
        int saisie;
        do {
            print("Veuillez choisir un nombre entre "+min+" et "+max+" : ");
            saisie = readInt();
        } while (saisie < min || saisie > max);
        return saisie;
    }

//-----fonctions aide-----

    void lireFichier(String cheminFichier) {
        File fichier = newFile(cheminFichier);
        while (ready(fichier)) {
            println(readLine(fichier));
        }
        String saisie;
        do {
            saisie = readString();
        } while (length(saisie) > 0);
        aide();
    }

    void aide() {
        println("1.Cases\n2.Combat\n3.Bonus\n4.retour");

        final String pathCommun = "aide/aide ";
        final String[] fichiers = new String[] {"cases", "combat", "bonus"};

        final int saisie = choixNombre(1, 4);
        if (saisie != 4) {
            lireFichier(pathCommun + fichiers[saisie - 1]);
        } else {
            afficherMenu();
        }
    }

//-----fonctions Menu-----

    void afficherMenu() {
        println("1.Jouer\n2.Aide\n3.Option");
        final int saisie = choixNombre(1, 3);
        //if (saisie == 1) jouer();
        if (saisie == 2) aide();
    }

//-----algo principal-----

    void algorithm() { afficherMenu(); }

//-----plateau-----
    void remplacer(String plateau, int _case, String _contenu) {
        // TODO Boucle sur plateau; quand on trouve un cas où plateau[i] + plateau[i+1] == _case (ou == "0"+_case si (_case <= 9)), on s'arrête et on garde l'index
        // Ensuite on utilisera l'index pour remplacer avec _contenu
    }
}