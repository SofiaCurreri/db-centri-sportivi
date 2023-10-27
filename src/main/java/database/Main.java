package database;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        //parametri di connessione
        String url = "jdbc:mysql://localhost:3306/java_db_centri_sportivi";
        String user = "root";
        String password = "";

        try (Connection con = DriverManager.getConnection(url, user, password)) {

            //*********** OPZIONE 1 **********//

            //query per ottenere info generali di tutti i centri sportivi (descrizione, indirizzo, n° iscritti, età media iscritti)
            String sql1 = """
                    SELECT cs.nome, cs.descrizione, cs.indirizzo, cs.citta, COUNT(m.id) as numero_iscritti, ROUND(AVG(datediff(curdate(), m.data_di_nascita) / 365)) as eta_media_iscritti
                    FROM centri_sportivi cs
                    JOIN membri m on m.id_centro_sportivo = cs.id
                    GROUP BY cs.nome , cs.descrizione , cs.indirizzo , cs.citta                              
                    """;

            //query per ottenere info riguardo ricavato totale annuo di ogni centro sportivo in base alle quote d' iscrizione
//            String sql2 = """
//                    SELECT cs.nome , cs.indirizzo , cs.citta , YEAR(m.data_iscrizione), (COUNT(m.id) * cs.quota_iscrizione) as ricavato
//                    FROM centri_sportivi cs
//                    JOIN membri m on m.id_centro_sportivo = cs.id
//                    GROUP BY cs.nome, cs.indirizzo , cs.citta, YEAR(m.data_iscrizione);
//                    """;

            //query per ottenere info riguardanti quali sport sono praticati in ogni centro sportivo
//            String sql3 = """
//                    SELECT cs.nome , cs.indirizzo , cs.citta , GROUP_CONCAT(s.nome separator ', ')  as sport
//                    FROM centri_sportivi cs
//                    JOIN centro_sportivo_sport css on cs.id = css.id_centro_sportivo
//                    JOIN sports s on css.id_sport = s.id
//                    GROUP BY cs.nome, cs.indirizzo, cs.citta;
//                    """;

            //chiedo alla connessione di prepararsi per eseguire sql1
            try (PreparedStatement ps = con.prepareStatement(sql1)) {

                //eseguo query che restituisce un risultato di tipo ResultSet perché è di lettura (select)
                try (ResultSet rs = ps.executeQuery()) {

                    //scorro le righe del ResultSet rs
                    while (rs.next()) {
                        String nome = rs.getString("nome");
                        String descrizione = rs.getString("descrizione");
                        String indirizzo = rs.getString("indirizzo");
                        String citta = rs.getString("citta");
                        Integer numIscritti = rs.getInt("numero_iscritti");
                        Integer etaMediaIscritti = rs.getInt("eta_media_iscritti");

                        String datiFormattati = String.format("Nome: %s%nDescrizione: %s%nIndirizzo: %s, %s%nN° iscritti: %d%nEtà media iscritti: %d%n",
                                nome, descrizione, indirizzo, citta, numIscritti, etaMediaIscritti);
                        System.out.println(datiFormattati);
                    }
                }
            }

            //*********** OPZIONE 2 **********//

            //query da copiare a terminale così da poterle eseguire da linea di comando
            //query1
            //select cs.nome, cs.descrizione, cs.indirizzo, cs.citta, count(m.id) as 'numero iscritti', round(avg(datediff(curdate(), m.data_di_nascita) / 365)) as 'eta media iscritti' from centri_sportivi cs join membri m on m.id_centro_sportivo = cs.id group by cs.nome , cs.descrizione , cs.indirizzo , cs.citta;

            //query2
            //select cs.nome , cs.indirizzo , cs.citta , year(m.data_iscrizione) as anno, (count(m.id) * cs.quota_iscrizione) as ricavato from centri_sportivi cs join membri m on m.id_centro_sportivo = cs.id group by cs.nome, cs.indirizzo , cs.citta, year(m.data_iscrizione)

            //query3
            //select cs.nome , cs.indirizzo , cs.citta , group_concat(s.nome separator ', ') as sport from centri_sportivi cs join centro_sportivo_sport css on cs.id = css.id_centro_sportivo join sports s on css.id_sport = s.id group by cs.nome, cs.indirizzo, cs.citta

            Scanner scan = new Scanner(System.in);
            System.out.println("Inserisci la query con cui vuoi interrogare il database");
            String sql = scan.nextLine();
            try (PreparedStatement ps = con.prepareStatement(sql)) {

                try (ResultSet rs = ps.executeQuery()) {
                    //ResultSetMetaData può restituirmi nome e numero colonne risultanti dalla query
                    ResultSetMetaData metaData = rs.getMetaData();

                    //qui ottengo il n° di colonne
                    int numColonne = metaData.getColumnCount();

                    //scorro ogni riga del risultato rs
                    while (rs.next()) {
                        //scorro tutte le colonne di ogni riga
                        for (int i = 1; i <= numColonne; i++) {
                            System.out.println(metaData.getColumnName(i) + ": " + rs.getString(i));
                        }
                        System.out.println();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed");
            e.printStackTrace();
        }
    }
}
