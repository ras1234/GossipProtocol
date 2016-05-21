package gossip;
import java.rmi.Remote;

public interface GossipmsgInterface extends Remote
{
	void hearGossip(byte[] gossiparray) throws Exception;
}