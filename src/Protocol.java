
public enum Protocol { 
	C_USER_LOGIN("!@"),
	S_USER_REPEAT("@#"),
	S_USER_SUCCESS("#$"),
	LENGTH("2");
    private final String tag;       
 
    private Protocol(String tag) {
        this.tag = tag;
    } 
 
    public boolean equalsTag(String otherTag) {
        // (otherName == null) check is not needed because name.equals(null) returns false  
        return this.tag.equals(otherTag);
    } 
 
    public String toString() {
       return this.tag;
    } 
} 