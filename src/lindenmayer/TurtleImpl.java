package lindenmayer;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TurtleImpl implements Turtle {

    TurtleState currentState;
    Stack<TurtleState> turtleStates;
    double step;
    double delta;

    public double minX, maxX, minY, maxY;

    public List<Line2D.Double> lines = new ArrayList<>();

    @Override
    public void draw() {
        double currentX = this.currentState.position.getX();
        double currentY = this.currentState.position.getY();

        double nextX = currentX + (step * Math.cos(currentState.getAngle()));
        double nextY = currentY + (step * Math.sin(currentState.getAngle()));

        minX = Math.min(minX, nextX);
        maxX = Math.max(maxX, nextX);
        minY = Math.min(minY, nextY);
        maxY = Math.max(maxY, nextY);
        lines.add(new Line2D.Double(currentX, currentY, nextX, nextY));
        this.currentState.setPosition(new Point2D.Double(nextX, nextY));
    }

    @Override
    public void move() {
        System.out.println("moving");

    }

    @Override
    public void turnR() {
        double nextAngle = this.currentState.getAngle() + delta;
        this.currentState = new TurtleState(currentState.getPosition(), nextAngle);
        //this.currentState.setAngle(nextAngle);

    }

    @Override
    public void turnL() {
        double nextAngle = this.currentState.getAngle() - delta;
        this.currentState = new TurtleState(currentState.getPosition(), nextAngle);
        //this.currentState.setAngle(nextAngle);
    }

    @Override
    public void push() {
        /*when the code reaches '[' symbol on the string then push the current state
          to stack to come back to this on reaching ']'
         */
        System.out.println("Pushing Current state is : " + currentState.getPosition().toString() + " Angle : " + currentState.getAngle());
        TurtleState pushState = new TurtleState();
        pushState.setPosition(currentState.getPosition());
        pushState.setAngle(currentState.getAngle());
        turtleStates.push(pushState);
    }

    @Override
    public void pop() {
        /*
            after reaching the ']' symbol it means i have reached then end of one path
            now go back to initial sate and draw in different direction
         */
        TurtleState state = turtleStates.pop();
        System.out.println("Popping Current state is : " + state.getPosition().toString());
        currentState = state;
    }

    @Override
    public void stay() {

    }

    @Override
    public void init(Point2D position, double angle_deg) {
        this.currentState = new TurtleState();
        currentState.setAngle(Math.toRadians(angle_deg));
        currentState.setPosition(position);
        minX = position.getX();
        maxX = position.getX();
        minY = position.getY();
        maxY = position.getY();

        this.turtleStates = new Stack<>();
    }

    @Override
    public Point2D getPosition() {
        return currentState.getPosition();
    }

    @Override
    public double getAngle() {
        return currentState.getAngle();
    }

    @Override
    public void setUnits(double step, double delta) {
        this.step = step;
        this.delta = Math.toRadians(delta);

    }

    class TurtleState{
        Point2D position;
        double angle;

        public TurtleState(){};

        public TurtleState(Point2D position, double angle){
            this.position = position;
            this.angle = angle;
        }

        public void setPosition(Point2D position){
            this.position = position;
        }

        public Point2D getPosition(){
            return this.position;
        }

        public void setAngle(double angle){
            this.angle = angle;
        }

        public double getAngle(){
            return this.angle;
        }
    }
}
