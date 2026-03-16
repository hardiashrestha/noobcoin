import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class NoobCoinServer {

    private Blockchain chain;

    public NoobCoinServer(Blockchain chain) {
        this.chain = chain;
    }

    public void start(int port) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/",               this::serveIndex);
        server.createContext("/api/wallets",    this::handleWallets);
        server.createContext("/api/chain",      this::handleChain);
        server.createContext("/api/send",       this::handleSend);
        server.createContext("/api/register",   this::handleRegister);
        server.createContext("/api/status",     this::handleStatus);

        server.setExecutor(null);
        server.start();
        System.out.println("\n  🌐 NoobCoin is LIVE at http://localhost:" + port);
        System.out.println("  Open your browser → http://localhost:" + port + "\n");
    }

    private void serveIndex(HttpExchange ex) throws IOException {
        byte[] response = Files.readAllBytes(Paths.get("static/index.html"));
        ex.getResponseHeaders().add("Content-Type", "text/html");
        ex.sendResponseHeaders(200, response.length);
        ex.getResponseBody().write(response);
        ex.getResponseBody().close();
    }

    private void handleWallets(HttpExchange ex) throws IOException {
        handleCors(ex);
        sendJson(ex, 200, Wallet.allWalletsJson());
    }

    private void handleChain(HttpExchange ex) throws IOException {
        handleCors(ex);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < chain.chain.size(); i++) {
            Block b = chain.chain.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"index\":" + i + ",");
            sb.append("\"hash\":\"" + b.hash + "\",");
            sb.append("\"prevHash\":\"" + b.previousHash + "\",");
            sb.append("\"time\":" + b.timeStamp + ",");
            sb.append("\"transactions\":[");
            for (int j = 0; j < b.transactions.size(); j++) {
                if (j > 0) sb.append(",");
                sb.append(b.transactions.get(j).toJson());
            }
            sb.append("]}");
        }
        sb.append("]");
        sendJson(ex, 200, sb.toString());
    }

    private void handleSend(HttpExchange ex) throws IOException {
        handleCors(ex);
        Map<String, String> params = parseQuery(ex.getRequestURI());
        String from   = params.get("from");
        String to     = params.get("to");
        String amtStr = params.get("amount");

        if (from == null || to == null || amtStr == null) {
            sendJson(ex, 400, "{\"success\":false,\"message\":\"Missing parameters\"}");
            return;
        }
        try {
            float amount  = Float.parseFloat(amtStr);
            boolean ok    = Wallet.sendFunds(from, to, amount, chain);
            if (ok) {
                sendJson(ex, 200, "{\"success\":true,\"message\":\"Transaction mined and added to chain!\"}");
            } else {
                sendJson(ex, 400, "{\"success\":false,\"message\":\"Insufficient funds or wallet not found.\"}");
            }
        } catch (Exception e) {
            sendJson(ex, 400, "{\"success\":false,\"message\":\"Invalid amount.\"}");
        }
    }

    private void handleRegister(HttpExchange ex) throws IOException {
        handleCors(ex);
        Map<String, String> params = parseQuery(ex.getRequestURI());
        String name   = params.get("name");
        String balStr = params.get("balance");
        float  bal    = (balStr != null) ? Float.parseFloat(balStr) : 10000f;

        if (name == null || name.trim().isEmpty()) {
            sendJson(ex, 400, "{\"success\":false,\"message\":\"Name required\"}");
            return;
        }
        boolean created = Wallet.createWallet(name.trim(), bal);
        if (created) {
            sendJson(ex, 200, "{\"success\":true,\"message\":\"Wallet created with " + bal + " NBC!\"}");
        } else {
            sendJson(ex, 200, "{\"success\":true,\"message\":\"Welcome back, " + name.trim() + "!\"}");
        }
    }

    private void handleStatus(HttpExchange ex) throws IOException {
        handleCors(ex);
        boolean valid = chain.isChainValid();
        sendJson(ex, 200, "{\"valid\":" + valid + ",\"blocks\":" + chain.chain.size() + "}");
    }

    private void handleCors(HttpExchange ex) throws IOException {
        if ("OPTIONS".equals(ex.getRequestMethod())) {
            ex.getResponseHeaders().add("Access-Control-Allow-Origin",  "*");
            ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            ex.sendResponseHeaders(204, -1);
            ex.getResponseBody().close();
        }
    }

    private void sendJson(HttpExchange ex, int code, String json) throws IOException {
        ex.getResponseHeaders().add("Content-Type",                "application/json");
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods","GET, POST, OPTIONS");
        byte[] bytes = json.getBytes("UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }

    private Map<String, String> parseQuery(URI uri) {
        Map<String, String> map = new HashMap<>();
        String query = uri.getQuery();
        if (query == null) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) map.put(kv[0], kv[1].replace("%20", " "));
        }
        return map;
    }
}

