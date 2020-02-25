package tse.info;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import redis.clients.jedis.Jedis;
/**
 * Hello world!
 *
 */
public class App 
{
	static HashMap<UUID, Call> calls = new HashMap<UUID, Call>();
	static HashMap<String, Operator> operators = new HashMap<String, Operator>();
	
    public static void main( String[] args )
    {
        Init();
        menu();
    }
    
    private static void Init()
    {
    	redisConnection.getCalls();
    	redisConnection.getOperators();
    }
    
    public static void menu() {
		boolean exit = false;
		Scanner sc = new Scanner(System.in);

		do {
			System.out.println("Que souhaitez vous faire ? \n (1) Afficher les appel en cours de traitement ? \n (2) Afficher les appel à affecter ? \n (3) Generer un nouvel appel  \n (4) Ajouter un nouvel opérateur \n (5) Passer au menu opérateur \n (6) Quitter");
			int choix = sc.nextInt();
			switch (choix) {
			case 1:
				Set<String> calls=redisConnection.getOnGoingCalls();
				for (String call: calls)
				{
					System.out.println(redisConnection.getCall(UUID.fromString(call))+ "\n");
				}
				break;
			case 2:
				Set<String> calls1=redisConnection.getWaitingCalls();
				for (String call: calls1)
				{
					System.out.println(redisConnection.getCall(UUID.fromString(call))+ "\n");
				}
				break;
			case 3:
				System.out.println("Entrez le numéro de téléphone");
				Call call = new Call(sc.next());
				App.calls.put(call.getId(), call);
				break;
			case 4:
				System.out.println("Entrez un identifiant");
				String input = sc.next();
				if (redisConnection.getOperator(input)!= null)
				{
					System.out.println("Un opérateur avec cet identifiant existe déjà");
					System.out.println(redisConnection.getOperator(input));
					break;
				}
				String id = input;
				System.out.println("Entrez le nom de l'opérateur");
				String nom = sc.next();
				System.out.println("Entrez le prenom de l'opérateur");
				String prenom = sc.next();
				App.operators.put(id, new Operator(id, nom, prenom));
				break;
			case 5:
				System.out.println("Entrez l'identifiant de l'opérateur");
				Operator operator = redisConnection.getOperator(sc.next());
				while (operator==null)
				{
					System.out.println("l'opérateur n'existe pas, entrez l'identifiant d'un opérateur existant");
					operator = redisConnection.getOperator(sc.next());
				}
				operatorMenu(operator);
				break;
			case 6:
				exit = true;
				break;
			default:
				break;
			}
		} while (!exit);

    }
    
    static public void operatorMenu(Operator operator)
    {
    	System.out.println("Bonjour " + operator.getPrenom() +" " + operator.getNom());
    	boolean exit = false;
		Scanner sc = new Scanner(System.in);
		
    	while(!exit)
    	{
	    	while (operator.getCall()!= null)
	    	{
	
	    		System.out.println("vous etes en appel");
	    		System.out.println(operator.getCall());
	    		System.out.println("Raccrocher ? (y/n)");
	    		if (sc.next().equals("y"))
	    		{
	    			System.out.println("Entrez une description");
	    			String str = sc.next();
	    			operator.getCall().setDescription(str + sc.nextLine());
	    			System.out.println(operator.getCall().getDescription());
	    			operator.endCall();
	    		}
	    	}
	    	
	    	System.out.println("Que souhaitez vous faire ? \n (1) voir la liste des appels en attente \n (2) prendre un appel (3) quitter");
	    	int selection = sc.nextInt();
	    	switch(selection)
	    	{
	    	case 1:
	    		Set<String> calls1=redisConnection.getWaitingCalls();
				for (String call: calls1)
				{
					System.out.println(redisConnection.getCall(UUID.fromString(call))+ "\n");
				}
	    		break;
	    	case 2:
	    		operator.takeCall();
	    		break;
	    	case 3:
	    		exit = true;
	    		break;
	    	default:
	    		break;
	    	}
	    }
    	return;
    }
}
