import java.util.*;
/**
 * Write a description of class BuffType here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public enum BuffType  
{
    Shocked(1),
    Burning(2);
    
    private int value;
    private static Map map = new HashMap<>();
    
    private BuffType(int value) {
        this.value = value;
    }
    
    static {
        for (BuffType type : BuffType.values()) {
            map.put(type.value, type);
        }
    }

    public static BuffType valueOf(int type) {
        return (BuffType) map.get(type);
    }
    
    public int getValue() {
        return value;
    }
}
