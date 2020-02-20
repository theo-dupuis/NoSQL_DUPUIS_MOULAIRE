db.scores.drop();
db.createCollection('scores');

// Création, pour chaque joueur, de sa liste de scores venant de tous ses matchs
db.match.find().forEach(function(m){
    
   // Pour chaque joueur de l'équipe à domicile du match
   m.joueurs_domicile.forEach(function(j){
    
      // Si le joueur ets déjà dans la collection "scores"
      if(db.scores.findOne({joueur_id : j.joueur_id}) != null){
          db.scores.updateOne({joueur_id : j.joueur_id}, {$push : {joueur_scores : j.joueur_note}});
      }else{
          // Sinon, on l'y ajoute
          db.scores.insertOne({
             joueur_id : j.joueur_id,
             joueur_scores : [j.joueur_note]
          });
      }
    
   });
   
   // Pour chaque joueur de l'équipe extérieure du match
   m.joueurs_exterieur.forEach(function(j){
    
      // Si le joueur ets déjà dans la collection "scores"
      if(db.scores.findOne({joueur_id : j.joueur_id}) != null){ 
          db.scores.updateOne({joueur_id : j.joueur_id}, {$push : {joueur_scores : j.joueur_note}});
      }else{
          // Sinon, on l'y ajoute
          db.scores.insertOne({
             joueur_id : j.joueur_id,
             joueur_scores : [j.joueur_note]
          });
      }
   
   });
   
});

// Calcul de la moyenne des scores de tout le monde
db.scores.aggregate([
   {
     $addFields: {
       moyenne_scores: { $avg: "$joueur_scores" }
     }
   }
]).forEach(function(x){
  db.scores.save(x);  
});
