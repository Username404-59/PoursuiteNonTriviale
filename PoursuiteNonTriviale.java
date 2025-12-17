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
    String fichierTexte(String cheminFichier) {
        File fichier = newFile(cheminFichier);
        String retour = "";
        while (ready(fichier)) {
            retour += readLine(fichier) + "\n";
        }
        return retour;
    }
    int charToInt(char c) { return c - '0'; }
    boolean isSDigit(int c) { return c >= 0 && c <= 9; }

//-----plateau-----
    final int NB_CASES = 20;
    int[] indices_cases(String plateau) { // Récupère indices des nombres dans le string de plateau
        int[] resultat = new int[NB_CASES];
        int cptCases = 0;
        for (int i = 0; i < length(plateau) - 1; i++) {
            final int actuel = charToInt(charAt(plateau, i));
            final int prochain = charToInt(charAt(plateau, i + 1));
            final boolean nb_un_chiffre = actuel == 0;
            final boolean nb_deux_chiffres = isSDigit(actuel) && isSDigit(prochain);

            if (nb_un_chiffre || nb_deux_chiffres) {
                resultat[(nb_un_chiffre ? prochain : (actuel*10) + prochain)] = i;
                cptCases++; i++;
            }
        }
        assertEquals(NB_CASES, cptCases); // Si c'est false soit il y a un problème, soit la fonction est utilisée après qu'on aie mis les cases bonus (on doit pas faire ça)
        return resultat;
    }

    String remplacer(String plateau, int _case, String _contenu) {
        final int[] indices = indices_cases(plateau);
        return substring(plateau, 0, indices[_case])
                + _contenu
                + substring(plateau, indices[_case]+2, length(plateau));
    }
    // Exemple pour remplacer une case avec un bonus: println(remplacer(fichierTexte("ascii/plateau.txt"), 19, "B "));

    String bonusAléatoires(String plateau) {
        final int[] indices = indices_cases(plateau);
        for (int i = 0; i < 3; i++) {
            remplacer(plateau, (int) (random() * 19), "B ");
        }
        return plateau;
    }

//-----fonctions aide-----

    void lireFichier(String cheminFichier) {
        println(fichierTexte(cheminFichier));
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
        int saisie;
        do {
            println(fichierTexte("ascii/PoursuiteNonTriviale.txt"));
            println("1.Jouer\n2.Aide\n3.Option\n4.Quitter");
            saisie = choixNombre(1, 4);
            if (saisie == 1) jouer();
            if (saisie == 2) aide();
            // if (saisie == 3) options();
        } while (saisie != 4);
    }

//-----algo principal-----

    void algorithm() { afficherMenu(); }

    int lancerDés() {
        final String dés = "⚀⚁⚂⚃⚄⚅";
        println("Lancer de dés:\n");

        int dé = (int) (random() * 5);
        println(
            "+---+\n" +
            "| " + charAt(dés, dé) + " |\n" +
            "+---+"
        );

        dé++;
        println("Vous avancez de "+dé+".");
        return dé;
    }

    void barreDeVie(int PV, int PV_MAX) {
        print("[");
        int i = 0;
        for (; i < PV; i += 10) {
            print("█");
        }
        for (; i < PV_MAX; i += 10) {
            print("▒");
        }
        println("]");
    }

    void jouer() {
        boolean fini = false;
        int PV = 100;
        final String plateauOG = fichierTexte("ascii/plateau.txt");
        final int[] indices = indices_cases(plateauOG);
        String plateau = plateauOG; //bonusAléatoires(plateauOG);

        int _case = 0;
        while (!fini) {
            _case += lancerDés();
            if (PV <= 0 || _case > 19) {
                fini = true;
            } else {
                char carac = charAt(plateau, indices[_case]);
                if (isSDigit(charToInt(carac))) {
                    // TODO Gérer le combat
                }
                //else if (carac == 'B') {
                //... TODO Gérer les cases bonus
                //}
            }
        }
    }
}