package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.repository.RepositoryOptional;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.InviteChangeEvent;
import socialnetwork.utils.events.PrietenieDTOChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InviteServiceFullDB implements Observable<InviteChangeEvent> {
    private RepositoryOptional<Long, Utilizator> userDataBase;
    private RepositoryOptional<Tuple<Long,Long>, Prietenie> repoPrietenie;
    private RepositoryOptional<Long, Message> messageRepo;
    private RepositoryOptional<Long, Invite> inviteRepo;

    public InviteServiceFullDB(RepositoryOptional<Long,Utilizator> userDataBase,
                             RepositoryOptional<Tuple<Long,Long>, Prietenie> repoPtr,
                             RepositoryOptional<Long, Message> messageRepo,
                             RepositoryOptional<Long, Invite> inviteRepo) {
        this.userDataBase = userDataBase;
        this.repoPrietenie = repoPtr;
        this.messageRepo = messageRepo;
        this.inviteRepo = inviteRepo;
    }
    private List<Observer<InviteChangeEvent>> observers=new ArrayList<>();


    @Override
    public void addObserver(Observer<InviteChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<InviteChangeEvent> e) {
        //observers.remove(e);
    }
    @Override
    public void notifyObservers(InviteChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

    public Iterable<Utilizator> getAllUsers(){
        return userDataBase.findAll();
    }


    public Utilizator findByNumePrenume(String nume, String prenume){
        for(Utilizator u : this.getAllUsers()){
            if(u.getFirstName().compareTo(nume) == 0 && u.getLastName().compareTo(prenume) == 0){
                return u;
            }
        }
        return null;
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
            notifyObservers(new InviteChangeEvent(ChangeEventType.ADD, null));
        }
        else{
            throw new ServiceException("Cei 2 useri sunt deja prieteni!");
        }


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
        List<Invite> invitationsUser = StreamSupport.stream(inviteRepo.findAll().spliterator(), false)
                .filter(toUser)
                .collect(Collectors.toList());
        return invitationsUser;
    }


    public Invite stergeInvitatie(Long id) throws Exception{

        if(id == null){
            throw new Exception("Id-ul invitatiei nu exista!");
        }
        Invite invite= inviteRepo.delete(new Long(id)).get();

        notifyObservers(new InviteChangeEvent(ChangeEventType.DELETE, null));

        return invite;
    }
}
