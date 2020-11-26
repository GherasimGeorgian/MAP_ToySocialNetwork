package socialnetwork.service;

import socialnetwork.Algorithm.ElementGraph;
import socialnetwork.Algorithm.Graph;
import socialnetwork.domain.*;
import socialnetwork.repository.RepositoryOptional;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.PrietenieDTOChangeEvent;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import static java.util.function.Predicate.not;

public class UserServiceFullDB implements Observable<PrietenieDTOChangeEvent> {
    private RepositoryOptional<Long, Utilizator> userDataBase;
    private RepositoryOptional<Tuple<Long,Long>, Prietenie> repoPrietenie;
    private RepositoryOptional<Long, Message> messageRepo;
    private RepositoryOptional<Long, Invite> inviteRepo;

    public UserServiceFullDB(RepositoryOptional<Long,Utilizator> userDataBase,
                             RepositoryOptional<Tuple<Long,Long>, Prietenie> repoPtr,
                             RepositoryOptional<Long, Message> messageRepo,
                             RepositoryOptional<Long, Invite> inviteRepo) {
        this.userDataBase = userDataBase;
        this.repoPrietenie = repoPtr;
        this.messageRepo = messageRepo;
        this.inviteRepo = inviteRepo;
        connectPrietenii();
    }



    private List<Observer<PrietenieDTOChangeEvent>> observers=new ArrayList<>();


    @Override
    public void addObserver(Observer<PrietenieDTOChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<PrietenieDTOChangeEvent> e) {
        //observers.remove(e);
    }
    @Override
    public void notifyObservers(PrietenieDTOChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
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

        //TODO
        //trebuie sa sterg toate mesajele de la userul sters
        //trebuie sa sterg toate invitatiile de la userul sters
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




        Prietenie ptr;
        if(repoPrietenie.findOne(new Tuple<>(gasitUser1.getId(),gasitUser2.getId())).isPresent()) {
            ptr = repoPrietenie.delete(new Tuple<>(gasitUser1.getId(),gasitUser2.getId())).get();
        }
        else{
            ptr =  repoPrietenie.delete(new Tuple<>(gasitUser2.getId(),gasitUser1.getId())).get();
        }

        this.connectPrietenii();
        notifyObservers(new PrietenieDTOChangeEvent(ChangeEventType.DELETE, null));

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
        String errors = new String();
        if(firstNameUser1.isEmpty()){
            errors+="|FirstName is empty!|";
        }
        if(lastNameUser1.isEmpty()){
            errors+="|LastName is empty!|";
        }
        if(errors.length()>0){
            throw new ServiceException(errors);
        }

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

    public List<PrietenieDTO> relatiiUserFiltrate(String firstNameUser1,String lastNameUser1,Integer luna) {
        String errors = new String();
        if(firstNameUser1.isEmpty()){
            errors+="|FirstName is empty!|";
        }
        if(lastNameUser1.isEmpty()){
            errors+="|LastName is empty!|";
        }
        if(luna < 0){
            errors+="|Month is negative!|";
        }
        if(errors.length()>0){
            throw new ServiceException(errors);
        }

        Predicate<Utilizator> byFirstName= x->x.getFirstName().equals(firstNameUser1);
        Predicate<Utilizator> byLastName= x->x.getLastName().equals(lastNameUser1);
        Predicate<PrietenieDTO> byMonth = x->x.getDate().getMonthValue() == luna;

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
                    .filter(byMonth)
                    .collect(Collectors.toList());

        }
        catch(IllegalStateException ex){
            throw new ServiceException("Utilizatorul nu a fost gasit!");
        }

        return allUsers;
    }

    public List<Message> afisareConversatii(String firstNameUser1,String lastNameUser1, String firstNameUser2,String lastNameUser2){
        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new ServiceException("Userul1 nu exista!");
        }
        Utilizator gasitUser2 = this.findByNumePrenume(firstNameUser2, lastNameUser2);
        if(gasitUser2 == null){
            throw new ServiceException("Userul2 nu exista!");
        }

        // mesajele de la user1 la user2
        Predicate<Message> fromUser1=x->x.getFrom().getId().toString().equals(gasitUser1.getId().toString());
        Predicate<Message> toUser2=x->x.getTo().contains(gasitUser2);
        List<Message> messagesUser1 = StreamSupport.stream(messageRepo.findAll().spliterator(), false)
                .filter(fromUser1)
                .filter(toUser2)
                .collect(Collectors.toList());

        // mesajele de la user2 la user1
        Predicate<Message> fromUser2=x->x.getFrom().getId().toString().equals(gasitUser2.getId().toString());
        Predicate<Message> toUser1=x->x.getTo().contains(gasitUser1);
        List<Message> messagesUser2 = StreamSupport.stream(messageRepo.findAll().spliterator(), false)
                .filter(fromUser2)
                .filter(toUser1)
                .collect(Collectors.toList());

        //sortam mesajele de la ambii utilizatori in fucntie de data
        List<Message> sortedList =  Stream.concat(messagesUser1.stream(), messagesUser2.stream())
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());

        return sortedList;

    }
    public Long createIdMessage(){
        do{
            boolean ok = true;
            Long id = new Random().nextLong();
            if(id< 0){
                id *= -1;
            }
            for(Message u : messageRepo.findAll()){
                if(id == u.getId()){
                    ok =false;
                    break;
                }
            }
            if(ok)
                return id;
        }while(true);
    }
    public void trimiteMesaj(String firstNameUser1,String lastNameUser1,List<AbstractMap.SimpleImmutableEntry<String, String>> useri,String mesaj,Long idreply){
        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new ServiceException("Userul1 nu exista!");
        }
        List<Utilizator> utilizatoriTo = new ArrayList<>();
        for(int i=0;i<useri.size();i++){

            Utilizator gasit = this.findByNumePrenume(useri.get(i).getKey(), useri.get(i).getValue());
            if(gasitUser1 == null){
                throw new ServiceException("Userul cu numele +" + useri.get(i).getKey() + " " +  useri.get(i).getValue()+ " nu exista !");
            }
            utilizatoriTo.add(gasit);
        }
        Message msg;
        if(idreply == -1){
             msg = new Message(createIdMessage(),gasitUser1,utilizatoriTo,mesaj,LocalDateTime.now());
        }
        else{
            Message mesajRaspuns=messageRepo.findOne(idreply).get();
            Message mesajBaza = new Message(createIdMessage(),gasitUser1,utilizatoriTo,mesaj,LocalDateTime.now());
            msg = new ReplyMessage(mesajBaza,mesajRaspuns.getId(),mesajRaspuns.getFrom(),mesajRaspuns.getTo(),mesajRaspuns.getMessage(),mesajRaspuns.getDate());
           }
        messageRepo.save(msg);
    }
    public void trimiteInvitatie(String firstNameUser1,String lastNameUser1, String firstNameUser2,String lastNameUser2){
        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new ServiceException("Userul1 nu exista!");
        }
        Utilizator gasitUser2 = this.findByNumePrenume(firstNameUser2, lastNameUser2);
        if(gasitUser2 == null){
            throw new ServiceException("Userul2 nu exista!");
        }

        //verificam daca este deja prieten x cu y
        Optional<Prietenie> prietenie1 = repoPrietenie.findOne(new Tuple<>(gasitUser1.getId(),gasitUser2.getId()));
        Optional<Prietenie> prietenie2 = repoPrietenie.findOne(new Tuple<>(gasitUser2.getId(),gasitUser1.getId()));
        if(prietenie1.isEmpty() && prietenie2.isEmpty()){
            Invite invitatie = new Invite(createIdInvite(),gasitUser1,gasitUser2,LocalDateTime.now(),InviteStatus.PENDING);
            inviteRepo.save(invitatie);
        }
        else{
            throw new ServiceException("Cei 2 useri sunt deja prieteni!");
        }


    }

    public List<Invite> invitationsByUser(String firstNameUser1,String lastNameUser1){
        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new ServiceException("Userul1 nu exista!");
        }
        Predicate<Invite> toUser=x->x.getToInvite().equals(gasitUser1);
        Predicate<Invite> statusInv = x->x.getStatus().equals(InviteStatus.PENDING);
        List<Invite> invitationsUser = StreamSupport.stream(inviteRepo.findAll().spliterator(), false)
                .filter(toUser)
                .filter(statusInv)
                .collect(Collectors.toList());
        return invitationsUser;
    }
    public Long createIdInvite(){
        do{
            boolean ok = true;
            Long id = new Random().nextLong();
            if(id< 0){
                id *= -1;
            }
            for(Invite u : inviteRepo.findAll()){
                if(id == u.getId()){
                    ok =false;
                    break;
                }
            }
            if(ok)
                return id;
        }while(true);
    }
    public void acceptaInvitatie(Long idInvite){
        //modificare stare din pending in approved
        Optional<Invite> invitatie = inviteRepo.findOne(idInvite);
        if(invitatie.isEmpty()){
            throw new ServiceException("Invitatia introdusa nu exista!");
        }
        invitatie.get().setStatus(InviteStatus.APPROVED);
        Optional<Invite> invite =  inviteRepo.update(invitatie.get());

        //adaugare prietenii respective
        if(invite.isPresent()){
            Prietenie ptr_noua = new Prietenie(invite.get().getFromInvite().getId(),invite.get().getToInvite().getId(),LocalDateTime.now());
            repoPrietenie.save(ptr_noua);
            connectPrietenii();
        }

    }
    public void stergeInvitatie(Long idInvite){
        Optional<Invite> invitatie = inviteRepo.findOne(idInvite);
        if(invitatie.isEmpty()){
            throw new ServiceException("Invitatia introdusa nu exista!");
        }
        invitatie.get().setStatus(InviteStatus.REJECTED);
        Optional<Invite> invite =  inviteRepo.update(invitatie.get());

    }

    /**
     *
     * @param firstNameUser1
     * @param lastNameUser1
     * @return List<Utilizator> lista utilizatorilor care nu sunt prieteni cu Utilizator(firstNameU1,lastNameU2)
     */
    public List<Utilizator> notRelatiiUser(String firstNameUser1,String lastNameUser1){

        String errors = new String();
        if(firstNameUser1.isEmpty()){
            errors+="|FirstName is empty!|";
        }
        if(lastNameUser1.isEmpty()){
            errors+="|LastName is empty!|";
        }
        if(errors.length()>0){
            throw new ServiceException(errors);
        }
        Utilizator resultUser;
        Predicate<Utilizator> byFirstName= x->x.getFirstName().equals(firstNameUser1);
        Predicate<Utilizator> byLastName= x->x.getLastName().equals(lastNameUser1);
        List<Utilizator> unFriends = new ArrayList<>();
        try {
             resultUser = StreamSupport.stream(userDataBase.findAll().spliterator(), false)
                    .filter(byFirstName)
                    .filter(byLastName)
                    .collect(toSingleton());
           for(Utilizator user :userDataBase.findAll()){
               if(!resultUser.getFriends().contains(user)){
                   unFriends.add(user);
               }
           }

        }
        catch(IllegalStateException ex){
            throw new ServiceException("Utilizatorul nu a fost gasit!");
        }
        return unFriends;

    }

    /**
     *
     * @param firstNameUser1
     * @param lastNameUser1
     * @param firstNameUser2
     * @param lastNameUser2
     * @return Invitatia de la Utilizator(fistName1 lastName1) pentru Util;izator(firstName2 lastName2) dace exista sau null daca nu exista
     */
    public Invite findInvitebytwoUsers(String firstNameUser1, String lastNameUser1,String firstNameUser2, String lastNameUser2){

        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new ServiceException("Userul1 nu exista!");
        }
        Utilizator gasitUser2 = this.findByNumePrenume(firstNameUser2, lastNameUser2);
        if(gasitUser2 == null){
            throw new ServiceException("Userul2 nu exista!");
        }

        for(Invite u : inviteRepo.findAll()){
            if(gasitUser1.getId().toString().equals(u.getFromInvite().getId().toString()) &&
                    gasitUser2.getId().toString().equals(u.getToInvite().getId().toString())){
                return u;
            }
        }
        return null;
    }

}
