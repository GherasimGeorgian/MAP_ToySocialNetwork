package socialnetwork.service;

import javafx.scene.control.DatePicker;
import socialnetwork.Algorithm.ElementGraph;
import socialnetwork.Algorithm.Graph;
import socialnetwork.domain.*;
import socialnetwork.repository.RepositoryOptional;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.ChangeEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

public class UserServiceFullDB implements Observable<ChangeEvent> {
    private PagingRepository<Long, Utilizator> userDataBase;
    private PagingRepository<Tuple<Long,Long>, Prietenie> repoPrietenie;
    private PagingRepository<Long, Message> messageRepo;
    private PagingRepository<Long, Invite> inviteRepo;
    private PagingRepository<Long, Eveniment> eventRepo;
    private PagingRepository<Long,Account> accountRepo;
    private PagingRepository<Tuple<Long,Long>,AbonareEveniment> repoAbonareEvent;
    public UserServiceFullDB(PagingRepository<Long,Utilizator> userDataBase,
                             PagingRepository<Tuple<Long,Long>, Prietenie> repoPtr,
                             PagingRepository<Long, Message> messageRepo,
                             PagingRepository<Long, Invite> inviteRepo,
                             PagingRepository<Long, Eveniment> evenimentRepo,
                             PagingRepository<Long,Account> accRepo,
                             PagingRepository<Tuple<Long,Long>,AbonareEveniment> repoAbonareEvent) {
        this.userDataBase = userDataBase;
        this.repoPrietenie = repoPtr;
        this.messageRepo = messageRepo;
        this.inviteRepo = inviteRepo;
        this.eventRepo = evenimentRepo;
        this.accountRepo = accRepo;
        this.repoAbonareEvent = repoAbonareEvent;
        connectPrietenii();

    }

    private int page = 0;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }


    private List<Observer<ChangeEvent>> observers=new ArrayList<>();


    @Override
    public void addObserver(Observer<ChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<ChangeEvent> e) {
        observers.remove(e);
    }
    @Override
    public void notifyObservers(ChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }




    public Set<Utilizator> getUsersOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Utilizator> usersPage = userDataBase.findAll(pageable);
        return usersPage.getContent().collect(Collectors.toSet());
    }
    public Set<Utilizator> getNextUsers() {

        this.page++;
        return getUsersOnPage(this.page);
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
            notifyObservers(new ChangeEvent(ChangeEventType.U_ADD, utilizator_nou));
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
    public List<Utilizator> getAllUsersList(){
        return  StreamSupport.stream(userDataBase.findAll().spliterator(), true).collect(Collectors.toList());
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
            notifyObservers(new ChangeEvent(ChangeEventType.U_DEL, gasit));
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

    public Invite findOneInvite(long id){
        return inviteRepo.findOne(id).get();
    }
    public Eveniment findOneEvent(long id){
        return eventRepo.findOne(id).get();
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
        notifyObservers(new ChangeEvent(ChangeEventType.P_ADD, ptr));
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
        notifyObservers(new ChangeEvent(ChangeEventType.P_DEL, ptr,gasitUser1));

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
    public List<Message> afisareConversatiiMultipleUsers(String firstNameUser1,String lastNameUser1, List<Utilizator> users){
        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new ServiceException("Userul1 nu exista!");
        }
        //mesajele de la user1 la user2.3.4
        Predicate<Message> fromUser1=x->x.getFrom().getId().toString().equals(gasitUser1.getId().toString());
        Predicate<Message> toUser2=x->x.getTo().containsAll(users);
        List<Message> messagesUser1 = StreamSupport.stream(messageRepo.findAll().spliterator(), false)
                .filter(fromUser1)
                .filter(toUser2)
                .collect(Collectors.toList());



        //mesajele de la user2 la user1.3.4
        //mesajele de la user3 la user1.2.4
        //mesajele de la user4 la user1.2.3
        for(Utilizator user: users){
            List<Utilizator> userssTo = new ArrayList<>();
            userssTo.add(gasitUser1);
            for (Utilizator usr : users){
                if(!usr.getId().toString().equals(user.getId().toString())){
                    userssTo.add(usr);
                }
            }
            Predicate<Message> fromUserR=x->x.getFrom().getId().toString().equals(user.getId().toString());
            Predicate<Message> toUserR=x->x.getTo().containsAll(userssTo);
            List<Message> messagesUserR = StreamSupport.stream(messageRepo.findAll().spliterator(), false)
                    .filter(fromUserR)
                    .filter(toUserR)
                    .collect(Collectors.toList());
            messagesUser1.addAll(messagesUserR);
        }




        //sortam mesajele de la ambii utilizatori in fucntie de data
        List<Message> sortedList =  messagesUser1.stream()
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
    public Message trimiteMesaj(String firstNameUser1,String lastNameUser1,List<AbstractMap.SimpleImmutableEntry<String, String>> useri,String mesaj,Long idreply){
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
        notifyObservers(new ChangeEvent(ChangeEventType.M_ADD, msg));
        return msg;
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
            Invite invitatie = new Invite(createIdInvite(),gasitUser1,gasitUser2, LocalDateTime.now(),InviteStatus.PENDING);
            inviteRepo.save(invitatie);
            notifyObservers(new ChangeEvent(ChangeEventType.I_ADD, invitatie));
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
        Predicate<Invite> toUser= x->x.getToInvite().equals(gasitUser1);
        Predicate<Invite> statusInv = x->x.getStatus().equals(InviteStatus.PENDING);
        List<Invite> invitationsUser = StreamSupport.stream(inviteRepo.findAll().spliterator(), false)
                .filter(toUser)
                .filter(statusInv)
                .collect(Collectors.toList());
        return invitationsUser;
    }
    public List<Invite> allinvitationsByUser(String firstNameUser1,String lastNameUser1){
        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new ServiceException("Userul1 nu exista!");
        }
        Predicate<Invite> toUser= x->x.getFromInvite().equals(gasitUser1);
        Predicate<Invite> pending= x->x.getStatus().equals(InviteStatus.PENDING);
        List<Invite> invitationsUser = StreamSupport.stream(inviteRepo.findAll().spliterator(), false)
                .filter(toUser)
                .filter(pending)
                .collect(Collectors.toList());
        return invitationsUser;
    }
    public List<Invite> allinvitationsToUsers(String firstNameUser1,String lastNameUser1){
        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new ServiceException("Userul1 nu exista!");
        }
        Predicate<Invite> toUser= x->x.getToInvite().equals(gasitUser1);
        Predicate<Invite> pending= x->x.getStatus().equals(InviteStatus.PENDING);
        List<Invite> invitationsUser = StreamSupport.stream(inviteRepo.findAll().spliterator(), false)
                .filter(toUser)
                .filter(pending)
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
        Prietenie ptr_noua = null;
        if(invite.isPresent()){
            ptr_noua = new Prietenie(invite.get().getToInvite().getId(),invite.get().getFromInvite().getId(),LocalDateTime.now());
            repoPrietenie.save(ptr_noua);
            connectPrietenii();
        }
        notifyObservers(new ChangeEvent(ChangeEventType.P_ADD, ptr_noua));

    }
    public Invite stergeInvitatie(Long id) throws Exception{

        if(id == null){
            throw new Exception("Id-ul invitatiei nu exista!");
        }
        Invite invite= inviteRepo.delete(new Long(id)).get();

        notifyObservers(new ChangeEvent(ChangeEventType.I_DEL, invite));

        return invite;
    }
    public Invite rejectInvite(Invite invite) throws Exception{
        if(invite == null){
            throw new Exception("Invitatia nu exista!");
        }
        invite.setStatus(InviteStatus.REJECTED);

        Invite inviteRez = inviteRepo.update(invite).get();
        notifyObservers(new ChangeEvent(ChangeEventType.I_UPD, invite));
        return inviteRez;
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
               if(!resultUser.getFriends().contains(user) && (user.getFirstName() != firstNameUser1 && user.getLastName() != lastNameUser1 )){
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
                    gasitUser2.getId().toString().equals(u.getToInvite().getId().toString()) &&
            u.getStatus() ==InviteStatus.PENDING){
                return u;
            }
        }
        return null;
    }


    public List<PrietenieDTO> filtreazaPrieteniiInterval(String firstNameUser1, String lastNameUser1, LocalDate date1, LocalDate date2) {
        LocalDateTime ld1 = date1.atStartOfDay();
        LocalDateTime ld2 = date2.atStartOfDay();

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
        Predicate<PrietenieDTO> dateCar1 = x->x.getDate().isBefore(ld2);
        Predicate<PrietenieDTO> dateCar2 = x->x.getDate().isAfter(ld1);
        System.out.println("ceva");
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
                    .filter(dateCar1)
                    .filter(dateCar2)
                    .collect(Collectors.toList());

        }
        catch(IllegalStateException ex){
            throw new ServiceException("Utilizatorul nu a fost gasit!");
        }

        return allUsers;
    }


    public List<Message> mesajePrimiteDeUnUserInterval(String firstNameUser1,String lastNameUser1, LocalDate date1, LocalDate date2){
        LocalDateTime ld1 = date1.atStartOfDay();
        LocalDateTime ld2 = date2.atStartOfDay();
        Predicate<Message> dateCar1 = x->x.getDate().isBefore(ld2);
        Predicate<Message> dateCar2 = x->x.getDate().isAfter(ld1);
        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new ServiceException("Userul1 nu exista!");
        }


        // mesajele de la user1 la user2
        Predicate<Message> toUser1=x->x.getTo().contains(gasitUser1);


        List<Message> messagesUser1 = StreamSupport.stream(messageRepo.findAll().spliterator(), false)
                .filter(toUser1)
                .filter(dateCar1)
                .filter(dateCar2)
                .collect(Collectors.toList());


        List<Message> sortedList =  messagesUser1.stream()
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());

        return sortedList;

    }

    public List<Message> mesajePrimiteFromOneUserInterval(String firstNameUser1,String lastNameUser1,String firstNameUser2,String lastNameUser2, LocalDate date1, LocalDate date2){
        LocalDateTime ld1 = date1.atStartOfDay();
        LocalDateTime ld2 = date2.atStartOfDay();
        Predicate<Message> dateCar1 = x->x.getDate().isBefore(ld2);
        Predicate<Message> dateCar2 = x->x.getDate().isAfter(ld1);
        Utilizator gasitUser1 = this.findByNumePrenume(firstNameUser1, lastNameUser1);
        if(gasitUser1 == null){
            throw new ServiceException("Userul1 nu exista!");
        }

        Utilizator gasitUser2 = this.findByNumePrenume(firstNameUser2, lastNameUser2);
        if(gasitUser2 == null){
            throw new ServiceException("Userul1 nu exista!");
        }



        Predicate<Message> fromUser2 = x->x.getFrom().getId().toString().equals(gasitUser2.getId().toString());
        Predicate<Message> toUser1=x->x.getTo().contains(gasitUser1);


        List<Message> messagesUser1 = StreamSupport.stream(messageRepo.findAll().spliterator(), false)
                .filter(toUser1)
                .filter(fromUser2)
                .filter(dateCar1)
                .filter(dateCar2)
                .collect(Collectors.toList());


        List<Message> sortedList =  messagesUser1.stream()
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());

        return sortedList;

    }

    public Long createIdEvent(){
        do{
            boolean ok = true;
            Long id = new Random().nextLong();
            if(id< 0){
                id *= -1;
            }
            for(Eveniment u : eventRepo.findAll()){
                if(id == u.getId()){
                    ok =false;
                    break;
                }
            }
            if(ok)
                return id;
        }while(true);
    }
    public Eveniment createEvent(String nameEvent, LocalDateTime date_event){
        Eveniment event = new Eveniment(createIdEvent(),nameEvent,date_event);
        if(eventRepo.save(event).isEmpty()){
            notifyObservers(new ChangeEvent(ChangeEventType.E_ADD, event));
           return eventRepo.findOne(event.getId()).get();
        }
        return null;
    }
    public Iterable<Eveniment> getAllEvents(){
        return eventRepo.findAll();
    }
    public Long createIdAccount(){
        do{
            boolean ok = true;
            Long id = new Random().nextLong();
            if(id< 0){
                id *= -1;
            }
            for(Account u : accountRepo.findAll()){
                if(id == u.getId()){
                    ok =false;
                    break;
                }
            }
            if(ok)
                return id;
        }while(true);
    }

    public Account createAccount(String firstName, String lastName, String Email, String parola, String tipCont){
        Utilizator user = findByNumePrenume(firstName,lastName);
        Utilizator userCreated;
        Account acc= null;
        if(user != null){
            //daca userul exista deja
            return null;
        }
        else{
            Long id_user = createIdAccount();

             try{
                 userCreated = addUtilizator(firstName, lastName);
                 acc =new Account(userCreated.getId(),LocalDateTime.now(),Email,parola,tipCont,"null");
                 accountRepo.save(acc);

            }catch(Exception ex){
                return null;
            }

            return acc;
        }

    }


    public Account findByEmailAndPassword(String email, String password){
        for(Account u : accountRepo.findAll()){
            if(u.getEmail().compareTo(email) == 0 && u.getParola().compareTo(password) == 0){
                return u;
            }
        }
        return null;
    }
    public Account findAccountByUserName(Utilizator user){
        for(Account u : accountRepo.findAll()){
            if(u.getId().toString().equals(user.getId().toString())){
                return u;
            }
        }
        return null;
    }
    public Account getAccountByUserId(Long id){
        for(Account u : accountRepo.findAll()){
            if(u.getId().toString().equals(id.toString())){
                return u;
            }
        }
        return null;
    }

    public void updateAccount(Account account){
        accountRepo.update(account);
    }

    public Set<Eveniment> getEventsOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, 2);
        Page<Eveniment> eventsPage = eventRepo.findAll(pageable);

        return eventsPage.getContent().collect(Collectors.toSet());

    }
    public Set<Eveniment> getNextEvents() {
        this.page++;
        return getEventsOnPage(this.page);
    }
    public Iterable<AbonareEveniment> abonatiEvenimente(){
        return repoAbonareEvent.findAll();
    }
    public Long createIdAbonare(){
        do{
            boolean ok = true;
            Long id = new Random().nextLong();
            if(id< 0){
                id *= -1;
            }
            for(AbonareEveniment u : repoAbonareEvent.findAll()){
                if(id == u.getIdAbonare()){
                    ok =false;
                    break;
                }
            }
            if(ok)
                return id;
        }while(true);
    }

    public AbonareEveniment findAbonarebyEventAndUser(Eveniment event,Utilizator user){
        for(AbonareEveniment u : repoAbonareEvent.findAll()){
            if(u.getIdUtilizator().toString().compareTo(user.getId().toString()) == 0 && u.getIdEveniment().toString().compareTo(event.getId().toString()) == 0){
                return u;
            }
        }
        return null;
    }
    public AbonareEveniment abonareEveniment(Eveniment event,Utilizator user){
        AbonareEveniment gasitAbonare = findAbonarebyEventAndUser(event,user);
        if(gasitAbonare == null){
            AbonareEveniment abonare = new AbonareEveniment(createIdAbonare(),user.getId(),event.getId(),LocalDateTime.now());
            repoAbonareEvent.save(abonare);
            notifyObservers(new ChangeEvent(ChangeEventType.AE_ADD, gasitAbonare));
            return abonare;
        }else
        {
            return null;
        }
    }
    public List<Eveniment> evenimenteLaCareSuntAbonat(Utilizator user){

        List<Long> listInt = StreamSupport.stream(repoAbonareEvent.findAll().spliterator(), false)
                .filter(x->x.getIdUtilizator().toString().equals(user.getId().toString()))
                .map(AbonareEveniment::getIdEveniment)
                .collect(Collectors.toList());

        List<Eveniment> events = new ArrayList<>();

        for(Eveniment event : eventRepo.findAll()){
            if(listInt.contains(event.getId())){
                if(LocalDateTime.now().isBefore(event.getDataEvent())){
                    events.add(event);
                }
            }
        }
        return events;
    }
    public AbonareEveniment dezabonareEveniment(Eveniment event,Utilizator user){
        AbonareEveniment gasitAbonare = findAbonarebyEventAndUser(event,user);
        if(gasitAbonare == null){
            return null;
        }else
        {
            repoAbonareEvent.delete(gasitAbonare.getId());
            notifyObservers(new ChangeEvent(ChangeEventType.AE_DEL, gasitAbonare));
            return gasitAbonare;
        }
    }






}
