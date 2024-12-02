import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class UtenteEventi {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/world";
        String username = "root";
        String password = "LunaAdelunalovegood99!";
        Connection conn = null;
        Scanner input = new Scanner(System.in);

        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connessione al database riuscita!");
            int scelta = 0;

            do {
                System.out.println("\n---Menu---"
                        + "\n1 per stampare tutti gli eventi"
                        + "\n2 per ricercare un evento"
                        + "\n3 per acquistare biglietti"
                        + "\n4 per vedere i biglietti disponibili"
                        + "\n5 per vedere la spesa totale"
                        + "\n0 per uscire");
                scelta = input.nextInt();
                input.nextLine(); 

                switch (scelta) {
                    case 1:
                    	stampaTuttiEventi(conn);
                        break;
                    case 2:
                    	ricercaEvento(conn, input);
                        break;
                    case 3:
                    	acquistaBiglietti(conn, input);
                        break;
                    case 4:
                    	visualizzaBigliettiDisponibili(conn, input);
                        break;
                    case 5:
                    	visualizzaSpesaTotale(conn);
                    	break;
                    case 0:
                        System.out.println("Uscita dal database.");
                        break;
                    default:
                        System.out.println("Scelta non valida! Riprovare.");
                        break;
                }

            } while (scelta != 0);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        input.close();
    }

    private static void stampaTuttiEventi(Connection conn) throws SQLException {
        String selectQuery = "SELECT * FROM eventi";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(selectQuery);

        while (rs.next()) {
            String Nome = rs.getString("Nome");
            String Data = rs.getString("Data");
            int Prezzo = rs.getInt("Prezzo");
            int Biglietti = rs.getInt("Biglietti");
            System.out.println("Nome: " + Nome + ", Data: " + Data + ", Prezzo: " + Prezzo + "€, Biglietti: " + Biglietti);
        }

        rs.close();
        stmt.close();
    }

    private static void ricercaEvento(Connection conn, Scanner input) throws SQLException {
        System.out.println("Scrivi il nome dell'evento da ricercare:");
        String Nome = input.nextLine();

        String selectQuery = "SELECT * FROM eventi WHERE Nome = ?";
        PreparedStatement stmt = conn.prepareStatement(selectQuery);
        stmt.setString(1, Nome);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String Nome1 = rs.getString("Nome");
            String Data = rs.getString("Data");
            int Prezzo = rs.getInt("Prezzo");
            int Biglietti = rs.getInt("Biglietti");
            System.out.println("Nome: " + Nome1 + ", Data: " + Data + ", Prezzo: " + Prezzo + "€, Biglietti: " + Biglietti);
        }

        rs.close();
        stmt.close();
    }

    private static int totaleBigliettiVenduti = 0;
    private static double totaleSpesa = 0.00;

    private static void acquistaBiglietti(Connection conn, Scanner input) throws SQLException {
        System.out.println("Nome dell'evento per cui acquistare i biglietti:");
        String Nome = input.nextLine();
        System.out.println("Quanti biglietti vuoi acquistare?");
        int numBiglietti = input.nextInt();
        input.nextLine();

        String selectQuery = "SELECT Biglietti, Prezzo FROM eventi WHERE Nome = ?";
        PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
        selectStmt.setString(1, Nome);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            int bigliettiDisponibili = rs.getInt("Biglietti");
            double prezzoBiglietto = rs.getDouble("Prezzo");

            if (bigliettiDisponibili >= numBiglietti) {
                String updateQuery = "UPDATE eventi SET Biglietti = Biglietti - ? WHERE Nome = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, numBiglietti);
                updateStmt.setString(2, Nome);
                int rows = updateStmt.executeUpdate();

                if (rows != 0) {
                    double spesaEvento = numBiglietti * prezzoBiglietto;
                    totaleBigliettiVenduti += numBiglietti;
                    totaleSpesa += spesaEvento;
                    System.out.println("Acquisto di " + numBiglietti + " biglietti per " + Nome + " completato con successo.");
                    System.out.println("Spesa totale per questo evento: " + spesaEvento + " €.");
                } else {
                    System.out.println("Errore durante l'acquisto dei biglietti!");
                }
                updateStmt.close();
            } else {
                System.out.println("Biglietti insufficienti per l'evento " + Nome + ". Biglietti disponibili: " + bigliettiDisponibili);
            }
        } else {
            System.out.println("Evento non trovato!");
        }

        rs.close();
        selectStmt.close();
    }


    private static void visualizzaBigliettiDisponibili(Connection conn, Scanner input) throws SQLException {
        System.out.println("Nome dell'evento per cui visualizzare i biglietti disponibili:");
        String Nome = input.nextLine();

        String selectQuery = "SELECT Biglietti FROM eventi WHERE Nome = ?";
        PreparedStatement stmt = conn.prepareStatement(selectQuery);
        stmt.setString(1, Nome);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            int bigliettiDisponibili = rs.getInt("Biglietti");
            System.out.println("Biglietti disponibili per l'evento " + Nome + ": " + bigliettiDisponibili);
        } else {
            System.out.println("Evento non trovato!");
        }

        rs.close();
        stmt.close();
    }
    
    private static void visualizzaSpesaTotale(Connection conn) throws SQLException {
    	    	System.out.println("Totale biglietti venduti: " + totaleBigliettiVenduti); 
    	    	System.out.println("Spesa totale di tutti i biglietti acquistati: " + totaleSpesa + " €.");
    	   

    }
}