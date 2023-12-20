public class DataServer {
	public String Name;
	public String ip;
	public int port;
	public boolean isOpen;
	public int connectAccountCount;

	public DataServer(String name, String ip, int port) {
		this.Name = "";
		this.ip = ip;
		this.port = port;
		this.isOpen = false;
		this.connectAccountCount = 0;
	}

	public DataServer(String Name, String ip, int port, boolean isOpen, int connectAccountCount) {
		this.Name = Name;
		this.ip = ip;
		this.port = port;
		this.isOpen = isOpen;
		this.connectAccountCount = connectAccountCount;
	}

}