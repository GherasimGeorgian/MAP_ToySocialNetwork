package socialnetwork.service;

        import jdk.jshell.execution.Util;
        import socialnetwork.Algorithm.ElementGraph;
        import socialnetwork.Algorithm.Graph;
        import socialnetwork.domain.Prietenie;
        import socialnetwork.domain.PrietenieDTO;
        import socialnetwork.domain.Tuple;
        import socialnetwork.domain.Utilizator;
        import socialnetwork.repository.Repository;
        import socialnetwork.repository.RepositoryOptional;

        import java.time.LocalDateTime;
        import java.util.*;
        import java.util.function.Predicate;
        import java.util.stream.Collector;
        import java.util.stream.Collectors;
        import java.util.stream.StreamSupport;

public class UserServiceFullDB {
    private RepositoryOptional<Long, Utilizator> userDataBase;
    private RepositoryOptional<Tuple<Long,Long>, Prietenie> repoPrietenie;

    public UserServiceFullDB(RepositoryOptional<Long,Utilizator> userDataBase,RepositoryOptional<Tuple<Long,Long>, Prietenie> repoPtr) {
        this.userDataBase = userDataBase;
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
            userDataBase.save(utilizator_nou);
            return utilizator_nou;
        }
    }
    public Long createId(){
        do{
            boolean ok = true;
            Long id = new Random().nextLong();
            if(id< 0){
                id *= -1;
            }
            for(Utilizator u : userDataBase.findAll()){
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
        return userDataBase.findAll();
    }
    public Utilizator stergeUtilizator(String firstName, String lastName) throws Exception{

        if(firstName.isEmpty()){
            throw new Exception("FirstName is empty!");
        }
        if(lastName.isEmpty()){
            throw new Exception("LastName is empty!");
        }
        Utilizator gasit = this.findByNumePrenume(firstName, lastName);
        if(gasit == null){
            throw new Exception("Nu exista userul cu numele"+ firstName +"!");
        }
        else{
            Map<Tuple<Long,Long>, Prietenie> map = new HashMap<>();
            for(Prietenie p : repoPrietenie.findAll()) {
                if((p.getId().getLeft().toString().equals(gasit.getId().toString()) || p.getId().getRight().toString().equals(gasit.getId().toString()))) {
                   // map.put(p.getId(), p);
                    repoPrietenie.delete(p.getId());
                }
            }
            //repoPrietenie.changeEntities(map);
            this.connectPrietenii();
            return userDataBase.delete(gasit.getId()).get();
        }
    }

    public void connectPrietenii(){
        for(Utilizator user: this.getAllUsers()){
            user.deleteAllFriends();
        }


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
        return userDataBase.findOne(id).get();
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

        Prietenie ptr2 = new Prietenie(gasitUser2.getId(), gasitUser1.getId(), LocalDateTime.now());
        if(repoPrietenie.findOne(ptr2.getId()).isEmpty()) {
            repoPrietenie.save(ptr);
        }
        this.connectPrietenii();
        return ptr;
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

        Prietenie ptr = repoPrietenie.delete(new Tuple<>(gasitUser1.getId(),gasitUser2.getId())).get();
        this.connectPrietenii();
        return ptr;
    }
    public List<Integer> keys = new ArrayList<Integer>();


    public Integer numarComunitati(){
        Graph g = new Graph(((Collection<?>)userDataBase.findAll()).size());
        Map<Long, ElementGraph> entities = new HashMap<>();
        int i=0;
        for(Utilizator u: userDataBase.findAll()){
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
        Graph g = new Graph(((Collection<?>)userDataBase.findAll()).size());
        Map<Long,ElementGraph> entities = new HashMap<>();
        int i=0;
        for(Utilizator u: userDataBase.findAll()){
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
    public static <T> Collector<T, ?, T> toSingleton() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }
    public List<PrietenieDTO> relatiiUser(String firstNameUser1,String lastNameUser1){
        Predicate<Utilizator> byFirstName= x->x.getFirstName().equals(firstNameUser1);
        Predicate<Utilizator> byLastName= x->x.getLastName().equals(lastNameUser1);
        List<PrietenieDTO> allUsers;
      try {
           Utilizator resultUser = StreamSupport.stream(userDataBase.findAll().spliterator(), false)
                .filter(byFirstName)
                .filter(byLastName)
                .collect(toSingleton());
           allUsers = StreamSupport.stream(resultUser.getFriends().spliterator(), false)
                  .map(x->new PrietenieDTO(x.getFirstName(),x.getLastName(),
                          StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                                  .filter(a -> a.getId().getLeft().toString().equals(resultUser.getId().toString())
                                          || a.getId().getRight().toString().equals(resultUser.getId().toString()))
                                  .collect(Collectors.toList()).get(0).getDate()
                  ))
                  .collect(Collectors.toList());

      }
       catch(IllegalStateException ex){
           throw new ServiceException("Utilizatorul nu a fost gasit!");
        }

        return allUsers;
    }
}
