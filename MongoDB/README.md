Théo DUPUIS
Yann MOULAIRE

# Projet MongoDB

Pour commencer à utiliser les différents scripts, il faut d'abord importer la base via les trois fichiers JSON (joueur.json, equipe.json et match.json). Pour cela, il suffit d'entrer les commandes suivantes dans le dossier contenant les fichiers JSON :

mongoimport --collection=joueur --db=TY --file=joueur.json
mongoimport --collection=equipe --db=TY --file=equipe.json
mongoimport --collection=match --db=TY --file=match.json

Vous vous trouverez avec une nouvelle base de donnée "TY" contenant les 3 Collections "joueur", "equipe" et "match".
