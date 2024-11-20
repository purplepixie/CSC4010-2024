import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Node
{
    // Variables
    public int port = 5000;
    public KeyboardListener keyboard;
    public NetworkListener network;
    public List<RemoteNode> nodes;

    // Inner Thread Classes
    public class NetworkListener extends Thread
    {
        private Node node = null;
        public NetworkListener(Node n)
        {
            node = n;
        }
        public void run()
        {
            System.out.println("Starting network listening on port: "+node.port);
            try
            {
                DatagramSocket socket = new DatagramSocket(node.port);
                DatagramPacket packet;
                while(true)
                {
                    byte[] receive = new byte[65535];
                    packet = new DatagramPacket(receive, receive.length);
                    socket.receive(packet);
                    String remoteip = packet.getAddress().toString().substring(1);
                    System.out.println("Network Rx from "+remoteip);
                    String data = ByteToString(receive);
                    node.network(data, remoteip);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        public String ByteToString(byte[] a)
        {
            if (a==null) return null;
            StringBuilder ret = new StringBuilder();
            int i=0;
            while(a[i]!=0)
            {
                ret.append((char) a[i]);
                i++;
            }
            return ret.toString();
        }
    }

    public class KeyboardListener extends Thread
    {
        private Node node = null;
        public KeyboardListener(Node n)
        {
            node = n;
        }
        public void run()
        {
            System.out.println("Starting keyboard listening");
            Scanner sc = new Scanner(System.in);
            while(true)
            {
                String line = sc.nextLine();
                node.keyboard(line);
            }
        }
    }

    // Inner Data Classes
    public class RemoteNode
    {
        public String ip;
        public int port;
    }

    // Methods
    public static void main(String[] args)
    {
        Node n = new Node();
        if (args.length>0) n.port = Integer.parseInt(args[0]);
        n.start();
    }

    public void start()
    {
        nodes = new ArrayList<RemoteNode>();
        network = new NetworkListener(this);
        network.start();
        keyboard = new KeyboardListener(this);
        keyboard.start();
    }

    public void keyboard(String line)
    {
        System.out.println("K> "+line);
        String[] cmd = line.split(" ");
        if (cmd[0].equalsIgnoreCase("send"))
        {
            if (cmd.length != 4) System.out.println("Usage: send remoteip remoteport data");
            else
            {
                String remoteip = cmd[1];
                int remoteport = Integer.parseInt(cmd[2]);
                String data = cmd[3];
                System.out.println("Sending "+data+" to "+remoteip+":"+remoteport);
                send(data, remoteip, remoteport);
            }
        }
        else if (cmd[0].equalsIgnoreCase("ping"))
        {
            if (cmd.length != 3) System.out.println("Usage: ping remoteip remoteport");
            else
            {
                String remoteip = cmd[1];
                int remoteport = Integer.parseInt(cmd[2]);
                String data = "PING "+port;
                System.out.println("Sending "+data+" to "+remoteip+":"+remoteport);
                send(data, remoteip, remoteport);
            }
        }
        else if (cmd[0].equalsIgnoreCase("add"))
        {
            if (cmd.length != 3) System.out.println("Usage: add remoteip remoteport");
            else
            {
                String remoteip = cmd[1];
                int remoteport = Integer.parseInt(cmd[2]);
                addNode(remoteip, remoteport);
            }
        }
        else if (cmd[0].equalsIgnoreCase("fetch"))
        {
            if (cmd.length != 3) System.out.println("Usage: fetch remoteip remoteport");
            else
            {
                String remoteip = cmd[1];
                int remoteport = Integer.parseInt(cmd[2]);
                String data = "FETCH "+port;
                System.out.println("Sending "+data+" to "+remoteip+":"+remoteport);
                send(data, remoteip, remoteport);
            }
        }
        else if (cmd[0].equalsIgnoreCase("list"))
        {
            for(RemoteNode n: nodes)
            {
                System.out.println(n.ip+":"+n.port);
            }
        }
        else if (cmd[0].equalsIgnoreCase("pingall"))
        {
            for(RemoteNode n: nodes)
            {
                String data = "PING "+port;
                System.out.println("Sending "+data+" to "+n.ip+":"+n.port);
                send(data, n.ip, n.port);
            }
        }
        else
        {
            System.out.println("Unknown Command");
        }
    }

    public void network(String data, String remoteip)
    {
        System.out.println("N> "+data+" [from "+remoteip+"]");
        String[] parts = data.split(" ");
        if (parts[0].equalsIgnoreCase("PING"))
        {
            System.out.println("PING received!");
            int remoteport = Integer.parseInt(parts[1]);
            send("PONG "+port,remoteip,remoteport);
            addNode(remoteip, remoteport);
        }
        if (parts[0].equalsIgnoreCase("PONG"))
        {
            System.out.println("PONG received!");
            int remoteport = Integer.parseInt(parts[1]);
            addNode(remoteip, remoteport);
        }
        if (parts[0].equalsIgnoreCase("FETCH"))
        {
            int remoteport = Integer.parseInt(parts[1]);
            System.out.println("FETCH received from "+remoteip+":"+remoteport);
            if(addNode(remoteip, remoteport))
            {
                System.out.println("Unknown node, now added.");
            }
            else
                System.out.println("I already know of this node.");
            for(RemoteNode n: nodes)
            {
                if (!(n.ip.equalsIgnoreCase(remoteip) && n.port == remoteport))
                {
                    String ndata = "NODE "+n.ip+" "+n.port;
                    System.out.println("Sending: "+ndata+" to "+n.ip+":"+n.port);
                    send(ndata, remoteip, remoteport);
                }
            }
        }
        if (parts[0].equalsIgnoreCase("NODE"))
        {
            System.out.println("NODE received!");
            String otherip = parts[1];
            int otherport = Integer.parseInt(parts[2]);
            if (addNode(otherip, otherport))
            {
                String message = "FETCH "+port;
                send(message,otherip,otherport);
            }
        }
    }

    public void send(String data, String remoteip, int remoteport)
    {
        try
        {
            byte[] bytes = data.getBytes();
            InetAddress inet = InetAddress.getByName(remoteip);
            DatagramSocket ds = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inet, remoteport);
            ds.send(packet);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean addNode(String remoteip, int remoteport)
    {
        for(RemoteNode n: nodes)
        {
            if (n.ip.equalsIgnoreCase(remoteip) && remoteport == n.port)
                return false;
        }
        RemoteNode rn = new RemoteNode();
        rn.ip = remoteip;
        rn.port = remoteport;
        nodes.add(rn);
        return true;
    }
}