package lindenmayer.lsystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Symbol {
    private char character;

    public Symbol(){};

    public Symbol(char sym){
        this.character = sym;
    }

    public void setCharacter(char axiom){
        this.character = axiom;
    }

    public char getCharacter(){
        return this.character;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.character);
    }

    @Override
    public boolean equals(Object obj) {
        return this.character == ((Symbol) obj).character;
    }

    @Override
    public String toString(){
        return String.valueOf(character);
    }



    /*
       Collection of Symbols
     */


    static class Seq implements Iterable<Symbol>{
        List<Symbol> seq = new ArrayList<>();

        @Override
        public Iterator<Symbol> iterator() {
            return this.seq.iterator();
        }

        @Override
        public String toString(){
            StringBuilder stringBuilder = new StringBuilder();
            seq.forEach(symbol -> {
                stringBuilder.append(String.valueOf(symbol.character));
            });
            return stringBuilder.toString();
        }

        public void addAll(Seq seq){
            this.seq.addAll(seq.seq);
        }
    }
}
