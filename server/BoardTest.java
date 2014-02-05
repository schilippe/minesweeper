package minesweeper.server;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;


public class BoardTest {

    @Test
    public void test() throws IOException {
        File file = new File("C:\\Users\\Philippe\\workspace\\pschiff\\ps3\\ps3-proj\\src\\minesweeper\\server\\testBoardFile");
        Board b = new Board(false, file);
        //System.out.println(b.showBombs());

        System.out.println(b.look());
        
    }

}
