package socialnetwork.Algorithm;

import socialnetwork.domain.Utilizator;

public class ElementGraph extends EntityGraph<Integer> {
    public Utilizator element;
    public ElementGraph(Utilizator element){
        this.element = element;
    }
    public Utilizator getElement(){
        return this.element;
    }
}
