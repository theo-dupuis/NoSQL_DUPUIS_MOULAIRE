db.match.insert({
    equipe_domicile_id : db.equipe.findOne({ nom: 'TSE Football Club'})._id,
    equipe_exterieur_id : db.equipe.findOne({ nom: 'ASSE'})._id,
    competition : 'match amical',
    score_domicile : NumberInt(0),
    score_exterieur : NumberInt(5),
    joueurs_domicile : [],
    joueurs_exterieur : []
});
