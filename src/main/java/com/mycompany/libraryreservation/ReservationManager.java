package com.mycompany.libraryreservation;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.PrintWriter;

/**
 * ReservationManager
 */
public class ReservationManager {

    //Library -> Topic -> List<Book>
    private final Map<String, Map<String, List<String>>> libData;

    
    private final CopyOnWriteArrayList<PrintWriter> clientOutputs = new CopyOnWriteArrayList<>();

    public ReservationManager(Map<String, Map<String, List<String>>> libData) {
        this.libData = Objects.requireNonNull(libData, "libData is null");
    }

    /* ====================== تسجيل العملا ====================== */

    /** سجل PrintWriter عميل بشكل مباشر. */
    public void registerClientOutput(PrintWriter out) {
        if (out != null) clientOutputs.add(out);
    }

    
    public void registerClient(Object clientMaybe) {
        try {
            if (clientMaybe == null) return;
            
            PrintWriter out = (PrintWriter) clientMaybe.getClass().getMethod("getOut").invoke(clientMaybe);
            registerClientOutput(out);
        } catch (Exception ignore) {
            
        }
    }

    private void notifyAllClients(String message) {
        for (PrintWriter out : clientOutputs) {
            try { out.println(message); } catch (Exception ignored) {}
        }
    }

    /* ====================== التحقق والعرض ====================== */

    
    public boolean selectionExists(String library, String topic, String book) {
        Map<String, List<String>> topics = libData.get(library);
        if (topics == null) return false;
        List<String> books = topics.get(topic);
        return books != null && books.contains(book);
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
        for (String b : books) {
            
            if (!DataStorage.isAlreadyReserved(library, topic, b, dateOrNull)) {
                available.add(b);
            }
        }
        return available;
    }

    /* ====================== الحجز ====================== */

    
    public static final class Result {
        public final boolean ok;
        public final String message;
        private Result(boolean ok, String message) { this.ok = ok; this.message = message; }
        public static Result ok(String m)   { return new Result(true,  m); }
        public static Result fail(String m) { return new Result(false, m); }
        @Override public String toString() { return (ok ? "OK: " : "FAIL: ") + message; }
    }

    
    public synchronized Result reserve(String user, String library, String topic, String book, String date) {
        // تحقق المدخلات الأساسية
        if (isBlank(user, library, topic, book, date))
            return Result.fail("Missing inputs.");

        // تحقق التكوين الصحيح
        if (!selectionExists(library, topic, book))
            return Result.fail("Unknown selection.");

        // منع التكرار لنفس التاريخ
        if (DataStorage.isAlreadyReserved(library, topic, book, date)) {
            return Result.fail("Already reserved on " + date + ".");
        }

        // حفظ في السجل 
        try {
            DataStorage.saveReservation(user, library, topic, book, date);
        } catch (Exception e) {
            return Result.fail("File error while saving.");
        }

        //  إشعار تحديث
        notifyAllClients("UPDATE|RESERVED|" + library + "|" + topic + "|" + book + "|" + date + "|by|" + user);

        return Result.ok("Reserved " + book + " on " + date + ".");
    }

    /* إلغاء حجز */
    public synchronized Result cancel(String user, String library, String topic, String book, String date) {
        
        File f = new File("reservaCons.txt");
        if (!f.exists()) return Result.fail("No records file.");
        File tmp = new File("reservaCons.tmp");
        boolean removed = false;

        try (BufferedReader br = new BufferedReader(new FileReader(f));
             PrintWriter pw = new PrintWriter(new FileWriter(tmp))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length == 5 &&
                    p[0].equals(user) && p[1].equals(library) &&
                    p[2].equals(topic) && p[3].equals(book) && p[4].equals(date)) {
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

        notifyAllClients("UPDATE|CANCELLED|" + library + "|" + topic + "|" + book + "|" + date + "|by|" + user);
        return Result.ok("Cancelled reservation for " + book + " on " + date + ".");
    }

    

    private static boolean isBlank(String... s) {
        for (String x : s) if (x == null || x.isBlank()) return true;
        return false;
    }
}