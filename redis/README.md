Ce projet est un projet Java utilisant redis pour gérer un call center.

Pour lancer le server, aller dans le dossier data et lancer redis-server.

Le projet java a besoin de la dépendance Jedis:
<dependency>
  <groupId>redis.clients</groupId>
  <artifactId>jedis</artifactId>
  <version>2.0.0</version>
</dependency>


# La Base de données:

les données des appels sont dans des hset commencant par **call:** suivi de l'id de l'appel (un UUID)

les données des operateurs sont dans des hset commencant par **operator:** suivi de l'id de l'operateur

les appels non affectés sont dans un zset appelé **waiting_calls**, trié par ordre chronologique (les plus ancien en premier)

les appels en cours sont dans un set non trié **on_going_calls**

on peut également retrouvé un appel par son operateur. **operator_call:** suivi de l'id de l'operrateur
