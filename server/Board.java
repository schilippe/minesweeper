package minesweeper.server;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Random;
import java.util.Vector;

/**
 * Board class to represent the board in a MineSweeper game.
 * The board is represented by a Vector of Vectors containing Squares
 * see def for Square in Square.java
 * @author Philippe
 *
 */
public class Board {
    Random generator = new Random();
    private boolean debug;
    private int size;
    private File file;
    
    //
    Vector<Vector<Square>> board;

    /**
     * constructor for case with default size and no file.
     * @param debug boolean needed for Boom!
     */
    public Board(boolean debug) {
        this.debug = debug;
        this.size = 10;
        this.file = null;

        this.constructBoard(this.size);
        this.addNumsToTiles();
    }

    /**
     * constructor for case with board size specified.
     * @param debug
     * @param size
     */
    public Board(boolean debug, int size) {
        this.debug = debug;
        this.size = size;
        this.file = null;

        this.constructBoard(this.size);
        this.addNumsToTiles();
    }

    /**
     * contructor for case with filePath specified
     * @param debug
     * @param file String of filePath from where to load board
     * @throws IOException
     */


    public Board(boolean debug, File file)  {
        this.debug = debug;
        this.size = -1;
        this.file = file;
        try {
            this.constructBoardFromFile(this.file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.addNumsToTiles();
    }

    public void setDebug(boolean b) {
        this.debug = b;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setSize(int size){
        this.size = size;
    }

    public int getSize(){
        return this.size;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }
    
    /**
     * implements the look function for call look in the minesweeper.
     * @return String. The string representation of the board according to 
     * pset3 specs in handout.
     */
    public String look(){
        String returnString = "";
        this.addNumsToTiles();
        for (Vector<Square> v : this.board) {
            for (Square s : v) {
                if (s.getState().equals("-")) returnString += "- ";
                if (s.getState().equals("flag")) returnString +="F ";
                if (s.getState().equals("dug")) {
                    addNumsToTiles();
                    if (!s.getValue().equals("0")) {
                        returnString += s.getValue()+" ";
                    } else { returnString += "  ";}
                }


            }
            String newLine = String.format("%n");
            returnString += newLine;
        }
        /*for (int i = 0; i < returnString.length(); i++) {
            int nlIndex = returnString.indexOf(String.format("&n"));
                if (nlIndex != -1) {
                    returnString.replace
                }
        }*/
        
        String returnStringFinal = returnString.replace(String.format(" %n"),String.format("%n"));
        returnStringFinal = returnStringFinal.substring(0, returnStringFinal.length() -2 );
        //returnStringFinal += String.format("%n");
        return returnStringFinal;
    }

    /**
     * implements the dig operation of MineSweeper in the x y 'th 
     * square. The recursive call to clear away knowable Squares is called
     * here but defined below in clearKnown method.
     * @param x int
     * @param y int
     * @return String either a look or boom depending on whether you hit a bomb.
     */
    public String dig(int x, int y) {
        if ( (x<0 || y<0) || (x>=this.size || y >= this.size) 
                || (!this.board.get(x).get(y).getState().equals("-"))){
            return look();
        }
        if (this.board.get(x).get(y).getState().equals("-")) {
            this.board.get(x).get(y).setState("dug");
        }
        if (this.board.get(x).get(y).getMine()) {
            this.board.get(x).get(y).setMine(false);
            this.addNumsToTiles();
            this.clearKnown(x, y);
            return boom();
        }
        this.addNumsToTiles();
        //if (this.board.get(x).get(y).getValue().equals(0)) {
        this.clearKnown(x,y);
        //}
        this.addNumsToTiles();
        return look();
    }
    
    /**
     * Implements the flag operation of MineSweeper. Flags the
     * x y 'th Square according to clients wishes.
     * @param x
     * @param y
     * @return
     */
    public String flag(int x, int y){
        try {
            if ((x >=0 && y >=0 && x <= this.size && y <= this.size) 
                    && (this.board.get(x).get(y).getState().equals("-"))) {
                this.board.get(x).get(y).setState("flag");
            }
        return look();
        } catch (ArrayIndexOutOfBoundsException e) {return look();}
    }
    

    /**
     * Implements the deflag operation of MineSweeper on x y 'th
     * Square. Undoes the action of flag.
     * @param x int
     * @param y int
     * @return
     */
    public String deflag(int x, int y) {
        try {
            if ((x >=0 && y >=0 && x <= this.size && y <= this.size) 
                    && (this.board.get(x).get(y).getState().equals("flag"))) {
                this.board.get(x).get(y).setState("-");
            }
        return look();
        } catch (ArrayIndexOutOfBoundsException e) {return look();}
    }
    
    /**
     * Implement the help requests. Allows user to find out what commands are possible
     * @return String of the helpful info.
     */
    public String help_req() {
        String nl = String.format("%n");
        String help = "You can use the following commands:" + nl
                       +"\"look\" to look at the current board state." + nl 
                       +"\"dig int int\" to dig in square int int." + nl
                       +"\"flag int int\" to flag square int int you want to remember." + nl
                       +"\"deflag int int\" to unflag a flagged square." + nl
                       +"\"help_req\" to get this message." + nl
                       +"\"bye\" to quit and close connection." + nl;
        return help;
    }



    /**
     * implemets the Boom case if you hit a mine. Does according to pset handout
     * accordingly in cases of debug or !debug.
     * @return String see above.
     */
    public String boom() {
        if (!this.debug) return "BOOM!"  + bye();
        
        return "BOOM!" + String.format("%n") + look();
    }
    
    /**
     *returns a notification to client that connection is being closed and closes the connection.
     * @return
     */
    public String bye() {
        return "";
    }




    /**
     * Updates all the number values of the tiles according to the  amount 
     * of bombs that are around it. Is called many times throughout program.
     */
    public void addNumsToTiles() {
        int numB = 0;
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (!(this.board.get(i).get(j).getMine())) {
                    try {
                        if (this.board.get(i-1).get(j-1).getMine()) numB++; 
                    } catch (ArrayIndexOutOfBoundsException e) {
                    } 
                    try {
                        if (this.board.get(i-1).get(j).getMine()) numB++;
                    }catch (ArrayIndexOutOfBoundsException e) {
                    } 
                    try { 
                        if (this.board.get(i-1).get(j+1).getMine()) numB++;
                    }catch (ArrayIndexOutOfBoundsException e) {
                    } 
                    try {
                        if (this.board.get(i).get(j-1).getMine()) numB++;
                    } catch (ArrayIndexOutOfBoundsException e) {
                    } 
                    try {
                        if (this.board.get(i).get(j+1).getMine()) numB++;
                    } catch (ArrayIndexOutOfBoundsException e) {
                    } 
                    try {
                        if (this.board.get(i+1).get(j-1).getMine()) numB++;
                    } catch (ArrayIndexOutOfBoundsException e) {
                    } 
                    try {
                        if (this.board.get(i+1).get(j).getMine()) numB++;
                    } catch (ArrayIndexOutOfBoundsException e) {
                    } 
                    try {
                        if (this.board.get(i+1).get(j+1).getMine()) numB++; 
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }  
                    this.board.get(i).get(j).setValue(numB);
                    numB = 0;

                } 
            }
        }
    }

    public void constructBoard(int size) {

        this.board = new Vector<Vector<Square>>(this.size);
        for (int i = 0; i < this.size; i++) {
            Vector<Square> inner = new Vector<Square>(this.size);
            for (int j = 0; j < this.size; j++) {
                int r = generator.nextInt(100);
                Square s = (r <= 25) ? 
                        new Square(true) : new Square(false);                
                        inner.add(s);
            }
            this.board.add(inner);
        }
    }
    @SuppressWarnings("deprecation")
    public void constructBoardFromFile(File fileIn) throws IOException{
/*        
        this.board = new Vector<Vector<Square>>(this.size);
        for (int i = 0; i < this.size; i++) {
            Vector<Square> inner = new Vector<Square>(this.size);
            for (int j = 0; j < this.size; j++) { 
                        Square s = new Square(false);                
                        inner.add(s);
            }
            this.board.add(inner);
        }*/
        
        LineNumberReader  lnr = new LineNumberReader(new FileReader(fileIn));
        lnr.skip(Long.MAX_VALUE);
        this.size = lnr.getLineNumber()+1;
        lnr.close();
        //System.out.println("this.size :" + this.size);
        
        File file = fileIn;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            
            int lineNr = 0;
            int width = 0;
            
            this.board = new Vector<Vector<Square>>(this.size);
            for (int i = 0; i < this.size; i++) {
                Vector<Square> inner = new Vector<Square>(this.size);
                for (int j = 0; j < this.size; j++) {
                    Square s = new Square(false);                
                    inner.add(s);
                }
                this.board.add(inner);
            }
            while (dis.available() != 0) {                
                String[] lineSplit = dis.readLine().split(" ");
                for (int i =0; i< lineSplit.length; i++) {
                    //System.out.println(lineSplit[i]);
                    if (!lineSplit[i].equals("1") && !lineSplit[i].equals("0")) {
                        //System.out.println(lineSplit[i]);
                        fis.close();
                        bis.close();
                        dis.close();
                        throw new RuntimeException("Invalid File.");
                    }
                    if (lineNr > lineSplit.length) {
                        //System.out.println("lineNr: " + lineNr);
                        //System.out.println("linesplit.lentgh: "+ lineSplit.length);
                        fis.close();
                        bis.close();
                        dis.close();
                        throw new RuntimeException("Invalid File");
                    }
                    if (lineSplit[i].equals("1")) {
                        //Square square = new Square(true);
                        //System.out.println(this.board);
                        this.board.get(lineNr).get(i).setMine(true);
                    } else {
                        //Square square = new Square(false);
                        //Vector<Square> v = new Vector<Square>();
                        this.board.get(lineNr).get(i).setMine(false);
                    }
                    width = lineSplit.length;
                    
                }
                lineNr++;
                
            }
            fis.close();
            bis.close();
            dis.close();
            if (width != lineNr) throw new RuntimeException("Invalid File");
            this.size=width;
            
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    public void clearKnown(int x, int y) {
        this.addNumsToTiles();
        //System.out.println(x +" " + y);
        //System.out.println(this.board.get(x).get(y).getValue());
        
        if (this.board.get(x).get(y).getValue().equals("0")) {
            try {
                if (this.board.get(x-1).get(y-1).getState().equals("-")) {
                    this.board.get(x-1).get(y-1).setState("dug");
                    clearKnown(x-1, y-1);
                }
            } catch(ArrayIndexOutOfBoundsException e) {}
            
            try {
                if (this.board.get(x-1).get(y).getState().equals("-")) {
                    this.board.get(x-1).get(y).setState("dug");
                    clearKnown(x-1, y);
                }
            } catch(ArrayIndexOutOfBoundsException e) {}
            
            try {
                if (this.board.get(x-1).get(y+1).getState().equals("-")) {
                    this.board.get(x-1).get(y+1).setState("dug");
                    clearKnown(x-1, y+1);
                }
            } catch(ArrayIndexOutOfBoundsException e) {}
            
            try {
                if (this.board.get(x).get(y-1).getState().equals("-")) {
                    this.board.get(x).get(y-1).setState("dug");
                    clearKnown(x, y-1);
                }
            } catch(ArrayIndexOutOfBoundsException e) {}
            
            try {
                if (this.board.get(x).get(y+1).getState().equals("-")) {
                    this.board.get(x).get(y+1).setState("dug");
                    clearKnown(x, y+1);
                }
            } catch(ArrayIndexOutOfBoundsException e) {}
            
            try {
                if (this.board.get(x+1).get(y-1).getState().equals("-")) {
                    this.board.get(x+1).get(y-1).setState("dug");
                    clearKnown(x+1, y-1);
                }
            } catch(ArrayIndexOutOfBoundsException e) {}
            
            try {
                if (this.board.get(x+1).get(y).getState().equals("-")) {
                    this.board.get(x+1).get(y).setState("dug");
                    clearKnown(x+1, y);
                }
            } catch(ArrayIndexOutOfBoundsException e) {}
            
            try {
                if (this.board.get(x+1).get(y+1).getState().equals("-")) {
                    this.board.get(x+1).get(y+1).setState("dug");
                    clearKnown(x+1, y+1);
                }
            } catch(ArrayIndexOutOfBoundsException e) {}
        }
    }
    
    public String showBombs() {
        String returnString = "";
        for (Vector<Square> v : this.board) {
            for (Square s : v) {
                if (s.getMine()) { returnString += "X ";}
                else { returnString += s.getValue() +" ";}

            }
            String newLine = String.format("%n");
            returnString += newLine;
        }
        return returnString;

    }


    public void clearKnownLatest(int x, int y) {
        //System.out.println("in clearKnown!");
        if (this.board.get(x).get(y).getValue().equals("0")) {

            if (this.board.get(x-1).get(y-1).getState().equals("0")) {
                try {
                    if (this.board.get(x-1).get(y-1).getState().equals("-")) {
                        this.board.get(x-1).get(y-1).setState("dug");
                        clearKnown(x-1, y-1);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {}
            }
        }
        if (this.board.get(x-1).get(y).getState().equals("0")) {
            try {
                if (this.board.get(x-1).get(y).getState().equals("-")) {
                    this.board.get(x-1).get(y).setState("dug");
                    clearKnown(x-1, y);
                }} catch (ArrayIndexOutOfBoundsException e) {}
        }
        if (this.board.get(x-1).get(y+1).getState().equals("0")) {
            try {
                if (this.board.get(x-1).get(y+1).getState().equals("-")) {
                    this.board.get(x-1).get(y+1).setState("dug");
                    clearKnown(x-1, y+1);
                }} catch (ArrayIndexOutOfBoundsException e) {}
        }
        if (this.board.get(x).get(y-1).getState().equals("0")) {
            try {
                if (this.board.get(x).get(y-1).getState().equals("-")) {
                    this.board.get(x).get(y-1).setState("dug");
                    clearKnown(x, y-1);
                }} catch (ArrayIndexOutOfBoundsException e) {}
        }
        if (this.board.get(x).get(y+1).getState().equals("0")) {
            try {
                if (this.board.get(x).get(y+1).getState().equals("-")) {
                    this.board.get(x).get(y+1).setState("dug");
                    clearKnown(x, y+1);
                }} catch (ArrayIndexOutOfBoundsException e) {}
        }
        if (this.board.get(x+1).get(y-1).getState().equals("0")) {
            try{ 
                if (this.board.get(x+1).get(y-1).getState().equals("-")) {
                    this.board.get(x+1).get(y-1).setState("dug");
                    clearKnown(x+1, y-1);
                }} catch (ArrayIndexOutOfBoundsException e) {}
        }
        if (this.board.get(x+1).get(y).getState().equals("0")) {
            if (this.board.get(x+1).get(y).getState().equals("-")) {
                this.board.get(x+1).get(y).setState("dug");
                clearKnown(x+1, y);
            }
        }
        if (this.board.get(x+1).get(y+1).getState().equals("0")) {
            try {
                if (this.board.get(x+1).get(y+1).getState().equals("-")) {
                    this.board.get(x+1).get(y+1).setState("dug");
                    clearKnown(x+1, y+1);
                }} catch (ArrayIndexOutOfBoundsException e) {}
        }

    }



}

      







    
 