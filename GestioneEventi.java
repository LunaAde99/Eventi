import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class GestioneEventi {

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
                        + "\n1 per inserire un nuovo evento"
                        + "\n2 per aggiornare il prezzo"
                        + "\n3 per aggiornare la data"
                        + "\n4 per stampare tutti gli eventi"
                        + "\n5 per ricercare un evento"
                        + "\n6 per cancellare un evento"
                        + "\n7 per vedere i biglietti disponibili"
                        + "\n0 per uscire");
                scelta = input.nextInt();
                input.nextLine(); 

                switch (scelta) {
                    case 1:
                        inserisciEvento(conn, input);
                        break;
                    case 2:
                        aggiornaPrezzo(conn, input);
                        break;
                    case 3:
                    	aggiornaData(conn, input);
                        break;
                    case 4:
                        stampaTuttiEventi(conn);
                        break;
                    case 5:
                        ricercaEvento(conn, input);
                        break;
                    case 6:
                        cancellaEvento(conn, input);
                        break;
                    case 7:
                        visualizzaBigliettiDisponibili(conn, input);
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

    private static void inserisciEvento(Connection conn, Scanner input) throws SQLException {
        System.out.println("Scrivi il nome dell'evento:");
        String Nome = input.nextLine();
        System.out.println("Scrivi la data:");
        String Data = input.nextLine();
        System.out.println("Scrivi il prezzo:");
        int Prezzo = input.nextInt();
        System.out.println("Scrivi il numero totale di biglietti:");
        int Biglietti = input.nextInt();
        input.nextLine();

        String insertQuery = "INSERT INTO eventi (Nome, Data, Prezzo, Biglietti) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(insertQuery);
        stmt.setString(1, Nome);
        stmt.setString(2, Data);
        stmt.setInt(3, Prezzo);
        stmt.setInt(4, Biglietti);

        int rowsAffected = stmt.executeUpdate();
        System.out.println("Numero di righe aggiornate: " + rowsAffected);
        stmt.close();
    }

    private static void aggiornaPrezzo(Connection conn, Scanner input) throws SQLException {
        System.out.println("Nome dell'evento:");
        String Nome = input.nextLine();
        System.out.println("Scrivi il nuovo Prezzo:");
        int Prezzo = input.nextInt();

        String query = "UPDATE eventi SET Prezzo = ? WHERE Nome = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, Prezzo);
        stmt.setString(2, Nome);

        int rows = stmt.executeUpdate();

        if (rows != 0) {
            System.out.println(Nome + " cambiato correttamente");
        } else {
            System.out.println("Evento non trovato!!!");
        }
        stmt.close();
    }

    private static void aggiornaData(Connection conn, Scanner input) throws SQLException {
        System.out.println("Nome dell'evento:");
        String Nome = input.nextLine();
        System.out.println("Scrivi la nuova data:");
        String Data = input.nextLine();

        String query = "UPDATE eventi SET Data = ? WHERE Nome = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, Data);
        stmt.setString(2, Nome);

        int rows = stmt.executeUpdate();

        if (rows != 0) {
            System.out.println(Nome + " cambiato correttamente");
        } else {
            System.out.println("Evento non trovato!!!");
        }
        stmt.close();
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

    private static void cancellaEvento(Connection conn, Scanner input) throws SQLException {
        System.out.println("Nome dell'evento da cancellare:");
        String Nome = input.nextLine();

        String query = "DELETE FROM eventi WHERE Nome = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, Nome);

        int rows = stmt.executeUpdate();

        if (rows != 0) {
            System.out.println(Nome + " cancellato correttamente");
        } else {
            System.out.println("Evento non trovato!!!");
        }
        stmt.close();
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
}
