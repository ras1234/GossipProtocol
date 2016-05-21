package gossip;
import java.io.BufferedReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.util.Random;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.rmi.Naming;

import gossip.HearGossipProto.hearGossipRequest;

public class Server implements  GossipmsgInterface
{
	static int myvectorclock[] = new int[10000];
	public static void processGossip(String gossipmessg, int clocktick, int totalprocess, int processId,int EventGenerator) throws Exception
	{
		int server1, server2;	
		Random randomGenerator = new Random();
        server1 = randomGenerator.nextInt(totalprocess);
        server2 = randomGenerator.nextInt(totalprocess);
        while(server1==processId)
        		server1 = randomGenerator.nextInt(totalprocess);
        while(server2==processId)
        		server2 = randomGenerator.nextInt(totalprocess);
        GossipmsgInterface obj1 =(GossipmsgInterface)Naming.lookup("Gossip"+server1);
        hearGossipRequest heargoss1=HearGossipProto.hearGossipRequest.newBuilder()
				.setMsg(gossipmessg)
				.setClk(clocktick)
				.setTotalprocess(totalprocess)
				.setIDofprocess(processId)
				.setServerno(server1)
				.setEventGenerator(EventGenerator).build();
    	byte[] heararr1=heargoss1.toByteArray();
		GossipmsgInterface obj2 =(GossipmsgInterface)Naming.lookup("Gossip"+server2);
		hearGossipRequest heargoss2=HearGossipProto.hearGossipRequest.newBuilder()
				.setMsg(gossipmessg)
				.setClk(clocktick)
				.setTotalprocess(totalprocess)
				.setIDofprocess(processId)
				.setServerno(server2)
				.setEventGenerator(EventGenerator).build();
		byte[] heararr2=heargoss2.toByteArray();
		obj1.hearGossip(heararr1);
		obj2.hearGossip(heararr2);
	}
	
	public static void main(String args[]) throws Exception
	{
		int totalprocess, processId;
		Server obj=new Server();
		Registry registry;
		GossipmsgInterface stub=(GossipmsgInterface)UnicastRemoteObject.exportObject(obj,0);
		try{
			registry=LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		}catch(Exception e){
			registry=LocateRegistry.getRegistry("127.0.0.1",Registry.REGISTRY_PORT);
		}
		registry.bind("Gossip"+args[0],stub);
		System.out.println("Server "+ args[0] +" started");
		Thread.sleep(20000);	
		processId = Integer.parseInt(args[0]);	
		totalprocess = Integer.parseInt(args[1]);	
		for(int i=0;i<totalprocess;i++)
		{
			myvectorclock[i]=0;
		}
		String new_line;	
		if(args.length>2)
		{
			String Inputfilename = args[3];
			Server gs=new Server();
			int EventGenerator=Integer.parseInt(args[0]);	
			if(args[2].equals("-i"))
			{
                BufferedReader br = new BufferedReader(new FileReader(Inputfilename));
                String new_filename = Inputfilename+"_"+processId;
                File file = new File(new_filename);
                PrintWriter pw = new PrintWriter(file);
                while((new_line = br.readLine())!= null)
                {
                	pw.write(args[0]);
                	pw.write(":");
                	pw.write(new_line.toString());
                	pw.write("\n");
                }
                pw.close();
                br.close();
                BufferedReader br2 = new BufferedReader(new FileReader(new_filename));
                while((new_line = br2.readLine())!= null)
                {
                	int counter=myvectorclock[processId];
                    counter++;
                    myvectorclock[processId]= counter;
                	processGossip(new_line, myvectorclock[processId], totalprocess, processId,EventGenerator);
                }
                br2.close();
            }
		}
	}

	@Override
	public void hearGossip(byte[] gossiparray) throws Exception {
		hearGossipRequest hear= hearGossipRequest.parseFrom(gossiparray);
		 String message=hear.getMsg();
		 int clocktick=hear.getClk();
		 int processId=hear.getIDofprocess();
		 int totalprocess=hear.getTotalprocess();
		 int serverno=hear.getServerno();
		 int EventGenerator=hear.getEventGenerator();
		 if(EventGenerator==serverno)
		 {
		 }
		 else if(( Server.myvectorclock[EventGenerator]<clocktick))
		 {
		   System.out.println("Accept"+" "+message);
		   Server.myvectorclock[EventGenerator]= clocktick;
		   Server.processGossip(message, clocktick, totalprocess, serverno,EventGenerator);
		
		 }
		 else
		 {   
			 System.out.println("Reject"+" "+message);
		 }
		
	}
}
