package minesweeper.server;
/**
 * Square class for MineSweeper. Goes into 2d Vector
 * @author Philippe
 *
 */
public class Square {
    private int value;
    private String state;
    private boolean hasBomb;
    
    
    public Square(boolean hasBomb) {
        this.hasBomb = hasBomb;
        this.state = "-"; // States: "-" | "flag" | "dug"| 
        this.value = 0;
    }
    
    
    public void setValue(int s) {
        this.value = s;
    }
    
    public String getValue() {
        
        return Integer.toString(this.value);
    }
    
    public void setMine(boolean b) {
        this.hasBomb = b;
    }
    
    public boolean getMine() {
        return this.hasBomb;
    }
    
    public void setState(String s) {
        this.state = s;
    }
    
    public String getState() {
        return this.state;
        
    }
}
