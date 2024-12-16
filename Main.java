import java.util.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class Main {

    // Process class to represent a process
    static class Process {
        private int at; // arrival time
        private int bt; // burst time
        private int ct; // completion time
        private int tat; // turn around time
        private int wt; // waiting time
        private int pid; // process ID

        // Getter method to get a variable value of the process
        public int getVar(String var) {
            if (var.equals("at"))
                return at;
            if (var.equals("bt"))
                return bt;
            if (var.equals("ct"))
                return ct;
            if (var.equals("tat"))
                return tat;
            if (var.equals("wt"))
                return wt;
            return pid;
        }

        // Setter method to set a variable value of the process
        public void setVar(String var, int value) {
            if (var.equals("at"))
                at = value;
            else if (var.equals("bt"))
                bt = value;
            else if (var.equals("ct"))
                ct = value;
            else if (var.equals("tat"))
                tat = value;
            else if (var.equals("wt"))
                wt = value;
            else
                pid = value;
        }

        // Update the turn around time and waiting time after completion
        public void updateAfterCt() {
            tat = ct - at;
            wt = tat - bt;
        }

        // Display the process details
        public void display() {
            System.out.printf("%d\t%d\t%d\t%d\t%d\t%d\n", pid, at, bt, ct, tat, wt);
        }
    }

    // Calculate the average of a variable value for all processes
    public static float average(ArrayList<Process> P, String var) {
        int total = 0;
        for (Process temp : P) {
            total += temp.getVar(var);
        }
        return (float) total / P.size();
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        // Read number of processes
        System.out.println("Enter the number of processes:");
        int n = sc.nextInt();

        int counter = 0;
        ArrayList<Process> P = new ArrayList<Process>(n);

        // Create a process object for each input and add to the process list
        for (int i = 0; i < n; i++) {
            Process temp = new Process();
            temp.setVar("pid", counter++);
            System.out.println("Enter arrival time and burst time for process " + (i+1) + ":");
            temp.setVar("at", sc.nextInt());
            temp.setVar("bt", sc.nextInt());
            P.add(temp);
        }

        // Sort the process list by arrival time
        Collections.sort(P, new Comparator<Process>() {
            public int compare(Process first, Process second) {
                return first.getVar("at") - second.getVar("at");
            }
        });

        StringBuilder result = new StringBuilder();
        result.append("pid\tat\tbt\tct\ttat\twt\n");

        // Calculate completion time and display the details of the first process
        P.get(0).setVar("ct", P.get(0).getVar("at") + P.get(0).getVar("bt"));
        P.get(0).updateAfterCt();
        P.get(0).display();
        result.append(String.format("%d\t%d\t%d\t%d\t%d\t%d\n", P.get(0).getVar("pid"), P.get(0).getVar("at"),
                P.get(0).getVar("bt"), P.get(0).getVar("ct"), P.get(0).getVar("tat"), P.get(0).getVar("wt")));

        // Calculate completion time and display the details of the remaining processes
        for (int i = 1; i < P.size(); i++) {
            if (P.get(i).getVar("at") < P.get(i - 1).getVar("ct")) {
                P.get(i).setVar("ct", P.get(i - 1).getVar("ct") + P.get(i).getVar("bt"));
            } else {
                P.get(i).setVar("ct", P.get(i).getVar("at") + P.get(i).getVar("bt"));
            }
            P.get(i).updateAfterCt();
            P.get(i).display();
            result.append(String.format("%d\t%d\t%d\t%d\t%d\t%d\n", P.get(i).getVar("pid"), P.get(i).getVar("at"),
                    P.get(i).getVar("bt"), P.get(i).getVar("ct"), P.get(i).getVar("tat"), P.get(i).getVar("wt")));
        }

        result.append(String.format("Average waiting time : %f\n", average(P, "wt")));

        // Display the results in the console
        System.out.println(result.toString());

        // Set up HTTP server to respond on port 8001
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = result.toString().getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });
        server.start();
        System.out.println("Server started on port 8001...");

        sc.close();
    }
}
