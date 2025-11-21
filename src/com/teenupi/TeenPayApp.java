package com.teenupi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.teenupi.model.Transaction;
import com.teenupi.model.User;
import com.teenupi.service.Database;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeenPayApp {

    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8000"));
    private static final String CURRENT_USER_UPI = "yatharth@teen";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new HomeHandler());
        server.createContext("/send-money", new SendMoneyHandler());
        server.createContext("/css", new StaticFileHandler());

        server.setExecutor(null); // creates a default executor
        System.out.println("Server started on port " + PORT);
        System.out.println("Open http://localhost:" + PORT + " in your browser");
        server.start();
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("GET")) {
                t.sendResponseHeaders(405, -1);
                return;
            }

            Database db = Database.getInstance();
            User currentUser = db.findUserByUpi(CURRENT_USER_UPI).orElseThrow();
            List<User> friends = db.getFriends(CURRENT_USER_UPI);
            List<Transaction> timeline = db.getRecentTransactions(20);

            // Calculate stats
            BigDecimal spentThisMonth = BigDecimal.ZERO; // Simplified for demo
            BigDecimal receivedThisMonth = BigDecimal.ZERO; // Simplified for demo
            long friendsPaidCount = 0; // Simplified for demo

            String template = new String(Files.readAllBytes(Paths.get("public/index.html")));

            // Simple Template Engine Replacement
            String response = template
                    .replace("{{name}}", currentUser.getName().split(" ")[0])
                    .replace("{{fullName}}", currentUser.getName())
                    .replace("{{upiId}}", currentUser.getUpiId())
                    .replace("{{balance}}", "₹" + currentUser.getBalance())
                    .replace("{{avatarHue}}", String.valueOf(currentUser.getAvatarHue()))
                    .replace("{{spentThisMonth}}", "₹" + spentThisMonth)
                    .replace("{{receivedThisMonth}}", "₹" + receivedThisMonth)
                    .replace("{{friendsPaidCount}}", String.valueOf(friendsPaidCount))
                    .replace("{{timelineCount}}", String.valueOf(timeline.size()));

            // Render Friends Options
            StringBuilder friendsOptions = new StringBuilder();
            for (User friend : friends) {
                friendsOptions.append(
                        String.format("<option value=\"%s\" style=\"background-color: #1e293b;\">%s (%s)</option>",
                                friend.getUpiId(), friend.getName(), friend.getUpiId()));
            }
            response = response.replace("{{friendsOptions}}", friendsOptions.toString());

            // Render Friends Rail
            StringBuilder friendsRail = new StringBuilder();
            for (User friend : friends) {
                friendsRail.append(String.format(
                        "<div class=\"flex flex-col items-center gap-2 min-w-[4rem]\">" +
                                "<div class=\"relative flex size-14 shrink-0 items-center justify-center overflow-hidden rounded-full border-2 border-white shadow-sm transition-transform hover:scale-110\" style=\"background-color: hsl(%d, 85%%, 75%%)\">"
                                +
                                "<span class=\"text-xl font-bold text-white drop-shadow-sm\">%s</span></div>" +
                                "<span class=\"text-xs font-medium text-slate-900\">%s</span></div>",
                        friend.getAvatarHue(), friend.getName().substring(0, 1), friend.getName().split(" ")[0]));
            }
            response = response.replace("{{friendsRail}}", friendsRail.toString());

            // Render Timeline
            StringBuilder timelineHtml = new StringBuilder();
            if (timeline.isEmpty()) {
                timelineHtml.append("<div class=\"pl-14 text-slate-500\">No transactions yet. Start the vibe!</div>");
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM d, h:mm a");
                for (Transaction tx : timeline) {
                    boolean isSender = tx.getSender().getUpiId().equals(currentUser.getUpiId());
                    User otherUser = isSender ? tx.getReceiver() : tx.getSender();
                    String colorClass = isSender ? "bg-orange-400 ring-orange-50" : "bg-emerald-400 ring-emerald-50";
                    String amountClass = isSender ? "text-slate-900" : "text-emerald-600";
                    String sign = isSender ? "-" : "+";
                    String desc = isSender ? "Paid " + otherUser.getName() : "Received from " + otherUser.getName();

                    timelineHtml.append(String.format(
                            "<div class=\"relative pl-14\">" +
                                    "<div class=\"absolute left-[1.35rem] top-4 size-2.5 rounded-full border-2 border-white bg-slate-300 ring-4 ring-white %s\"></div>"
                                    +
                                    "<div class=\"group relative overflow-hidden rounded-2xl border border-white/50 bg-white/40 p-4 transition-all hover:bg-white hover:shadow-md\">"
                                    +
                                    "<div class=\"flex items-start justify-between gap-4\">" +
                                    "<div class=\"flex items-center gap-3\">" +
                                    "<div class=\"relative flex size-10 shrink-0 items-center justify-center overflow-hidden rounded-full border-2 border-white shadow-sm\" style=\"background-color: hsl(%d, 85%%, 75%%)\">"
                                    +
                                    "<span class=\"text-sm font-bold text-white drop-shadow-sm\">%s</span></div>" +
                                    "<div><p class=\"font-medium text-slate-900\">%s</p><p class=\"text-xs text-slate-500\">%s</p></div></div>"
                                    +
                                    "<div class=\"text-right\"><p class=\"font-bold %s\">%s₹%s</p><p class=\"text-xs text-slate-500 italic\">%s</p></div></div></div></div>",
                            colorClass, otherUser.getAvatarHue(), otherUser.getName().substring(0, 1), desc,
                            tx.getCreatedAt().format(dtf), amountClass, sign, tx.getAmount(),
                            tx.getNote() == null ? "" : "\"" + tx.getNote() + "\""));
                }
            }
            response = response.replace("{{timeline}}", timelineHtml.toString());

            t.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class SendMoneyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("POST".equals(t.getRequestMethod())) {
                String formData = new String(t.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseFormData(formData);

                String receiverUpi = params.get("receiverUpiId");
                BigDecimal amount = new BigDecimal(params.get("amount"));
                String note = params.get("note");

                try {
                    Database.getInstance().addTransaction(amount, note,
                            Database.getInstance().findUserByUpi(CURRENT_USER_UPI).get(),
                            Database.getInstance().findUserByUpi(receiverUpi).get());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                t.getResponseHeaders().set("Location", "/");
                t.sendResponseHeaders(302, -1);
                t.close();
            }
        }

        private Map<String, String> parseFormData(String formData) {
            Map<String, String> map = new HashMap<>();
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    map.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
                }
            }
            return map;
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String path = t.getRequestURI().getPath();
            String filePath = "public" + path;
            if (Files.exists(Paths.get(filePath))) {
                byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                t.sendResponseHeaders(200, fileBytes.length);
                OutputStream os = t.getResponseBody();
                os.write(fileBytes);
                os.close();
            } else {
                t.sendResponseHeaders(404, -1);
                t.close();
            }
        }
    }
}
