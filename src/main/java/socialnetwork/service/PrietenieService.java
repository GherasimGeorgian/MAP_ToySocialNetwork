package socialnetwork.service;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;

public class PrietenieService {
    private Repository<Tuple<Long,Long>, Prietenie> repo;

    public PrietenieService(Repository<Tuple<Long,Long>, Prietenie> repo) {
        this.repo = repo;
    }

    public Iterable<Prietenie> getAll(){
        return repo.findAll();
    }
    public Prietenie adaugaPrieten(Utilizator u1,Utilizator u2){

        long id_utilizator1 = u1.getId();
        long id_utilizator2 = u2.getId();


            //alegem id-ul cu nr mai mic, atunci cand adaugam 1-3 si dupa 3-1 se v-a adauga tot 1-3
            if (id_utilizator1 > id_utilizator2) {
                long aux = id_utilizator1;
                id_utilizator1 = id_utilizator2;
                id_utilizator2 = aux;
            }

            Prietenie ptr = new Prietenie((long) id_utilizator1, (long) id_utilizator2);
        Prietenie task = repo.save(ptr);
        return task;
    }
    public Prietenie stergePrietenie(Utilizator u1,Utilizator u2){
        long id_utilizator1 = u1.getId();
        long id_utilizator2 = u2.getId();

        return repo.delete(new Tuple<>(id_utilizator1,id_utilizator2));
    }


    ///TO DO: add other methods
}
