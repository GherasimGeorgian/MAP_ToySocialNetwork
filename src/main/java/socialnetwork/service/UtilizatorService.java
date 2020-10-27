package socialnetwork.service;

import socialnetwork.Algorithm.ElementGraph;
import socialnetwork.Algorithm.Graph;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;

import java.time.LocalDateTime;
import java.util.*;

public class UtilizatorService {
    private Repository<Long, Utilizator> repoUser;
    private Repository<Tuple<Long,Long>, Prietenie> repoPrietenie;

    public UtilizatorService(Repository<Long, Utilizator> repoUsr,Repository<Tuple<Long,Long>, Prietenie> repoPtr) {
        this.repoUser = repoUsr;
        this.repoPrietenie = repoPtr;
        connectPrietenii();
    }

    public Utilizator addUtilizator(String firstName,String lastName) throws Exception{


        Utilizator utilizator_nou = new Utilizator(firstName,lastName);
        utilizator_nou.setId(this.createId());
        Utilizator gasit = this.findByNumePrenume(firstName,lastName);
        if(gasit !=null){
            throw new Exception("Userul pe care doresti sa-l introduci exista deja!");
        }
        else{
            Utilizator task = repoUser.save(utilizator_nou );
            return task;
        }
    }
    public Long createId(){
        do{
            boolean ok = true;
            Long id = new Random().nextLong();
            if(id< 0){
                id *= -1;
            }
            for(Utilizator u : repoUser.findAll()){
                if(id == u.getId()){
                    ok =false;
                    break;
                }
            }
            if(ok)
                return id;
        }while(true);
    }
    public Utilizator findByNumePrenume(String nume, String prenume){
        for(Utilizator u : this.getAllUsers()){
            if(u.getFirstName().compareTo(nume) == 0 && u.getLastName().compareTo(prenume) == 0){
                return u;
            }
        }
        return null;
    }
    public Iterable<Utilizator> getAllUsers(){
        return repoUser.findAll();
    }
    public Utilizator stergeUtilizator(String firstName, String lastName) throws Exception{
        Utilizator gasit = this.findByNumePrenume(firstName, lastName);
        if(gasit == null){
            throw new Exception("Nu exista userul cu numele"+ firstName +"!");
        }
        else{
            this.connectPrietenii();
            return repoUser.delete(gasit.getId());
        }


    }


    public void connectPrietenii(){
        this.getAllUsers().forEach(Utilizator::deleteAllFriends);
        Long id1,id2;
        for(Prietenie p : this.getAllFriends()){
            id1 = p.getId().getLeft();
            id2 = p.getId().getRight();
            Utilizator u1 = this.findOneUser(id1);
            Utilizator u2 = this.findOneUser(id2);
            u1.addFriend(u2);
            u2.addFriend(u1);
        }
    }


    public Utilizator findOneUser(long id){
        return repoUser.findOne(id);
    }

    public Iterable<Prietenie> getAllFriends(){
        return repoPrietenie.findAll();
    }
    public Prietenie adaugaPrieten(String firstNameUser1,String lastNameUser1, String firstNameUser2,String lastNameUser2) throws Exception{

        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new Exception("Userul1 nu exista!");
        }
        Utilizator gasitUser2 = this.findByNumePrenume(firstNameUser2, lastNameUser2);
        if(gasitUser2 == null){
            throw new Exception("Userul2 nu exista!");
        }

        Prietenie ptr = new Prietenie(gasitUser1.getId(), gasitUser2.getId(), LocalDateTime.now());
        Prietenie task = repoPrietenie.save(ptr);
        this.connectPrietenii();
        return task;
    }
    public Prietenie stergePrietenie(String firstNameUser1,String lastNameUser1, String firstNameUser2,String lastNameUser2) throws Exception{
        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new Exception("Userul1 nu exista!");
        }
        Utilizator gasitUser2 = this.findByNumePrenume(firstNameUser2, lastNameUser2);
        if(gasitUser2 == null){
            throw new Exception("Userul2 nu exista!");
        }

        Prietenie ptr = repoPrietenie.delete(new Tuple<>(gasitUser1.getId(),gasitUser2.getId()));
        this.connectPrietenii();
        return ptr;
    }
    public List<Integer> keys = new ArrayList<Integer>();


    public Integer numarComunitati(){
        Graph g = new Graph(((Collection<?>)repoUser.findAll()).size());
        Map<Long,ElementGraph> entities = new HashMap<>();
        int i=0;
        for(Utilizator u: repoUser.findAll()){
            ElementGraph elem = new ElementGraph(u);
            elem.setIdGraph(i);
            entities.put(u.getId(),elem);
            i++;
        }
        for(Prietenie p : repoPrietenie.findAll()){
            ElementGraph elem1 = entities.get(p.getId().getLeft());
            ElementGraph elem2 = entities.get(p.getId().getRight());
            g.addEdge((int)elem1.getIdGraph(), (int)elem2.getIdGraph());
        }

        Integer nr_componente = g.connectedComponents();

        for(ArrayList<Integer> listofList : g.getAllConexComponents()){
            for(Integer val : listofList){
                System.out.println(val + " ");
            }
            System.out.println("Next line");
        }
        return nr_componente;
    }


    public ArrayList<ArrayList<String>> comunitateSociabila() {
        Graph g = new Graph(((Collection<?>)repoUser.findAll()).size());
        Map<Long,ElementGraph> entities = new HashMap<>();
        int i=0;
        for(Utilizator u: repoUser.findAll()){
            ElementGraph elem = new ElementGraph(u);
            elem.setIdGraph(i);
            entities.put(u.getId(),elem);
            i++;
        }
        for(Prietenie p : repoPrietenie.findAll()){
            ElementGraph elem1 = entities.get(p.getId().getLeft());
            ElementGraph elem2 = entities.get(p.getId().getRight());
            g.addEdge((int)elem1.getIdGraph(), (int)elem2.getIdGraph());
        }
        Integer nr_componente = g.connectedComponents();
        ArrayList<ArrayList<String>> componente = new ArrayList<ArrayList<String>>();
        ArrayList<String> var;
        for(ArrayList<Integer> listofList : g.getAllConexComponents()){
            var = new ArrayList<String>();
            for(Integer val : listofList){
                for(ElementGraph u : entities.values()){
                    if((int)u.getIdGraph() == val){
                        var.add(u.getElement().getFirstName());
                    }
                }
            }
            componente.add(var);
        }
        return componente;
    }
    public ArrayList<String> biggestArray(ArrayList<ArrayList<String>> param1){
        ArrayList<String> biggestArray = new ArrayList<String>();
        for(ArrayList<String> listCurrent : param1){
            if(listCurrent.size() > biggestArray.size()){
                biggestArray = listCurrent;
            }
        }
        return biggestArray;
    }
    ///TO DO: add other methods
}
