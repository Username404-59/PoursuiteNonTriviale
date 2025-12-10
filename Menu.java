import extensions.File;
class Menu extends Program{

//-----fonctions aide-----

    void lireFichier(String cheminFichier){
        File fichier = newFile(cheminFichier);
        while (ready(fichier)) {
            println(readLine(fichier));
        }
        String saisie = " ";
        do{
            saisie=readString();
        }while(!equals(saisie,""));
        aide();
    }

    void aide(){
        println("1.Cases\n2.Combat\n3.Bonus\n4.retour");
        int saisie;
        do{
            print("Veuillez choisir un nombre entre 1 et 4 : ");
            saisie = readInt();
        }while(saisie<1 || saisie>4);
        if(saisie==1){
            lireFichier("aide/aide cases");
        }else if(saisie==2){
            lireFichier("aide/aide combat");
        }else if(saisie==3){
            lireFichier("aide/aide bonus");
        }else{
            afficherMenu();
        }
    }

//-----fonctions Menu-----

    void afficherMenu(){
        println("1.Jouer\n2.Aide\n3.Option");
        int saisie;
        do{
            print("Veuillez choisir un nombre entre 1 et 3 : ");
            saisie = readInt();
        }while(saisie<1 || saisie>3);
        if (saisie==2){
            aide();
        }
    }

//-----algo principal-----

    void algorithm(){
        afficherMenu();
    }
}
