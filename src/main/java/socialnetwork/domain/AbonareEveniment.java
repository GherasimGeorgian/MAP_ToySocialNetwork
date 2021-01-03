package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AbonareEveniment extends Entity<Tuple<Long,Long>>{

    private Long idAbonare;
    private Long idUtilizator;
    private Long idEveniment;
    private LocalDateTime dataAbonare;

    public AbonareEveniment(long id_abonare, long id_utilizator,long id_eveniment, LocalDateTime data_aboanare) {
        setId(new Tuple<Long,Long>(id_utilizator,id_eveniment));
        this.idAbonare = id_abonare;
        this.idUtilizator = id_utilizator;
        this.idEveniment = id_eveniment;
        this.dataAbonare = data_aboanare;
    }

    public Long getIdAbonare() {
        return idAbonare;
    }

    public Long getIdUtilizator() {
        return idUtilizator;
    }

    public Long getIdEveniment() {
        return idEveniment;
    }

    public LocalDateTime getDataAbonare() {
        return dataAbonare;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String data_abonare = dataAbonare.format(formatter);
        return new String(data_abonare + " "  + getIdEveniment() + " " + getIdUtilizator());
    }
}
