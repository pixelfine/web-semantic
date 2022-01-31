Aller sur : https://dbpedia.org/sparql

Vérifier les paramètres : 
	Default Data Set Name (Graph IRI) :	
		http://dbpedia.org
	Query Text :
		DESCRIBE ?album {?album a dbo:Album} LIMIT 10
	Results Format :
		Turtle

Lancez Execute Query.
	Vous obtiendrez un fichier contenant le graphe de données. (nous l'avons renommé :artist.ttl)

Nous pouvons maintenant lancer le programme sur : 
	-r artist.ttl -s artist_schema.ttl -q selectQuery.sparql