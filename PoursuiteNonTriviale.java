import extensions.File;
import extensions.CSVFile;

class PoursuiteNonTriviale extends Program {
    int choixNombre(int min, int max) {
        int saisie;
        do {
            print("Veuillez choisir un nombre entre "+min+" et "+max+" : ");
            saisie = charToInt(readChar()); // Utilisé au lieu de readInt pour ne pas crash si l'utilisateur fait une erreur
        } while (saisie < min || saisie > max);
        return saisie;
    }
    String fichierTexte(String cheminFichier) { // Produit un String avec le contenu du fichier
        File fichier = newFile(cheminFichier);
        String retour = "";
        while (ready(fichier))
            retour += readLine(fichier) + "\n";
        return retour;
    }
    void test_fichierTexte(){
        assertEquals("c",fichierTexte("assets/test.txt"));
    }
    int charToInt(char c) { return c - '0'; }
    boolean isSDigit(int c) { return c >= 0 && c <= 9; }
    int strToInt(String str) {
        final int taille = length(str);
        int rang = 1;
        int resultat = 0;
        for (int i = taille - 1; i >= 0; i--) {
            resultat += charToInt(charAt(str, i)) * rang;
            rang = rang * 10;
        }
        return resultat;
    }

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
        for (int i = 0; i < 3; i++)
            plateau = remplacer(plateau, (int) (random() * 19), "B ");
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
    boolean bonus = false;
    int typeQuestions = 1;

    void options() {
        print("1.difficulté : "+diff+"\n2.cases bonus : "+ (bonus ? "Oui" : "Non") +"\n3.types de questions : "+ texteQuestions(typeQuestions));
        int choix = choixNombre(1,3);
        if (choix==1) {
            diff = choixNombre(1,5);
        } else if (choix==2) {
            bonus = !bonus;
        }else {
            println("1 : Tous les types de questions\n2 : Seulement des questions de Maths\n3 : Seulement des questions de français");
            typeQuestions = choixNombre(1,3);
        }
    }

    String texteQuestions(int question) {
        return new String[]{"Toutes", "Maths", "Français"}[question - 1];
    }

//-----fonctions Menu-----

    void afficherMenu() {
        int saisie;
        do {
            println(fichierTexte("assets/ascii/PoursuiteNonTriviale.txt"));
            println("1.Jouer\n2.Aide\n3.Option\n4.Quitter");
            saisie = choixNombre(1, 4);
            if (saisie == 1) jouer();
            else if (saisie == 2) aide();
            else if (saisie == 3) options();
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
        while (i < PV_MAX) {
            if (i < PV)
                print("█");
            else
                print("▒");
            i += 10;
        }
        println("] " + PV + "/" + PV_MAX);
    }

    // TODO Mettre de meilleures questions
    CSVFile questions = loadCSV("assets/csv/questions.csv");
    CSVFile monstres = loadCSV("assets/csv/monstres.csv");

    int PV = 100;
    void jouer() {
        boolean fini = false;
        final String plateauOG = fichierTexte("assets/ascii/plateau.txt");
        final int[] indices = indices_cases(plateauOG);
        String plateau = plateauOG;
        if (bonus) {
            plateau = bonusAléatoires(plateauOG);
        }

        int _case = 0;
        boolean fuite = false;
        while (!fini) {
            if (!fuite)
                _case += lancerDés();
            else
                if (_case < 0) PV = -1;
                fuite = false;
            if (PV <= 0 || _case > 19) {
                fini = true;
            } else {
                println(remplacer(plateau, _case, "XX"));
                sleep(1750);
                final char carac = charAt(plateau, indices[_case]);
                if (isSDigit(charToInt(carac))) {
                    boolean gagnéCombat = combat();
                    if (gagnéCombat){
                        println("Gagné!");
                        // TODO Gérer le gain d'un objet etc
                    } else {
                        if (PV <= 0) {
                            println("Perdu! Vous êtes mort.");
                            fini = true;
                        } else {
                            fuite = true;
                            println("Vous êtes renvoyé 2 cases en arrière.");
                            _case -= 2;
                        }
                    }
                }
                else if (carac == 'B') {
                    println("Case bonus! Vous ne combattez pas ce tour.");
                    //... TODO Gérer d'autres effets de case bonus
                }
            }
        }
        println("Vous avez " + (_case > 19 ? "gagné" : "perdu") + " la partie.");
        PV = 100;
    }

    boolean combat() {
        final String[] donnéesMonstre = liste_aléatoire(monstres);
        final String monstre = fichierTexte("assets/ascii/" + donnéesMonstre[0] + ".txt");
        int PV_Monstre = strToInt(donnéesMonstre[1]);
        final int PV_MaxMstr = PV_Monstre;

        println(fichierTexte("assets/ascii/approche_figure.txt"));
        while (PV > 0 && PV_Monstre > 0) {
            println(monstre);
            barreDeVie(PV_Monstre, PV_MaxMstr);
            println(fichierTexte("assets/ascii/menu_combat.txt"));
            print("\n\n\n");

            int choix = choixNombre(1, 4);
            if (choix == 1) {
                final String[] question = liste_aléatoire(questions);
                println(question[0]);
                print("--> ");
                final String réponse = readString();
                // TODO Varier les dégats pris/reçus
                if (equals(réponse, question[1])) {
                    println("Bonne réponse! Le monstre perd 33PV");
                    PV_Monstre -= 33;
                } else {
                    println("Perdu! Vous perdez 33PV");
                    PV -= 33;
                    barreDeVie(PV, 100);
                }
                sleep(750);
            } else if (choix == 2) {
                action();
            } else if (choix == 3) {
                objet();
            } else {
                return false;
            }
        }
        return PV_Monstre <= 0;
    }

    String[] liste_aléatoire(CSVFile csv) {
        final int r = (int)random(0, rowCount(csv) - 1);
        final int taille = columnCount(csv);
        String[] contenus = new String[taille];
        for (int i = 0; i < taille; ++i) {
            contenus[i] = getCell(csv, r, i);
        }
        return contenus;
    }

    void action() {
        println("1.Regarder les statistiques");
        int choix = choixNombre(1,1);
        if (choix == 1){
            println("Points de vie de monstre : "+PV);
        }
    }

    void objet() {
        println("Il n'y a rien ici !");
    }
}