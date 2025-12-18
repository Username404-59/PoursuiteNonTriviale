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
    // Exemple pour remplacer une case avec un bonus: println(remplacer(fichierTexte("assets/ascii/plateau.txt"), 19, "B "));

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

        final String pathCommun = "assets/aide/aide ";
        final String[] fichiers = new String[] {"cases", "combat", "bonus"};

        final int saisie = choixNombre(1, 4);
        if (saisie != 4) {
            lireFichier(pathCommun + fichiers[saisie - 1]);
        } else {
            afficherMenu();
        }
    }

//-----fonctions Options--
    // TODO stocker les options dans un fichier, pas dans des variables
    int diff = 1;
    boolean Bonus = true;
    int Questions = 1;

    void options() {
        print("1.difficulté : "+diff+"\n2.cases bonus : "+ (Bonus ? "Oui" : "Non") +"\n3.types de questions : "+TexteQuestions(Questions));
        int choix = choixNombre(1,3);
        if (choix==1) {
            diff = choixNombre(1,5);
        } else if (choix==2) {
            Bonus = !Bonus;
        }else {
            println("1 : Tous les types de questions\n2 : Seulement des questions de Maths\n3 : Seulement des questions de français");
            Questions=choixNombre(1,3);
        }
    }

    String TexteQuestions(int Questions){
        return new String[]{"Toutes", "Maths", "Français"}[Questions - 1];
    }

//-----fonctions Menu-----

    void afficherMenu() {
        int saisie;
        do {
            println(fichierTexte("assets/ascii/PoursuiteNonTriviale.txt"));
            println("1.Jouer\n2.Aide\n3.Option\n4.Quitter");
            saisie = choixNombre(1, 4);
            if (saisie == 1) jouer();
            if (saisie == 2) aide();
            if (saisie == 3) options();
        } while (saisie != 4);
    }

//-----algo principal-----

    void algorithm() { afficherMenu(); }

    int lancerDés() {
        final String dés = "⚀⚁⚂⚃⚄⚅";
        println("Lancer de dés:\n");
        sleep(2000);

        int dé = (int) (random() * 5);
        println(
            "+---+\n" +
            "| " + charAt(dés, dé) + " |\n" +
            "+---+"
        );

        dé++;
        sleep(1500);
        println("Vous avancez de "+dé+".");
        sleep(1500);
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

    String[][] questions = new String[][]{
        {"Quel petit pays se situe entre la Suisse et l'Autriche?", "Liechenstein"},
        {"3*12", "36"},
        {"", ""},
    };

    int PV = 100;
    void jouer() {
        boolean fini = false;
        int score = 0;
        final String plateauOG = fichierTexte("assets/ascii/plateau.txt");
        final int[] indices = indices_cases(plateauOG);
        String plateau = plateauOG;
        if (Bonus) {
            plateau = bonusAléatoires(plateauOG);
        }

        int _case = 0;
        while (!fini) {
            _case += lancerDés();
            if (PV <= 0 || _case > 19) {
                fini = true;
            } else {
                final char carac = charAt(plateau, indices[_case]);
                if (isSDigit(charToInt(carac))) {
                    boolean gagnéCombat = combat();
                    if (gagnéCombat){
                        score+=1; // TODO changer par le gain d'un objet
                    } else {
                        _case-=2;
                    }
                }
                //else if (carac == 'B') {
                //... TODO Gérer les cases bonus
                //}
            }
        }
        PV = 100;
    }

    boolean combat() {
        int PV_Monstre = 100;
        final int PV_MaxMstr = PV_Monstre;

        println(fichierTexte("assets/ascii/approche_monstre.txt"));
        while (PV > 0 && PV_Monstre > 0) {
            println(fichierTexte(monstreAleatoire()));
            barreDeVie(PV_Monstre, PV_MaxMstr);
            println(fichierTexte("assets/ascii/menu_combat.txt"));
            print("\n\n\n");

            int choix = choixNombre(1, 4);
            if (choix == 1) {
                println(questions[0][0]);
                print("-->");
                String réponse = readString();
                if (equals(réponse, questions[0][1])) {
                    PV_Monstre -= 33;
                } else {
                    PV -= 33;
                }
            } else if (choix==2) {
                action();
            } else if (choix==3) {
                objet();
            } else {
                return false; // La fuite fait comme si on avait perdu pour l'instant
            }
        }
        return PV_Monstre <= 0;
    }

    String monstreAleatoire() {
        int monstre = (int)random();
        String[] monstres = new String[]{
            "assets/ascii/gaster.txt",
            "assets/ascii/wolf.txt"
        };
        return monstres[monstre];
    }

    void action() {
        println("1.Regarder les statistiques");
        int choix = choixNombre(1,1);
        if (choix==1){
            println("Points de vie de monstre : "+PV);
        }
    }

    void objet() {
        println("Il n'y a rien ici !");
    }
}