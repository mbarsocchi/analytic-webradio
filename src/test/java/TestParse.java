
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import com.mirobarsa.analytic.DataBaseConnector;
import com.mirobarsa.analytic.FileProcessor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mbarsocchi
 */
public class TestParse {

    public static void main(String[] args) throws IOException, FileNotFoundException, ParseException, ClassNotFoundException, SQLException, InterruptedException {
        FileProcessor fp = new FileProcessor("C:\\Dev\\analytic-webradio\\src\\test\\resources\\");
        DataBaseConnector db = DataBaseConnector.getInstance("localhost:3306/rca_analytic", "root", "");
        fp.processFiles(db.getConn());
    }
}
