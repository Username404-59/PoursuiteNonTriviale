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
    int difficulté = 1;
    boolean bonus = false;
    final String[] typesQuestions = new String[]{"Toutes", "Maths", "Culture générale"};
    int typeSélectionné = 0;

    void options() {
        println("1.difficulté : "+ difficulté +"\n2.cases bonus : "+ (bonus ? "Oui" : "Non") +"\n3.types de questions : "+ typesQuestions[typeSélectionné]+ "\n4.retour");
        int choix = choixNombre(1,4);
        if (choix==1) {
            difficulté = choixNombre(1,3);
        } else if (choix==2) {
            bonus = !bonus;
        } else if (choix==3){
            println("1 : Tous les types de questions\n2 : Seulement des questions de Maths\n3 : Seulement des questions de culture générale");
            typeSélectionné = choixNombre(1,3) - 1;
        }
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

    CSVFile questions = loadCSV("assets/csv/questions.csv");
    CSVFile monstres = loadCSV("assets/csv/monstres.csv");
    CSVFile objets = loadCSV("assets/csv/objets.csv");

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
                    final boolean gagnéCombat = combat();
                    if (gagnéCombat){
                        println("Gagné!");
                        if (((int) random(1, 3)) == 3) { // Une chance sur 3 d'avoir un objet en gagnant
                            gainObjet();
                        }
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
                    gainObjet();
                    sleep(1500);
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
        final int ATQ_Monstre = strToInt(donnéesMonstre[2]) * difficulté;
        int ATQ_Joueur = 33;

        println(fichierTexte("assets/ascii/approche_figure.txt"));
        while (PV > 0 && PV_Monstre > 0) {
            println(monstre);
            barreDeVie(PV_Monstre, PV_MaxMstr);
            println(fichierTexte("assets/ascii/menu_combat.txt"));
            print("\n\n\n");

            int choix = choixNombre(1, 4);
            if (choix == 1) {
                final String[] question = (typeSélectionné == 0 ? liste_aléatoire(questions) : question_aléatoire_type(questions, typeSélectionné));
                println(question[0]);
                print("--> ");
                final String réponse = readString();
                if (equals(réponse, question[1])) {
                    println("Bonne réponse! Le monstre perd "+ATQ_Joueur+"PV");
                    PV_Monstre -= ATQ_Joueur;
                } else {
                    println("Perdu! Vous perdez "+ATQ_Monstre+"PV");
                    PV -= ATQ_Monstre;
                    barreDeVie(PV, 100); // Note: il n'y a pas vraiment de PVs max pour le joueur, 100 est simplement la valeur de base
                }
                sleep(750);
            } else if (choix == 2) {
                action(PV_Monstre, PV_MaxMstr, ATQ_Monstre);
            } else if (choix == 3) {
                if (indice_inventaire == 0) {
                    println("Vous n'avez pas d'objets!");
                    sleep(2000);
                } else {
                    for (int i = 0; i < indice_inventaire; i++) {
                        final String nom_objet = inventaire[i][0];
                        final String stat_objet = inventaire[i][1];
                        println((i+1)+". "+nom_objet+" (+"+stat_objet+")");
                    }
                    println(indice_inventaire+1+". retour");
                    final int choixObjet = choixNombre(1, indice_inventaire+2);
                    if (choixObjet < indice_inventaire+1) {
                        final String[] selection = inventaire[indice_inventaire-1];
                        final int longueur = length(selection[1]);
                        final int valeur = strToInt(substring(selection[1], 0, longueur-3));
                        final String suffixe = substring(selection[1], longueur-3, longueur);
                        if (equals(suffixe, "PVS")) {
                            PV += valeur;
                        } else if (equals(suffixe, "ATQ")) {
                            ATQ_Joueur += valeur;
                        };
                        println("Vous gagnez +"+selection[1]+" !");
                        inventaire[indice_inventaire-1] = null; indice_inventaire--; // On retire l'objet de l'inventaire
                        sleep(3000);
                    };
                }
            } else {
                return false; // Fuite -> combat perdu
            }
        }
        return PV_Monstre <= 0; // Issue du combat
    }

//-----Fonctions de sélection dans les fichiers CSV-----
    String[] liste(CSVFile csv, int r) {
        final int taille = columnCount(csv);
        String[] contenus = new String[taille];
        for (int i = 0; i < taille; ++i) {
            contenus[i] = getCell(csv, r, i);
        }
        return contenus;
    }

    int random_csv(CSVFile csv) {
        return (int) random(0, rowCount(csv) - 1);
    }
    String[] liste_aléatoire(CSVFile csv) {
        return liste(csv, random_csv(csv));
    }
    String[] question_aléatoire_type(CSVFile csv, int typeQuestion) {
        int r;
        do {
            r = random_csv(csv);
        } while (strToInt(getCell(csv, r, 2)) != typeQuestion);
        return liste(csv, r);
    }

    void action(int p, int pmax, int atq) {
        println("1.Regarder les statistiques\n2.retour");
        int choix = choixNombre(1,2);
        if (choix == 1){
            println("Points de vie actuels du monstre : "+p);
            println("Points de vie maximum du monstre : "+pmax);
            println("Points d'attaque du monstre : "+atq);
            sleep(3000);
        }
    }

    String[][] inventaire = new String[2][20]; // Il y a 20 cases, l'inventaire ne devrait pas excéder 20 objets
    int indice_inventaire = 0; // Indice du dernier objet + 1
    void gainObjet() {
        inventaire[indice_inventaire] = liste_aléatoire(objets);
        println("Vous gagnez un objet: "+inventaire[indice_inventaire][0]+".");
        indice_inventaire++;
    }

//----- Tests -----
    void test_fichierTexte() {
        assertEquals("c\n",fichierTexte("assets/test.txt"));
    }

    void test_isSDigit() {
        assertEquals(true, isSDigit(9));
        assertEquals(false, isSDigit(99));
    }

    void test_charToInt() {
        assertEquals(9, charToInt('9'));
    }

    void test_liste() {
        final String[] gaster = new String[] {"gaster","133","33"};
        final String[] liste_csv = liste(monstres, 0);
        for (int i = 0; i < length(liste_csv); i++) {
            assertEquals(gaster[i], liste_csv[i]);
        }
    }
}