db.getCollection('joueur').find({poste : 'defenseur', date_de_naissance : {$gt : new Date((new Date())-1000*60*60*24*365*25)}})
