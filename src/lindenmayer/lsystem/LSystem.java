package lindenmayer.lsystem;

import lindenmayer.Frame;
import lindenmayer.Turtle;
import lindenmayer.TurtleImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LSystem {
    Symbol.Seq alphabet;
    Map<Symbol, String> rules;
    Map<Symbol, String> actions;
    Symbol.Seq axiom;

    public LSystem(){};

    public Symbol addSymbol(char sym){
        if(this.alphabet == null ){
            this.alphabet = new Symbol.Seq();
        }
        alphabet.seq.add(new Symbol(sym));
        return alphabet.seq.get(alphabet.seq.size()-1);
    }

    public void addRule(Symbol sym, String expression){
        if(this.rules == null){
            this.rules = new HashMap<>();
        }

        rules.put(sym, expression);
    }

    public void addActions(Symbol sym, String action){
        if(this.actions == null){
            this.actions = new HashMap<>();
        }
        this.actions.put(sym, action);
    }

    public void setAxiom(String axiom){
        if(this.axiom == null){
            this.axiom = new Symbol.Seq();
        }
        this.axiom.seq.add(new Symbol(axiom.charAt(0)));
    }

    public Symbol.Seq getAxiom(){
        return this.axiom;
    }

    public Symbol.Seq rewrite(Symbol sym){
        if(! rules.containsKey(sym)){
            return null;
        }

        Symbol.Seq rewriteSequence = new Symbol.Seq();
        String newSeq = rules.get(sym);
        for(char character : newSeq.toCharArray()){
            rewriteSequence.seq.add(new Symbol(character));
        }
        return rewriteSequence;
    }

    void tell(Turtle turtle, Symbol sym){
        if(! alphabet.seq.contains(sym)){
            // if the symbol is not in the alphabet then return do nothing;
            return;
        }

        if(! actions.containsKey(sym)){
            return;
        }

        String action = actions.get(sym);

        switch (action){
            case "draw": turtle.draw();
                break;
            case "push": turtle.push();
                break;
            case "pop": turtle.pop();
                break;
            case "turnL": turtle.turnL();
                break;
            case "turnR": turtle.turnR();
                break;
            default: break;
        }
    }

    public Symbol.Seq applyRules(Symbol.Seq seq, int n){
        Symbol.Seq resultSeq = seq;
        while(n > 0){
            Iterator<Symbol> seqIterator = resultSeq.iterator();
            Symbol.Seq tempSequence = new Symbol.Seq();
            while(seqIterator.hasNext()){
                Symbol nextSymbol = seqIterator.next();
                Symbol.Seq replacementSeq = rewrite(nextSymbol);
                if(replacementSeq != null){
                    tempSequence.addAll(rewrite(nextSymbol));
                }else{
                    tempSequence.seq.add(nextSymbol);
                }
            }
            resultSeq = tempSequence;
            n--;
        }
        return resultSeq;
    }


    public static void readJSONFile(String file, LSystem S, Turtle T){
        JSONParser jsonParser = new JSONParser();

        try {

            //Read JSON file
            FileReader reader = new FileReader(file);
            Object obj = null;
            obj = jsonParser.parse(reader);

            JSONObject jsonObject = (JSONObject) obj;

            // Parsing the alphabet
            JSONArray alphabetJsonArray =  (JSONArray) jsonObject.get("alphabet");
            ListIterator<String> iterator = alphabetJsonArray.listIterator();
            ArrayList<String> alphabet = new ArrayList<>();
            while(iterator.hasNext()){
                S.addSymbol(iterator.next().charAt(0));
            }

            // Parse Rules
            JSONObject rulesJsonObject = (JSONObject)  jsonObject.get("rules");
            Set<String> rulesKeys = rulesJsonObject.keySet();
            HashMap<String, List<String>> ruleMap = new HashMap<>();
            rulesKeys.forEach(
                    ruleKey -> {
                        JSONArray ruleJson = (JSONArray) rulesJsonObject.get(ruleKey);
                        ListIterator<String> ruleIterator = ruleJson.listIterator();
                        ArrayList<String> rule = new ArrayList<>();
                        while(ruleIterator.hasNext()){
                            rule.add(ruleIterator.next());
                        }
                        S.addRule(new Symbol(ruleKey.charAt(0)), rule.toString().substring(1, rule.toString().length()-1));
                    }
            );

            // Parse axiom
            String axiom = (String) jsonObject.get("axiom");
            S.setAxiom(axiom);

            // Parse action
            JSONObject actionsJson = (JSONObject) jsonObject.get("actions");
            Set<String> actionsKeys = actionsJson.keySet();
            Map<String, String> actionsMap = new HashMap<>();
            actionsKeys.forEach(
                    actionsKey -> {
                        S.addActions(new Symbol(actionsKey.charAt(0)), (String)actionsJson.get(actionsKey));
                    }
            );


            // Parse the parameters of the Turtle
            JSONObject parameterJson = (JSONObject) jsonObject.get("parameters");
            T.setUnits(((Long) parameterJson.get("step")).doubleValue(), (Double) parameterJson.get("angle"));
            JSONArray startJson = (JSONArray) parameterJson.get("start");
            Point2D startPoint = new Point2D.Double();
            startPoint.setLocation(((Long)startJson.get(0)).doubleValue(), ((Long)startJson.get(1)).doubleValue());
            Double startAngle = ((Long)startJson.get(2)).doubleValue();


            ((TurtleImpl)T).init(startPoint, startAngle);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public Rectangle2D tell(Turtle turtle, Symbol.Seq seq, int n){
        Symbol.Seq finalString = applyRules(seq, n);
        Iterator<Symbol> drawingIterator = finalString.iterator();
        while(drawingIterator.hasNext()){
            tell(turtle, drawingIterator.next());
        }

        double width =((TurtleImpl)turtle).maxX - ((TurtleImpl)turtle).minX;
        double height =((TurtleImpl)turtle).maxY - ((TurtleImpl)turtle).minY;

        return new Rectangle2D.Double(((TurtleImpl)turtle).minX, ((TurtleImpl)turtle).minY, width, height);
    }

    public static void main(String args[]){
        TurtleImpl turtle = new TurtleImpl();
        LSystem lSystem = new LSystem();
        readJSONFile(args[0], lSystem, turtle);
        int iterations = Integer.parseInt(args[1]);

        Symbol.Seq resultSeq = lSystem.applyRules(lSystem.getAxiom(), iterations);

        System.out.println(resultSeq);


        Rectangle2D rectangle2D = lSystem.tell(turtle, lSystem.getAxiom(), iterations);


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Frame(((TurtleImpl)turtle).lines, rectangle2D).setVisible(true);
            }
        });



        System.out.println(rectangle2D.toString());
    }

}
