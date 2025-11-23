package com.mycompany.libraryreservation;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.PrintWriter;

/**
 * ReservationManager 
 */
public class ReservationManager {

    // Library -> Topic -> List<BookDisplay>  
    private final Map<String, Map<String, List<String>>> libData;
    private final CopyOnWriteArrayList<PrintWriter> clientOutputs = new CopyOnWriteArrayList<>();

    public ReservationManager(Map<String, Map<String, List<String>>> libData) {
        this.libData = Objects.requireNonNull(libData, "libData is null");
    }

    /* ======================تسجيل العملاء====================== */

    public void registerClientOutput(PrintWriter out) {
        if (out != null) clientOutputs.add(out);
    }

    public void registerClient(Object clientMaybe) {
        try {
            if (clientMaybe == null) return;
            PrintWriter out = (PrintWriter) clientMaybe.getClass().getMethod("getOut").invoke(clientMaybe);
            registerClientOutput(out);
        } catch (Exception ignore) { }
    }

    private void notifyAllClients(String message) {
        for (PrintWriter out : clientOutputs) {
            try { out.println(message); } catch (Exception ignored) {}
        }
    }

    /* ======================  تحويل  ID   ====================== */

    private String toDisplayBook(String library, String topic, String bookArg) {
        if (bookArg == null) return null;
        String s = bookArg.trim();

        Map<String, List<String>> topics = libData.get(library);
        if (topics == null) return s;
        List<String> books = topics.get(topic);
        if (books == null) return s;


        if (books.contains(s)) return s;


        for (String entry : books) {
            int dash = entry.indexOf(" - ");
            if (dash > 0) {
                String id = entry.substring(0, dash).trim();
                if (s.equals(id)) return entry; 
            }
        }

        return s;
    }

    /* ====================== التحقق والعرض ====================== */

    public boolean selectionExists(String library, String topic, String bookInput) {
        String display = toDisplayBook(library, topic, bookInput);
        Map<String, List<String>> topics = libData.get(library);
        if (topics == null) return false;
        List<String> books = topics.get(topic);
        return books != null && books.contains(display);
    }


    public List<String> getAvailableBooks(String library, String topic, String dateOrNull) {
        Map<String, List<String>> topics = libData.get(library);
        if (topics == null) return Collections.emptyList();

        List<String> books = topics.get(topic);
        if (books == null) return Collections.emptyList();

        if (dateOrNull == null || dateOrNull.isBlank()) {
            return new ArrayList<>(books);
        }

        List<String> available = new ArrayList<>();
        for (String displayTitle : books) {

            if (!DataStorage.isAlreadyReserved(library, topic, displayTitle, dateOrNull)) {
                available.add(displayTitle);
            }
        }
        return available;
    }

    /* ====================== الحجز/الإلغاء ====================== */

    public static final class Result {
        public final boolean ok;
        public final String message;
        private Result(boolean ok, String message) { this.ok = ok; this.message = message; }
        public static Result ok(String m)   { return new Result(true,  m); }
        public static Result fail(String m) { return new Result(false, m); }
        @Override public String toString() { return (ok ? "OK: " : "FAIL: ") + message; }
    }

    public synchronized Result reserve(String user, String library, String topic, String bookInput, String date) {

        String displayTitle = toDisplayBook(library, topic, bookInput);

        if (isBlank(user, library, topic, displayTitle, date))
            return Result.fail("Missing inputs.");

        if (!selectionExists(library, topic, displayTitle))
            return Result.fail("Unknown selection.");

        if (DataStorage.isAlreadyReserved(library, topic, displayTitle, date)) {
            return Result.fail("Already reserved on " + date + ".");
        }

        try {

            DataStorage.saveReservation(user, library, topic, displayTitle, date);
        } catch (Exception e) {
            return Result.fail("File error while saving.");
        }

        notifyAllClients("UPDATE|RESERVED|" + library + "|" + topic + "|" + displayTitle + "|" + date + "|by|" + user);
        return Result.ok("Reserved " + displayTitle + " on " + date + ".");
    }

    

    public synchronized Result cancel(String user, String library, String topic, String bookInput, String date) {

        String displayTitle = toDisplayBook(library, topic, bookInput);

        File f = new File("reservations.txt");
        if (!f.exists()) return Result.fail("No records file.");
        File tmp = new File("reservations.tmp");
        boolean removed = false;

        try (BufferedReader br = new BufferedReader(new FileReader(f));
             PrintWriter pw = new PrintWriter(new FileWriter(tmp))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length == 5 &&
                    p[0].equals(user) && p[1].equals(library) &&
                    p[2].equals(topic) && p[3].equals(displayTitle) && p[4].equals(date)) {
                    removed = true; 
                    continue;
                }
                pw.println(line);
            }
        } catch (IOException e) {
            return Result.fail("I/O error while cancelling.");
        }

        if (!removed) { tmp.delete(); return Result.fail("Reservation not found."); }
        if (!f.delete() || !tmp.renameTo(f)) {
            return Result.fail("Failed to finalize cancellation.");
        }

        notifyAllClients("UPDATE|CANCELLED|" + library + "|" + topic + "|" + displayTitle + "|" + date + "|by|" + user);
        return Result.ok("Cancelled reservation for " + displayTitle + " on " + date + ".");
    }


    private static boolean isBlank(String... s) {
        for (String x : s) if (x == null || x.isBlank()) return true;
        return false;
    }
}
